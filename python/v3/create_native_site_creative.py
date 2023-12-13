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

"""This example creates a native site creative."""

import argparse
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath(".."))
import samples_util
from v3_util import upload_creative_asset


def main(service, flags):
    """Creates a new DV360 Native site creative.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    advertiser_id = flags.advertiser_id

    # Upload image asset
    image_asset = upload_creative_asset(
        service, advertiser_id, flags.image_asset_path
    )

    # Upload logo asset
    logo_asset = upload_creative_asset(
        service, advertiser_id, flags.logo_asset_path
    )

    # Create a creative object.
    creative_obj = {
        "displayName": flags.display_name,
        "entityStatus": "ENTITY_STATUS_ACTIVE",
        "hostingSource": "HOSTING_SOURCE_HOSTED",
        "creativeType": "CREATIVE_TYPE_NATIVE",
        "dimensions": {
            "heightPixels": flags.creative_height_pixels,
            "widthPixels": flags.creative_width_pixels,
        },
        "assets": [
            {
                "asset": {"mediaId": image_asset["mediaId"]},
                "role": "ASSET_ROLE_MAIN",
            },
            {
                "asset": {"mediaId": logo_asset["mediaId"]},
                "role": "ASSET_ROLE_ICON",
            },
            {
                "asset": {"content": flags.advertiser_name},
                "role": "ASSET_ROLE_ADVERTISER_NAME",
            },
            {
                "asset": {"content": flags.headline},
                "role": "ASSET_ROLE_HEADLINE",
            },
            {"asset": {"content": flags.body_text}, "role": "ASSET_ROLE_BODY"},
            {
                "asset": {"content": flags.caption_url},
                "role": "ASSET_ROLE_CAPTION_URL",
            },
            {
                "asset": {"content": flags.call_to_action},
                "role": "ASSET_ROLE_CALL_TO_ACTION",
            },
        ],
        "exitEvents": [
            {"type": "EXIT_EVENT_TYPE_DEFAULT", "url": flags.landing_page_url}
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
        "image_asset_path",
        help="The path to the file being uploaded and assigned as a image "
        "asset.",
    )
    argparser.add_argument(
        "logo_asset_path",
        help="The path to the file being uploaded and assigned as a logo "
        "asset.",
    )
    argparser.add_argument(
        "creative_height_pixels",
        type=int,
        help="The height of the creative asset in pixels.",
    )
    argparser.add_argument(
        "creative_width_pixels",
        type=int,
        help="The width of the creative asset in pixels.",
    )
    argparser.add_argument(
        "advertiser_name", help="The advertiser name used in the creative."
    )
    argparser.add_argument(
        "headline", help="The headline used in the creative."
    )
    argparser.add_argument(
        "body_text", help="The body text used in the creative."
    )
    argparser.add_argument(
        "landing_page_url", help="The landing page URL used in the creative."
    )
    argparser.add_argument(
        "caption_url", help="The caption URL used in the creative."
    )
    argparser.add_argument(
        "call_to_action", help="The call to action used in the creative."
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
