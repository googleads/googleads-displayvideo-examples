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
import com.google.api.services.displayvideo.v3.DisplayVideo;
import com.google.api.services.displayvideo.v3.DisplayVideo.Advertisers;
import com.google.api.services.displayvideo.v3.model.Advertiser;
import com.google.api.services.displayvideo.v3.model.AdvertiserAdServerConfig;
import com.google.api.services.displayvideo.v3.model.AdvertiserBillingConfig;
import com.google.api.services.displayvideo.v3.model.AdvertiserGeneralConfig;
import com.google.api.services.displayvideo.v3.model.ThirdPartyOnlyConfig;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;

/** This example creates an advertiser under the given Display &amp; Video 360 partner. */
public class CreateAdvertiser {

  private static class CreateAdvertiserParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.PARTNER_ID,
        description = "The ID of the parent partner of the advertiser to be created.",
        required = true)
    public Long partnerId;

    @Parameter(
        names = ArgumentNames.DISPLAY_NAME,
        description = "The display name of the advertiser to be created.",
        required = true)
    public String displayName;

    @Parameter(
        names = ArgumentNames.DOMAIN_URL,
        description = "The domain URL of the advertiser to be created.",
        required = true)
    public String domainUrl;

    @Parameter(
        names = ArgumentNames.BILLING_PROFILE_ID,
        description = "The billing profile ID to be used by the advertiser to be created.",
        required = true)
    public Long billingProfileId;
  }

  public static void main(String[] args) throws Exception {
    CreateAdvertiserParams params = new CreateAdvertiserParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.partnerId = Long.valueOf("INSERT_PARTNER_ID_HERE");
      params.displayName = "INSERT_DISPLAY_NAME_HERE";
      params.domainUrl = "INSERT_DOMAIN_URL_HERE";
      params.billingProfileId = Long.valueOf("INSERT_BILLING_PROFILE_ID_HERE");
    }

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile,
            params.useServiceAccount,
            params.serviceAccountKeyFile,
            params.additionalScopes);

    runExample(
        service, params.partnerId, params.displayName, params.domainUrl, params.billingProfileId);
  }

  public static void runExample(
      DisplayVideo service,
      long partnerId,
      String displayName,
      String domainUrl,
      long billingProfileId)
      throws Exception {

    // Create the advertiser structure.
    Advertiser advertiser =
        new Advertiser()
            .setPartnerId(partnerId)
            .setDisplayName(displayName)
            .setEntityStatus("ENTITY_STATUS_ACTIVE");

    // Create and set the advertiser general configuration.
    AdvertiserGeneralConfig advertiserGeneralConfig =
        new AdvertiserGeneralConfig().setDomainUrl(domainUrl).setCurrencyCode("USD");
    advertiser.setGeneralConfig(advertiserGeneralConfig);

    // Create the ad server configuration structure.
    AdvertiserAdServerConfig advertiserAdServerConfig = new AdvertiserAdServerConfig();

    // Create and add the third party only configuration to the ad server configuration.
    ThirdPartyOnlyConfig thirdPartyOnlyConfig = new ThirdPartyOnlyConfig();
    advertiserAdServerConfig.setThirdPartyOnlyConfig(thirdPartyOnlyConfig);

    // Set the ad server configuration.
    advertiser.setAdServerConfig(advertiserAdServerConfig);

    // Create and set the billing configuration.
    AdvertiserBillingConfig advertiserBillingConfig = new AdvertiserBillingConfig();
    advertiserBillingConfig.setBillingProfileId(billingProfileId);
    advertiser.setBillingConfig(advertiserBillingConfig);

    // Configure the create request.
    Advertisers.Create request = service.advertisers().create(advertiser);

    // Create the advertiser.
    Advertiser response = request.execute();

    // Display the new advertiser ID.
    System.out.printf("Advertiser %s was created.%n", response.getName());
  }
}
