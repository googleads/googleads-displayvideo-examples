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
 * This example creates an advertiser under the given Display & Video 360
 * partner.
 */
class CreateAdvertiser extends BaseExample
{
    /**
     * (non-PHPdoc)
     * @see BaseExample::getInputParameters()
     */
    protected function getInputParameters(): array
    {
        return array(
            array(
                'name' => 'partner_id',
                'display' => 'Partner ID',
                'required' => true
            ),
            array(
                'name' => 'display_name',
                'display' => 'Display Name',
                'required' => true
            ),
            array(
                'name' => 'domain_url',
                'display' => 'Domain URL',
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
        $displayName = $values['display_name'];
        $domainUrl = $values['domain_url'];

        $advertiser = new Google_Service_DisplayVideo_Advertiser();
        $advertiser->setPartnerId($partnerId);
        $advertiser->setDisplayName($displayName);
        $advertiser->setEntityStatus('ENTITY_STATUS_ACTIVE');

        $generalConfig =
            new Google_Service_DisplayVideo_AdvertiserGeneralConfig();
        $generalConfig->setDomainUrl($domainUrl);
        $generalConfig->setCurrencyCode('USD');
        $advertiser->setGeneralConfig($generalConfig);

        $adServerConfig =
            new Google_Service_DisplayVideo_AdvertiserAdServerConfig();

        $thirdPartyOnlyConfig =
            new Google_Service_DisplayVideo_ThirdPartyOnlyConfig();

        $adServerConfig->setThirdPartyOnlyConfig(
            new Google_Service_DisplayVideo_ThirdPartyOnlyConfig()
        );
        $advertiser->setAdServerConfig($adServerConfig);

        $advertiser->setCreativeConfig(
            new Google_Service_DisplayVideo_AdvertiserCreativeConfig()
        );

        // Call the API, creating the advertiser.
        try {
            $result = $this->service->advertisers->create($advertiser);
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        printf('<p>Advertiser %s was created.</p>', $result['name']);
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Create Advertiser';
    }
}