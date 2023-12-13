// Copyright 2023 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.displayvideo.api.samples;

import com.beust.jcommander.Parameter;
import com.google.api.services.displayvideo.v3.DisplayVideo;
import com.google.api.services.displayvideo.v3.DisplayVideo.Users;
import com.google.api.services.displayvideo.v3.DisplayVideoScopes;
import com.google.api.services.displayvideo.v3.model.AssignedUserRole;
import com.google.api.services.displayvideo.v3.model.BulkEditAssignedUserRolesRequest;
import com.google.api.services.displayvideo.v3.model.BulkEditAssignedUserRolesResponse;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This example edits the access roles of a Display &amp; Video 360 user in bulk.
 *
 * <p>This example makes requests to the Display &amp; Video 360 API Users service that require
 * authentication via service account. Requests made not using a service account will return an
 * error.
 */
public class EditUserAccess {

  private static class EditUserAccessParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.USER_ID,
        description = "The ID of the user whose access will be edited.",
        required = true)
    public Long userId;

    @Parameter(
        names = ArgumentNames.REMOVE_PARTNER_ID,
        description =
            "The partner IDs to remove the user's access from. Multiple instances may be included"
                + " in the command.")
    public List<String> removePartnerIds;

    @Parameter(
        names = ArgumentNames.REMOVE_ADVERTISER_ID,
        description =
            "The advertiser IDs to remove the user's access from. Multiple instances may be"
                + " included in the command.")
    public List<String> removeAdvertiserIds;

    @Parameter(
        names = ArgumentNames.ADD_PARTNER_ROLE,
        description =
            "A partner ID and user role, separated by a semicolon, to assign to the user to grant"
                + " the appropriate access to the partner. Multiple instances may be included in"
                + "  the command. Ex: \"--partnerRole 123;ADMIN\".")
    public List<String> addPartnerRoles;

    @Parameter(
        names = ArgumentNames.ADD_ADVERTISER_ROLE,
        description =
            "An advertiser ID and user role, separated by a semicolon, to assign to the user to"
                + " grant the appropriate access to the advertiser. Multiple instances may be"
                + " included in the command. Ex: \"--advertiserRole 456;STANDARD\".")
    public List<String> addAdvertiserRoles;
  }

  public static void main(String[] args) throws Exception {
    EditUserAccessParams params = new EditUserAccessParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.userId = Long.valueOf("INSERT_USER_ID_HERE");
      params.removePartnerIds = Arrays.asList("INSERT_REMOVE_PARTNER_IDS_HERE".split(","));
      params.removeAdvertiserIds = Arrays.asList("INSERT_REMOVE_ADVERTISER_IDS_HERE".split(","));
      params.addPartnerRoles = Arrays.asList("INSERT_ADD_PARTNER_ROLES_HERE".split(","));
      params.addAdvertiserRoles = Arrays.asList("INSERT_ADD_ADVERTISER_ROLES_HERE".split(","));
    }

    System.out.println(
        "This function requires the use of a service account and an additional Users service"
            + " scope. These configurations will be applied regardless of specified flags.");

    // Adding user service scope to passed parameter of additional scopes.
    List<String> additionalScopes = new ArrayList<>();
    if (params.additionalScopes != null && !params.additionalScopes.isEmpty()) {
      additionalScopes = params.additionalScopes;
    }
    additionalScopes.add(DisplayVideoScopes.DISPLAY_VIDEO_USER_MANAGEMENT);

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile, true, params.serviceAccountKeyFile, additionalScopes);

    runExample(
        service,
        params.userId,
        params.removePartnerIds,
        params.removeAdvertiserIds,
        params.addPartnerRoles,
        params.addAdvertiserRoles);
  }

  public static void runExample(
      DisplayVideo service,
      long userId,
      List<String> removePartnerIds,
      List<String> removeAdvertiserIds,
      List<String> addPartnerRoles,
      List<String> addAdvertiserRoles)
      throws Exception {

    // Create a bulk edit request.
    BulkEditAssignedUserRolesRequest requestContent = new BulkEditAssignedUserRolesRequest();

    // Build user roles to grant to the user.
    List<AssignedUserRole> addedUserRoles = new ArrayList<>();
    if (addPartnerRoles != null) {
      for (String partnerRole : addPartnerRoles) {
        // Parse the given partner role argument into the partner ID and role.
        List<String> partnerAndRole = Arrays.asList(partnerRole.split(";"));

        // Check to make sure that the parsed role argument is formatted correctly.
        if (partnerAndRole.size() != 2) {
          System.out.printf(
              "Given partner role argument is not formatted correctly and not assigned to the"
                  + " created user: %s %n",
              partnerRole);
          continue;
        }

        // Create new partner user role and add to list.
        AssignedUserRole newPartnerRole =
            new AssignedUserRole()
                .setPartnerId(Long.valueOf(partnerAndRole.get(0)))
                .setUserRole(partnerAndRole.get(1));
        addedUserRoles.add(newPartnerRole);
      }
    }
    if (addAdvertiserRoles != null) {
      for (String advertiserRole : addAdvertiserRoles) {
        // Parse the given advertiser role argument into the advertiser ID and role.
        List<String> advertiserAndRole = Arrays.asList(advertiserRole.split(";"));

        // Check to make sure that the parsed role argument is formatted correctly.
        if (advertiserAndRole.size() != 2) {
          System.out.printf(
              "Given advertiser role argument is not formatted correctly and not assigned to the"
                  + " created user: %s %n",
              advertiserRole);
          continue;
        }

        // Create new advertiser user role and add to list.
        AssignedUserRole newAdvertiserRole =
            new AssignedUserRole()
                .setAdvertiserId(Long.valueOf(advertiserAndRole.get(0)))
                .setUserRole(advertiserAndRole.get(1));
        addedUserRoles.add(newAdvertiserRole);
      }
    }

    // Add list of user roles to create to bulk edit request.
    requestContent.setCreatedAssignedUserRoles(addedUserRoles);

    // Build list of resources whose access to remove from user.
    List<String> deletedResourceIds = new ArrayList<>();
    if (removePartnerIds != null) {
      for (String partnerId : removePartnerIds) {
        deletedResourceIds.add("partner-" + partnerId);
      }
    }
    if (removeAdvertiserIds != null) {
      for (String advertiserId : removeAdvertiserIds) {
        deletedResourceIds.add("advertiser-" + advertiserId);
      }
    }

    // Add list of resources whose access to remove to bulk edit request.
    requestContent.setDeletedAssignedUserRoles(deletedResourceIds);

    // Configure the bulk list request.
    Users.BulkEditAssignedUserRoles request =
        service.users().bulkEditAssignedUserRoles(userId, requestContent);

    // Execute bulk edit request.
    BulkEditAssignedUserRolesResponse response = request.execute();

    // Check if response is empty.
    // If not, iterate over created assigned user roles.
    if (response.isEmpty()) {
      System.out.print("Bulk edit request created no new assigned user roles");
    } else {
      for (AssignedUserRole assignedUserRole : response.getCreatedAssignedUserRoles()) {
        System.out.printf(
            "Assigned user role %s was created%n", assignedUserRole.getAssignedUserRoleId());
      }
    }
  }
}
