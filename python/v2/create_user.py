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

"""This example creates a Display & Video 360 user.

This example requires authentication using a service account.
"""

import argparse
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath(".."))
import samples_util


def main(service, flags):
    """Creates a new DV360 user.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    # Build list of roles for the new user.
    user_roles = []
    if flags.partner_role:
        for role in flags.partner_role:
            user_roles.append({"partnerId": role[0], "userRole": role[1]})
    if flags.advertiser_role:
        for role in flags.advertiser_role:
            user_roles.append({"advertiserId": role[0], "userRole": role[1]})

    # Create a user object.
    user_obj = {
        "email": flags.email_address,
        "displayName": flags.display_name,
        "assignedUserRoles": user_roles,
    }

    # Build and execute request.
    response = service.users().create(body=user_obj).execute()

    # Display the new user.
    print(f"User {response['name']} was created.")


if __name__ == "__main__":
    # Declare command-line flags.
    argparser = argparse.ArgumentParser(add_help=False)
    argparser.add_argument(
        "email_address", help="The email address of the user to be created."
    )
    argparser.add_argument(
        "display_name", help="The display name of the user to be created."
    )
    argparser.add_argument(
        "--partner_role",
        nargs=2,
        action="append",
        help="A partner ID and user role to assign to the user to grant the "
        "appropriate access to the partner. Multiple instances of this "
        'argument may be included in the command. Ex: "--partner_role 123 '
        'ADMIN"',
    )
    argparser.add_argument(
        "--advertiser_role",
        nargs=2,
        action="append",
        help="An advertiser ID and user role to assign to the user to grant "
        "the appropriate access to the advertiser. Multiple instances of this "
        'argument may be included in the command. Ex: "--advertiser_role 456 '
        'STANDARD"',
    )

    # Retrieve command line arguments.
    flags = samples_util.get_arguments(
        sys.argv,
        __doc__,
        parents=[samples_util.get_default_parser(), argparser],
    )

    # Authenticate and construct service.
    service = samples_util.get_service(
        version="v2",
        useServiceAccount=flags.use_service_account,
        addUserServiceScope=True,
    )

    try:
        main(service, flags)
    except HttpError as e:
        print(e)
        sys.exit(1)
