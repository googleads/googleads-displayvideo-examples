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
 * This example lists all of the targeting options available for the Browser
 * targeting type.
 */
class ListBrowserTargetingOptions extends BaseExample
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

        print '<h2>Listing all available browser targeting options</h2>';

        $response = null;
        $nextPageToken = null;

        do {
            $optParams = array(
                'advertiserId' => $advertiserId,
                'pageToken' => $nextPageToken
            );

            // Call the API, getting the browser targeting options for the
            // identified advertiser.
            $response = $this
                ->service
                ->targetingTypes_targetingOptions
                ->listTargetingTypesTargetingOptions(
                    'TARGETING_TYPE_BROWSER',
                    $optParams
                );

            if (!empty($response->getTargetingOptions())) {
                $this->printTargetingOptions($response->getTargetingOptions());
            } else {
                print '<p>No targeting options returned</p>';
            }

            // Update the next page token.
            $nextPageToken = $response->getNextPageToken();
        } while (
            !empty($response->getTargetingOptions()) &&
            !empty($nextPageToken)
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'List Browser Targeting Options';
    }

    /**
     * Prints the given browser targeting options.
     * @param array $options browser targeting options to be printed.
     */
    protected function printTargetingOptions(array $options)
    {
        foreach ($options as $option) {
            printf(
                '<p>Targeting Option ID: %s, Browser Display Name: %s</p>',
                $option['targetingOptionId'],
                $option['browserDetails']['displayName']
            );
        }
    }
}