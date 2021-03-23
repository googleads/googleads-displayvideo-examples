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
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.displayvideo.v1.DisplayVideo;
import com.google.api.services.displayvideo.v1.DisplayVideo.Media;
import com.google.api.services.displayvideo.v1.DisplayVideo.Sdfdownloadtasks;
import com.google.api.services.displayvideo.v1.model.CreateSdfDownloadTaskRequest;
import com.google.api.services.displayvideo.v1.model.Operation;
import com.google.api.services.displayvideo.v1.model.ParentEntityFilter;
import com.google.displayvideo.api.samples.utils.ArgumentNames;
import com.google.displayvideo.api.samples.utils.CodeSampleParams;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This example creates a structured data file (SDF) download task using a {@link
 * ParentEntityFilter}, monitors that task, and downloads the resulting structured data files once
 * completed.
 */
public class DownloadStructuredDataFiles {

  private static class DownloadStructuredDataFilesParams extends CodeSampleParams {

    @Parameter(
        names = ArgumentNames.ADVERTISER_ID,
        description = "The ID of the advertiser to download the SDF for.")
    public Long advertiserId;

    @Parameter(
        names = ArgumentNames.OUTPUT_FILE,
        description = "The path to download the resulting SDF zip file to.")
    public String outputFile;

    @Parameter(
        names = ArgumentNames.SDF_VERSION,
        description = "The SDF version of the generated file.")
    public String sdfVersion;

    @Parameter(names = ArgumentNames.FILE_TYPES, description = "The SDF file types to generate.")
    public List<String> fileTypes;

    @Parameter(
        names = ArgumentNames.FILTER_TYPE,
        description = "The filter type to use to filter the fetched entities.")
    public String filterType;

    @Parameter(
        names = ArgumentNames.FILTER_IDS,
        description = "The ids of the specified filter type to filter the fetched entities by.")
    public List<Long> filterIds;
  }

  public static void main(String[] args) throws Exception {
    DownloadStructuredDataFilesParams params = new DownloadStructuredDataFilesParams();
    if (!params.parseArguments(args)) {

      // Either pass the required parameters for this example on the command line, or insert them
      // into the code here. See the parameter class definition above for descriptions.
      params.advertiserId = Long.valueOf("INSERT_ADVERTISER_ID_HERE");
      params.outputFile = "INSERT_OUTPUT_FILE_HERE";
      params.sdfVersion = "INSERT_SDF_VERSION_HERE";
      params.fileTypes = Arrays.asList("INSERT_FILE_TYPES_HERE".split(","));
      params.filterType = "INSERT_FILTER_TYPE_HERE";

      // Convert string list of ID values to Long values.
      List<String> filterIdsStr = Arrays.asList("INSERT_FILTER_TYPES_HERE".split(","));
      ArrayList<Long> filterIdsTmp = new ArrayList<Long>();
      for (String id : filterIdsStr) {
        if (id != "") {
          filterIdsTmp.add(Long.parseLong(id));
        }
      }
      params.filterIds = filterIdsTmp;
    }

    DisplayVideo service = DisplayVideoFactory.getInstance(params.clientSecretsFile);

    runExample(
        service,
        params.advertiserId,
        params.outputFile,
        params.sdfVersion,
        params.fileTypes,
        params.filterType,
        params.filterIds);
  }

  public static void runExample(
      DisplayVideo service,
      long advertiserId,
      String outputFile,
      String sdfVersion,
      List<String> fileTypes,
      String filterType,
      List<Long> filterIds)
      throws Exception {

    // Build and create SDF Download task.
    Operation operation =
        createSdfDownloadTask(service, advertiserId, sdfVersion, fileTypes, filterType, filterIds);

    // Get current status of operation with exponential backoff retry logic.
    Operation completedOperation = waitForTask(service, operation);

    // Check whether operation is not done and timed out, if it finished with an error, or is
    // completed and ready to download.
    if (completedOperation.getDone() == null) {
      return;
    } else if (completedOperation.getError() != null) {
      System.out.printf(
          "The operation finished in error with code %s: %s%n",
          completedOperation.getError().getCode(), completedOperation.getError().getMessage());
      return;
    } else {
      System.out.printf(
          "The operation completed successfully. Resource %s was created.%n",
          completedOperation.getResponse().get("resourceName").toString());
    }

    // Download resulting SDFs to specified output file.
    downloadFile(
        service, completedOperation.getResponse().get("resourceName").toString(), outputFile);
  }

  private static Operation createSdfDownloadTask(
      DisplayVideo service,
      long advertiserId,
      String sdfVersion,
      List<String> fileTypes,
      String filterType,
      List<Long> filterIds)
      throws Exception {

    // Create the filter structure.
    ParentEntityFilter parentEntityFilter =
        new ParentEntityFilter().setFileType(fileTypes).setFilterType(filterType);
    if (filterIds.size() != 0) {
      parentEntityFilter.setFilterIds(filterIds);
    }

    // Configure the Sdfdownloadtasks.create request.
    Sdfdownloadtasks.Create request =
        service
            .sdfdownloadtasks()
            .create(
                new CreateSdfDownloadTaskRequest()
                    .setVersion(sdfVersion)
                    .setAdvertiserId(advertiserId)
                    .setParentEntityFilter(parentEntityFilter));

    // Create the SDF download task.
    Operation operationResponse = request.execute();

    System.out.printf("Operation %s was created.%n", operationResponse.getName());

    return operationResponse;
  }

  private static Operation waitForTask(DisplayVideo service, Operation operation) throws Exception {

    // Configure the Operations.get request.
    Sdfdownloadtasks.Operations.Get operationRequest =
        service.sdfdownloadtasks().operations().get(operation.getName());

    // Configure exponential backoff for checking the status of our operation.
    ExponentialBackOff backOff =
        new ExponentialBackOff.Builder()
            .setInitialIntervalMillis(5000) // setting initial interval to five seconds
            .setMaxIntervalMillis(300000) // setting max interval to five minutes
            .setMaxElapsedTimeMillis(18000000) // setting max elapsed time to five hours
            .build();

    while (operation.getDone() == null) {
      long backoffMillis = backOff.nextBackOffMillis();
      if (backoffMillis == ExponentialBackOff.STOP) {
        System.out.printf("The operation has taken more than five hours to complete.%n");
        return operation;
      }
      Thread.sleep(backoffMillis);

      // Get current status of operation.
      operation = operationRequest.execute();
    }

    return operation;
  }

  private static void downloadFile(DisplayVideo service, String resourceName, String outputFile)
      throws Exception {

    // Configure the Media.download request.
    Media.Download downloadRequest = service.media().download(resourceName);

    // Create output stream for downloaded file.
    FileOutputStream outStream = new FileOutputStream(outputFile);

    // Download file.
    downloadRequest.executeMediaAndDownloadTo(outStream);

    System.out.printf("File downloaded to %s%n", outputFile);
  }
}
