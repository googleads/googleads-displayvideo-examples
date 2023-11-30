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
import com.google.api.services.displayvideo.v2.DisplayVideo;
import com.google.api.services.displayvideo.v2.DisplayVideo.Advertisers.Creatives;
import com.google.api.services.displayvideo.v2.model.Asset;
import com.google.api.services.displayvideo.v2.model.AssetAssociation;
import com.google.api.services.displayvideo.v2.model.Creative;
import com.google.api.services.displayvideo.v2.model.Dimensions;
import com.google.api.services.displayvideo.v2.model.ExitEvent;
import com.google.common.collect.ImmutableList;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import com.google.displayvideo.api.samples.utils.CreativeUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * This example uploads the given image and logo assets and creates a native site creative object
 * under the given Display &amp; Video 360 advertiser.
 */
public class CreateNativeSiteCreative {

  private static class CreateNativeSiteCreativeParams extends CodeSampleParams {

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
        description = "The path to the file being uploaded and assigned as a image asset.",
        required = true)
    public String assetPath;

    @Parameter(
        names = ArgumentNames.LOGO_ASSET_PATH,
        description = "The path to the file being uploaded and assigned as a logo asset.",
        required = true)
    public String logoAssetPath;

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
        names = ArgumentNames.ADVERTISER_NAME,
        description = "The advertiser name displayed in the rendered creative.",
        required = true)
    public String advertiserName;

    @Parameter(
        names = ArgumentNames.HEADLINE,
        description = "The headline displayed in the rendered creative.",
        required = true)
    public String headline;

    @Parameter(
        names = ArgumentNames.BODY_TEXT,
        description = "The body text displayed in the rendered creative.",
        required = true)
    public String bodyText;

    @Parameter(
        names = ArgumentNames.LANDING_PAGE_URL,
        description = "The landing page URL used in the rendered creative.",
        required = true)
    public String landingPageUrl;

    @Parameter(
        names = ArgumentNames.CAPTION_URL,
        description = "The caption URL used in the rendered creative.",
        required = true)
    public String captionUrl;

    @Parameter(
        names = ArgumentNames.CALL_TO_ACTION,
        description = "The call to action displayed in the rendered creative.",
        required = true)
    public String callToAction;
  }

  public static void main(String[] args) throws Exception {
    CreateNativeSiteCreativeParams params = new CreateNativeSiteCreativeParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.displayName = "INSERT_DISPLAY_NAME_HERE";
      params.assetPath = "INSERT_ASSET_PATH_HERE";
      params.logoAssetPath = "INSERT_LOGO_ASSET_PATH_HERE";
      params.creativeHeightPixels = Integer.valueOf("INSERT_CREATIVE_HEIGHT_PIXELS_HERE");
      params.creativeWidthPixels = Integer.valueOf("INSERT_CREATIVE_WIDTH_PIXELS_HERE");
      params.advertiserName = "INSERT_ADVERTISER_NAME_HERE";
      params.headline = "INSERT_HEADLINE_HERE";
      params.bodyText = "INSERT_BODY_TEXT_HERE";
      params.landingPageUrl = "INSERT_LANDING_PAGE_URL_HERE";
      params.captionUrl = "INSERT_CAPTION_URL_HERE";
      params.callToAction = "INSERT_CALL_TO_ACTION_HERE";
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
        params.assetPath,
        params.logoAssetPath,
        params.creativeHeightPixels,
        params.creativeWidthPixels,
        params.advertiserName,
        params.headline,
        params.bodyText,
        params.landingPageUrl,
        params.captionUrl,
        params.callToAction);
  }

  public static void runExample(
      DisplayVideo service,
      long advertiserId,
      String displayName,
      String imageAssetPath,
      String logoAssetPath,
      int creativeHeightPixels,
      int creativeWidthPixels,
      String advertiserName,
      String headline,
      String bodyText,
      String landingPageUrl,
      String captionUrl,
      String callToAction)
      throws Exception {

    // Upload image asset.
    Asset imageAsset = CreativeUtils.uploadAsset(service, advertiserId, imageAssetPath);

    // Upload logo asset.
    Asset logoAsset = CreativeUtils.uploadAsset(service, advertiserId, logoAssetPath);

    // Create a creative object.
    Creative creative =
        new Creative()
            .setDisplayName(displayName)
            .setEntityStatus("ENTITY_STATUS_ACTIVE")
            .setHostingSource("HOSTING_SOURCE_HOSTED")
            .setCreativeType("CREATIVE_TYPE_NATIVE");

    // Create and add the dimensions object.
    Dimensions dimensions =
        new Dimensions().setHeightPixels(creativeHeightPixels).setWidthPixels(creativeWidthPixels);
    creative.setDimensions(dimensions);

    // Create list for associated assets.
    List<AssetAssociation> assetAssociations = new ArrayList<AssetAssociation>();

    // Assign the image asset to a role.
    AssetAssociation mainImageAssetAssociation =
        new AssetAssociation()
            .setAsset(new Asset().setMediaId(imageAsset.getMediaId()))
            .setRole("ASSET_ROLE_MAIN");
    assetAssociations.add(mainImageAssetAssociation);

    // Assign the logo asset to a role.
    AssetAssociation iconAssetAssociation =
        new AssetAssociation()
            .setAsset(new Asset().setMediaId(logoAsset.getMediaId()))
            .setRole("ASSET_ROLE_ICON");
    assetAssociations.add(iconAssetAssociation);

    // Create and assign advertiser name asset.
    Asset advertiserNameAsset = new Asset().setContent(advertiserName);
    AssetAssociation advertiserNameAssetAssociation =
        new AssetAssociation().setAsset(advertiserNameAsset).setRole("ASSET_ROLE_ADVERTISER_NAME");
    assetAssociations.add(advertiserNameAssetAssociation);

    // Create and assign headline asset.
    Asset headlineAsset = new Asset().setContent(headline);
    AssetAssociation headlineAssetAssociation =
        new AssetAssociation().setAsset(headlineAsset).setRole("ASSET_ROLE_HEADLINE");
    assetAssociations.add(headlineAssetAssociation);

    // Create and assign body text asset.
    Asset bodyTextAsset = new Asset().setContent(bodyText);
    AssetAssociation bodyTextAssetAssociation =
        new AssetAssociation().setAsset(bodyTextAsset).setRole("ASSET_ROLE_BODY");
    assetAssociations.add(bodyTextAssetAssociation);

    // Create and assign caption URL asset.
    Asset captionUrlAsset = new Asset().setContent(captionUrl);
    AssetAssociation captionUrlAssetAssociation =
        new AssetAssociation().setAsset(captionUrlAsset).setRole("ASSET_ROLE_CAPTION_URL");
    assetAssociations.add(captionUrlAssetAssociation);

    // Create and assign call to action asset.
    Asset callToActionAsset = new Asset().setContent(callToAction);
    AssetAssociation callToActionAssetAssociation =
        new AssetAssociation().setAsset(callToActionAsset).setRole("ASSET_ROLE_CALL_TO_ACTION");
    assetAssociations.add(callToActionAssetAssociation);

    // Create and set the list of creative assets.
    creative.setAssets(assetAssociations);

    // Create an exit event.
    ExitEvent exitEvent = new ExitEvent().setType("EXIT_EVENT_TYPE_DEFAULT").setUrl(landingPageUrl);

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
