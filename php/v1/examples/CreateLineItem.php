<?php
/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Require the base class.
require_once __DIR__ . "/../BaseExample.php";

/**
 * This example creates a line item under the given Display & Video 360
 * insertion order.
 */
class CreateLineItem extends BaseExample
{
    /**
     * (non-PHPdoc)
     * @see BaseExample::getInputParameters()
     */
    protected function getInputParameters(): array
    {
        return array(
            array(
                'name' => 'advertiser_id',
                'display' => 'Advertiser ID',
                'required' => true
            ),
            array(
                'name' => 'insertion_order_id',
                'display' => 'Insertion Order ID',
                'required' => true
            ),
            array(
                'name' => 'display_name',
                'display' => 'Display Name',
                'required' => true
            )
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::run()
     */
    public function run()
    {
        $values = $this->formValues;

        $advertiserId = $values['advertiser_id'];
        $insertionOrderId = $values['insertion_order_id'];
        $displayName = $values['display_name'];

        $lineItem = new Google_Service_DisplayVideo_LineItem();
        $lineItem->setInsertionOrderId($insertionOrderId);
        $lineItem->setDisplayName($displayName);
        $lineItem->setLineItemType('LINE_ITEM_TYPE_DISPLAY_DEFAULT');
        $lineItem->setEntityStatus('ENTITY_STATUS_DRAFT');

        $flight = new Google_Service_DisplayVideo_LineItemFlight();
        $flight->setFlightDateType('LINE_ITEM_FLIGHT_DATE_TYPE_INHERITED');
        $lineItem->setFlight($flight);

        $budget = new Google_Service_DisplayVideo_LineItemBudget();
        $budget->setBudgetAllocationType(
            'LINE_ITEM_BUDGET_ALLOCATION_TYPE_FIXED'
        );
        $lineItem->setBudget($budget);

        $pacing = new Google_Service_DisplayVideo_Pacing();
        $pacing->setPacingPeriod('PACING_PERIOD_DAILY');
        $pacing->setPacingType('PACING_TYPE_EVEN');
        $pacing->setDailyMaxMicros(10000);
        $lineItem->setPacing($pacing);

        $frequencyCap = new Google_Service_DisplayVideo_FrequencyCap();
        $frequencyCap->setMaxImpressions(10);
        $frequencyCap->setTimeUnit('TIME_UNIT_DAYS');
        $frequencyCap->setTimeUnitCount(1);
        $lineItem->setFrequencyCap($frequencyCap);

        $partnerRevenueModel =
            new Google_Service_DisplayVideo_PartnerRevenueModel();
        $partnerRevenueModel->setMarkupType(
            'PARTNER_REVENUE_MODEL_MARKUP_TYPE_CPM'
        );
        $partnerRevenueModel->setMarkupAmount(10000);
        $lineItem->setPartnerRevenueModel($partnerRevenueModel);

        $biddingStrategy =  new Google_Service_DisplayVideo_BiddingStrategy();
        $fixedBidStrategy = new Google_Service_DisplayVideo_FixedBidStrategy();
        $fixedBidStrategy->setBidAmountMicros(100000);
        $biddingStrategy->setFixedBid($fixedBidStrategy);
        $lineItem->setBidStrategy($biddingStrategy);

        // Call the API, creating the line item under the advertiser and
        // insertion order given.
        try {
            $result = $this->service->advertisers_lineItems->create(
                $advertiserId,
                $lineItem
            );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        printf('<p>Line Item %s was created.</p>', $result['name']);
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public function getName(): string
    {
        return 'Create Line Item';
    }
}
