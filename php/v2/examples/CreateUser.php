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
 * This example creates a Display & Video 360 user.
 *
 * This example makes requests to the Display & Video 360 API Users service
 * that require authentication via service account. Requests made not using a
 * service account will return an error.
 */
class CreateUser extends BaseExample
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
                'display' => 'Email Address',
                'required' => true
            ),
            array(
                'name' => 'display_name',
                'display' => 'Display Name',
                'required' => true
            ),
            array(
                'name' => 'partner_roles',
                'display' => 'List of partner ID and user role pairings. '
                    . 'Pairings split by semicolons, entries '
                    . 'comma-separated (Ex: 123;ADMIN,456;ADMIN)',
                'required' => false
            ),
            array(
                'name' => 'advertiser_roles',
                'display' => 'List of advertiser ID and user role pairings. '
                    . 'Pairings split by semicolons, entries '
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

        $emailAddress = $values['email_address'];
        $displayName = $values['display_name'];
        $partnerRoles = array();
        $advertiserRoles = array();

        if (!empty($values['partner_roles'])) {
            $partnerRoles = explode(",", $values['partner_roles']);
        }
        if (!empty($values['advertiser_roles'])) {
            $advertiserRoles = explode(",", $values['advertiser_roles']);
        }

        // Create the user object.
        $user = new Google_Service_DisplayVideo_User();
        $user->setEmail($emailAddress);
        $user->setDisplayName($displayName);

        // Build the user roles from the given arguments.
        $assignedUserRoles = array();
        foreach ($partnerRoles as $partnerRole) {
            $partnerAndRole = explode(";", $partnerRole);
            if (count($partnerAndRole) == 2) {
                $assignedUserRole =
                    new Google_Service_DisplayVideo_AssignedUserRole();
                $assignedUserRole->setPartnerId($partnerAndRole[0]);
                $assignedUserRole->setUserRole($partnerAndRole[1]);
                $assignedUserRoles[] = $assignedUserRole;
            } else {
                printf(
                    '<p>Given partner role was not formatted correctly and '
                        . 'not assigned to the created user: %s</p>',
                    $partnerRole);
            }
        }
        foreach ($advertiserRoles as $advertiserRole) {
            $advertiserAndRole = explode(";", $advertiserRole);
            if (count($advertiserAndRole) == 2) {
                $assignedUserRole =
                    new Google_Service_DisplayVideo_AssignedUserRole();
                $assignedUserRole->setAdvertiserId($advertiserAndRole[0]);
                $assignedUserRole->setUserRole($advertiserAndRole[1]);
                $assignedUserRoles[] = $assignedUserRole;
            } else {
                printf(
                    '<p>Given advertiser role was not formatted correctly and '
                        . 'not assigned to the created user: %s</p>',
                    $advertiserRole);
            }
        }

        // Set the assigned user roles for the user object.
        $user->setAssignedUserRoles($assignedUserRoles);

        // Call the API, creating the user.
        try {
            $result = $this->service->users->create($user);
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        // Display the created user.
        printf('<p>User %s was created.</p>', $result['name']);
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public static function getName(): string
    {
        return 'Create User (Requires service account)';
    }
}
