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
import com.google.api.services.displayvideo.v4.DisplayVideo.Advertisers.Campaigns;
import com.google.api.services.displayvideo.v4.model.Campaign;
import com.google.api.services.displayvideo.v4.model.CampaignFlight;
import com.google.api.services.displayvideo.v4.model.CampaignGoal;
import com.google.api.services.displayvideo.v4.model.Date;
import com.google.api.services.displayvideo.v4.model.DateRange;
import com.google.api.services.displayvideo.v4.model.FrequencyCap;
import com.google.api.services.displayvideo.v4.model.PerformanceGoal;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import java.time.LocalDateTime;

/** This example creates an campaign under the given Display &amp; Video 360 advertiser. */
public class CreateCampaign {

  private static class CreateCampaignParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the parent advertiser of the campaign to be created.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.DISPLAY_NAME,
        description = "The display name of the campaign to be created.",
        required = true)
    public String displayName;
  }

  public static void main(String[] args) throws Exception {
    CreateCampaignParams params = new CreateCampaignParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.displayName = "INSERT_DISPLAY_NAME_HERE";
    }

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile,
            params.useServiceAccount,
            params.serviceAccountKeyFile,
            params.additionalScopes);

    runExample(service, params.advertiserId, params.displayName);
  }

  public static void runExample(DisplayVideo service, long advertiserId, String displayName)
      throws Exception {

    // Create the campaign structure.
    Campaign campaign =
        new Campaign().setDisplayName(displayName).setEntityStatus("ENTITY_STATUS_ACTIVE");

    // Create the campaign goal structure.
    CampaignGoal campaignGoal =
        new CampaignGoal().setCampaignGoalType("CAMPAIGN_GOAL_TYPE_BRAND_AWARENESS");

    // Create and add the performance goal to the campaign goal structure.
    PerformanceGoal performanceGoal =
        new PerformanceGoal()
            .setPerformanceGoalType("PERFORMANCE_GOAL_TYPE_CPC")
            .setPerformanceGoalAmountMicros(1_000_000L);
    campaignGoal.setPerformanceGoal(performanceGoal);

    // Set the campaign goal.
    campaign.setCampaignGoal(campaignGoal);

    // Create the campaign flight structure.
    // This object details the planned spend and duration of the campaign.
    CampaignFlight campaignFlight = new CampaignFlight().setPlannedSpendAmountMicros(1_000_000L);

    // Create future start and end dates and assign to date range object.
    LocalDateTime startDate = LocalDateTime.now().plusDays(7);
    LocalDateTime endDate = LocalDateTime.now().plusDays(14);
    DateRange dateRange =
        new DateRange()
            .setStartDate(
                new Date()
                    .setYear(startDate.getYear())
                    .setMonth(startDate.getMonthValue())
                    .setDay(startDate.getDayOfMonth()))
            .setEndDate(
                new Date()
                    .setYear(endDate.getYear())
                    .setMonth(endDate.getMonthValue())
                    .setDay(endDate.getDayOfMonth()));

    // Add the planned date range to the campaign flight.
    campaignFlight.setPlannedDates(dateRange);

    // Set the campaign flight.
    campaign.setCampaignFlight(campaignFlight);

    // Create and set the frequency cap.
    FrequencyCap frequencyCap =
        new FrequencyCap().setMaxImpressions(10).setTimeUnit("TIME_UNIT_DAYS").setTimeUnitCount(1);
    campaign.setFrequencyCap(frequencyCap);

    // Configure the create request.
    Campaigns.Create request = service.advertisers().campaigns().create(advertiserId, campaign);

    // Create the campaign.
    Campaign response = request.execute();

    // Display the new campaign ID.
    System.out.printf("Campaign %s was created.", response.getName());
  }
}
