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

"""This example uploads a script for the custom bidding algorithm."""

import argparse
import os
import sys

from apiclient.http import MediaFileUpload
from apiclient.http import HttpRequest

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath(".."))
import samples_util


def main(service, flags):
    """Uploads a custom bidding script file under a given algorithm resource.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    # Retrieve a usable custom bidding script reference.
    scriptRef = (
        service.customBiddingAlgorithms()
        .uploadScript(
            customBiddingAlgorithmId=flags.custom_bidding_algorithm_id,
            advertiserId=flags.advertiser_id,
        )
        .execute()
    )

    # Display the new custom bidding script reference object.
    print(f"The following script ref was created: {scriptRef}")

    # Create media upload object.
    media = MediaFileUpload(flags.script_path)

    # Configure upload request.
    upload_request = service.media().upload(
        resourceName=scriptRef["resourceName"], media_body=media
    )
    upload_request.postproc = HttpRequest.null_postproc

    # Upload script file.
    upload_request.execute()

    # Create script object.
    script_obj = {"script": scriptRef}

    # Build and execute request.
    script = (
        service.customBiddingAlgorithms()
        .scripts()
        .create(
            customBiddingAlgorithmId=flags.custom_bidding_algorithm_id,
            advertiserId=flags.advertiser_id,
            body=script_obj,
        )
        .execute()
    )

    # Display the new script object.
    print(f"The following script object was created: {script}")


if __name__ == "__main__":
    # Declare command-line flags.
    argparser = argparse.ArgumentParser(add_help=False)
    argparser.add_argument(
        "advertiser_id",
        help="The ID of the parent advertiser of the parent algorithm.",
    )
    argparser.add_argument(
        "custom_bidding_algorithm_id",
        help="The ID of the algorithm to upload a script for.",
    )
    argparser.add_argument(
        "script_path", help="The path to the script file being uploaded."
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
