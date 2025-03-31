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
 * This example uploads the given image and logo assets and creates a native
 * site creative object under the given Display & Video 360 advertiser.
 */
class CreateNativeSiteCreative extends BaseExample
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
                'name' => 'image_asset_file',
                'display' => 'Image Asset File',
                'file' => true,
                'required' => true
            ),
            array(
                'name' => 'logo_asset_file',
                'display' => 'Logo Asset File',
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
                'name' => 'advertiser_name',
                'display' => 'Advertiser Name',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'headline',
                'display' => 'Headline',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'body_text',
                'display' => 'Body Text',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'landing_page_url',
                'display' => 'Landing Page URL',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'caption_url',
                'display' => 'Caption URL',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'call_to_action',
                'display' => 'Call to Action',
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
        $imageAsset = $values['image_asset_file'];
        $logoAsset = $values['logo_asset_file'];
        $creativeHeightPixels = $values['creative_height_pixels'];
        $creativeWidthPixels = $values['creative_width_pixels'];
        $advertiserName = $values['advertiser_name'];
        $headline = $values['headline'];
        $bodyText = $values['body_text'];
        $landingPageUrl = $values['landing_page_url'];
        $captionUrl = $values['caption_url'];
        $callToAction = $values['call_to_action'];

        $creative = new Google_Service_DisplayVideo_Creative();
        $creative->setDisplayName($displayName);
        $creative->setEntityStatus('ENTITY_STATUS_ACTIVE');
        $creative->setHostingSource('HOSTING_SOURCE_HOSTED');
        $creative->setCreativeType('CREATIVE_TYPE_NATIVE');

        $dimensions = new Google_Service_DisplayVideo_Dimensions();
        $dimensions->setHeightPixels($creativeHeightPixels);
        $dimensions->setWidthPixels($creativeWidthPixels);
        $creative->setDimensions($dimensions);

        $exitEvent = new Google_Service_DisplayVideo_ExitEvent();
        $exitEvent->setType('EXIT_EVENT_TYPE_DEFAULT');
        $exitEvent->setUrl($landingPageUrl);
        $creative->setExitEvents(array($exitEvent));

        // Upload and set the assets and create the native site creative under
        // the given advertiser.
        try {
            $creative->setAssets(
                array(
                    CreativeAssetUtils::createAssetAssociation(
                        CreativeAssetUtils::uploadAsset(
                            $this->service,
                            $advertiserId,
                            $imageAsset
                        ),
                        'ASSET_ROLE_MAIN'
                    ),
                    CreativeAssetUtils::createAssetAssociation(
                        CreativeAssetUtils::uploadAsset(
                            $this->service,
                            $advertiserId,
                            $logoAsset
                        ),
                        'ASSET_ROLE_ICON'
                    ),
                    CreativeAssetUtils::createAssetAssociation(
                        CreativeAssetUtils::createAsset(
                            $advertiserName
                        ),
                        'ASSET_ROLE_ADVERTISER_NAME'
                    ),
                    CreativeAssetUtils::createAssetAssociation(
                        CreativeAssetUtils::createAsset(
                            $headline
                        ),
                        'ASSET_ROLE_HEADLINE'
                    ),
                    CreativeAssetUtils::createAssetAssociation(
                        CreativeAssetUtils::createAsset(
                            $bodyText
                        ),
                        'ASSET_ROLE_BODY'
                    ),
                    CreativeAssetUtils::createAssetAssociation(
                        CreativeAssetUtils::createAsset(
                            $captionUrl
                        ),
                        'ASSET_ROLE_CAPTION_URL'
                    ),
                    CreativeAssetUtils::createAssetAssociation(
                        CreativeAssetUtils::createAsset(
                            $callToAction
                        ),
                        'ASSET_ROLE_CALL_TO_ACTION'
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

        printf('<p>Native Site Creative %s was created.</p>', $result['name']);
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Create Native Site Creative';
    }
}