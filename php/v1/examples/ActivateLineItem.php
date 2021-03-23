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
 * This example activates an existing Display & Video 360 line item by
 * updating its entity status to "active". The given line item must currently
 * have a "draft" or "paused" entity status to be activated.
 */
class ActivateLineItem extends BaseExample
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

        $lineItem = new Google_Service_DisplayVideo_LineItem();
        $lineItem->setEntityStatus('ENTITY_STATUS_ACTIVE');
        $optParams = array('updateMask' => 'entityStatus');

        // Call the API, updating the entity status for the identified line
        // item.
        try {
            $result = $this->service->advertisers_lineItems->patch(
                $advertiserId,
                $lineItemId,
                $lineItem,
                $optParams
            );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        printf(
            '<p>Line Item %s now has entity status %s.</p>',
            $result['name'],
            $result['entityStatus']
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public function getName(): string
    {
        return 'Activate Line Item';
    }
}