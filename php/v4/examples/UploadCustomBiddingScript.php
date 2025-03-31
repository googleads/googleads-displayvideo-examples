<?php
/*
 * Copyright 2023 Google LLC
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
 * This example uploads a script for the custom bidding algorithm.
 */
class UploadCustomBiddingScript extends BaseExample
{
    /**
     * (non-PHPdoc)
     * @see BaseExample::getInputParameters()
     */
    protected function getInputParameters(): array
    {
        return array(
            array(
                'name' => 'advertiser_id',
                'display' => 'Advertiser ID',
                'required' => true
            ),
            array(
                'name' => 'custom_bidding_algorithm_id',
                'display' => 'Custom Bidding Algorithm ID',
                'required' => true
            ),
            array(
                'name' => 'script_path',
                'display' => 'Script Path',
                'file' => true,
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

        $advertiserId = $values['advertiser_id'];
        $customBiddingAlgorithmId = $values['custom_bidding_algorithm_id'];
        $script = $values['script_path'];

        $uploadScriptOptParams = array(
            'advertiserId' => $advertiserId
        );

        try {
            // Retrieve a usable custom bidding script reference.
            $scriptRefResponse =
                $this->service->customBiddingAlgorithms->uploadScript(
                    $customBiddingAlgorithmId,
                    $uploadScriptOptParams
                );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        // Print the retrieved resource path.
        printf(
            '<p>The script will be uploaded to the following resource path: '
                . '%s</p>',
            $scriptRefResponse->getResourceName()
        );

        // Create the media body.
        $mediaBody = new Google_Service_DisplayVideo_GoogleBytestreamMedia();
        $mediaBody->setResourceName($scriptRefResponse->getResourceName());

        // Build params array for the upload request.
        $mediaUploadOptParams = array(
            'data' => file_get_contents($script['tmp_name']),
            'uploadType' => 'media',
            'resourceName' => $scriptRefResponse->getResourceName()
        );

        try {
            // Call the API, uploading the script file to Display & Video 360.
            $this->service->media->upload(
                $scriptRefResponse->getResourceName(),
                $mediaBody,
                $mediaUploadOptParams
            );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        // Create script object.
        $customBiddingScript =
            new Google_Service_DisplayVideo_CustomBiddingScript();
        $customBiddingScript->setScript($scriptRefResponse);

        $createScriptOptParams = array(
            'advertiserId' => $advertiserId
        );

        try {
            // Call the API, creating the custom bidding script using the script
            // file and under the advertiser and custom bidding algorithm given.
            $result = $this->service->customBiddingAlgorithms_scripts->create(
                $customBiddingAlgorithmId,
                $customBiddingScript,
                $createScriptOptParams
            );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        // Print the created custom bidding script.
        printf(
            '<p>The following script was created: %s.</p>',
            $result->getName()
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Upload Custom Bidding Script';
    }
}