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
import com.google.api.services.displayvideo.v1.DisplayVideo.Advertisers.InsertionOrders;
import com.google.api.services.displayvideo.v1.model.Date;
import com.google.api.services.displayvideo.v1.model.DateRange;
import com.google.api.services.displayvideo.v1.model.FrequencyCap;
import com.google.api.services.displayvideo.v1.model.InsertionOrder;
import com.google.api.services.displayvideo.v1.model.InsertionOrderBudget;
import com.google.api.services.displayvideo.v1.model.InsertionOrderBudgetSegment;
import com.google.api.services.displayvideo.v1.model.Pacing;
import com.google.api.services.displayvideo.v1.model.PerformanceGoal;
import com.google.common.collect.ImmutableList;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import java.time.LocalDateTime;

/** This example creates an insertion order under the given Display &amp; Video 360 campaign. */
public class CreateInsertionOrder {

  private static class CreateInsertionOrderParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the parent advertiser of the insertion order to be created.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.CAMPAIGN_ID,
        description = "The ID of the campaign of the insertion order to be created.",
        required = true)
    public Long campaignId;

    @Parameter(
        names = ArgumentNames.DISPLAY_NAME,
        description = "The display name of the insertion order to be created.",
        required = true)
    public String displayName;
  }

  public static void main(String[] args) throws Exception {
    CreateInsertionOrderParams params = new CreateInsertionOrderParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.campaignId = Long.valueOf("INSERT_CAMPAIGN_ID_HERE");
      params.displayName = "INSERT_DISPLAY_NAME_HERE";
    }

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile,
            params.useServiceAccount,
            params.serviceAccountKeyFile,
            params.additionalScopes);

    runExample(service, params.advertiserId, params.campaignId, params.displayName);
  }

  public static void runExample(
      DisplayVideo service, long advertiserId, long campaignId, String displayName)
      throws Exception {

    // Create the insertion order structure.
    InsertionOrder insertionOrder =
        new InsertionOrder()
            .setCampaignId(campaignId)
            .setDisplayName(displayName)
            .setEntityStatus("ENTITY_STATUS_DRAFT");

    // Create and add the pacing setting.
    Pacing pacing =
        new Pacing()
            .setPacingPeriod("PACING_PERIOD_DAILY")
            .setPacingType("PACING_TYPE_EVEN")
            .setDailyMaxMicros(10_000L);
    insertionOrder.setPacing(pacing);

    // Create and set the frequency cap.
    FrequencyCap frequencyCap =
        new FrequencyCap().setMaxImpressions(10).setTimeUnit("TIME_UNIT_DAYS").setTimeUnitCount(1);
    insertionOrder.setFrequencyCap(frequencyCap);

    // Create and set the performance goal.
    PerformanceGoal performanceGoal =
        new PerformanceGoal()
            .setPerformanceGoalType("PERFORMANCE_GOAL_TYPE_CPC")
            .setPerformanceGoalAmountMicros(1_000_000L);
    insertionOrder.setPerformanceGoal(performanceGoal);

    // Create the budget structure.
    InsertionOrderBudget insertionOrderBudget =
        new InsertionOrderBudget().setBudgetUnit("BUDGET_UNIT_CURRENCY");

    // Create a budget segment structure.
    InsertionOrderBudgetSegment insertionOrderBudgetSegment =
        new InsertionOrderBudgetSegment().setBudgetAmountMicros(100_000L);

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

    // Add the date range to the budget segment.
    insertionOrderBudgetSegment.setDateRange(dateRange);

    // Add budget segment list to the budget.
    insertionOrderBudget.setBudgetSegments(ImmutableList.of(insertionOrderBudgetSegment));

    // Set budget.
    insertionOrder.setBudget(insertionOrderBudget);

    // Configure the create request.
    InsertionOrders.Create request =
        service.advertisers().insertionOrders().create(advertiserId, insertionOrder);

    // Create the insertion order.
    InsertionOrder response = request.execute();

    // Display the new insertion order ID.
    System.out.printf("InsertionOrder %s was created.", response.getName());
  }
}
