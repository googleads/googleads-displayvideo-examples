<?php
/*
 * Copyright 2023 Google LLC
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

/**
 * Helper class to upload assets and create the Display & Video 360 API
 * asset and asset association objects.
 */
class CreativeAssetUtils
{
    /**
     * Creates an asset object by uploading a media file.
     * @param Google_Service_DisplayVideo $service an authenticated
     *     instance of Google_Service_DisplayVideo.
     * @param int $advertiserId the ID of the advertiser to upload the file
     *     and create the asset beneath.
     * @param array $asset the file to upload.
     * @return Google_Service_DisplayVideo_Asset the asset object created by
     *     the upload.
     */
    public static function uploadAsset(
        Google_Service_DisplayVideo $service,
        int $advertiserId,
        array $asset
    ): Google_Service_DisplayVideo_Asset {
        $body = new Google_Service_DisplayVideo_CreateAssetRequest();
        $body->setFilename($asset['name']);

        $optParams = array(
            'data' => file_get_contents($asset['tmp_name']),
            'mimeType' => $asset['type'],
            'uploadType' => 'media'
        );

        // Call the API, uploading the asset file to Display & Video 360.
        $result = $service->advertisers_assets->upload(
            $advertiserId,
            $body,
            $optParams
        );

        return $result->getAsset();
    }

    /**
     * Creates an asset object used in Creative resources.
     * @param string $content the content of the asset to create.
     * @return Google_Service_DisplayVideo_Asset the resulting asset object.
     */
    public static function createAsset(
        string $content
    ): Google_Service_DisplayVideo_Asset {
        $asset = new Google_Service_DisplayVideo_Asset();
        $asset->setContent($content);

        return $asset;
    }

    /**
     * Creates an asset association object used in Creative resources.
     * @param Google_Service_DisplayVideo_Asset $asset an asset to be associated
     *     with the given role.
     * @param string $assetRole an AssetRole enum value to associate with the
     *     given asset.
     * @return Google_Service_DisplayVideo_AssetAssociation the resulting asset
     *     association object.
     */
    public static function createAssetAssociation(
        Google_Service_DisplayVideo_Asset $asset,
        string $assetRole
    ): Google_Service_DisplayVideo_AssetAssociation {
        $assetAssociation = new Google_Service_DisplayVideo_AssetAssociation();
        $assetAssociation->setAsset($asset);
        $assetAssociation->setRole($assetRole);

        return $assetAssociation;
    }
}