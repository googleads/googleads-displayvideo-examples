<?php
/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Require the base class.
require_once __DIR__ . "/../BaseExample.php";

/**
 * This example retrieves accessible Display & Video 360 users based on given
 * filter values.
 *
 * This example makes requests to the Display & Video 360 API Users service that
 * require authentication via service account. Requests made not using a service
 * account will return an error.
 */
class RetrieveUsers extends BaseExample
{
    /**
     * (non-PHPdoc)
     * @see BaseExample::getInputParameters()
     */
    protected function getInputParameters(): array
    {
        return array(
            array(
                'name' => 'email_address',
                'display' => 'Email Address filter value',
                'required' => false
            ),
            array(
                'name' => 'display_name',
                'display' => 'Display Name filter value',
                'required' => false
            ),
            array(
                'name' => 'user_role',
                'display' => 'User Role filter value',
                'required' => false
            ),
            array(
                'name' => 'user_role_entity_type',
                'display' => 'User Role entity type',
                'required' => false,
                'values' => array('PARTNER', 'ADVERTISER')
            ),
            array(
                'name' => 'partner_id',
                'display' => 'Partner Id filter value',
                'required' => false
            ),
            array(
                'name' => 'advertiser_id',
                'display' => 'Advertiser Id filter value',
                'required' => false
            ),
            array(
                'name' => 'parent_partner_id',
                'display' => 'Parent Partner Id filter value',
                'required' => false
            ),
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::run()
     */
    public function run()
    {
        $values = $this->formValues;
        $emailAddress = $values['email_address'];
        $displayName = $values['display_name'];
        $userRole = $values['user_role'];
        $userRoleEntityType = $values['user_role_entity_type'];
        $partnerId = $values['partner_id'];
        $advertiserId = $values['advertiser_id'];
        $parentPartnerId = $values['parent_partner_id'];

        print '<h2>Users you can access that match filter values</h2>';

        $response = null;
        $nextPageToken = null;

        // Create list to store the built filters.
        $filters = array();

        // Build filters with given arguments.
        if (!empty($emailAddress)) {
            $filters[] = 'email:"' . $emailAddress . '"';
        }
        if (!empty($displayName)) {
            $filters[] = 'displayName:"' . $displayName . '"';
        }
        if (!empty($userRole)) {
            $filters[] = 'assignedUserRole.userRole="' . $userRole .'"';
        }
        if (!empty($userRoleEntityType)) {
            $filters[] =
                'assignedUserRole.entityType="' . $userRoleEntityType . '"';
        }
        if (!empty($partnerId)) {
            $filters[] = 'assignedUserRole.partnerId="' . $partnerId .'"';
        }
        if (!empty($advertiserId)) {
            $filters[] = 'assignedUserRole.advertiserId="' . $advertiserId .'"';
        }
        if (!empty($parentPartnerId)) {
            $filters[] =
                'assignedUserRole.parentPartnerId="' . $parentPartnerId .'"';
        }

        // Build full filter string out of filter list.
        $filterStr = implode(' AND ', $filters);

        do {
            $optParams = array(
                'filter' => $filterStr,
                'pageToken' => $nextPageToken
            );

            // Call the API, retrieving a page of accessible users matching the
            // given filter values.
            try {
                $response = $this
                    ->service
                    ->users
                    ->listUsers(
                        $optParams
                    );
            } catch (\Exception $e) {
                $this->renderError($e);
                return;
            }

            // Print the retrieved users.
            $this->printUsers(
                $response->getUsers()
            );


            // Update the next page token.
            $nextPageToken = $response->getNextPageToken();
        } while (!empty($nextPageToken));
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Retrieve Users (Requires service account)';
    }

    /**
     * Prints the given users, their details, and their assigned user roles.
     * @param array $users user resources to be printed.
     */
    protected function printUsers(array $users)
    {
        if (!empty($users)) {
            print '<ul>';

            // Iterate over and print list display of given users.
            foreach ($users as $user) {
                printf(
                    '<li>User %s found:<ul><li>Email address: %s</li><li>'
                        . 'Display name:  %s</li><li>Assigned User Roles:<ul>',
                    $user['name'],
                    $user['email'],
                    $user['displayName']
                );

                // Iterate over and print user's assigned user roles.
                foreach ($user['assignedUserRoles'] as $userRole) {
                    if (isset($userRole['partnerId'])) {
                        printf('<li>Partner ID: %s, User Role: %s</li>',
                            $userRole['partnerId'],
                            $userRole['userRole']
                        );
                    } else if (isset($userRole['advertiserId'])) {
                        printf(
                            '<li>Advertiser ID: %s, User Role: %s</li>',
                            $userRole['advertiserId'],
                            $userRole['userRole']
                        );
                    }
                }
                print '</ul></li></ul></li>';

            }
            print '</ul>';
        } else {
            print '<p>No users returned</p>';
        }
    }
}