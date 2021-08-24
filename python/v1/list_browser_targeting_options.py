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

"""This example lists browser targeting options for the given advertiser."""

import argparse
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath('..'))
import samples_util


argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'advertiser_id', help='The ID of the advertiser this request is being made within.')


def main(service, flags):
  # Create the page token variable.
  next_page_token = ''

  while True:
    try:
      # Request the targeting options list.
      response = service.targetingTypes().targetingOptions().list(
          advertiserId=flags.advertiser_id,
          targetingType='TARGETING_TYPE_BROWSER',
          pageToken=next_page_token).execute()
    except HttpError as e:
      print(e)
      sys.exit(1)

    # Check if response is empty.
    if not response:
      print('List request returned no Targeting Options.')
      break

    # Iterate over retrieved targeting options.
    for option in response['targetingOptions']:
      print(f'Targeting Option ID: {option["targetingOptionId"]}, Browser Display Name: '
            f'{option["browserDetails"]["displayName"]}')

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
