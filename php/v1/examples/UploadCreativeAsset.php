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

// Require the base class.
require_once __DIR__ . "/../BaseExample.php";

// Require the necessary utility class.
require_once __DIR__ . "/Utils/CreativeAssetUtils.php";

/**
 * This example uploads the given asset under the given Display & Video 360
 * advertiser.
 */
class UploadCreativeAsset extends BaseExample
{
    /**
     * (non-PHPdoc)
     * @see BaseExample::getInputParameters()
     */
    protected function getInputParameters(): array
    {
        return array(
            array(
                'name' => 'advertiser_id',
                'display' => 'Advertiser ID',
                'file' => false,
                'required' => true
            ),
            array(
                'name' => 'asset_file',
                'display' => 'Asset File',
                'file' => true,
                'required' => true
            )
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::run()
     */
    public function run()
    {
        $values = $this->formValues;

        $advertiserId = $values['advertiser_id'];
        $asset = $values['asset_file'];

        try {
            $uploadedAsset = CreativeAssetUtils::uploadAsset(
                $this->service,
                $advertiserId,
                $asset
            );
        } catch (\Exception $e) {
            $this->renderError($e);
            return;
        }

        printf(
            '<p>Asset was created with media ID %s.</p>',
            $uploadedAsset->getMediaId()
        );
    }

    /**
     * (non-PHPdoc)
     * @see BaseExample::getName()
     */
    public function getName(): string
    {
        return 'Upload Creative Asset';
    }
}