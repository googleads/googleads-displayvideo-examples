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
 * This example edits the targeting of a line item across multiple targeting
 * types. It takes a list of currently assigned browser targeting options and a
 * list of currently assigned device type targeting options to unassign, as well
 * as a list of new browser targeting options to assign.
 */
class BulkEditAssignedTargetingOptions extends BaseExample
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
                'name' => 'browser_del_targeting_ops',
                'display' => 'Browser Assigned Targeting Options to Delete '
                    . '(comma-separated)',
                'required' => false
            ),
            array(
                'name' => 'device_del_targeting_ops',
                'display' => 'Device Type Assigned Targeting Options to Delete '
                    . '(comma-separated)',
                'required' => false
            ),
            array(
                'name' => 'browser_create_targeting_ops',
                'display' => 'Browser Assigned Targeting Options to Create '
                    . '(comma-separated)',
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
        $browserDelOptions = explode(",", $values['browser_del_targeting_ops']);
        $deviceDelOptions = explode(",", $values['device_del_targeting_ops']);
        $browserCreateOptions = explode(
            ",",
            $values['browser_create_targeting_ops']
        );

        $deleteRequests = array();

        $deleteBrowserTargetingRequest =
            new Google_Service_DisplayVideo_DeleteAssignedTargetingOptionsRequest();
        $deleteBrowserTargetingRequest->setTargetingType(
            "TARGETING_TYPE_BROWSER"
        );
        $deleteBrowserTargetingRequest->setAssignedTargetingOptionIds(
            $browserDelOptions
        );
        $deleteRequests[] = $deleteBrowserTargetingRequest;

        $deleteDeviceTargetingRequest =
            new Google_Service_DisplayVideo_DeleteAssignedTargetingOptionsRequest();
        $deleteDeviceTargetingRequest->setTargetingType(
            "TARGETING_TYPE_DEVICE_TYPE"
        );
        $deleteDeviceTargetingRequest->setAssignedTargetingOptionIds(
            $deviceDelOptions
        );
        $deleteRequests[] = $deleteDeviceTargetingRequest;

        $createRequests = array();

        $createBrowserAssignedTargetingOptions = array();
        foreach ($browserCreateOptions as $optionId) {
            $option = new Google_Service_DisplayVideo_AssignedTargetingOption();
            $details =
                new Google_Service_DisplayVideo_BrowserAssignedTargetingOptionDetails();
            $details->setTargetingOptionId($optionId);

            $option->setBrowserDetails($details);
            $createBrowserAssignedTargetingOptions[] = $option;
        }

        $createBrowserTargetingRequest =
            new Google_Service_DisplayVideo_CreateAssignedTargetingOptionsRequest();
        $createBrowserTargetingRequest->setTargetingType(
            "TARGETING_TYPE_BROWSER"
        );
        $createBrowserTargetingRequest->setAssignedTargetingOptions(
            $createBrowserAssignedTargetingOptions
        );
        $createRequests[] = $createBrowserTargetingRequest;

        $body =
            new Google_Service_DisplayVideo_BulkEditLineItemAssignedTargetingOptionsRequest();

        $body->setCreateRequests($createRequests);
        $body->setDeleteRequests($deleteRequests);

        // Call the API, editing the assigned targeting options for the identified
        // line item.
        try {
            $response = $this
                ->service
                ->advertisers_lineItems
                ->bulkEditLineItemAssignedTargetingOptions(
                    $advertiserId,
                    $lineItemId,
                    $body
                );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        if (!empty($response->getCreatedAssignedTargetingOptions())) {
            $this->printAssignedTargetingOptions(
                $response->getCreatedAssignedTargetingOptions()
            );
        } else {
            print '<p>No assigned targeting options created</p>';
        }
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public function getName(): string
    {
        return 'Bulk Edit Assigned Targeting Options';
    }

    /**
     * Prints the given created assigned targeting options.
     * @param array $options created assigned targeting options to be printed.
     */
    protected function printAssignedTargetingOptions(array $options)
    {
        foreach ($options as $option) {
            printf(
                '<p>Assigned Targeting Option %s was created</p>',
                $option['name']
            );
        }
    }
}