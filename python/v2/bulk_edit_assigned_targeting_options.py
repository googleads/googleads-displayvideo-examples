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

"""This example bulk edits the targeting of a given line item.

It deletes the given browser and device type assigned targeting options and
assigns the given browser targeting options.
"""

import argparse
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath(".."))
import samples_util
from v2_util import print_bulk_edit_targeting_response


def main(service, flags):
    """Bulk edits the targeting of a line item.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    browser_create_targeting_options = flags.browser_create_targeting_ops

    # Build assigned targeting option objects to create.
    if browser_create_targeting_options != None:
        create_browser_assigned_targeting_options = [
            {"browserDetails": {"targetingOptionId": targeting_id}}
            for targeting_id in browser_create_targeting_options
        ]
    else:
        create_browser_assigned_targeting_options = []

    # Create a bulk edit request.
    bulk_edit_line_item_request = {
        "lineItemIds": [flags.line_item_id],
        "deleteRequests": [
            {
                "targetingType": "TARGETING_TYPE_BROWSER",
                "assignedTargetingOptionIds": flags.browser_del_targeting_ops,
            },
            {
                "targetingType": "TARGETING_TYPE_DEVICE_MAKE_MODEL",
                "assignedTargetingOptionIds": (
                    flags.device_make_model_del_targeting_ops
                ),
            },
        ],
        "createRequests": [
            {
                "targetingType": "TARGETING_TYPE_BROWSER",
                "assignedTargetingOptions": (
                    create_browser_assigned_targeting_options
                ),
            }
        ],
    }

    # Edit the line item targeting.
    response = (
        service.advertisers()
        .lineItems()
        .bulkEditAssignedTargetingOptions(
            advertiserId=flags.advertiser_id,
            body=bulk_edit_line_item_request,
        )
        .execute()
    )

    # If response is not empty, display the updated line items.
    if response:
        print_bulk_edit_targeting_response(response)
    else:
        print("Error: No response was returned. Exiting.")
        sys.exit(1)


if __name__ == "__main__":
    # Declare command-line flags.
    argparser = argparse.ArgumentParser(add_help=False)
    argparser.add_argument(
        "advertiser_id",
        help="The ID of the parent advertiser of the line item to which the "
        "listed targeting options are assigned.",
    )
    argparser.add_argument(
        "line_item_id",
        help="The ID of the line item to which the listed targeting options "
        "are assigned.",
    )
    argparser.add_argument(
        "--browser_del_targeting_ops",
        nargs="+",
        help="The browser assigned targeting options to delete from the line "
        "item. Multiple values can be listed after declaring the argument. "
        'Ex: "--browser_del_targeting_ops 10001 10002 10003"',
    )
    argparser.add_argument(
        "--device_make_model_del_targeting_ops",
        nargs="+",
        help="The device type assigned targeting options to delete from the "
        "line item. Multiple values can be listed after declaring the "
        'argument. Ex: "--device_del_targeting_ops 10001 10002 10003"',
    )
    argparser.add_argument(
        "--browser_create_targeting_ops",
        nargs="+",
        help="The browser targeting options to assign to the line item. "
        "Multiple values can be listed after declaring the argument. Ex: "
        '"--browser_create_targeting_ops 10001 10002 10003"',
    )

    # Retrieve command line arguments.
    flags = samples_util.get_arguments(
        sys.argv,
        __doc__,
        parents=[samples_util.get_default_parser(), argparser],
    )

    # Authenticate and construct service.
    service = samples_util.get_service(
        version="v2", useServiceAccount=flags.use_service_account
    )

    try:
        main(service, flags)
    except HttpError as e:
        print(e)
        sys.exit(1)
