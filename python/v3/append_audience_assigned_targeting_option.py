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

"""This example updates the audience targeting of a line item.

It takes a list of Google audience IDs and adds them to be included in any
existing audience targeting of the line item.
"""

import argparse
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath(".."))
import samples_util
from v3_util import print_bulk_edit_targeting_response


def main(service, flags):
    """Adds new Google Audiences to the audience targeting of a line item.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    advertiser_id = flags.advertiser_id
    line_item_id = flags.line_item_id
    additional_google_audiences = flags.additional_google_audiences

    # Build Google audience targeting settings objects to add.
    new_google_audience_targeting_settings = [
        {"googleAudienceId": google_audience_id}
        for google_audience_id in additional_google_audiences
    ]

    # Retrieve any existing line item audience targeting.
    retrieved_audience_targeting_list = (
        service.advertisers()
        .lineItems()
        .targetingTypes()
        .assignedTargetingOptions()
        .list(
            advertiserId=advertiser_id,
            lineItemId=line_item_id,
            targetingType="TARGETING_TYPE_AUDIENCE_GROUP",
        )
        .execute()
    )

    # Create empty objects as default if there is no existing audience
    # targeting.
    updated_audience_group_details = {}
    bulk_edit_request = {"lineItemIds": line_item_id}

    # If audience targeting already exists, copy over the details and set it to
    # be deleted in the bulk edit.
    if "assignedTargetingOptions" in retrieved_audience_targeting_list:
        for option in retrieved_audience_targeting_list[
            "assignedTargetingOptions"
        ]:
            if option["assignedTargetingOptionId"] == "audienceGroup":
                updated_audience_group_details = option["audienceGroupDetails"]
                bulk_edit_request["deleteRequests"] = [
                    {
                        "targetingType": "TARGETING_TYPE_AUDIENCE_GROUP",
                        "assignedTargetingOptionIds": ["audienceGroup"],
                    }
                ]

    # Add new Google audiences to existing Google audience group.
    if "includedGoogleAudienceGroup" in updated_audience_group_details:
        updated_audience_group_details["includedGoogleAudienceGroup"][
            "settings"
        ].extend(new_google_audience_targeting_settings)
    else:
        updated_audience_group_details["includedGoogleAudienceGroup"] = {
            "settings": new_google_audience_targeting_settings
        }

    # Build and add create request to bulk edit request.
    bulk_edit_request["createRequests"] = [
        {
            "targetingType": "TARGETING_TYPE_AUDIENCE_GROUP",
            "assignedTargetingOptions": [
                {"audienceGroupDetails": updated_audience_group_details}
            ],
        }
    ]

    # Replace the audience targeting option for the line item.
    response = (
        service.advertisers()
        .lineItems()
        .bulkEditAssignedTargetingOptions(
            advertiserId=advertiser_id, body=bulk_edit_request
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
        help="The ID of the parent advertiser of the line item whose audience "
        "targeting option will be updated.",
    )
    argparser.add_argument(
        "line_item_id",
        help="The ID of the line item whose audience targeting option will be "
        "updated.",
    )
    argparser.add_argument(
        "--additional_google_audiences",
        nargs="+",
        help="The Google audience IDs to add to the line item audience "
        "targeting. Multiple values can be listed after declaring the argument."
        ' Ex: "--additional_google_audiences 10001 10002 10003"',
        required=True,
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
