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

// Require the necessary utility class.
require_once __DIR__ . "/Utils/DateUtils.php";

/**
 * This example creates a campaign under the given Display & Video 360
 * advertiser.
 */
class CreateCampaign extends BaseExample
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
        $displayName = $values['display_name'];

        $campaign = new Google_Service_DisplayVideo_Campaign();
        $campaign->setDisplayName($displayName);
        $campaign->setEntityStatus('ENTITY_STATUS_ACTIVE');

        $campaignGoal = new Google_Service_DisplayVideo_CampaignGoal();
        $campaignGoal->setCampaignGoalType(
            'CAMPAIGN_GOAL_TYPE_BRAND_AWARENESS'
        );

        $performanceGoal = new Google_Service_DisplayVideo_PerformanceGoal();
        $performanceGoal->setPerformanceGoalType('PERFORMANCE_GOAL_TYPE_CPC');
        $performanceGoal->setPerformanceGoalAmountMicros(1000000);

        $campaignGoal->setPerformanceGoal($performanceGoal);
        $campaign->setCampaignGoal($campaignGoal);

        $campaignFlight = new Google_Service_DisplayVideo_CampaignFlight();
        $campaignFlight->setPlannedSpendAmountMicros(1000000);
        $campaignFlight->setPlannedDates(DateUtils::createFutureDateRange());
        $campaign->setCampaignFlight($campaignFlight);

        $frequencyCap = new Google_Service_DisplayVideo_FrequencyCap();
        $frequencyCap->setMaxImpressions(10);
        $frequencyCap->setTimeUnit('TIME_UNIT_DAYS');
        $frequencyCap->setTimeUnitCount(1);
        $campaign->setFrequencyCap($frequencyCap);

        // Call the API, creating the campaign under the given advertiser.
        try {
            $result = $this->service->advertisers_campaigns->create(
                $advertiserId,
                $campaign
            );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        printf('<p>Campaign %s was created.</p>', $result['name']);
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public function getName(): string
    {
        return 'Create Campaign';
    }
}