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

"""This example activates the given line item."""

import argparse
import os
import sys
from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath('..'))
import samples_util


# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'advertiser_id',
    help='The ID of the parent advertiser of the line item to activate.')
argparser.add_argument(
    'line_item_id', help='The ID of the line item to activate.')


def main(service, flags):
  # Create a line item object with only updated entity status.
  line_item_obj = {
      'entityStatus': 'ENTITY_STATUS_ACTIVE',
  }

  try:
    # Build and execute request.
    response = service.advertisers().lineItems().patch(
        advertiserId=flags.advertiser_id, lineItemId=flags.line_item_id,
        updateMask='entityStatus', body=line_item_obj).execute()
  except HttpError as e:
    print(e)
    sys.exit(1)

  # Display the line item's new entity status
  print(f'Line Item {response["name"]} now has entity status '
        f'{response["entityStatus"]}.')


if __name__ == '__main__':
  # Retrieve command line arguments.
  flags = samples_util.get_arguments(sys.argv, __doc__, parents=[argparser])

  # Authenticate and construct service.
  service = samples_util.get_service(version='v1')

  main(service, flags)
