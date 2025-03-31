// Copyright 2023 Google LLC
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
import com.google.api.services.displayvideo.v4.DisplayVideo;
import com.google.api.services.displayvideo.v4.DisplayVideo.Advertisers.LineItems;
import com.google.api.services.displayvideo.v4.model.AssignedTargetingOption;
import com.google.api.services.displayvideo.v4.model.BrowserAssignedTargetingOptionDetails;
import com.google.api.services.displayvideo.v4.model.BulkEditAssignedTargetingOptionsRequest;
import com.google.api.services.displayvideo.v4.model.BulkEditAssignedTargetingOptionsResponse;
import com.google.api.services.displayvideo.v4.model.CreateAssignedTargetingOptionsRequest;
import com.google.api.services.displayvideo.v4.model.DeleteAssignedTargetingOptionsRequest;
import com.google.api.services.displayvideo.v4.model.Status;
import com.google.displayvideo.api.samples.utils.ApiConstants;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This example edits the targeting of a line item across multiple targeting types. It takes a list
 * of currently assigned browser targeting options and a list of currently assigned device type
 * targeting options to unassign, as well as a list of new browser targeting options to assign.
 */
public class BulkEditAssignedTargetingOptions {

  private static class BulkEditAssignedTargetingOptionsParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description =
            "The ID of the parent advertiser of the line item whose targeting is being edited.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.LINE_ITEM_ID,
        description = "The ID of the line item whose targeting is being edited.",
        required = true)
    public Long lineItemId;

    @Parameter(
        names = ArgumentNames.DELETE_BROWSER_OPTIONS,
        description =
            "Currently assigned targeting option IDs of the browser targeting type to unassign"
                + " from the line item.")
    public List<String> deleteBrowserOptions;

    @Parameter(
        names = ArgumentNames.DELETE_DEVICE_MAKE_MODEL_OPTIONS,
        description =
            "Currently assigned targeting option IDs of the device targeting type to unassign from"
                + " the line item.")
    public List<String> deleteDeviceMakeModelOptions;

    @Parameter(
        names = ArgumentNames.CREATE_BROWSER_OPTIONS,
        description =
            "Targeting option IDs of the browser targeting type to assign to the line item.")
    public List<String> createBrowserOptions;
  }

  public static void main(String[] args) throws Exception {
    BulkEditAssignedTargetingOptionsParams params = new BulkEditAssignedTargetingOptionsParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.lineItemId = Long.valueOf("INSERT_LINE_ITEM_ID_HERE");
      params.deleteBrowserOptions = Arrays.asList("INSERT_DELETE_BROWSER_OPTIONS".split(","));
      params.deleteDeviceMakeModelOptions =
          Arrays.asList("INSERT_DELETE_DEVICE_MAKE_MODEL_OPTIONS".split(","));
      params.createBrowserOptions = Arrays.asList("INSERT_CREATE_BROWSER_OPTIONS".split(","));
    }

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile,
            params.useServiceAccount,
            params.serviceAccountKeyFile,
            params.additionalScopes);

    runExample(
        service,
        params.advertiserId,
        params.lineItemId,
        params.deleteBrowserOptions,
        params.deleteDeviceMakeModelOptions,
        params.createBrowserOptions);
  }

  public static void runExample(
      DisplayVideo service,
      long advertiserId,
      long lineItemId,
      List<String> deleteBrowserAssignedTargetingIds,
      List<String> deleteDeviceMakeModelAssignedTargetingIds,
      List<String> createBrowserTargetingIds)
      throws Exception {

    // Create a bulk edit request.
    BulkEditAssignedTargetingOptionsRequest requestContent =
        new BulkEditAssignedTargetingOptionsRequest();
    requestContent.setLineItemIds(Arrays.asList(lineItemId));

    // Build delete request list.
    List<DeleteAssignedTargetingOptionsRequest> deleteRequests = new ArrayList<>();

    // Add browser assigned targeting option IDs to delete request list.
    deleteRequests.add(
        new DeleteAssignedTargetingOptionsRequest()
            .setTargetingType(ApiConstants.BROWSER_TARGETING_TYPE)
            .setAssignedTargetingOptionIds(deleteBrowserAssignedTargetingIds));

    // Add household income assigned targeting option IDs to delete request list.
    deleteRequests.add(
        new DeleteAssignedTargetingOptionsRequest()
            .setTargetingType(ApiConstants.DEVICE_MAKE_MODEL_TARGETING_TYPE)
            .setAssignedTargetingOptionIds(deleteDeviceMakeModelAssignedTargetingIds));

    // Set delete requests in edit request.
    requestContent.setDeleteRequests(deleteRequests);

    // Build create request list.
    List<CreateAssignedTargetingOptionsRequest> createRequests = new ArrayList<>();

    // Create browser assigned targeting option create request.
    CreateAssignedTargetingOptionsRequest createBrowserTargetingRequest =
        new CreateAssignedTargetingOptionsRequest()
            .setTargetingType(ApiConstants.BROWSER_TARGETING_TYPE);

    // Create and set list of browser assigned targeting options.
    List<AssignedTargetingOption> createBrowserAssignedTargetingOptions = new ArrayList<>();
    if (createBrowserTargetingIds != null) {
      for (String targetingOptionId : createBrowserTargetingIds) {
        createBrowserAssignedTargetingOptions.add(
            new AssignedTargetingOption()
                .setBrowserDetails(
                    new BrowserAssignedTargetingOptionDetails()
                        .setTargetingOptionId(targetingOptionId)));
      }
    }
    createBrowserTargetingRequest.setAssignedTargetingOptions(
        createBrowserAssignedTargetingOptions);

    // Add browser assigned targeting options to create request list.
    createRequests.add(createBrowserTargetingRequest);

    // Set create requests in edit request.
    requestContent.setCreateRequests(createRequests);

    // Configure the bulk list request.
    LineItems.BulkEditAssignedTargetingOptions request =
        service
            .advertisers()
            .lineItems()
            .bulkEditAssignedTargetingOptions(advertiserId, requestContent);

    // Execute bulk edit request.
    BulkEditAssignedTargetingOptionsResponse response = request.execute();

    // Display API response information.
    if (response.getUpdatedLineItemIds() != null && !response.getUpdatedLineItemIds().isEmpty()) {
      System.out.println("The targeting of the following line item IDs were updated:");
      for (Long updatedLineItemId : response.getUpdatedLineItemIds()) {
        System.out.printf("%s%n", updatedLineItemId);
      }
    }
    if (response.getFailedLineItemIds() != null && !response.getFailedLineItemIds().isEmpty()) {
      System.out.println("The targeting of the following line item IDs failed to update:");
      for (Long failedLineItemId : response.getFailedLineItemIds()) {
        System.out.printf("%s%n", failedLineItemId);
      }
    }
    if (response.getErrors() != null && !response.getErrors().isEmpty()) {
      System.out.println("The following errors were thrown when attempting to edit the targeting:");
      for (Status error : response.getErrors()) {
        System.out.printf("%s: %s%n", error.getCode(), error.getMessage());
      }
    }
  }
}
