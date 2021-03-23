<?php
/*
 * Copyright 2021 Google LLC
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

function uploadAsset(
    $service,
    $advertiserId,
    $asset
) {
    $body = new Google_Service_DisplayVideo_CreateAssetRequest();
    $body->setFilename($asset['name']);

    $optParams = [
      'data' => file_get_contents($asset['tmp_name']),
      'mimeType' => $asset['type'],
      'uploadType' => 'media'
    ];

    // Call the API, uploading the asset file to Display & Video 360.
    $result = $service->advertisers_assets->upload($advertiserId, $body, $optParams);

    return $result->getAsset();
}

function createAsset(
    $content
) {
    $asset = new Google_Service_DisplayVideo_Asset();
    $asset->setContent($content);

    return $asset;
}

function createAssetAssociation(
    $asset,
    $assetRole
) {
    $assetAssociation = new Google_Service_DisplayVideo_AssetAssociation();
    $assetAssociation->setAsset($asset);
    $assetAssociation->setRole($assetRole);

    return $assetAssociation;
}
