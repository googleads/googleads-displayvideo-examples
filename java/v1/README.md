# Display & Video 360 API Java Samples

These samples demonstrate basic usage of the Display & Video 360 REST API.

The complete documentation for the Display & Video 360 API is
available from <https://developers.google.com/display-video/api>.

## Prerequisites

- [`Java 8+`](http://java.com)
- [`Maven`](http://maven.apache.org)

## Announcements and updates

For API and client library updates and news, please follow our Google Ads
Developers blog: <https://ads-developers.googleblog.com/search/label/dv360_api>.

## Running the examples

### Download the repository contents

To download the contents of the repository, you can use the command

```
git clone https://github.com/googleads/googleads-displayvideo-examples
```

or browse to <https://github.com/googleads/googleads-displayvideo-examples> and
 download a zip.

### Authorization Setup

The API uses OAuth2 for authorization.

 * Launch the Google Developers Console <https://console.developers.google.com>
 * Select a project
 * Click **APIs & auth**
 * Click the **Credentials** tab
 * If you need to create a ```Client ID for native application```
   * Click **Create a new client ID**
   * Select **Installed Application**
 * Click **Download JSON** for a ```Client ID for native application```
 * Copy this file to ```src/main/resources/client_secrets.json```
   * If this JSON is stored elsewhere, provide the path to the file through the
     ```--clientSecretsFile``` command line argument.

**Note**: To use a service account for authentication include the
```--useServiceAccount``` command line argument. If storing the service account
key in a location different than the default set in `DisplayVideoFactory.java`,
provide a path to the file through the ```---serviceAccountKeyFile``` command
line argument.

## Setup the environment

### Set the Application name

Edit `DisplayVideoFactory.java` and change **APPLICATION_NAME**

### Via the command line

Execute the following command:

```
$ mvn compile
```

### Via Eclipse

1. Setup Eclipse preferences:
   1. Window > Preferences .. (or on Mac, Eclipse > Preferences)
   2. Select Maven
   3. Select "Download Artifact Sources"
   4. Select "Download Artifact JavaDoc"
2. Import the sample project
   1. "File > Import..."
   2. Select "Maven > Existing Maven Project" and click "Next"
   3. Click "Browse" next to "Select root directory", find the sample directory
   and click "Next"
   4. Click "Finish"

## Running the Examples

Once you've checked out the code:

1. Run ListBrowserTargetingOptions.java
   1. Via the command line, execute the following command:

      ```
      $ mvn exec:java -Dexec.mainClass="com.google.displayvideo.api.samples.ListBrowserTargetingOptions" -Dexec.args="--advertiserId INSERT_ADVERTISER_ID_HERE"
      ```
   2. Via eclipse, right-click on the project and select Run As > Java
   Application

