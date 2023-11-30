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
 * This example generates a default line item under the given
 * Display & Video 360 insertion order. The line item will inherit settings,
 * including targeting, from the insertion order. If generating a
 * Mobile App Install line item, an app ID must be provided.
 */
class GenerateDefaultLineItem extends BaseExample
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
                'name' => 'insertion_order_id',
                'display' => 'Insertion Order ID',
                'required' => true
            ),
            array(
                'name' => 'display_name',
                'display' => 'Display Name',
                'required' => true
            ),
            array(
                'name' => 'line_item_type',
                'display' => 'Line Item Type '
                    . '(<a href="https://developers.google.com/display-video/api/reference/rest/v1/advertisers.lineItems#lineitemtype">'
                    . 'LineItemType enum documentation</a>)',
                'required' => true
            ),
            array(
                'name' => 'app_id',
                'display' => 'App ID (For Mobile App Install Line Item Types)',
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
        $insertionOrderId = $values['insertion_order_id'];
        $displayName = $values['display_name'];
        $lineItemType = $values['line_item_type'];
        $appId = $values['app_id'];

        // Build the generateDefault request with the given parameters.
        $generateDefaultLineItemRequest =
            new Google_Service_DisplayVideo_GenerateDefaultLineItemRequest();
        $generateDefaultLineItemRequest
            ->setInsertionOrderId($insertionOrderId);
        $generateDefaultLineItemRequest->setDisplayName($displayName);
        $generateDefaultLineItemRequest->setLineItemType($lineItemType);

        // Add Mobile App object to request generating a Mobile App Install
        // line item.
        if ($lineItemType == "LINE_ITEM_TYPE_DISPLAY_MOBILE_APP_INSTALL"
            or $lineItemType == "LINE_ITEM_TYPE_VIDEO_MOBILE_APP_INSTALL"
        ) {
            if(empty($appId)) {
                $this->renderError(
                    new Exception(
                        'No App ID given for Mobile App Install line item.'
                    )
                );
                return;
            }
            $mobileApp = new Google_Service_DisplayVideo_MobileApp();
            $mobileApp->setAppId($appId);
            $generateDefaultLineItemRequest->setMobileApp($mobileApp);
        }

        // Call the API, generating the default line item under the advertiser
        // and insertion order given.
        try {
            $result = $this->service->advertisers_lineItems->generateDefault(
                $advertiserId,
                $generateDefaultLineItemRequest
            );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        // Display the new line item resource name.
        printf('<p>Line Item %s was created.</p>', $result['name']);
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Generate Default Line Item';
    }
}
