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

package com.google.displayvideo.api.samples.utils;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.displayvideo.v3.DisplayVideo;
import com.google.api.services.displayvideo.v3.DisplayVideo.Advertisers.Assets;
import com.google.api.services.displayvideo.v3.model.Asset;
import com.google.api.services.displayvideo.v3.model.CreateAssetRequest;
import com.google.api.services.displayvideo.v3.model.CreateAssetResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

/** This class implements creative utility methods used across samples. */
public class CreativeUtils {

  /**
   * Uploads the given file as a creative asset under the given advertiser.
   *
   * @return The created asset
   * @throws Exception If an error occurs while uploading the asset
   */
  public static Asset uploadAsset(DisplayVideo service, long advertiserId, String path)
      throws Exception {

    // Get filename from path
    String filename = new File(path).getName();

    // Create the asset upload request content
    CreateAssetRequest content = new CreateAssetRequest().setFilename(filename);

    // Create input stream for the creative asset
    InputStreamContent assetStream =
        new InputStreamContent(getMimeType(filename), new FileInputStream(path));

    // Configure the asset upload request
    Assets.Upload assetRequest =
        service.advertisers().assets().upload(advertiserId, content, assetStream);

    // Upload the asset
    CreateAssetResponse assetResponse = assetRequest.execute();

    // Return the created asset object
    return assetResponse.getAsset();
  }

  private static String getMimeType(String filename) {

    // Parse filename for appropriate MIME type
    FileNameMap filenameMap = URLConnection.getFileNameMap();
    String mimeType = filenameMap.getContentTypeFor(filename);

    // If MIME type was found, return it
    if (mimeType != null) {
      return mimeType;
    }

    // Otherwise, return a default value
    return "application/octet-stream";
  }
}
