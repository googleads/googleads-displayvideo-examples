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

"""This example generates and downloads requested Structured Data Files."""

import argparse
import io
import os
import sys

from googleapiclient import http as googleHttp
from googleapiclient.errors import HttpError
from retry.api import retry_call

sys.path.insert(0, os.path.abspath(".."))
import samples_util


# The following values control retry behavior while the report is processing.
# Minimum amount of time between polling requests. Defaults to 5 seconds.
MIN_RETRY_INTERVAL = 5
# Maximum amount of time between polling requests. Defaults to 5 minutes.
MAX_RETRY_INTERVAL = 5 * 60
# Maximum number of requests to make when polling. Defaults to 100 requests.
MAX_RETRY_COUNT = 100


def main(service, flags):
    """Generates and downloads Structured Data Files.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      RuntimeError: If operation does not finish or finish successfully.
      HttpError: If an API request is not made successfully.
    """

    # Build and create SDF Download task.
    operation = create_sdfdownloadtask(
        service,
        flags.partner_id,
        flags.advertiser_id,
        flags.version,
        flags.file_types,
        flags.filter_type,
        flags.filter_ids,
    )

    # Configure the Operations.get request.
    get_request = (
        service.sdfdownloadtasks().operations().get(name=operation["name"])
    )

    # Poll operation with retry logic until done or maximum number of tries
    # exceeded.
    finished_operation = retry_call(
        poll_task,
        fargs=[get_request],
        exceptions=RuntimeError,
        tries=MAX_RETRY_COUNT,
        delay=MIN_RETRY_INTERVAL,
        max_delay=MAX_RETRY_INTERVAL,
        backoff=2,
        jitter=(0, 60),
    )

    # Check if operation has finished in error.
    if "error" in finished_operation:
        raise RuntimeError(
            "The operation finished in error with code "
            f"{finished_operation['error']['code']}: "
            f"{finished_operation['error']['message']}."
        )

    resource_name = finished_operation["response"]["resourceName"]

    print(
        f"The operation completed successfully. Resource {resource_name} "
        "was created."
    )

    # Download generated SDF zip file to the given output file.
    download_file(service, resource_name, flags.output_file)


def create_sdfdownloadtask(
    service,
    partner_id,
    advertiser_id,
    version,
    file_types,
    filter_type,
    filter_ids,
):
    """Builds and runs the Sdfdownloadtasks.Create request.

    Args:
      service: the displayvideo service object.
      partner_id: long, the ID of the partner context of the request.
      advertiser_id: long, the ID of the advertiser context of the request.
      version: string, the SDF version to generate.
      file_types: list of strings, the SDF file types to generate
      filter_type: string, the type of resource to filter by.
      filter_ids: list of strings, the resource IDs to filter results by.

    Returns:
      The created operation.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    create_sdf_download_task_request = {"version": version}

    if partner_id:
        create_sdf_download_task_request["partnerId"] = partner_id
    else:
        create_sdf_download_task_request["advertiserId"] = advertiser_id

    parent_entity_filter = {"fileType": file_types, "filterType": filter_type}

    if filter_ids:
        parent_entity_filter["filterIds"] = filter_ids

    create_sdf_download_task_request[
        "parentEntityFilter"
    ] = parent_entity_filter

    # Create the sdfdownloadtask.
    operation = (
        service.sdfdownloadtasks()
        .create(body=create_sdf_download_task_request)
        .execute()
    )

    print(f"Operation {operation['name']} was created.")

    return operation


def poll_task(get_request):
    """Polls sdfdownloadtask operation to see if it is finished.

    Args:
      get_request: the Display & Video 360 API sdfdownloadtask.operations.get
        request object.

    Returns:
      The resource name of the generated media.

    Raises:
      RuntimeError: If operation is not finished.
      HttpError: If an API request is not made successfully.
    """

    print("Polling task...")

    # Retrieve initial operation.
    operation = get_request.execute()
    if "done" in operation:
        return operation
    else:
        raise RuntimeError(
            "SDF Download task polling unsuccessful. Structured Data Files "
            "still generating."
        )


def download_file(service, resource_name, output_file):
    """Downloads the generated media to the given output file.

    Args:
      service: the displayvideo service object.
      resource_name: string, the resource name for the generated media.
      output_file: string, the file where the generated media will be
        downloaded.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    # Configure the Media.download request
    download_request = service.media().download_media(
        resourceName=resource_name
    )

    # Create output stream for downloaded file
    with open(output_file, mode="wb") as out_stream:
        # Make downloader object
        downloader = googleHttp.MediaIoBaseDownload(
            out_stream, download_request
        )

        # Download media file in chunks until finished
        while True:
            _, download_finished = downloader.next_chunk()
            if download_finished:
                break

    print(f"File downloaded to {output_file}.")


if __name__ == "__main__":
    # Declare command-line flags.
    argparser = argparse.ArgumentParser(add_help=False)

    group = argparser.add_mutually_exclusive_group(required=True)
    group.add_argument(
        "--partner_id",
        help="The ID of the partner for which to download the SDF. Required "
        "if the --advertiser_id argument is not set.",
    )
    group.add_argument(
        "--advertiser_id",
        help="The ID of the advertiser for which to download the SDF. "
        "Required if the --partner_id argument is not set.",
    )

    argparser.add_argument(
        "output_file", help="The file in which to save the downloaded SDF."
    )
    argparser.add_argument(
        "version", help="The SDF version to use for the resulting SDF."
    )
    argparser.add_argument(
        "filter_type",
        help="The type of resource by which to filter the data included in "
        "the generated SDFs.",
    )
    argparser.add_argument(
        "--file_types",
        nargs="+",
        help="The SDF file types to generate and include in this download. "
        "Multiple values can be listed after declaring the argument. Ex: "
        '"--file_types FILE_TYPE_CAMPAIGN FILE_TYPE_INSERTION_ORDER '
        'FILE_TYPE_LINE_ITEM"',
        required=True,
    )
    argparser.add_argument(
        "--filter_ids",
        nargs="+",
        help="The IDs of the resources by which to filter the data included "
        "in the generated SDFs. Multiple values can be listed after declaring "
        'the argument. Ex: "--filter_ids 10001 10002 10003"',
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
    except (HttpError, RuntimeError) as e:
        print(e)
        sys.exit(1)
