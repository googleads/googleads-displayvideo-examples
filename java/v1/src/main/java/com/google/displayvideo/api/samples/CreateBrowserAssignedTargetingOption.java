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
import com.google.api.services.displayvideo.v1.DisplayVideo.Advertisers.LineItems.TargetingTypes.AssignedTargetingOptions;
import com.google.api.services.displayvideo.v1.model.AssignedTargetingOption;
import com.google.api.services.displayvideo.v1.model.BrowserAssignedTargetingOptionDetails;
import com.google.displayvideo.api.samples.utils.ApiConstants;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;

/** This example assigns a browser targeting option to a given line item. */
public class CreateBrowserAssignedTargetingOption {

  private static class CreateBrowserAssignedTargetingOptionParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description =
            "The ID of the parent advertiser of the line item to which "
                + "this targeting option will be assigned.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.LINE_ITEM_ID,
        description = "The ID of the line item to which this targeting option will be assigned.",
        required = true)
    public Long lineItemId;

    @Parameter(
        names = ArgumentNames.BROWSER_TARGETING_OPTION_ID,
        description = "The targeting option id representing the browser to be targeted",
        required = true)
    public String browserTargetingOptionId;
  }

  public static void main(String[] args) throws Exception {
    CreateBrowserAssignedTargetingOptionParams params =
        new CreateBrowserAssignedTargetingOptionParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.lineItemId = Long.valueOf("INSERT_LINE_ITEM_ID_HERE");
      params.browserTargetingOptionId = "INSERT_BROWSER_TARGETING_OPTION_ID_HERE";
    }

    DisplayVideo service = DisplayVideoFactory.getInstance(params.clientSecretsFile);

    runExample(service, params.advertiserId, params.lineItemId, params.browserTargetingOptionId);
  }

  public static void runExample(
      DisplayVideo service, long advertiserId, long lineItemId, String browserTargetingOptionId)
      throws Exception {

    // Create an AssignedTargetingOption object of the browser targeting type.
    AssignedTargetingOption assignedTargetingOption =
        new AssignedTargetingOption()
            .setBrowserDetails(
                new BrowserAssignedTargetingOptionDetails()
                    .setTargetingOptionId(browserTargetingOptionId));

    // Configure the create request.
    AssignedTargetingOptions.Create request =
        service
            .advertisers()
            .lineItems()
            .targetingTypes()
            .assignedTargetingOptions()
            .create(
                advertiserId,
                lineItemId,
                ApiConstants.BROWSER_TARGETING_TYPE,
                assignedTargetingOption);

    // Send the request.
    AssignedTargetingOption response = request.execute();
    System.out.printf("AssignedTargetingOption %s was created.", response.getName());
  }
}
