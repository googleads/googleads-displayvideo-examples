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
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.displayvideo.v2.DisplayVideo;
import com.google.api.services.displayvideo.v2.DisplayVideo.Media;
import com.google.api.services.displayvideo.v2.model.CustomBiddingScript;
import com.google.api.services.displayvideo.v2.model.CustomBiddingScriptRef;
import com.google.api.services.displayvideo.v2.model.GoogleBytestreamMedia;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import java.io.FileInputStream;

/**
 * This example uploads a script file and creates a script resource for the custom bidding
 * algorithm.
 */
public class UploadCustomBiddingScript {

  private static class UploadCustomBiddingScriptParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the advertiser that owns the custom bidding algorithm.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.CUSTOM_BIDDING_ALGORITHM_ID,
        description = "The ID of the custom bidding algorithm to upload a script for.",
        required = true)
    public Long customBiddingAlgorithmId;

    @Parameter(
        names = ArgumentNames.SCRIPT_PATH,
        description = "The path to the script file being uploaded.",
        required = true)
    public String scriptPath;
  }

  public static void main(String[] args) throws Exception {
    UploadCustomBiddingScriptParams params = new UploadCustomBiddingScriptParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.customBiddingAlgorithmId = Long.valueOf("INSERT_CUSTOM_BIDDING_ALGORITHM_ID_HERE");
      params.scriptPath = "INSERT_SCRIPT_PATH_HERE";
    }

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile,
            params.useServiceAccount,
            params.serviceAccountKeyFile,
            params.additionalScopes);

    runExample(service, params.advertiserId, params.customBiddingAlgorithmId, params.scriptPath);
  }

  public static void runExample(
      DisplayVideo service, long advertiserId, long customBiddingAlgorithmId, String scriptPath)
      throws Exception {

    // Retrieve a usable custom bidding script reference.
    CustomBiddingScriptRef scriptRef =
        service
            .customBiddingAlgorithms()
            .uploadScript(customBiddingAlgorithmId)
            .setAdvertiserId(advertiserId)
            .execute();

    // Display the custom bidding script reference resource path.
    System.out.printf(
        "The script can be uploaded to the following resource path: %s%n",
        scriptRef.getResourceName());

    // Create media object.
    GoogleBytestreamMedia media = new GoogleBytestreamMedia();
    media.setResourceName(scriptRef.getResourceName());

    // Create input stream for the script file.
    InputStreamContent scriptFileStream =
        new InputStreamContent(null, new FileInputStream(scriptPath));

    // Create media.upload request.
    Media.Upload uploadRequest =
        service.media().upload(scriptRef.getResourceName(), media, scriptFileStream);

    // Retrieve uploader from the request and set it to us a simple
    // upload request.
    MediaHttpUploader uploader = uploadRequest.getMediaHttpUploader();
    uploader.setDirectUploadEnabled(true);

    // Execute the upload using an Upload URL with the destination resource name.
    uploader.upload(
        new GenericUrl(
            "https://displayvideo.googleapis.com/upload/media/" + scriptRef.getResourceName()));

    // Create the custom bidding script structure.
    CustomBiddingScript customBiddingScript = new CustomBiddingScript().setScript(scriptRef);

    // Create the custom bidding script.
    CustomBiddingScript response =
        service
            .customBiddingAlgorithms()
            .scripts()
            .create(customBiddingAlgorithmId, customBiddingScript)
            .setAdvertiserId(advertiserId)
            .execute();

    // Display the new script resource name.
    System.out.printf("The following script was created: %s%n", response.getName());
  }
}
