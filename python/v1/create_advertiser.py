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

"""This example creates an advertiser."""

import argparse
import os
import sys
from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath('..'))
import samples_util


# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument(
    'partner_id', help='The ID of the parent partner of the advertiser to be '
                       'created.')
argparser.add_argument(
    'display_name', help='The display name of the advertiser to be created.')
argparser.add_argument(
    'domain_url', help='The domain url of the advertiser to be created.')


def main(service, flags):
  # Create an advertiser object.
  advertiser_obj = {
      'partnerId': flags.partner_id,
      'displayName': flags.display_name,
      'entityStatus': 'ENTITY_STATUS_ACTIVE',
      'generalConfig': {
          'domainUrl': flags.domain_url,
          'currencyCode': 'USD'
      },
      'adServerConfig': {
          'thirdPartyOnlyConfig': {
              'pixelOrderIdReportingEnabled': False
          }
      }
  }

  try:
    # Build and execute request.
    response = service.advertisers().create(body=advertiser_obj).execute()
  except HttpError as e:
    print(e)
    sys.exit(1)

  # Display the new advertiser.
  print(f'Advertiser {response["name"]} was created.')


if __name__ == '__main__':
  # Retrieve command line arguments.
  flags = samples_util.get_arguments(sys.argv, __doc__, parents=[argparser])

  # Authenticate and construct service.
  service = samples_util.get_service(version='v1')

  main(service, flags)
