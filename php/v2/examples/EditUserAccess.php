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
 * This example edits the access roles of a Display & Video 360 user in bulk.
 *
 * This example makes requests to the Display & Video 360 API Users service
 * that require authentication via service account. Requests made not using a
 * service account will return an error.
 */
class EditUserAccess extends BaseExample
{
    /**
     * (non-PHPdoc)
     * @see BaseExample::getInputParameters()
     */
    protected function getInputParameters(): array
    {
        return array(
            array(
                'name' => 'user_id',
                'display' => 'User ID',
                'required' => true
            ),
            array(
                'name' => 'remove_partner_ids',
                'display' => 'Partner IDs to remove access to '
                    . '(comma-separated)',
                'required' => false
            ),
            array(
                'name' => 'remove_advertiser_ids',
                'display' => 'Advertiser IDs to remove access to '
                    . '(comma-separated)',
                'required' => false
            ),
            array(
                'name' => 'add_partner_roles',
                'display' => 'List of partner ID and user role pairings to '
                    . 'add to the user. Pairings split by semicolons, '
                    . 'entries comma-separated (Ex: 123;ADMIN,456;ADMIN)',
                'required' => false
            ),
            array(
                'name' => 'add_advertiser_roles',
                'display' => 'List of advertiser ID and user role pairings to '
                    . 'add to the user. Pairings split by semicolons, entries '
                    . 'comma-separated (Ex: 123;STANDARD,456;READ_ONLY)',
                'required' => false
            )
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::run()
     */
    public function run()
    {
        $values = $this->formValues;
        $userId = $values['user_id'];
        $removePartnerIds = array();
        $removeAdvertiserIds = array();
        $addPartnerRoles = array();
        $addAdvertiserRoles = array();

        if (!empty($values['remove_partner_ids'])) {
            $removePartnerIds = explode(",", $values['remove_partner_ids']);
        }
        if (!empty($values['remove_advertiser_ids'])) {
            $removeAdvertiserIds =
                explode(",", $values['remove_advertiser_ids']);
        }
        if (!empty($values['add_partner_roles'])) {
            $addPartnerRoles = explode(",", $values['add_partner_roles']);
        }
        if (!empty($values['add_advertiser_roles'])) {
            $addAdvertiserRoles =
                explode(",", $values['add_advertiser_roles']);
        }

        // Create list to store the roles to add to user.
        $createdAssignedUserRoles = array();

        // Create assigned user roles from given arguments and add to list of
        // user roles to add to the user.
        foreach ($addPartnerRoles as $partnerRole) {
            $partnerAndRole = explode(";", $partnerRole);
            if (count($partnerAndRole) == 2) {
                $assignedUserRole =
                    new Google_Service_DisplayVideo_AssignedUserRole();
                $assignedUserRole->setPartnerId($partnerAndRole[0]);
                $assignedUserRole->setUserRole($partnerAndRole[1]);
                $createdAssignedUserRoles[] = $assignedUserRole;
            } else {
                printf(
                    '<p>Given partner role was not formatted correctly and '
                        . 'not assigned to the created user: %s</p>',
                    $partnerRole);
            }
        }
        foreach ($addAdvertiserRoles as $advertiserRole) {
            $advertiserAndRole = explode(";", $advertiserRole);
            if (count($advertiserAndRole) == 2) {
                $assignedUserRole =
                    new Google_Service_DisplayVideo_AssignedUserRole();
                $assignedUserRole->setAdvertiserId($advertiserAndRole[0]);
                $assignedUserRole->setUserRole($advertiserAndRole[1]);
                $createdAssignedUserRoles[] = $assignedUserRole;
            } else {
                printf(
                    '<p>Given advertiser role was not formatted correctly and '
                        . 'not assigned to the created user: %s</p>',
                    $advertiserRole);
            }
        }

        // Create list to store the resource IDs to remove user access from.
        $deletedResourceIds = array();

        // Build IDs of assigned user roles to remove.
        // Assigned user role IDs are formatted as the entity type and
        // entity ID separated by a dash.
        foreach ($removePartnerIds as $partnerId) {
            $deletedResourceIds[] = 'partner-' . $partnerId;
        }
        foreach ($removeAdvertiserIds as $advertiserId) {
            $deletedResourceIds[] = 'advertiser-' . $advertiserId;
        }

        // Create and configure the bulk edit request body.
        $body =
            new Google_Service_DisplayVideo_BulkEditAssignedUserRolesRequest();
        $body->setCreatedAssignedUserRoles($createdAssignedUserRoles);
        $body->setDeletedAssignedUserRoles($deletedResourceIds);

        // Call the API, editing the assigned user roles for the identified
        // user.
        try {
            $response = $this
                ->service
                ->users
                ->bulkEditAssignedUserRoles(
                    $userId,
                    $body
                );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        // Print the created assigned user roles.
        $this->printCreatedAssignedUserRoles(
            $userId,
            $response->getCreatedAssignedUserRoles()
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Edit User Access (Requires service account)';
    }

    /**
     * Prints a list of given created assigned user roles.
     * @param string $userId ID for user the created roles are assigned to.
     * @param array $assignedUserRoles created assigned user roles to be
     *     printed.
     */
    protected function printCreatedAssignedUserRoles(
        string $userId,
        array $assignedUserRoles
    ) {
        if (!empty($assignedUserRoles)) {
            printf(
                '<h2>User roles newly assigned to User %s</h2>',
                $userId
            );
            print '<ul>';
            foreach ($assignedUserRoles as $assignedUserRole) {
                if (isset($assignedUserRole['partnerId'])) {
                    printf(
                        '<li>Partner ID: %s, User Role: %s</li>',
                        $assignedUserRole['partnerId'],
                        $assignedUserRole['userRole']
                    );
                } else {
                    printf(
                        '<li>Advertiser ID: %s, User Role: %s</li>',
                        $assignedUserRole['advertiserId'],
                        $assignedUserRole['userRole']
                    );
                }
            }
            print '</ul>';
        } else {
            printf('<p>No user roles newly assigned to User %s/p>', $userId);
        }
    }
}