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
 * This example lists all of the targeting options assigned to a line item
 * across targeting types, optionally filtered by a given filter expression.
 */
class BulkListAssignedTargetingOptions extends BaseExample
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
                'name' => 'filter',
                'display' => 'Filter',
                'required' => false
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
        $filter = $values['filter'];

        printf(
            '<h2>Listing targeting options assigned to Line Item %s</h2>',
            $lineItemId
        );

        $response = null;
        $nextPageToken = null;

        do {
            $optParams = array(
                'filter' => $filter,
                'pageToken' => $nextPageToken
            );

            // Call the API, getting all the assigned targeting options for the
            // identified line item.
            try {
                $response = $this
                    ->service
                    ->advertisers_lineItems
                    ->bulkListLineItemAssignedTargetingOptions(
                        $advertiserId,
                        $lineItemId,
                        $optParams
                    );
            } catch (\Exception $e) {
                $this->renderError($e);
                return;
            }

            if (!empty($response->getAssignedTargetingOptions())) {
                $this->printAssignedTargetingOptions(
                    $response->getAssignedTargetingOptions()
                );
            } else {
                print '<p>No assigned targeting options returned</p>';
            }

            // Update the next page token.
            $nextPageToken = $response->getNextPageToken();
        } while (
            !empty($response->getAssignedTargetingOptions())
            && !empty($nextPageToken)
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public function getName(): string
    {
        return 'Bulk List Assigned Targeting Options';
    }

    /**
     * Prints the given found assigned targeting options.
     * @param array $options found assigned targeting options to be printed.
     */
    protected function printAssignedTargetingOptions(array $options)
    {
        foreach ($options as $option) {
            printf(
                '<p>Assigned Targeting Option %s found</p>',
                $option['name']
            );
        }
    }
}