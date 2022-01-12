// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.displayvideo.api.samples;

import com.beust.jcommander.Parameter;
import com.google.api.services.displayvideo.v1.DisplayVideo;
import com.google.api.services.displayvideo.v1.DisplayVideo.Advertisers.LineItems;
import com.google.api.services.displayvideo.v1.DisplayVideo.Advertisers.LineItems.TargetingTypes.AssignedTargetingOptions;
import com.google.api.services.displayvideo.v1.model.AssignedTargetingOption;
import com.google.api.services.displayvideo.v1.model.AudienceGroupAssignedTargetingOptionDetails;
import com.google.api.services.displayvideo.v1.model.BulkEditLineItemAssignedTargetingOptionsRequest;
import com.google.api.services.displayvideo.v1.model.BulkEditLineItemAssignedTargetingOptionsResponse;
import com.google.api.services.displayvideo.v1.model.CreateAssignedTargetingOptionsRequest;
import com.google.api.services.displayvideo.v1.model.DeleteAssignedTargetingOptionsRequest;
import com.google.api.services.displayvideo.v1.model.GoogleAudienceGroup;
import com.google.api.services.displayvideo.v1.model.GoogleAudienceTargetingSetting;
import com.google.api.services.displayvideo.v1.model.ListLineItemAssignedTargetingOptionsResponse;
import com.google.displayvideo.api.samples.utils.ApiConstants;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This example updates the audience targeting of a line item. It takes a list of Google audience
 * IDs with which to add to the existing audience targeting of the line item.
 */
public class AppendAudienceAssignedTargetingOption {

