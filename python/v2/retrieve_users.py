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

"""This example retrieves accessible DV360 users based on given filter values.

This example requires authentication using a service account.
"""

import argparse
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath(".."))
import samples_util


def main(service, flags):
    """Retrieve visible users based on given filter values.

    Args:
      service: the googleapiclient.discovery.Resource instance used to interact
        with the Display & Video 360 API.
      flags: the parsed command-line arguments.

    Raises:
      HttpError: If an API request is not made successfully.
    """

    # Create list to store the built filters.
    filters = []

    # Build filters with given arguments.
    if flags.email_address:
        filters.append(f"email:\"{flags.email_address}\"")
    if flags.display_name:
        filters.append(f"displayName:\"{flags.display_name}\"")
    if flags.user_role:
        filters.append(f"assignedUserRole.userRole=\"{flags.user_role}\"")
    if flags.has_partner_role:
        filters.append("assignedUserRole.entityType=\"PARTNER\"")
    if flags.has_advertiser_role:
        filters.append("assignedUserRole.entityType=\"ADVERTISER\"")
    if flags.partner_id:
        filters.append(f"assignedUserRole.partnerId=\"{flags.partner_id}\"")
    if flags.advertiser_id:
        filters.append(
            f"assignedUserRole.advertiserId=\"{flags.advertiser_id}\""
        )
    if flags.parent_partner_id:
        filters.append(
            f"assignedUserRole.parentPartnerId=\"{flags.parent_partner_id}\""
        )

    # Build full filter string out of filter list.
    and_operator = " AND "
    filter_str = and_operator.join(filters)

    # Create the page token variable.
    next_page_token = ""

    while True:
        # Request the targeting options list.
        response = (
            service.users()
            .list(filter=filter_str, pageToken=next_page_token)
            .execute()
        )

        # Check if response is empty.
        if response:
            # Iterate over retrieved users and print them and their assigned
            # user roles.
            for user in response["users"]:
                print(
                    f"User ID: {user['userId']}, Display name: "
                    f"{user['displayName']}, Email: {user['email']}"
                )
                for role in user["assignedUserRoles"]:
                    if "partnerId" in role:
                        print(
                            f"\tPartner ID: {role['partnerId']}, Role: "
                            f"{role['userRole']}"
                        )
                    elif "advertiserId" in role:
                        print(
                            f"\tAdvertiser ID: {role['advertiserId']}, Role: "
                            f"{role['userRole']}"
                        )
        else:
            print("List request returned no users.")
            break

        # Update the next page token.
        # Break out of loop if there is no next page.
        if "nextPageToken" in response:
            next_page_token = response["nextPageToken"]
        else:
            break


if __name__ == "__main__":
    # Declare command-line flags.
    argparser = argparse.ArgumentParser(add_help=False)
    argparser.add_argument(
        "--email_address",
        help="String that the email address of retrieved users must contain.",
    )
    argparser.add_argument(
        "--display_name",
        help="String that the display name of retrieved users must contain.",
    )
    argparser.add_argument(
        "--user_role",
        help="User role that the retrieved users must have assigned.",
    )
    argparser.add_argument(
        "--has_partner_role",
        action="store_true",
        help="Whether the retrieved users must have an assigned user role for "
        "a partner.",
    )
    argparser.add_argument(
        "--has_advertiser_role",
        action="store_true",
        help="Whether the retrieved users must have an assigned user role for "
        "an advertiser.",
    )
    argparser.add_argument(
        "--partner_id",
        help="ID for partner that retrieved users must have a user role "
        "assigned for.",
    )
    argparser.add_argument(
        "--advertiser_id",
        help="ID for advertiser that retrieved users must have a user role "
        "assigned for.",
    )
    argparser.add_argument(
        "--parent_partner_id",
        help="ID for parent partner of advertisers that retrieved users must "
        "have a user role assigned for.",
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
