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
import com.google.api.services.displayvideo.v4.DisplayVideo;
import com.google.api.services.displayvideo.v4.DisplayVideo.Users;
import com.google.api.services.displayvideo.v4.DisplayVideoScopes;
import com.google.api.services.displayvideo.v4.model.AssignedUserRole;
import com.google.api.services.displayvideo.v4.model.User;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This example creates a Display &amp; Video 360 user.
 *
 * <p>This example makes requests to the Display &amp; Video 360 API Users service that require
 * authentication via service account. Requests made not using a service account will return an
 * error.
 */
public class CreateUser {

  private static class CreateUserParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.EMAIL_ADDRESS,
        description = "The email address of the user to be created.",
        required = true)
    public String emailAddress;

    @Parameter(
        names = ArgumentNames.DISPLAY_NAME,
        description = "The display name of the user to be created.",
        required = true)
    public String displayName;

    @Parameter(
        names = ArgumentNames.PARTNER_ROLE,
        description =
            "A partner ID and user role, separated by a semicolon, to assign to the user to grant"
                + " the appropriate access to the partner. Multiple instances may included in the"
                + " command. Ex: \"--partnerRole 123;ADMIN\".",
        required = false)
    public List<String> partnerRoles;

    @Parameter(
        names = ArgumentNames.ADVERTISER_ROLE,
        description =
            "An advertiser ID and user role, separated by a semicolon, to assign to the user to"
                + " grant the appropriate access to the advertiser. Multiple instances may"
                + " included in the command. Ex: \"--advertiserRole 456;STANDARD\".",
        required = false)
    public List<String> advertiserRoles;
  }

  public static void main(String[] args) throws Exception {
    CreateUserParams params = new CreateUserParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.emailAddress = "INSERT_EMAIL_ADDRESS_HERE";
      params.displayName = "INSERT_DISPLAY_NAME_HERE";
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
        params.emailAddress,
        params.displayName,
        params.partnerRoles,
        params.advertiserRoles);
  }

  public static void runExample(
      DisplayVideo service,
      String emailAddress,
      String displayName,
      List<String> partnerRoles,
      List<String> advertiserRoles)
      throws Exception {

    // Create the user structure.
    User user = new User().setEmail(emailAddress).setDisplayName(displayName);

    // Instantiate and build list of assigned user roles for the new user.
    List<AssignedUserRole> userRoles = new ArrayList<>();
    if (partnerRoles != null) {
      // Iterate over given partner roles.
      for (String partnerRole : partnerRoles) {
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

        // Create new role and add to list.
        AssignedUserRole newPartnerRole =
            new AssignedUserRole()
                .setPartnerId(Long.valueOf(partnerAndRole.get(0)))
                .setUserRole(partnerAndRole.get(1));
        userRoles.add(newPartnerRole);
      }
    }
    if (advertiserRoles != null) {
      // Iterate over given advertiser roles.
      for (String advertiserRole : advertiserRoles) {
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

        // Create new role and add to list.
        AssignedUserRole newAdvertiserRole =
            new AssignedUserRole()
                .setAdvertiserId(Long.valueOf(advertiserAndRole.get(0)))
                .setUserRole(advertiserAndRole.get(1));
        userRoles.add(newAdvertiserRole);
      }
    }

    // Add the assigned user roles to the user.
    user.setAssignedUserRoles(userRoles);

    // Configure the create request.
    Users.Create request = service.users().create(user);

    // Create the user.
    User response = request.execute();

    // Display the new user.
    System.out.printf("User '%s' was created.%n", response.getName());
  }
}
