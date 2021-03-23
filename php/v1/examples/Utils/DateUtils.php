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

/**
 * Helper class to create date and default future date range objects.
 */
class DateUtils
{
    /**
     * Creates a date range object starting a week from the current date and
     * ending a week later, two weeks from the current date.
     * @return Google_Service_DisplayVideo_DateRange the created date range
     *     object.
     */
    public static function createFutureDateRange(
    ): Google_Service_DisplayVideo_DateRange {
        $dateRange = new Google_Service_DisplayVideo_DateRange();

        $startDate = new DateTime('today + 7 days');
        $endDate = new DateTime('today + 14 days');

        $dateRange->setStartDate(
            DateUtils::createDate(
                $startDate->format('Y'),
                $startDate->format('n'),
                $startDate->format('j')
            )
        );
        $dateRange->setEndDate(
             DateUtils::createDate(
                  $endDate->format('Y'),
                  $endDate->format('n'),
                  $endDate->format('j')
             )
        );

        return $dateRange;
    }

    /**
     * Creates a date object.
     * @param int $year four-digit year of the date to create.
     * @param int $month month of the date to create.
     * @param int $day day of the month of the date to create.
     * @return Google_Service_DisplayVideo_Date the created date object.
     */
    private static function createDate(
        int $year,
        int $month,
        int $day
    ): Google_Service_DisplayVideo_Date {
        $date = new Google_Service_DisplayVideo_Date();
        $date->setYear($year);
        $date->setMonth($month);
        $date->setDay($day);

        return $date;
    }
}