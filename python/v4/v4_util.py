#!/usr/bin/python
#
# Copyright 2023 Google LLC
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

"""Handles common tasks across v3 samples."""

import os

from apiclient.http import MediaFileUpload


def upload_creative_asset(service, advertiser_id, path):
    """Uploads creative asset at given file under the given advertiser.

    Args:
      service: the displayvideo service object.
      advertiser_id: long, the ID of the advertiser parent of the asset.
      path: string, the path to the file to upload.

    Returns:
      The created asset object.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    # Create the request body.
    body = {"filename": os.path.basename(path)}

    # Create upload object.
    media = MediaFileUpload(path)

    # If no obvious MIME type value, apply a default
    if not media.mimetype():
        media = MediaFileUpload(path, "application/octet-stream")

    # Build and execute the request.
    response = (
        service.advertisers()
        .assets()
        .upload(advertiserId=advertiser_id, body=body, media_body=media)
        .execute()
    )

    return response["asset"]


def print_bulk_edit_targeting_response(response):
    """Prints the given response of an
    advertisers.lineItems.bulkEditAssignedTargetingOptions request in an easily
    readable format.

    Args:
      response: the object returned by an
        advertisers.lineItems.bulkEditAssignedTargetingOptions request.
    """

    if "updatedLineItemIds" in response:
        print("The targeting of the following line item IDs were updated:")
        for id in response["updatedLineItemIds"]:
            print(id)
    if "failedLineItemIds" in response:
        print("The targeting of the following line item IDs failed to update:")
        for id in response["failedLineItemIds"]:
            print(id)
    if "errors" in response:
        print(
            "The following errors were thrown when attempting to edit the "
            "targeting:"
        )
        for error in response["errors"]:
            print(f"{error['code']}: {error['message']}")
