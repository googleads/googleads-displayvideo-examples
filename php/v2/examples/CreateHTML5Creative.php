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

// Require the necessary utility class.
require_once __DIR__ . "/Utils/CreativeAssetUtils.php";

/**
 * This example uploads the given HTML asset and creates an HTML5 creative
 * object under the given Display & Video 360 advertiser.
 */
class CreateHTML5Creative extends BaseExample
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
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'display_name',
                'display' => 'Display Name',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'html_asset_file',
                'display' => 'HTML Asset File',
                'file' => true,
                'required' => true
            ),
            array(
                'name' => 'creative_height_pixels',
                'display' => 'Creative Height in Pixels',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'creative_width_pixels',
                'display' => 'Creative Width in Pixels',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'exit_event_name',
                'display' => 'Exit Event Name',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'exit_event_url',
                'display' => 'Exit Event URL',
                'file' => false,
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
        $displayName = $values['display_name'];
        $htmlAsset = $values['html_asset_file'];
        $creativeHeightPixels = $values['creative_height_pixels'];
        $creativeWidthPixels = $values['creative_width_pixels'];
        $exitEventName = $values['exit_event_name'];
        $exitEventUrl = $values['exit_event_url'];

        $creative = new Google_Service_DisplayVideo_Creative();
        $creative->setDisplayName($displayName);
        $creative->setEntityStatus('ENTITY_STATUS_ACTIVE');
        $creative->setHostingSource('HOSTING_SOURCE_HOSTED');
        $creative->setCreativeType('CREATIVE_TYPE_STANDARD');

        $dimensions = new Google_Service_DisplayVideo_Dimensions();
        $dimensions->setHeightPixels($creativeHeightPixels);
        $dimensions->setWidthPixels($creativeWidthPixels);
        $creative->setDimensions($dimensions);

        $exitEvent = new Google_Service_DisplayVideo_ExitEvent();
        $exitEvent->setName($exitEventName);
        $exitEvent->setType('EXIT_EVENT_TYPE_DEFAULT');
        $exitEvent->setUrl($exitEventUrl);
        $creative->setExitEvents(array($exitEvent));

        // Upload and set the asset and create the HTML5 creative under the
        // given advertiser.
        try {
            $creative->setAssets(
                array(
                    CreativeAssetUtils::createAssetAssociation(
                        CreativeAssetUtils::uploadAsset(
                            $this->service,
                            $advertiserId,
                            $htmlAsset
                        ),
                        'ASSET_ROLE_MAIN'
                    )
                )
            );

            $result = $this->service->advertisers_creatives->create(
                $advertiserId,
                $creative
            );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        printf('<p>HTML5 Creative %s was created.</p>', $result['name']);
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Create HTML5 Creative';
    }
}