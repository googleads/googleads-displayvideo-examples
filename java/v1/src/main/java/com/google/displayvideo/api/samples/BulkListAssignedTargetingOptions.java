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
import com.google.api.client.util.Strings;
import com.google.api.services.displayvideo.v1.DisplayVideo;
import com.google.api.services.displayvideo.v1.DisplayVideo.Advertisers.LineItems;
import com.google.api.services.displayvideo.v1.model.AssignedTargetingOption;
import com.google.api.services.displayvideo.v1.model.BulkListLineItemAssignedTargetingOptionsResponse;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;

/**
 * This example lists all of the targeting options assigned to a line item across targeting types.
 */
public class BulkListAssignedTargetingOptions {

  private static class BulkListAssignedTargetingOptionsParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description =
            "The ID of the parent advertiser of the line item whose targeting is being retrieved.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.LINE_ITEM_ID,
        description = "The ID of the line item whose targeting is being retrieved.",
        required = true)
    public Long lineItemId;

    @Parameter(
        names = ArgumentNames.FILTER,
        description = "The filter expression by which to filter the list results.")
    public String filter;
  }

  public static void main(String[] args) throws Exception {
    BulkListAssignedTargetingOptionsParams params = new BulkListAssignedTargetingOptionsParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.lineItemId = Long.valueOf("INSERT_LINE_ITEM_ID_HERE");

      // If no filter is needed, please leave filter empty.
      params.filter = "INSERT_FILTER_HERE";
    }

    DisplayVideo service = DisplayVideoFactory.getInstance(params.clientSecretsFile);

    runExample(service, params.advertiserId, params.lineItemId, params.filter);
  }

  public static void runExample(
      DisplayVideo service, long advertiserId, long lineItemId, String filter) throws Exception {

    // Configure the bulk list request and set filter.
    LineItems.BulkListLineItemAssignedTargetingOptions request =
        service
            .advertisers()
            .lineItems()
            .bulkListLineItemAssignedTargetingOptions(advertiserId, lineItemId)
            .setFilter(filter);

    // Create the response and nextPageToken variables.
    BulkListLineItemAssignedTargetingOptionsResponse response;
    String nextPageToken = null;

    do {
      // Set page token and execute the list request.
      response = request.setPageToken(nextPageToken).execute();

      // Check if response is empty.
      if (response.isEmpty()) {
        System.out.print("Bulk list request returned no Assigned Targeting Options");
        break;
      }

      // Iterate over retrieved targeting options.
      for (AssignedTargetingOption assignedOption : response.getAssignedTargetingOptions()) {
        System.out.printf("Assigned Targeting Option %s found%n", assignedOption.getName());
      }

      // Update the next page token.
      nextPageToken = response.getNextPageToken();
    } while (!Strings.isNullOrEmpty(nextPageToken));
  }
}
