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
import com.google.api.services.displayvideo.v1.DisplayVideo;
import com.google.api.services.displayvideo.v1.DisplayVideo.Advertisers.Creatives;
import com.google.api.services.displayvideo.v1.model.Asset;
import com.google.api.services.displayvideo.v1.model.AssetAssociation;
import com.google.api.services.displayvideo.v1.model.Creative;
import com.google.api.services.displayvideo.v1.model.ExitEvent;
import com.google.common.collect.ImmutableList;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import com.google.displayvideo.api.samples.utils.CreativeUtils;

/**
 * This example uploads the given video asset and creates a video creative object under the given
 * Display &amp; Video 360 advertiser.
 */
public class CreateVideoCreative {

  private static class CreateVideoCreativeParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the parent advertiser of the creative to be created.",
        required = true)
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.DISPLAY_NAME,
        description = "The display name of the creative to be created.",
        required = true)
    public String displayName;

    @Parameter(
        names = ArgumentNames.VIDEO_ASSET_PATH,
        description = "The path to the file being uploaded and assigned as a video asset.",
        required = true)
    public String videoAssetPath;

    @Parameter(
        names = ArgumentNames.EXIT_EVENT_NAME,
        description = "The name of the main exit event.",
        required = true)
    public String exitEventName;

    @Parameter(
        names = ArgumentNames.EXIT_EVENT_URL,
        description = "The URL of the main exit event.",
        required = true)
    public String exitEventUrl;
  }

  public static void main(String[] args) throws Exception {
    CreateVideoCreativeParams params = new CreateVideoCreativeParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.displayName = "INSERT_DISPLAY_NAME_HERE";
      params.videoAssetPath = "INSERT_VIDEO_ASSET_PATH_HERE";
      params.exitEventName = "INSERT_EXIT_EVENT_NAME_HERE";
      params.exitEventUrl = "INSERT_EXIT_EVENT_URL_HERE";
    }

    DisplayVideo service =
        DisplayVideoFactory.getInstance(
            params.clientSecretsFile,
            params.useServiceAccount,
            params.serviceAccountKeyFile,
            params.additionalScopes);

    runExample(
        service,
        params.advertiserId,
        params.displayName,
        params.videoAssetPath,
        params.exitEventName,
        params.exitEventUrl);
  }

  public static void runExample(
      DisplayVideo service,
      long advertiserId,
      String displayName,
      String videoAssetPath,
      String exitEventName,
      String exitEventUrl)
      throws Exception {

    // Upload video asset.
    Asset videoAsset = CreativeUtils.uploadAsset(service, advertiserId, videoAssetPath);

    // Create a creative object.
    Creative creative =
        new Creative()
            .setDisplayName(displayName)
            .setEntityStatus("ENTITY_STATUS_ACTIVE")
            .setHostingSource("HOSTING_SOURCE_HOSTED")
            .setCreativeType("CREATIVE_TYPE_VIDEO");

    // Assign the video asset to a role.
    AssetAssociation assetAssociation =
        new AssetAssociation()
            .setAsset(new Asset().setMediaId(videoAsset.getMediaId()))
            .setRole("ASSET_ROLE_MAIN");

    // Create and set the list of creative assets.
    creative.setAssets(ImmutableList.of(assetAssociation));

    // Create an exit event.
    ExitEvent exitEvent =
        new ExitEvent()
            .setName(exitEventName)
            .setType("EXIT_EVENT_TYPE_DEFAULT")
            .setUrl(exitEventUrl);

    // Create and set the list of exit events for the creative.
    creative.setExitEvents(ImmutableList.of(exitEvent));

    // Configure the create request.
    Creatives.Create request = service.advertisers().creatives().create(advertiserId, creative);

    // Send the request.
    Creative response = request.execute();

    // Display the new creative.
    System.out.printf("Creative %s was created.%n", response.getName());
  }
}
