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
 * This example assigns a browser targeting option to a given line item.
 */
class CreateBrowserAssignedTargetingOption extends BaseExample
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
                'name' => 'line_item_id',
                'display' => 'Line Item ID',
                'required' => true
            ),
            array(
                'name' => 'browser_targeting_option_id',
                'display' => 'Browser Targeting Option ID',
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
        $lineItemId = $values['line_item_id'];
        $browserTargetingOptionId = $values['browser_targeting_option_id'];

        $assignedTargetingOption =
            new Google_Service_DisplayVideo_AssignedTargetingOption();

        $details =
            new Google_Service_DisplayVideo_BrowserAssignedTargetingOptionDetails();
        $details->setTargetingOptionId($browserTargetingOptionId);

        $assignedTargetingOption->setBrowserDetails($details);

        // Call the API, creating the browser assigned targeting option for the
        // given line item.
        try {
            $result = $this
                ->service
                ->advertisers_lineItems_targetingTypes_assignedTargetingOptions
                ->create(
                    $advertiserId,
                    $lineItemId,
                    'TARGETING_TYPE_BROWSER',
                    $assignedTargetingOption
                );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        printf(
            '<p>Assigned Targeting Option %s was created.</p>',
            $result['name']
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public function getName(): string
    {
        return 'Create Browser Assigned Targeting Option';
    }
}