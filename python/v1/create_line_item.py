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

"""This example creates a line item."""

import argparse
import os
import sys
from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath('..'))
import samples_util


# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'advertiser_id', help='The ID of the parent advertiser of the line item to '
                          'be created.')
argparser.add_argument(
    'insertion_order_id', help='The ID of the insertion order of the line item '
                               'to be created.')
argparser.add_argument(
    'display_name', help='The display name of the line item to be created.')


def main(service, flags):
  # Create a line item object with example values.
  line_item_obj = {
      'insertionOrderId': flags.insertion_order_id,
      'displayName': flags.display_name,
      'lineItemType': 'LINE_ITEM_TYPE_DISPLAY_DEFAULT',
      'entityStatus': 'ENTITY_STATUS_DRAFT',
      'flight': {
          'flightDateType': 'LINE_ITEM_FLIGHT_DATE_TYPE_INHERITED'
      },
      'budget': {
          'budgetAllocationType': 'LINE_ITEM_BUDGET_ALLOCATION_TYPE_FIXED'
      },
      'pacing': {
          'pacingPeriod': 'PACING_PERIOD_DAILY',
          'pacingType': 'PACING_TYPE_EVEN',
          'dailyMaxMicros': 10000
      },
      'frequencyCap': {
          'timeUnit': 'TIME_UNIT_DAYS',
          'timeUnitCount': 1,
          'maxImpressions': 10
      },
      'partnerRevenueModel': {
          'markupType': 'PARTNER_REVENUE_MODEL_MARKUP_TYPE_CPM',
          'markupAmount': 10000
      },
      'bidStrategy': {
          'fixedBid': {
              'bidAmountMicros': 100000
          }
      }
  }

  try:
    # Build and execute request.
    response = service.advertisers().lineItems().create(
        advertiserId=flags.advertiser_id, body=line_item_obj).execute()
  except HttpError as e:
    print(e)
    sys.exit(1)

  # Display the new line item.
  print(f'Line Item {response["name"]} was created.')


if __name__ == '__main__':
  # Retrieve command line arguments.
  flags = samples_util.get_arguments(sys.argv, __doc__, parents=[argparser])

  # Authenticate and construct service.
  service = samples_util.get_service(version='v1')

  main(service, flags)
