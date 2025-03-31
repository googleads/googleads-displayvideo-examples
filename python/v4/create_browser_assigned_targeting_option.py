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

"""This example assigns a targeting option to the given line item.

The targeting type of the assigned targeting option in this example is
TARGETING_TYPE_BROWSER.
"""

import argparse
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath(".."))
import samples_util


def main(service, flags):
    """Assigns a browser targeting option to a line item.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    # Create an assigned targeting option object.
    assigned_targeting_option_obj = {
        "browserDetails": {
            "targetingOptionId": flags.browser_targeting_option_id
        }
    }

    # Build and execute request.
    response = (
        service.advertisers()
        .lineItems()
        .targetingTypes()
        .assignedTargetingOptions()
        .create(
            advertiserId=flags.advertiser_id,
            lineItemId=flags.line_item_id,
            targetingType="TARGETING_TYPE_BROWSER",
            body=assigned_targeting_option_obj,
        )
        .execute()
    )

    # Display the new assigned targeting option.
    print(f"Assigned Targeting Option {response['name']} was created.")


if __name__ == "__main__":
    # Declare command-line flags.
    argparser = argparse.ArgumentParser(add_help=False)
    argparser.add_argument(
        "advertiser_id",
        help="The ID of the parent advertiser of the line item to which this "
        "targeting option will be assigned.",
    )
    argparser.add_argument(
        "line_item_id",
        help="The ID of the line item to which this targeting option will be "
        "assigned.",
    )
    argparser.add_argument(
        "browser_targeting_option_id",
        help="The targeting option id representing the browser to be targeted.",
    )

    # Retrieve command line arguments.
    flags = samples_util.get_arguments(
        sys.argv,
        __doc__,
        parents=[samples_util.get_default_parser(), argparser],
    )

    # Authenticate and construct service.
    service = samples_util.get_service(
        version="v4", useServiceAccount=flags.use_service_account
    )

    try:
        main(service, flags)
    except HttpError as e:
        print(e)
        sys.exit(1)
