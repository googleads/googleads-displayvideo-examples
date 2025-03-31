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

/**
 * Helper class to print information retrieved from the DV360 API.
 */
class PrintUtils
{
    /**
     * Prints the response of a bulkEditAssignedTargetingOptions request.
     * @param Google_Service_DisplayVideo_BulkEditAssignedTargetingOptionsResponse
     *     $response response from the BulkEditAssignedTargetingOptions request
     *     with lists of line item IDs that successfully updated and failed, as
     *     well as errors.
     */
    public static function printBulkEditTargetingResponse(
        Google_Service_DisplayVideo_BulkEditAssignedTargetingOptionsResponse $response
    ) {
        // List updated line item IDs.
        if (empty($response->getUpdatedLineItemIds())) {
            print '<p>No line items were successfully updated.</p>';
        } else {
            print '<p>The targeting of the following line item IDs were '
                . 'updated:</p><ul>';
            foreach ($response->getUpdatedLineItemIds() as $id) {
                printf('<li>%s</li>',$id);
            }
            print '</ul>';
        }

        // List line item IDs that failed to update.
        if (empty($response->getFailedLineItemIds())) {
            print '<p>No line items failed to update.</p>';
        } else {
            print '<p>The targeting of the following line item IDs failed to '
                . 'update:</p><ul>';
            foreach ($response->getFailedLineItemIds() as $id) {
                printf('<li>%s</li>',$id);
            }
            print '</ul>';
        }

        // List the errors thrown when the targeting was updated.
        if (empty($response->getErrors())) {
            print '<p>No errors were thrown.</p>';
        } else {
            print '<p>The following errors were thrown when attempting to '
                . 'update the targeting:</p><ul>';
            foreach ($response->getErrors() as $error) {
                printf(
                    '<li>%s: %s</li>',
                    $error->getCode(),
                    $error->getMessage()
                );
            }
            print '</ul>';
        }
    }

    /**
     * Prints the results of a bulkListAssignedTargetingOptions request.
     * @param array $lineItemIds the IDs of the relevant line items.
     * @param array $lineItemOptions line item assigned targeting options to
     *     be printed.
     */
    public static function printBulkListTargetingResponse(
        array $lineItemIds,
        array $lineItemOptions
    ) {
        // Print the IDs of the relevant line items.
        print '<h3>Listing targeting options assigned to the following line '
            . 'items:</h3>';
        print '<ul>';
        foreach ($lineItemIds as $id) {
            printf('<li>%s</li>', $id);
        }
        print '</ul>';

        // Print the resource names of the line item assigned targeting
        // options.
        print '<h3>Retrieved assigned targeting options:</h3>';
        print '<ul>';
        foreach ($lineItemOptions as $lineItemOption) {
            $option = $lineItemOption->getAssignedTargetingOption();
            printf('<li>%s</li>', $option->getName());
        }
        print '</ul>';
    }
}
