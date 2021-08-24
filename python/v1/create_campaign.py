#!/usr/bin/python
#
# Copyright 2021 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""This example creates a campaign."""

import argparse
from datetime import date
from datetime import timedelta
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath('..'))
import samples_util


# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'advertiser_id', help='The ID of the parent advertiser of the campaign to be created.')
argparser.add_argument('display_name', help='The display name of the campaign to be created.')


def main(service, flags):
  # Create a future campaign flight start date a week from now.
  startDate = date.today() + timedelta(days=7)

  # Create a future campaign flight end date two weeks from now.
  endDate = date.today() + timedelta(days=14)

  # Create a campaign object with example values.
  campaign_obj = {
      'displayName': flags.display_name,
      'entityStatus': 'ENTITY_STATUS_ACTIVE',
      'campaignGoal': {
          'campaignGoalType': 'CAMPAIGN_GOAL_TYPE_BRAND_AWARENESS',
          'performanceGoal': {
              'performanceGoalType': 'PERFORMANCE_GOAL_TYPE_CPC',
              'performanceGoalAmountMicros': 1000000
          }
      },
      'campaignFlight': {
          'plannedSpendAmountMicros': 1000000,
          'plannedDates': {
              'startDate': {
                  'year': startDate.year,
                  'month': startDate.month,
                  'day': startDate.day
              },
              'endDate': {
                  'year': endDate.year,
                  'month': endDate.month,
                  'day': endDate.day
              }
          }
      },
      'frequencyCap': {
          'maxImpressions': 10,
          'timeUnit': 'TIME_UNIT_DAYS',
          'timeUnitCount': 1
      }
  }

  try:
    # Build and execute request.
    response = service.advertisers().campaigns().create(
        advertiserId=flags.advertiser_id, body=campaign_obj).execute()
  except HttpError as e:
    print(e)
    sys.exit(1)

  # Display the new campaign.
  print(f'Campaign {response["name"]} was created.')


if __name__ == '__main__':
  # Retrieve command line arguments.
  flags = samples_util.get_arguments(sys.argv, __doc__, parents=[argparser])

  # Authenticate and construct service.
  service = samples_util.get_service(version='v1')

  main(service, flags)
