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

"""This example creates a video creative."""

import argparse
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath(".."))
import samples_util
from v2_util import upload_creative_asset


def main(service, flags):
    """Creates a new DV360 video creative.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    advertiser_id = flags.advertiser_id

    # Upload video asset
    video_asset = upload_creative_asset(
        service, advertiser_id, flags.video_asset_path
    )

    # Create a creative object.
    creative_obj = {
        "displayName": flags.display_name,
        "entityStatus": "ENTITY_STATUS_ACTIVE",
        "hostingSource": "HOSTING_SOURCE_HOSTED",
        "creativeType": "CREATIVE_TYPE_VIDEO",
        "assets": [
            {
                "asset": {"mediaId": video_asset["mediaId"]},
                "role": "ASSET_ROLE_MAIN",
            }
        ],
        "exitEvents": [
            {
                "name": flags.exit_event_name,
                "type": "EXIT_EVENT_TYPE_DEFAULT",
                "url": flags.exit_event_url,
            }
        ],
    }

    # Create the creative.
    creative = (
        service.advertisers()
        .creatives()
        .create(advertiserId=advertiser_id, body=creative_obj)
        .execute()
    )

    # Display the new creative.
    print(f"creative {creative['name']} was created.")


if __name__ == "__main__":
    # Declare command-line flags.
    argparser = argparse.ArgumentParser(add_help=False)
    argparser.add_argument(
        "advertiser_id",
        help="The ID of the parent advertiser of the creative to be created.",
    )
    argparser.add_argument(
        "display_name", help="The display name of the creative to be created."
    )
    argparser.add_argument(
        "video_asset_path",
        help="The path to the file being uploaded and assigned as a video "
        "asset.",
    )
    argparser.add_argument(
        "exit_event_name", help="The name of the main exit event."
    )
    argparser.add_argument(
        "exit_event_url", help="The url of the main exit event."
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
