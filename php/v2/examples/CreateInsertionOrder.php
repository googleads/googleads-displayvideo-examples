<?php
/*
 * Copyright 2023 Google LLC
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

// Require the necessary utility class.
require_once __DIR__ . "/Utils/DateUtils.php";

/**
 * This example creates an insertion order under the given Display & Video 360
 * campaign.
 */
class CreateInsertionOrder extends BaseExample
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
                'name' => 'campaign_id',
                'display' => 'Campaign ID',
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
        $campaignId = $values['campaign_id'];
        $displayName = $values['display_name'];

        $insertionOrder = new Google_Service_DisplayVideo_InsertionOrder();
        $insertionOrder->setCampaignId($campaignId);
        $insertionOrder->setDisplayName($displayName);
        $insertionOrder->setEntityStatus('ENTITY_STATUS_DRAFT');

        $pacing = new Google_Service_DisplayVideo_Pacing();
        $pacing->setPacingPeriod('PACING_PERIOD_DAILY');
        $pacing->setPacingType('PACING_TYPE_EVEN');
        $pacing->setDailyMaxMicros(10000);
        $insertionOrder->setPacing($pacing);

        $frequencyCap = new Google_Service_DisplayVideo_FrequencyCap();
        $frequencyCap->setMaxImpressions(10);
        $frequencyCap->setTimeUnit('TIME_UNIT_DAYS');
        $frequencyCap->setTimeUnitCount(1);
        $insertionOrder->setFrequencyCap($frequencyCap);

        $performanceGoal = new Google_Service_DisplayVideo_PerformanceGoal();
        $performanceGoal->setPerformanceGoalType('PERFORMANCE_GOAL_TYPE_CPC');
        $performanceGoal->setPerformanceGoalAmountMicros(1000000);
        $insertionOrder->setPerformanceGoal($performanceGoal);

        $budget = new Google_Service_DisplayVideo_InsertionOrderBudget();
        $budget->setBudgetUnit('BUDGET_UNIT_CURRENCY');

        $budgetSegment =
            new Google_Service_DisplayVideo_InsertionOrderBudgetSegment();
        $budgetSegment->setBudgetAmountMicros(100000);
        $budgetSegment->setDateRange(DateUtils::createFutureDateRange());
        $budget->setBudgetSegments(array($budgetSegment));

        $insertionOrder->setBudget($budget);

        // Call the API, creating the insertion order under the advertiser and
        // campaign given.
        try {
            $result = $this->service->advertisers_insertionOrders->create(
                $advertiserId,
                $insertionOrder
            );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        printf('<p>Insertion Order %s was created.</p>', $result['name']);
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Create Insertion Order';
    }
}