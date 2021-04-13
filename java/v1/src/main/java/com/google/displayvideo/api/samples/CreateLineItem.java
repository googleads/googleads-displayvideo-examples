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
import com.google.api.services.displayvideo.v1.model.BiddingStrategy;
import com.google.api.services.displayvideo.v1.model.FixedBidStrategy;
import com.google.api.services.displayvideo.v1.model.FrequencyCap;
import com.google.api.services.displayvideo.v1.model.LineItem;
import com.google.api.services.displayvideo.v1.model.LineItemBudget;
import com.google.api.services.displayvideo.v1.model.LineItemFlight;
import com.google.api.services.displayvideo.v1.model.Pacing;
import com.google.api.services.displayvideo.v1.model.PartnerRevenueModel;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;

/** This example creates a line item under the given Display &amp; Video 360 insertion order. */
public class CreateLineItem {

  private static class CreateLineItemParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the parent advertiser of the line item to be created.")
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.INSERTION_ORDER_ID,
        description = "The ID of the insertion order of the line item to be created.")
    public Long insertionOrderId;

    @Parameter(
        names = ArgumentNames.DISPLAY_NAME,
        description = "The display name of the line item to be created.")
    public String displayName;
  }

  public static void main(String[] args) throws Exception {
    CreateLineItemParams params = new CreateLineItemParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.insertionOrderId = Long.valueOf("INSERT_INSERTION_ORDER_ID_HERE");
      params.displayName = "INSERT_DISPLAY_NAME_HERE";
    }

    DisplayVideo service = DisplayVideoFactory.getInstance(params.clientSecretsFile);

    runExample(service, params.advertiserId, params.insertionOrderId, params.displayName);
  }

  public static void runExample(
      DisplayVideo service, long advertiserId, long insertionOrderId, String displayName)
      throws Exception {

    // Create the line item structure.
    LineItem lineItem =
        new LineItem()
            .setInsertionOrderId(insertionOrderId)
            .setDisplayName(displayName)
            .setLineItemType("LINE_ITEM_TYPE_DISPLAY_DEFAULT")
            .setEntityStatus("ENTITY_STATUS_DRAFT");

    // Create and set the line item flight.
    LineItemFlight lineItemFlight =
        new LineItemFlight().setFlightDateType("LINE_ITEM_FLIGHT_DATE_TYPE_INHERITED");
    lineItem.setFlight(lineItemFlight);

    // Create and set the line item budget.
    LineItemBudget lineItemBudget =
        new LineItemBudget().setBudgetAllocationType("LINE_ITEM_BUDGET_ALLOCATION_TYPE_FIXED");
    lineItem.setBudget(lineItemBudget);

    // Create and set the pacing setting.
    Pacing pacing =
        new Pacing()
            .setPacingPeriod("PACING_PERIOD_DAILY")
            .setPacingType("PACING_TYPE_EVEN")
            .setDailyMaxMicros(10_000L);
    lineItem.setPacing(pacing);

    // Create and set the frequency cap.
    FrequencyCap frequencyCap =
        new FrequencyCap().setTimeUnit("TIME_UNIT_DAYS").setTimeUnitCount(1).setMaxImpressions(10);
    lineItem.setFrequencyCap(frequencyCap);

    // Create and set the partner revenue model.
    PartnerRevenueModel partnerRevenueModel =
        new PartnerRevenueModel()
            .setMarkupType("PARTNER_REVENUE_MODEL_MARKUP_TYPE_CPM")
            .setMarkupAmount(10_000L);
    lineItem.setPartnerRevenueModel(partnerRevenueModel);

    // Create and set the bidding strategy.
    BiddingStrategy biddingStrategy =
        new BiddingStrategy().setFixedBid(new FixedBidStrategy().setBidAmountMicros(100_000L));
    lineItem.setBidStrategy(biddingStrategy);

    // Configure the create request.
    LineItems.Create request = service.advertisers().lineItems().create(advertiserId, lineItem);

    // Create the line item.
    LineItem response = request.execute();

    // Display the new line item ID.
    System.out.printf("LineItem %s was created.", response.getName());
  }
}
