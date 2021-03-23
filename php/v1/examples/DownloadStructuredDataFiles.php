<?php
/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Require the base class.
require_once __DIR__ . "/../BaseExample.php";

/**
 * This example creates a structured data file (SDF) download task using a
 * ParentEntityFilter, monitors that task, and downloads the resulting
 * structured data files once completed.
 */
class DownloadStructuredDataFiles extends BaseExample
{
    // The following values control retry behavior while the task is processing.
    // Minimum amount of time between polling requests. Defaults to 5 seconds.
    private const MIN_RETRY_INTERVAL = 5;
    // Maximum amount of time between polling requests. Defaults to 5 minutes.
    private const MAX_RETRY_INTERVAL = 300;
    // Maximum amount of time to spend polling. Defaults to 5 hours.
    private const MAX_RETRY_ELAPSED_TIME = 18000;

    /**
     * (non-PHPdoc)
     * @see BaseExample::getInputParameters()
     */
    protected function getInputParameters(): array
    {
        return array(
            array(
                'name' => 'partner_id',
                'display' => 'Partner ID '
                    . '(must be included if Advertiser ID is not)',
                'required' => false
            ),
            array(
                'name' => 'advertiser_id',
                'display' => 'Advertiser ID '
                    . '(must be included if Partner ID is not)',
                'required' => false
            ),
            array(
                'name' => 'version',
                'display' => 'Version',
                'required' => true
            ),
            array(
                'name' => 'file_types',
                'display' => 'File Types (comma-separated)',
                'required' => true
            ),
            array(
                'name' => 'filter_type',
                'display' => 'Filter Type',
                'required' => true
            ),
            array(
                'name' => 'filter_ids',
                'display' => 'Filter IDs (comma-separated)',
                'required' => false
            ),
            array(
                'name' => 'output_file',
                'display' => 'Output File',
                'required' => true
            )
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::run()
     */
    public function run()
    {
        $values = $this->formValues;

        $partnerId = $values['partner_id'];
        $advertiserId = $values['advertiser_id'];

        if ($advertiserId != "" && $partnerId != ""){
            printf('<p>Provide either a partner or advertiser ID.</p>');
            $this->renderInputForm();
            return;
        }

        $version = $values['version'];
        $fileTypes = explode(",", $values['file_types']);
        $filterType = $values['filter_type'];
        $filterIds = explode(",", $values['filter_ids']);
        $outputFile = $values['output_file'];

        try {
            $operation = $this->createSdfDownloadTask(
                $partnerId,
                $advertiserId,
                $version,
                $fileTypes,
                $filterType,
                $filterIds
            );

            print '<h3>Waiting for task to complete...</h3>';

            $resourceName = $this->waitForTask($operation);
            $this->downloadFile($resourceName, $outputFile);
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }
    }

    /**
     * Creates an SDF Download Task using a Parent Entity Filter.
     * @param string $partnerId the root ID determining the context of the
     *     download request. This may be an empty string if an advertiser ID is
     *     being used instead.
     * @param string $advertiserId the root ID determining the context of the
     *     download request. This may be an empty string if an partner ID is
     *     being used instead.
     * @param string $version an SdfVersion enum value specifying the SDF
     *     version format to generate the files in.
     * @param array $fileTypes an array of FileType enum values specifying the
     *     SDF file types to generate.
     * @param string $filterType a FilterType enum value specifying the resource
     *     type by which the content of the SDFs will be filtered by.
     * @param array $filterIds an array of resource IDs of the type specified in
     *     filterType parameter. If the filterType parameter is
     *     FILTER_TYPE_NONE, than this array can be empty.
     * @return Google_Service_DisplayVideo_Operation the created SdfDownloadTask
     */
    private function createSdfDownloadTask(
        string $partnerId,
        string $advertiserId,
        string $version,
        array $fileTypes,
        string $filterType,
        array $filterIds
    ): Google_Service_DisplayVideo_Operation {
        $createSdfDownloadTaskRequest =
            new Google_Service_DisplayVideo_CreateSdfDownloadTaskRequest();

        if ($advertiserId != "") {
            $createSdfDownloadTaskRequest->setAdvertiserId($advertiserId);
        } else {
            $createSdfDownloadTaskRequest->setPartnerId($partnerId);
        }

        $createSdfDownloadTaskRequest->setVersion($version);

        $parentEntityFilter =
            new Google_Service_DisplayVideo_ParentEntityFilter();
        $parentEntityFilter->setFileType($fileTypes);
        $parentEntityFilter->setFilterType($filterType);
        if ($filterIds[0] != "") {
            $parentEntityFilter->setFilterIds($filterIds);
        }
        $createSdfDownloadTaskRequest->setParentEntityFilter(
            $parentEntityFilter
        );

        // Call the API, creating the SDF Download Task.
        $operation = $this->service->sdfdownloadtasks->create(
            $createSdfDownloadTaskRequest
        );

        printf('<p>SDF download task %s was created.</p>', $operation['name']);

        return $operation;
    }

    /**
     * Polls the running SdfDownloadTask to see if it is finished processing.
     * Uses an exponential backoff policy to limit retries and conserve quota.
     * @param Google_Service_DisplayVideo_Operation $operation the
     *     SdfDownloadTask to be monitored for completion.
     * @return string|null the resource name of the generated SDF media
     *     resource. This may be null if the SdfDownloadTask finishes in error
     *     or takes longer to generate than the set maximum allowed elapsed
     *     time.
     */
    private function waitForTask(
        Google_Service_DisplayVideo_Operation $operation
    ): ?string {
        $sleep = 0;
        $startTime = time();

        do {
            // Call the API, retrieving the SDF Download Task.
            $operation = $this->service->sdfdownloadtasks_operations->get(
                $operation->getName()
            );

            if ($operation->getDone() === true) {
                if($operation->getError() !== null) {
                    $error = $operation->getError();
                    printf(
                        'The operation finished in error with code %s: %s<br>',
                        $error->setCode(),
                        $error->setMessage()
                    );
                    return null;
                } else {
                    $response = $operation->getResponse();
                    printf(
                        'The operation completed successfully. Resource %s was '
                            . 'created. Ready to download.<br>',
                        $response['resourceName']
                    );
                    return $response['resourceName'];
                }
            } elseif (time() - $startTime > self::MAX_RETRY_ELAPSED_TIME) {
                printf('SDF download task processing deadline exceeded<br>');
                return null;
            }

            $sleep = $this->getNextSleepInterval($sleep);
            printf(
                'The operation is still running, sleeping for %d seconds<br>',
                $sleep
            );
            sleep($sleep);
        } while (true);
    }

    /**
     * Returns the next sleep interval to be used.
     * @param int $previousSleepInterval the previous sleep interval used.
     * @return int the next sleep interval to use.
     */
    private function getNextSleepInterval(int $previousSleepInterval): int
    {
        $minInterval = max(self::MIN_RETRY_INTERVAL, $previousSleepInterval);
        $maxInterval = max(
            self::MIN_RETRY_INTERVAL,
            $previousSleepInterval * 3
        );
        return min(self::MAX_RETRY_INTERVAL, rand($minInterval, $maxInterval));
    }

    /**
     * Downloads the given media resource to the given file location.
     * @param string $resourceName the resource name of the generated SDF media
     *     resource to download.
     * @param string $outputFile the location to which the SDF zip file will be
     *     downloaded.
     */
    private function downloadFile(string $resourceName, string $outputFile)
    {
        $client = $this->service->getClient();
        $client->setDefer(true);

        $request = $this->service->media->download(
            $resourceName,
            array('alt' => 'media')
        );

        // Call the API, getting the generated SDF.
        $response = $client->execute($request);
        $responseBody = $response->getBody();

        // Writes the downloaded file. If the file already exists, it is
        // overwritten.
        file_put_contents($outputFile, $responseBody);
        $client->setDefer(false);

        printf('<h3>Report file saved to: %s</h3>', $outputFile);

    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public function getName(): string
    {
        return 'Download Structured Data Files';
    }
}