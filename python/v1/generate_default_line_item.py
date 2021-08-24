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

"""This example generates a default line item under the given insertion order.

The line item will inherit settings, including targeting, from the insertion order. If generating a
Mobile App Install line item, an app ID must be provided.
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
    'advertiser_id', help='The ID of the parent advertiser of the line item to be created.')
argparser.add_argument(
    'insertion_order_id', help='The ID of the insertion order of the line item to be created.')
argparser.add_argument(
    'display_name', help='The display name of the line item to be created.')
argparser.add_argument(
    'line_item_type', help='The type of the line item to be created.')

argparser.add_argument(
    '--app_id',
    help='The app ID of the mobile app promoted by the line item. Required and only valid if line '
         'item type is either LINE_ITEM_TYPE_DISPLAY_MOBILE_APP_INSTALL or '
         'LINE_ITEM_TYPE_VIDEO_MOBILE_APP_INSTALL.')


def main(service, flags):
  # Create and populate the generateDefault request body.
  generate_default_line_item_request = {
      'insertionOrderId': flags.insertion_order_id,
      'displayName': flags.display_name,
      'lineItemType': flags.line_item_type
  }

  # Add Mobile App object to request generating a Mobile App Install
  # line item.
  if flags.line_item_type in [
      'LINE_ITEM_TYPE_DISPLAY_MOBILE_APP_INSTALL',
      'LINE_ITEM_TYPE_VIDEO_MOBILE_APP_INSTALL'
  ]:
    if not flags.app_id:
      print('Error: No app ID given for Mobile App Install line item. Exiting.')
      sys.exit(1)

    generate_default_line_item_request['mobileApp'] = {'appId': flags.app_id}

  try:
    # Build and execute request.
    response = service.advertisers().lineItems().generateDefault(
        advertiserId=flags.advertiser_id, body=generate_default_line_item_request).execute()
  except HttpError as e:
    print(e)
    sys.exit(1)

  # Display the new line item resource name.
  print(f'Line Item {response["name"]} was created.')


if __name__ == '__main__':
  # Retrieve command line arguments.
  flags = samples_util.get_arguments(sys.argv, __doc__, parents=[argparser])

  # Authenticate and construct service.
  service = samples_util.get_service(version='v1')

  main(service, flags)
