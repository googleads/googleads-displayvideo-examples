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
import com.google.api.services.displayvideo.v4.model.GenerateDefaultLineItemRequest;
import com.google.api.services.displayvideo.v4.model.LineItem;
import com.google.api.services.displayvideo.v4.model.MobileApp;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;

/**
 * This example generates a default line item under the given Display &amp; Video 360 insertion
 * order. The line item will inherit settings, including targeting, from the insertion order. If
 * generating a Mobile App Install line item, an app ID must be provided.
 */
public class GenerateDefaultLineItem {

  private static class GenerateDefaultLineItemParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the parent advertiser of the line item to be created.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.INSERTION_ORDER_ID,
        description = "The ID of the insertion order to generate the default line item from.",
        required = true)
    public Long insertionOrderId;

    @Parameter(
        names = ArgumentNames.DISPLAY_NAME,
        description = "The display name of the line item to be created.",
        required = true)
    public String displayName;

    @Parameter(
        names = ArgumentNames.LINE_ITEM_TYPE,
        description = "The type of the line item to be created.",
        required = true)
    public String lineItemType;

    @Parameter(
        names = ArgumentNames.APP_ID,
        description =
            "The app ID of the mobile app promoted by the line item. Required and only valid if "
                + "line item type is either LINE_ITEM_TYPE_DISPLAY_MOBILE_APP_INSTALL or "
                + "LINE_ITEM_TYPE_VIDEO_MOBILE_APP_INSTALL.")
    public String appId;
  }

  public static void main(String[] args) throws Exception {
    GenerateDefaultLineItemParams params = new GenerateDefaultLineItemParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.insertionOrderId = Long.valueOf("INSERT_INSERTION_ORDER_ID_HERE");
      params.displayName = "INSERT_DISPLAY_NAME_HERE";
      params.lineItemType = "INSERT_LINE_ITEM_TYPE_HERE";
      params.appId = "INSERT_APP_ID_HERE";
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
        params.insertionOrderId,
        params.displayName,
        params.lineItemType,
        params.appId);
  }

  public static void runExample(
      DisplayVideo service,
      long advertiserId,
      long insertionOrderId,
      String displayName,
      String lineItemType,
      String appId)
      throws Exception {

    // Create the generateDefault request structure.
    GenerateDefaultLineItemRequest generateDefaultLineItemRequest =
        new GenerateDefaultLineItemRequest()
            .setInsertionOrderId(insertionOrderId)
            .setDisplayName(displayName)
            .setLineItemType(lineItemType);

    // Add Mobile App object to request for a Mobile App Install line item.
    if (lineItemType.equals("LINE_ITEM_TYPE_DISPLAY_MOBILE_APP_INSTALL")
        || lineItemType.equals("LINE_ITEM_TYPE_VIDEO_MOBILE_APP_INSTALL")) {
      if (appId == null) {
        System.out.print("Error: No app ID given for Mobile App Install line item.\n");
        return;
      }
      generateDefaultLineItemRequest.setMobileApp(new MobileApp().setAppId(appId));
    }

    // Build the request.
    LineItems.GenerateDefault request =
        service
            .advertisers()
            .lineItems()
            .generateDefault(advertiserId, generateDefaultLineItemRequest);

    // Execute the request to generate the line item.
    LineItem response = request.execute();

    // Display the new line item resource name.
    System.out.printf("LineItem %s was created.%n", response.getName());
  }
}
