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
import com.google.api.services.displayvideo.v1.model.AssignedTargetingOption;
import com.google.api.services.displayvideo.v1.model.BrowserAssignedTargetingOptionDetails;
import com.google.api.services.displayvideo.v1.model.BulkEditLineItemAssignedTargetingOptionsRequest;
import com.google.api.services.displayvideo.v1.model.BulkEditLineItemAssignedTargetingOptionsResponse;
import com.google.api.services.displayvideo.v1.model.CreateAssignedTargetingOptionsRequest;
import com.google.api.services.displayvideo.v1.model.DeleteAssignedTargetingOptionsRequest;
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
        names = ArgumentNames.DELETE_DEVICE_OPTIONS,
        description =
            "Currently assigned targeting option IDs of the device targeting type to unassign from"
                + " the line item.")
    public List<String> deleteDeviceOptions;

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
      params.deleteDeviceOptions = Arrays.asList("INSERT_DELETE_DEVICE_OPTIONS".split(","));
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
        params.deleteDeviceOptions,
        params.createBrowserOptions);
  }

  public static void runExample(
      DisplayVideo service,
      long advertiserId,
      long lineItemId,
      List<String> deleteBrowserAssignedTargetingIds,
      List<String> deleteDeviceAssignedTargetingIds,
      List<String> createBrowserTargetingIds)
      throws Exception {

    // Create a bulk edit request.
    BulkEditLineItemAssignedTargetingOptionsRequest requestContent =
        new BulkEditLineItemAssignedTargetingOptionsRequest();

    // Build delete request list.
    ArrayList<DeleteAssignedTargetingOptionsRequest> deleteRequests =
        new ArrayList<DeleteAssignedTargetingOptionsRequest>();

    // Add browser assigned targeting option IDs to delete request list.
    deleteRequests.add(
        new DeleteAssignedTargetingOptionsRequest()
            .setTargetingType(ApiConstants.BROWSER_TARGETING_TYPE)
            .setAssignedTargetingOptionIds(deleteBrowserAssignedTargetingIds));

    // Add household income assigned targeting option IDs to delete request list.
    deleteRequests.add(
        new DeleteAssignedTargetingOptionsRequest()
            .setTargetingType(ApiConstants.DEVICE_TARGETING_TYPE)
            .setAssignedTargetingOptionIds(deleteDeviceAssignedTargetingIds));

    // Set delete requests in edit request.
    requestContent.setDeleteRequests(deleteRequests);

    // Build create request list.
    ArrayList<CreateAssignedTargetingOptionsRequest> createRequests =
        new ArrayList<CreateAssignedTargetingOptionsRequest>();

    // Create browser assigned targeting option create request.
    CreateAssignedTargetingOptionsRequest createBrowserTargetingRequest =
        new CreateAssignedTargetingOptionsRequest()
            .setTargetingType(ApiConstants.BROWSER_TARGETING_TYPE);

    // Create and set list of browser assigned targeting options.
    ArrayList<AssignedTargetingOption> createBrowserAssignedTargetingOptions =
        new ArrayList<AssignedTargetingOption>();
    for (String targetingOptionId : createBrowserTargetingIds) {
      createBrowserAssignedTargetingOptions.add(
          new AssignedTargetingOption()
              .setBrowserDetails(
                  new BrowserAssignedTargetingOptionDetails()
                      .setTargetingOptionId(targetingOptionId)));
    }
    createBrowserTargetingRequest.setAssignedTargetingOptions(
        createBrowserAssignedTargetingOptions);

    // Add browser assigned targeting options to create request list.
    createRequests.add(createBrowserTargetingRequest);

    // Set create requests in edit request.
    requestContent.setCreateRequests(createRequests);

    // Configure the bulk list request.
    LineItems.BulkEditLineItemAssignedTargetingOptions request =
        service
            .advertisers()
            .lineItems()
            .bulkEditLineItemAssignedTargetingOptions(advertiserId, lineItemId, requestContent);

    // Execute bulk edit request.
    BulkEditLineItemAssignedTargetingOptionsResponse response = request.execute();

    // Check if response is empty.
    // If not, iterate over created AssignedTargetingOptions.
    if (response.isEmpty()) {
      System.out.print("Bulk edit request created no new AssignedTargetingOptions");
    } else {
      for (AssignedTargetingOption assignedOption : response.getCreatedAssignedTargetingOptions()) {
        System.out.printf("AssignedTargetingOption %s was created%n", assignedOption.getName());
      }
    }
  }
}
