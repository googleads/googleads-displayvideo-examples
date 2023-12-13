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

sys.path.insert(0, os.path.abspath(".."))
import samples_util


def main(service, flags):
    """Retrieve available browser targeting options.

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
        # Request the targeting options list.
        response = (
            service.targetingTypes()
            .targetingOptions()
            .list(
                advertiserId=flags.advertiser_id,
                targetingType="TARGETING_TYPE_BROWSER",
                pageToken=next_page_token,
            )
            .execute()
        )

        # Check if response is empty.
        if response:
            # Iterate over retrieved targeting options.
            for option in response["targetingOptions"]:
                print(
                    f"Targeting Option ID: {option['targetingOptionId']}, "
                    "Browser Display Name: "
                    f"{option['browserDetails']['displayName']}"
                )
        else:
            print("List request returned no Targeting Options.")
            break

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
        help="The ID of the advertiser this request is being made within.",
    )

    # Retrieve command line arguments.
    flags = samples_util.get_arguments(
        sys.argv,
        __doc__,
        parents=[samples_util.get_default_parser(), argparser],
    )

    # Authenticate and construct service.
    service = samples_util.get_service(
        version="v3", useServiceAccount=flags.use_service_account
    )

    try:
        main(service, flags)
    except HttpError as e:
        print(e)
        sys.exit(1)
