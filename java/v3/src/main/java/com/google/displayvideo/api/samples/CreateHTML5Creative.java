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
import com.google.api.services.displayvideo.v3.DisplayVideo.Advertisers.Creatives;
import com.google.api.services.displayvideo.v3.model.Asset;
import com.google.api.services.displayvideo.v3.model.AssetAssociation;
import com.google.api.services.displayvideo.v3.model.Creative;
import com.google.api.services.displayvideo.v3.model.Dimensions;
import com.google.api.services.displayvideo.v3.model.ExitEvent;
import com.google.common.collect.ImmutableList;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import com.google.displayvideo.api.samples.utils.CreativeUtils;

/**
 * This example uploads the given HTML asset and creates an HTML5 creative object under the given
 * Display &amp; Video 360 advertiser.
 */
public class CreateHTML5Creative {

  private static class CreateHTML5CreativeParams extends CodeSampleParams {

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
        names = ArgumentNames.ASSET_PATH,
        description = "The path to the file being uploaded and assigned as an HTML asset.",
        required = true)
    public String assetPath;

    @Parameter(
        names = ArgumentNames.CREATIVE_HEIGHT_PIXELS,
        description = "The height of the creative asset in pixels.",
        required = true)
    public Integer creativeHeightPixels;

    @Parameter(
        names = ArgumentNames.CREATIVE_WIDTH_PIXELS,
        description = "The width of the creative asset in pixels.",
        required = true)
    public Integer creativeWidthPixels;

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
    CreateHTML5CreativeParams params = new CreateHTML5CreativeParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.displayName = "INSERT_DISPLAY_NAME_HERE";
      params.assetPath = "INSERT_ASSET_PATH_HERE";
      params.creativeHeightPixels = Integer.valueOf("INSERT_CREATIVE_HEIGHT_PIXELS_HERE");
      params.creativeWidthPixels = Integer.valueOf("INSERT_CREATIVE_WIDTH_PIXELS_HERE");
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
        params.assetPath,
        params.advertiserId,
        params.displayName,
        params.creativeHeightPixels,
        params.creativeWidthPixels,
        params.exitEventName,
        params.exitEventUrl);
  }

  public static void runExample(
      DisplayVideo service,
      String htmlAssetPath,
      long advertiserId,
      String displayName,
      int creativeHeightPixels,
      int creativeWidthPixels,
      String exitEventName,
      String exitEventUrl)
      throws Exception {

    // Upload HTML asset.
    Asset htmlAsset = CreativeUtils.uploadAsset(service, advertiserId, htmlAssetPath);

    // Create the creative structure.
    Creative creative =
        new Creative()
            .setDisplayName(displayName)
            .setEntityStatus("ENTITY_STATUS_ACTIVE")
            .setHostingSource("HOSTING_SOURCE_HOSTED")
            .setCreativeType("CREATIVE_TYPE_STANDARD");

    // Create and add the dimensions object.
    Dimensions dimensions =
        new Dimensions().setHeightPixels(creativeHeightPixels).setWidthPixels(creativeWidthPixels);
    creative.setDimensions(dimensions);

    // Assign the HTML5 asset to a role.
    AssetAssociation assetAssociation =
        new AssetAssociation()
            .setAsset(new Asset().setMediaId(htmlAsset.getMediaId()))
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
