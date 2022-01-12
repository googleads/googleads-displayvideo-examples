// Copyright 2021 Google LLC
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
import com.google.api.client.util.Strings;
import com.google.api.services.displayvideo.v1.DisplayVideo;
import com.google.api.services.displayvideo.v1.DisplayVideo.Users;
import com.google.api.services.displayvideo.v1.DisplayVideoScopes;
import com.google.api.services.displayvideo.v1.model.AssignedUserRole;
import com.google.api.services.displayvideo.v1.model.ListUsersResponse;
import com.google.api.services.displayvideo.v1.model.User;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import java.util.ArrayList;
import java.util.List;

/**
 * This example retrieves accessible Display &amp; Video 360 users based on given filter values.
 *
 * This example makes requests to the Display &amp; Video 360 API Users service that require
 * authentication via service account. Requests made not using a service account will return
 * an error.
 */
public class RetrieveUsers {

  private static class RetrieveUsersParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.EMAIL_ADDRESS,
        description = "String that the email address of retrieved users must contain.")
    public String emailAddress;

    @Parameter(
        names = ArgumentNames.DISPLAY_NAME,
        description = "String that the display name of retrieved users must contain.")
    public String displayName;

    @Parameter(
        names = ArgumentNames.USER_ROLE,
        description = "User role that the retrieved users must have assigned.")
    public String userRole;

    @Parameter(
        names = ArgumentNames.HAS_PARTNER_ROLE,
        description = "Whether the retrieved users must have an assigned user role for a partner.")
    public boolean hasPartnerRole;

    @Parameter(
        names = ArgumentNames.HAS_ADVERTISER_ROLE,
        description =
            "Whether the retrieved users must have an assigned user role for an advertiser.")
    public boolean hasAdvertiserRole;

    @Parameter(
        names = ArgumentNames.PARTNER_ID,
        description = "ID for partner that retrieved users must have a user role assigned for.")
    public String partnerId;

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "ID for advertiser that retrieved users must have a user role assigned for.")
    public String advertiserId;

    @Parameter(
        names = ArgumentNames.PARENT_PARTNER_ID,
        description =
            "ID for parent partner of advertisers that retrieved users must have a user role"
                + " assigned for.")
    public String parentPartnerId;
  }

  public static void main(String[] args) throws Exception {
    RetrieveUsersParams params = new RetrieveUsersParams();

    if (!params.parseArguments(args)) {
      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.hasPartnerRole = false;
      params.hasAdvertiserRole = false;
    }

    System.out.println(
        "This function requires the use of a service account and an additional Users service"
            + " scope. These configurations will be applied regardless of specified flags.");

    // Adding user service scope to passed parameter of additional scopes.
    List<String> additionalScopes = new ArrayList<String>();
    if (params.additionalScopes != null && !params.additionalScopes.isEmpty()) {
      additionalScopes = params.additionalScopes;
    }
    additionalScopes.add(DisplayVideoScopes.DISPLAY_VIDEO_USER_MANAGEMENT);

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile, true, params.serviceAccountKeyFile, additionalScopes);

    runExample(
        service,
        params.emailAddress,
        params.displayName,
        params.userRole,
        params.hasPartnerRole,
        params.hasAdvertiserRole,
        params.partnerId,
        params.advertiserId,
        params.parentPartnerId);
  }

  public static void runExample(
      DisplayVideo service,
      String emailAddress,
      String displayName,
      String userRole,
      boolean hasPartnerRole,
      boolean hasAdvertiserRole,
      String partnerId,
      String advertiserId,
      String parentPartnerId)
      throws Exception {

    // Create list to store the built filters.
    ArrayList<String> filters = new ArrayList<String>();

    // Build filters with given arguments.
    if (emailAddress != null) {
      filters.add("email:\"" + emailAddress + "\"");
    }
    if (displayName != null) {
      filters.add("displayName:\"" + displayName + "\"");
    }
    if (userRole != null) {
      filters.add("assignedUserRole.userRole=\"" + userRole + "\"");
    }
    if (hasPartnerRole) {
      filters.add("assignedUserRole.entityType=\"PARTNER\"");
    }
    if (hasAdvertiserRole) {
      filters.add("assignedUserRole.entityType=\"ADVERTISER\"");
    }
    if (partnerId != null) {
      filters.add("assignedUserRole.partnerId=\"" + partnerId + "\"");
    }
    if (advertiserId != null) {
      filters.add("assignedUserRole.advertiserId=\"" + advertiserId + "\"");
    }
    if (parentPartnerId != null) {
      filters.add("assignedUserRole.parentPartnerId=\"" + parentPartnerId + "\"");
    }

    // Build full filter string out of filter list.
    String filterStr = String.join(" AND ", filters);

    // Configure the list request.
    Users.List request = service.users().list().setFilter(filterStr);

    // Create the response and nextPageToken variables.
    ListUsersResponse response;
    String nextPageToken = null;

    do {
      // Create and execute the list request.
      response = request.setPageToken(nextPageToken).execute();

      // Check if response is empty.
      if (response.isEmpty()) {
        System.out.print("List request returned no Users");
        break;
      }

      // Iterate over retrieved users.
      for (User user : response.getUsers()) {
        // Print general information about user.
        System.out.printf(
            "User ID: %s, Display name: %s, Email: %s%n",
            user.getUserId(), user.getDisplayName(), user.getEmail());

        // Iterate over and print user's assigned user roles.
        for (AssignedUserRole retrievedRole : user.getAssignedUserRoles()) {
          if (retrievedRole.getPartnerId() != null) {
            System.out.printf(
                "\tPartner ID: %s, Role: %s%n",
                retrievedRole.getPartnerId(), retrievedRole.getUserRole());
          } else if (retrievedRole.getAdvertiserId() != null) {
            System.out.printf(
                "\tAdvertiser ID: %s, Role: %s%n",
                retrievedRole.getAdvertiserId(), retrievedRole.getUserRole());
          }
        }
      }

      // Update the next page token.
      nextPageToken = response.getNextPageToken();
    } while (!Strings.isNullOrEmpty(nextPageToken));
  }
}
