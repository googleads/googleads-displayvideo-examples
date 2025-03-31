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

"""This example uploads a file as a creative asset for the given advertiser."""

import argparse
import os
import sys

from apiclient.http import MediaFileUpload
from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath(".."))
import samples_util
from v4_util import upload_creative_asset


def main(service, flags):
    """Uploads given file as a creative asset under the given advertiser.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    # Upload creative asset and get resulting asset object
    asset = upload_creative_asset(service, flags.advertiser_id, flags.path)

    # Display the new asset media ID.
    print(f"Asset was created with media ID {asset['mediaId']}.")


if __name__ == "__main__":
    # Declare command-line flags.
    argparser = argparse.ArgumentParser(add_help=False)
    argparser.add_argument(
        "advertiser_id",
        help="The ID of the advertiser to upload this asset for.",
    )
    argparser.add_argument(
        "path", help="The path to the file being uploaded as an asset."
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
