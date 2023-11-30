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
import com.google.api.services.displayvideo.v2.DisplayVideo;
import com.google.api.services.displayvideo.v2.DisplayVideo.TargetingTypes.TargetingOptions;
import com.google.api.services.displayvideo.v2.model.ListTargetingOptionsResponse;
import com.google.api.services.displayvideo.v2.model.TargetingOption;
import com.google.displayvideo.api.samples.utils.ApiConstants;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;

/** This example lists all of the targeting options available for a given targeting type. */
public class ListBrowserTargetingOptions {

  private static class ListBrowserTargetingOptionsParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the advertiser to list the targeting options for.",
        required = true)
    public Long advertiserId;
  }

  public static void main(String[] args) throws Exception {
    ListBrowserTargetingOptionsParams params = new ListBrowserTargetingOptionsParams();
    if (!params.parseArguments(args)) {
      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
    }

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile,
            params.useServiceAccount,
            params.serviceAccountKeyFile,
            params.additionalScopes);

    runExample(service, params.advertiserId);
  }

  public static void runExample(DisplayVideo service, long advertiserId) throws Exception {

    // Configure the list request.
    TargetingOptions.List request =
        service
            .targetingTypes()
            .targetingOptions()
            .list(ApiConstants.BROWSER_TARGETING_TYPE)
            .setAdvertiserId(advertiserId);

    // Create the response and nextPageToken variables.
    ListTargetingOptionsResponse response;
    String nextPageToken = null;

    do {
      // Create and execute the list request.
      response = request.setPageToken(nextPageToken).execute();

      // Check if response is empty.
      if (response.isEmpty()) {
        System.out.print("List request returned no Targeting Options");
        break;
      }

      // Iterate over retrieved targeting options.
      for (TargetingOption option : response.getTargetingOptions()) {
        System.out.printf(
            "Targeting Option ID: %s, Browser Display Name: '%s'%n",
            option.getTargetingOptionId(), option.getBrowserDetails().getDisplayName());
      }

      // Update the next page token.
      nextPageToken = response.getNextPageToken();
    } while (!Strings.isNullOrEmpty(nextPageToken));
  }
}
