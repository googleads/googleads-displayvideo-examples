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
require_once __DIR__ . "/Utils/PrintUtils.php";

/**
 * This example updates the audience targeting of a line item. It takes a list
 * of Google audience IDs and adds them to be included in any existing audience
 * targeting of the line item.
 */
class AppendAudienceAssignedTargetingOption extends BaseExample
{
    // Audience Group TargetingType Enum value.
    const AUDIENCE_TARGETING_TYPE = "TARGETING_TYPE_AUDIENCE_GROUP";
    // Assigned targeting option ID used for audience targeting.
    const AUDIENCE_TARGETING_OPTION_ID = "audienceGroup";

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
                'name' => 'additional_google_audiences',
                'display' => 'Google audience IDs to add to audience '
                    . 'targeting, comma-separated (available IDs are '
                    . 'retrievable using '
                    . '<a href="https://developers.google.com/display-video/api/reference/rest/v1/googleAudiences/list">'
                    . '<code>googleAudiences.list</code></a>)',
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
        $additionalGoogleAudiences =
            explode(",", $values['additional_google_audiences']);

        $newGoogleAudienceTargetingSettings = array();

        // Build Google audience targeting setting objects to add.
        foreach ($additionalGoogleAudiences as $googleAudienceId) {
            $googleAudienceSetting =
                new Google_Service_DisplayVideo_GoogleAudienceTargetingSetting();
            $googleAudienceSetting->setGoogleAudienceId($googleAudienceId);
            $newGoogleAudienceTargetingSettings[] = $googleAudienceSetting;
        }

        // Call the API, retrieving the existing audience targeting for the
        // line item.
        try {
            $response = $this
                ->service
                ->advertisers_lineItems_targetingTypes_assignedTargetingOptions
                ->listAdvertisersLineItemsTargetingTypesAssignedTargetingOptions(
                    $advertiserId,
                    $lineItemId,
                    AppendAudienceAssignedTargetingOption::AUDIENCE_TARGETING_TYPE
                );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        // Create a bulk edit request. It will delete any existing targeting
        // option and create the updated audience targeting option.
        $bulkEditRequest =
           new Google_Service_DisplayVideo_BulkEditAssignedTargetingOptionsRequest();
        $bulkEditRequest->setLineItemIds(array($lineItemId));

        // Iterate over existing targeting, find audience targeting option,
        // retrieve the details and set it to be deleted in the bulk edit.
        $updatedAudienceGroupDetails =
            new Google_Service_DisplayVideo_AudienceGroupAssignedTargetingOptionDetails();
        foreach ($response->getAssignedTargetingOptions() as $option) {
            if (
                $option->getAssignedTargetingOptionId()
                == AppendAudienceAssignedTargetingOption::AUDIENCE_TARGETING_OPTION_ID
            ) {
                $updatedAudienceGroupDetails =
                    $option->getAudienceGroupDetails();

                $deleteAudienceTargetingRequest =
                    new Google_Service_DisplayVideo_DeleteAssignedTargetingOptionsRequest();
                $deleteAudienceTargetingRequest->setTargetingType(
                    AppendAudienceAssignedTargetingOption::AUDIENCE_TARGETING_TYPE
                );
                $deleteAudienceTargetingRequest->setAssignedTargetingOptionIds(
                    array($option->getAssignedTargetingOptionId())
                );

                $bulkEditRequest->setDeleteRequests(
                    array($deleteAudienceTargetingRequest)
                );

                break;
            }
        }

        // Retrieve existing Google audience group. If empty, create new
        // GoogleAudienceGroup object.
        $updatedIncludedGoogleAudienceGroup =
            $updatedAudienceGroupDetails->getIncludedGoogleAudienceGroup();
        if (empty($updatedIncludedGoogleAudienceGroup)) {
            $updatedIncludedGoogleAudienceGroup =
                new Google_Service_DisplayVideo_GoogleAudienceGroup();
        }

        // Add new Google audiences to existing Google audience group.
        $updatedIncludedGoogleAudienceGroup->setSettings(
            array_merge(
                $updatedIncludedGoogleAudienceGroup->getSettings() ?? array(),
                $newGoogleAudienceTargetingSettings
            )
        );

        // Set updated Google audience group to existing audience targeting
        // settings.
        $updatedAudienceGroupDetails->setIncludedGoogleAudienceGroup(
            $updatedIncludedGoogleAudienceGroup
        );

        // Build new assigned targeting option with updated audience details.
        $newAudienceTargetingOption =
            new Google_Service_DisplayVideo_AssignedTargetingOption();
        $newAudienceTargetingOption->setAudienceGroupDetails(
            $updatedAudienceGroupDetails
        );

        // Build and add create request to bulk edit request.
        $createAudienceTargetingRequest =
            new Google_Service_DisplayVideo_CreateAssignedTargetingOptionsRequest();
        $createAudienceTargetingRequest->setTargetingType(
            AppendAudienceAssignedTargetingOption::AUDIENCE_TARGETING_TYPE
        );
        $createAudienceTargetingRequest->setAssignedTargetingOptions(
            array($newAudienceTargetingOption)
        );
        $bulkEditRequest->setCreateRequests(
            array($createAudienceTargetingRequest)
        );

        // Call the API, replacing the audience assigned targeting option for
        // line item.
        try {
            $response = $this
                ->service
                ->advertisers_lineItems
                ->bulkEditAssignedTargetingOptions(
                    $advertiserId,
                    $bulkEditRequest
                );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        // Print information returned by the bulk edit request.
        PrintUtils::printBulkEditTargetingResponse($response);
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Append Audience Assigned Targeting Option';
    }
}
