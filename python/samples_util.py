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

"""Handles common tasks across all API samples."""

import argparse
import os
import socket

from google.oauth2 import service_account
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient import discovery

_API_NAME = "displayvideo"
_DEFAULT_API_VERSION = "v3"

_DEFAULT_API_SCOPES = ["https://www.googleapis.com/auth/display-video"]
_USER_API_SCOPE = (
    "https://www.googleapis.com/auth/display-video-user-management"
)

_API_URL = "https://displayvideo.googleapis.com/"

_CREDENTIALS_FILE = "INSERT_PATH_TO_CREDENTIALS_FILE"

_SERVICE_ACCOUNT_CREDENTIALS_FILE = "INSERT_PATH_TO_SERVICE_ACCOUNT_KEY_FILE"


def get_default_parser():
    """Returns general arguments to use across samples.

    Returns:
      The general command-line argument argparser object.
    """
    argparser = argparse.ArgumentParser(add_help=False)
    argparser.add_argument(
        "--use_service_account",
        action="store_true",
        help="Authenticate the requests using a service account.",
    )
    return argparser


def get_arguments(argv, desc, parents=None):
    """Validates and parses command line arguments.

    Args:
      argv: list of strings, the command-line parameters of the application.
      desc: string, a description of the sample being executed.
      parents: list of argparsers, the argparsers passed in by the method
        calling this function.

    Returns:
      The parsed command-line arguments.
    """
    parser = argparse.ArgumentParser(
        description=desc,
        formatter_class=argparse.RawDescriptionHelpFormatter,
        parents=parents,
    )
    return parser.parse_args(argv[1:])


def get_credentials(scopes):
    """Steps through installed app OAuth 2.0 flow to retrieve credentials.

    Args:
      scopes: list of strings, the scopes to request when retrieving
        credentials.

    Returns:
      The initialized credentials.
    """
    credentials_file = _CREDENTIALS_FILE

    # Asks for path to credentials file if not found in default location.
    if os.path.isfile(credentials_file):
        return InstalledAppFlow.from_client_secrets_file(
            credentials_file, scopes
        ).run_local_server()
    else:
        print(
            f"A client secrets file could not be found at {credentials_file}."
        )
        print("Please provide the path to a client secrets JSON file.")
        while True:
            print("Enter path to client secrets file:")
            credentials_file = input()
            if os.path.isfile(credentials_file):
                return InstalledAppFlow.from_client_secrets_file(
                    credentials_file, scopes
                ).run_local_server()
            else:
                print(f"No file was found at {credentials_file}.")


def get_service_account_credentials(scopes):
    """Steps through Service Account OAuth 2.0 flow to retrieve credentials.

    Args:
      scopes: list of strings, the scopes to request when retrieving
        credentials.

    Returns:
      The initialized credentials.
    """
    service_account_credentials_file = _SERVICE_ACCOUNT_CREDENTIALS_FILE

    # Asks for path to credentials file is not found in default location.
    if os.path.isfile(service_account_credentials_file):
        return service_account.Credentials.from_service_account_file(
            service_account_credentials_file, scopes=scopes
        )
    else:
        print(
            "A service account key file could not be found at "
            f"{service_account_credentials_file}."
        )
        print("Please provide the path to a service account key JSON file.")
        while True:
            print("Enter path to service account key file:")
            service_account_credentials_file = input()
            if os.path.isfile(service_account_credentials_file):
                return service_account.Credentials.from_service_account_file(
                    service_account_credentials_file, scopes=scopes
                )
            else:
                print(
                    f"No file was found at {service_account_credentials_file}."
                )


def build_discovery_url(version, label, key):
    """Builds a discovery url from which to fetch the discovery document.

    Args:
      version: a str indicating the version number of the API.
      label: a str indicating a label to be applied to the discovery service
        request. This may be used as a means of programmatically retrieving a
        copy of a discovery document containing allowlisted content.
      key: a str generated by the user project attempting to retrieve this
        discovery document.

    Returns:
      A str that can be used to retrieve the disovery document for the API
      version given.
    """
    discovery_url = f"{_API_URL}/$discovery/rest?version={version}"
    if label:
        discovery_url = discovery_url + f"&labels={label}"
    if key:
        discovery_url = discovery_url + f"&key={key}"
    return discovery_url


def get_service(
    version=_DEFAULT_API_VERSION,
    label=None,
    key=None,
    useServiceAccount=False,
    addUserServiceScope=False,
):
    """Builds the Display & Video 360 API service used for the REST API.

    Args:
      version: a str indicating the Display & Video 360 API version to be
        retrieved.
      label: a str indicating a label to be applied to the discovery service
        request. This may be used as a means of programmatically retrieving a
        copy of a discovery document containing allowlisted content.
      key: a str identifying the user project.
      useServiceAccount: a bool indicating whether to authenticate using a
        service account.
      addUserServiceScope: a bool indicating whether to authenticate use of the
        User service scope in addition to the default Display & Video 360 API
        scopes. Only applicable if authenticating with a service account.

    Returns:
      A googleapiclient.discovery.Resource instance used to interact with the
      Display & Video 360 API.
    """

    if useServiceAccount:
        scopes = _DEFAULT_API_SCOPES

        # Add the user service API scope to the scopes list if requested
        if addUserServiceScope:
            scopes.append(_USER_API_SCOPE)

        credentials = get_service_account_credentials(scopes)
    else:
        credentials = get_credentials(_DEFAULT_API_SCOPES)

    discovery_url = build_discovery_url(version, label, key)

    socket.setdefaulttimeout(180)

    # Initialize client for Display & Video 360 API
    service = discovery.build(
        _API_NAME,
        version,
        discoveryServiceUrl=discovery_url,
        credentials=credentials,
    )

    return service
