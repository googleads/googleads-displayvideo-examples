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

"""This example edits the access roles of a Display & Video 360 user in bulk.

This example requires authentication using a service account.
"""

import argparse
import os
import sys

from googleapiclient.errors import HttpError

sys.path.insert(0, os.path.abspath('..'))
import samples_util


# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('user_id', help='The ID of the user whose access will be edited.')
argparser.add_argument(
    '--remove_partner_id',
    nargs='*',
    help='The partner IDs to remove the user\'s access from. Ex: "--remove_partner_access 123 456"')
argparser.add_argument(
    '--remove_advertiser_id',
    nargs='*',
    help='The advertiser IDs to remove the user\'s access from. Ex: "--remove_advertiser_access '
         '123 456"')
argparser.add_argument(
    '--add_partner_role',
    nargs=2,
    action='append',
    help='A partner ID and user role to assign to the user to grant the appropriate access to the '
         'partner. Multiple instances of this argument may be included in the command. Ex: '
         '"--add_partner_role 123 ADMIN"')
argparser.add_argument(
   '--add_advertiser_role',
   nargs=2,
   action='append',
   help='An advertiser ID and user role to assign to the user to grant the appropriate access to '
        'the advertiser. Multiple instances of this argument may be included in the command. Ex: '
        '"--add_advertiser_role 456 STANDARD"')


def main(service, flags):
  # Build list of user roles to grant to the user.
  added_user_roles = []
  if flags.add_partner_role != None:
    for role in flags.add_partner_role:
      added_user_roles.append({
          'partnerId': role[0],
          'userRole': role[1]
      })
  if flags.add_advertiser_role != None:
    for role in flags.add_advertiser_role:
      added_user_roles.append({
          'advertiserId': role[0],
          'userRole': role[1]
      })

  # Build list of strings identifying the resources to remove the user's access from.
  deleted_resource_ids = []
  if flags.remove_partner_id != None:
    for partner_id in flags.remove_partner_id:
      deleted_resource_ids.append(f'partner-{partner_id}')
  if flags.remove_advertiser_id != None:
    for advertiser_id in flags.remove_advertiser_id:
      deleted_resource_ids.append(f'advertiser-{advertiser_id}')

  # Build bulk edit request.
  bulk_edit_user_roles_request = {
      'deletedAssignedUserRoles': deleted_resource_ids,
      'createdAssignedUserRoles': added_user_roles
  }

  try:
    # Edit the assigned user roles.
    response = service.users().bulkEditAssignedUserRoles(
        userId=flags.user_id,
        body=bulk_edit_user_roles_request).execute()
  except HttpError as e:
    print(e)
    sys.exit(1)

  # Check if response is empty.
  # If not, iterate over and display new assigned user roles.
  if not response:
    print('Bulk edit request created no new assigned user roles.')
  else:
    print('The following assigned user roles were created:')
    for assigned_user_role in response['createdAssignedUserRoles']:
      print(f'{assigned_user_role}')


if __name__ == '__main__':
  # Retrieve command line arguments.
  flags = samples_util.get_arguments(sys.argv, __doc__, parents=[argparser])

  # Authenticate and construct service.
  service = samples_util.get_service(
      version='v1',
      useServiceAccount=True,
      addUserServiceScope=True)

  main(service, flags)