  private static class AppendAudienceAssignedTargetingOptionParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description =
            "The ID of the parent advertiser of the line item whose audience targeting option will"
                + " be updated.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.LINE_ITEM_ID,
        description = "The ID of the line item whose audience targeting option will be updated.",
        required = true)
    public Long lineItemId;

    @Parameter(
        names = ArgumentNames.ADDITIONAL_GOOGLE_AUDIENCES,
        description = "The Google audience IDs to add to the audience targeting.",
        required = true)
    public List<String> additionalGoogleAudiences;
  }

  public static void main(String[] args) throws Exception {
    AppendAudienceAssignedTargetingOptionParams params =
        new AppendAudienceAssignedTargetingOptionParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.lineItemId = Long.valueOf("INSERT_LINE_ITEM_ID_HERE");
      params.additionalGoogleAudiences =
          Arrays.asList("INSERT_ADDITIONAL_GOOGLE_AUDIENCES".split(","));
    }

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile,
            params.useServiceAccount,
            params.serviceAccountKeyFile,
            params.additionalScopes);

    runExample(service, params.advertiserId, params.lineItemId, params.additionalGoogleAudiences);
  }

  public static void runExample(
      DisplayVideo service,
      long advertiserId,
      long lineItemId,
      List<String> additionalGoogleAudiences)
      throws Exception {

    // Build Google audience targeting setting objects to add.
    List<GoogleAudienceTargetingSetting> newGoogleAudienceTargetingSettings =
        new ArrayList<GoogleAudienceTargetingSetting>();
    for (String googleAudienceId : additionalGoogleAudiences) {
      newGoogleAudienceTargetingSettings.add(
          new GoogleAudienceTargetingSetting()
              .setGoogleAudienceId(Long.parseLong(googleAudienceId)));
    }

    // Create a bulk edit request body.
    BulkEditLineItemAssignedTargetingOptionsRequest bulkEditRequestContent =
        new BulkEditLineItemAssignedTargetingOptionsRequest();

    // Retrieve any existing audience group targeting details.
    AudienceGroupAssignedTargetingOptionDetails audienceDetails =
        getAudienceTargeting(service, advertiserId, lineItemId);

    // If audience targeting already exists, set it to be deleted in the bulk edit. Existing
    // assigned targeting options cannot be updated, so any existing audience targeting option must
    // be deleted in order to assign a new, updated audience targeting option.
    if (audienceDetails != null) {
      bulkEditRequestContent.setDeleteRequests(
          Collections.singletonList(
              new DeleteAssignedTargetingOptionsRequest()
                  .setTargetingType(ApiConstants.AUDIENCE_TARGETING_TYPE)
                  .setAssignedTargetingOptionIds(
                      Arrays.asList(ApiConstants.AUDIENCE_TARGETING_OPTION_ID))));
    } else {
      audienceDetails = new AudienceGroupAssignedTargetingOptionDetails();
    }

    // Retrieve positively targeted Google audiences from existing audience details.
    GoogleAudienceGroup googleAudienceGroup = audienceDetails.getIncludedGoogleAudienceGroup();

    // If there are Google audiences that are positively targeted, add the new Google audience
    // settings to the retrieved settings list.
    // If not, create a new Google audience group object and assign the list of new Google audience
    // settings.
    if (googleAudienceGroup != null) {
      List<GoogleAudienceTargetingSetting> googleAudienceTargetingSettings =
          googleAudienceGroup.getSettings();
      googleAudienceTargetingSettings.addAll(newGoogleAudienceTargetingSettings);
      googleAudienceGroup.setSettings(googleAudienceTargetingSettings);
    } else {
      googleAudienceGroup =
          new GoogleAudienceGroup().setSettings(newGoogleAudienceTargetingSettings);
    }

    // Set updated Google audience group to existing audience targeting settings.
    audienceDetails.setIncludedGoogleAudienceGroup(googleAudienceGroup);

    // Build and add create request to bulk edit request body.
    bulkEditRequestContent.setCreateRequests(
        Collections.singletonList(
            new CreateAssignedTargetingOptionsRequest()
                .setTargetingType(ApiConstants.AUDIENCE_TARGETING_TYPE)
                .setAssignedTargetingOptions(
                    Collections.singletonList(
                        new AssignedTargetingOption().setAudienceGroupDetails(audienceDetails)))));

    // Configure the bulk edit request.
    LineItems.BulkEditLineItemAssignedTargetingOptions request =
        service
            .advertisers()
            .lineItems()
            .bulkEditLineItemAssignedTargetingOptions(
                advertiserId, lineItemId, bulkEditRequestContent);

    // Execute bulk edit request.
    BulkEditLineItemAssignedTargetingOptionsResponse response = request.execute();

    // Display new assigned audience targeting.
    if (response.isEmpty()) {
      System.out.print("Error: No new assigned audience targeting option was returned.");
      return;
    } else {
      for (AssignedTargetingOption assignedOption : response.getCreatedAssignedTargetingOptions()) {
        System.out.printf(
            "Assigned Targeting Option with the following audience information was created: %s%n",
            assignedOption.getAudienceGroupDetails());
      }
    }
  }

  /**
   * Retrieves the existing audience targeting details for a line item. Returns null if no audience
   * targeting is found.
   */
  private static AudienceGroupAssignedTargetingOptionDetails getAudienceTargeting(
      DisplayVideo service, long advertiserId, long lineItemId) throws Exception {

    // Configure the list request.
    AssignedTargetingOptions.List request =
        service
            .advertisers()
            .lineItems()
            .targetingTypes()
            .assignedTargetingOptions()
            .list(advertiserId, lineItemId, ApiConstants.AUDIENCE_TARGETING_TYPE);

    // Create and execute the list request.
    ListLineItemAssignedTargetingOptionsResponse response = request.execute();

    // Iterate over retrieved assigned targeting options and return existing audience targeting.
    if (!response.isEmpty()) {
      for (AssignedTargetingOption option : response.getAssignedTargetingOptions()) {
        if (option
            .getAssignedTargetingOptionId()
            .equals(ApiConstants.AUDIENCE_TARGETING_OPTION_ID)) {
          return option.getAudienceGroupDetails();
        }
      }
    }

    // If no audience targeting exists, return null.
    return null;
  }
}
