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
import random
import sys
import time

from googleapiclient import http as googleHttp
from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath('..'))
import samples_util


# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)

group = argparser.add_mutually_exclusive_group(required=True)
group.add_argument(
    '--partner_id',
    type=str,
    help='The ID of the partner for which to download the SDF. Required if the --advertiser_id '
         'argument is not set.')
group.add_argument(
    '--advertiser_id',
    type=str,
    help='The ID of the advertiser for which to download the SDF. Required if the --partner_id '
         'argument is not set.')

argparser.add_argument('output_file', help='The file in which to save the downloaded SDF.')
argparser.add_argument('version', help='The SDF version to use for the resulting SDF.')
argparser.add_argument(
    'filter_type',
    help='The type of resource by which to filter the data included in the generated SDFs.')
argparser.add_argument(
    '--file_types',
    nargs='+',
    help='The SDF file types to generate and include in this download. Multiple values '
         'can be listed after declaring the argument. Ex: "--file_types FILE_TYPE_CAMPAIGN '
         'FILE_TYPE_INSERTION_ORDER FILE_TYPE_LINE_ITEM"',
    required=True)
argparser.add_argument(
    '--filter_ids',
    nargs='+',
    type=str,
    help='The IDs of the resources by which to filter the data included in the generated SDFs. '
         'Multiple values can be listed after declaring the argument. Ex: "--filter_ids 10001 '
         '10002 10003"')

# The following values control retry behavior while
# the report is processing.
# Minimum amount of time between polling requests. Defaults to 5 seconds.
MIN_RETRY_INTERVAL = 5
# Maximum amount of time between polling requests. Defaults to 5 minutes.
MAX_RETRY_INTERVAL = 5 * 60
# Maximum amount of time to spend polling. Defaults to 5 hours.
MAX_RETRY_ELAPSED_TIME = 5 * 60 * 60


def main(service, flags):
  try:
    # Build and create SDF Download task.
    operation = create_sdfdownloadtask(service, flags.partner_id, flags.advertiser_id,
                                       flags.version, flags.file_types, flags.filter_type,
                                       flags.filter_ids)
  except HttpError as e:
    print(e)
    sys.exit(1)

  try:
    # Get current status of operation with exponential backoff retry logic.
    resource_name = wait_for_task(service, operation)
  except HttpError as e:
    print(e)
    sys.exit(1)

  try:
    # Download generated SDF zip file to the given output file.
    download_file(service, resource_name, flags.output_file)
  except HttpError as e:
    print(e)
    sys.exit(1)


def create_sdfdownloadtask(service, partner_id, advertiser_id, version, file_types, filter_type,
                           filter_ids):
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
  """

  create_sdf_download_task_request = {'version': version}

  if partner_id is not None:
    create_sdf_download_task_request['partnerId'] = partner_id
  else:
    create_sdf_download_task_request['advertiserId'] = advertiser_id

  parent_entity_filter = {'fileType': file_types, 'filterType': filter_type}

  if filter_ids:
    parent_entity_filter['filterIds'] = filter_ids

  create_sdf_download_task_request['parentEntityFilter'] = parent_entity_filter

  # Create the sdfdownloadtask.
  operation = service.sdfdownloadtasks().create(body=create_sdf_download_task_request).execute()

  print(f'Operation {operation["name"]} was created.')

  return operation


def wait_for_task(service, operation):
  """Wrapper for retried function checking sdfdownloadtask completion.

  Args:
    service: the displayvideo service object.
    operation: the sdfdownloadtask operation in progress.

  Returns:
    The resource name of the generated media.
  Raises:
    RuntimeError: If operation finishes in error or does not finish before the given deadline.
  """

  # Configure the Operations.get request.
  get_request = service.sdfdownloadtasks().operations().get(name=operation['name'])

  sleep = 0
  start_time = time.time()

  # Retrieve initial operation.
  operation = get_request.execute()
  done = 'done' in operation

  while not done:
    # Raise error if maximum retry time has elapsed.
    if time.time() - start_time > MAX_RETRY_ELAPSED_TIME:
      raise RuntimeError('Structured Data File generation deadline exceeded.')

    # Retrieve next sleep interval and sleep before retrieving operation again.
    sleep = next_sleep_interval(sleep)
    print(f'Operation still running, sleeping for {sleep} seconds.')
    time.sleep(sleep)

    # Get current status of operation.
    operation = get_request.execute()
    done = 'done' in operation

  # Check if operation has finished in error.
  if 'error' in operation:
    raise RuntimeError(f'The operation finished in error with code {operation["error"]["code"]}: '
                       f'{operation["error"]["message"]}.')

  resource_name = operation['response']['resourceName']

  print(f'The operation completed successfully. Resource {resource_name} was created.')

  return resource_name


def next_sleep_interval(previous_sleep_interval):
  """Calculates the next sleep interval based on the previous using exponential backoff logic.

  Args:
    previous_sleep_interval: int, the previous sleep interval length in seconds.

  Returns:
    The next sleep interval length in seconds.
  """
  min_interval = previous_sleep_interval or MIN_RETRY_INTERVAL
  max_interval = previous_sleep_interval * 3 or MIN_RETRY_INTERVAL
  return min(MAX_RETRY_INTERVAL, random.randint(min_interval, max_interval))


def download_file(service, resource_name, output_file):
  """Downloads the generated media to the given output file.

  Args:
    service: the displayvideo service object.
    resource_name: string, the resource name for the generated media.
    output_file: string, the file where the generated media will be downloaded.
  """

  # Configure the Media.download request
  download_request = service.media().download_media(resourceName=resource_name)

  # Create output stream for downloaded file
  out_stream = io.FileIO(output_file, mode='wb')

  # Make downloader object
  downloader = googleHttp.MediaIoBaseDownload(out_stream, download_request)

  # Download media file in chunks until finished
  download_finished = False
  while not download_finished:
    _, download_finished = downloader.next_chunk()

  print(f'File downloaded to {output_file}.')


if __name__ == '__main__':
  # Retrieve command line arguments.
  flags = samples_util.get_arguments(sys.argv, __doc__, parents=[argparser])

  # Authenticate and construct service.
  service = samples_util.get_service(version='v1')

  main(service, flags)
