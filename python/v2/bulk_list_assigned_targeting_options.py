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

sys.path.insert(0, os.path.abspath(".."))
import samples_util


def main(service, flags):
    """Lists the assigned targeting of multiple line items.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    # Create the page token variable.
    next_page_token = ""

    while True:
        # Execute the list request.
        response = (
            service.advertisers()
            .lineItems()
            .bulkListAssignedTargetingOptions(
                advertiserId=flags.advertiser_id,
                lineItemIds=flags.line_item_ids,
                filter=flags.filter,
                pageToken=next_page_token,
            )
            .execute()
        )

        # If response is not empty, display the retrieved assigned targeting
        # options line items.
        if response:
            for assigned_option in response.get(
                "lineItemAssignedTargetingOptions", []
            ):
                ato_name = assigned_option.get(
                    "assignedTargetingOption", {}
                ).get("name", None)
                if ato_name:
                    print(f"Assigned Targeting Option {ato_name} found.")
        else:
            print("Error: No response was returned. Exiting.")
            sys.exit(1)

        # Update the next page token.
        # Break out of loop if there is no next page.
        if "nextPageToken" in response:
            next_page_token = response["nextPageToken"]
        else:
            break


if __name__ == "__main__":
    # Declare command-line flags.
    argparser = argparse.ArgumentParser(add_help=False)
    argparser.add_argument(
        "advertiser_id",
        help="The ID of the parent advertiser of the line items to which the "
        "listed targeting options are assigned.",
    )
    argparser.add_argument(
        "--line_item_ids",
        nargs="+",
        help="The IDs of the line items to list the assigned targeting "
        "options for. Multiple values can be listed after declaring the "
        'argument. Ex: "--line_item_ids 1 2 3"',
    )
    argparser.add_argument(
        "--filter",
        default='inheritance="NOT_INHERITED"',
        help="The filter expression by which to filter the list results. "
        "Leave empty to not use a filter.",
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
