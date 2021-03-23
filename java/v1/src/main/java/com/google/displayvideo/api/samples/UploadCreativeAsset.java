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
import com.google.api.services.displayvideo.v1.model.Asset;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import com.google.displayvideo.api.samples.utils.CreativeUtils;

/** This example uploads the given asset under the given Display &amp; Video 360 advertiser. */
public class UploadCreativeAsset {

  private static class UploadCreativeAssetParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the advertiser to upload the creative asset for.")
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.ASSET_PATH,
        description = "The path to the file being uploaded.")
    public String assetPath;
  }

  public static void main(String[] args) throws Exception {
    UploadCreativeAssetParams params = new UploadCreativeAssetParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.assetPath = "INSERT_ASSET_PATH_HERE";
    }

    DisplayVideo service = DisplayVideoFactory.getInstance(params.clientSecretsFile);

    runExample(service, params.advertiserId, params.assetPath);
  }

  public static void runExample(DisplayVideo service, long advertiserId, String assetPath)
      throws Exception {

    // Upload the asset and get the new asset object.
    Asset asset = CreativeUtils.uploadAsset(service, advertiserId, assetPath);

    // Display the new asset media ID.
    System.out.printf("The asset has been uploaded: %s", asset.getMediaId());
  }
}
