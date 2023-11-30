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
import com.google.api.services.displayvideo.v2.DisplayVideo;
import com.google.api.services.displayvideo.v2.DisplayVideo.Advertisers.LineItems;
import com.google.api.services.displayvideo.v2.model.LineItem;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;

/**
 * This example activates an existing Display &amp; Video 360 line item by updating its entity
 * status to "active." The given line item must currently have a "draft" or "paused" entity status
 * to be activated.
 */
public class ActivateLineItem {

  private static class ActivateLineItemParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the parent advertiser of the line item to activate.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.LINE_ITEM_ID,
        description = "The ID of the line item to activate.",
        required = true)
    public Long lineItemId;
  }

  public static void main(String[] args) throws Exception {
    ActivateLineItemParams params = new ActivateLineItemParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.lineItemId = Long.valueOf("INSERT_LINE_ITEM_ID_HERE");
    }

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile,
            params.useServiceAccount,
            params.serviceAccountKeyFile,
            params.additionalScopes);

    runExample(service, params.advertiserId, params.lineItemId);
  }

  public static void runExample(DisplayVideo service, long advertiserId, long lineItemId)
      throws Exception {

    // Create the line item structure.
    LineItem lineItem = new LineItem().setEntityStatus("ENTITY_STATUS_ACTIVE");

    // Configure the patch request and set update mask to only update entity status.
    LineItems.Patch request =
        service
            .advertisers()
            .lineItems()
            .patch(advertiserId, lineItemId, lineItem)
            .setUpdateMask("entityStatus");

    // Create the advertiser.
    LineItem response = request.execute();

    // Display the new advertiser ID.
    System.out.printf(
        "LineItem %s now has entity status %s%n", response.getName(), response.getEntityStatus());
  }
}
