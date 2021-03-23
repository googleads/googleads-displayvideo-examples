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

"""This example lists the targeting options assigned to the given line item.

The response is optionally filtered by a given filter expression. The list is
filtered to only include targeting directly assigned to the line item by
default.
"""

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
                          'which the listed targeting options are assigned.')
argparser.add_argument(
    'line_item_id', help='The ID of the line item to which the listed '
                         'targeting options are assigned.')
argparser.add_argument(
    '--filter', default='inheritance="NOT_INHERITED"',
    help='The filter expression by which to filter the list results. Leave '
         'empty to not use a filter.')


def main(service, flags):
  # Create the page token variable.
  next_page_token = ''

  while True:
    try:
      # Execute the list request.
      response = service.advertisers().lineItems(
          ).bulkListLineItemAssignedTargetingOptions(
              advertiserId=flags.advertiser_id, lineItemId=flags.line_item_id,
              filter=flags.filter, pageToken=next_page_token).execute()
    except HttpError as e:
      print(e)
      sys.exit(1)

    # Check if response is empty.
    if not response:
      print('Bulk list request returned no Assigned Targeting Options.')
      break

    # Iterate over retrieved assigned targeting options.
    for assigned_option in response['assignedTargetingOptions']:
      print(f'Assigned Targeting Option {assigned_option["name"]} found.')

    # Break out of loop if there is no next page.
    if 'nextPageToken' not in response:
      break

    # Update the next page token.
    next_page_token = response['nextPageToken']


if __name__ == '__main__':
  # Retrieve command line arguments.
  flags = samples_util.get_arguments(sys.argv, __doc__, parents=[argparser])

  # Authenticate and construct service.
  service = samples_util.get_service(version='v1')

  main(service, flags)
