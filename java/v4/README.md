# Display & Video 360 API v4 Java Samples

These samples demonstrate basic usage of the [Display & Video 360 API
v4](https://developers.google.com/display-video/api/reference/rest/v4).

The complete documentation for the Display & Video 360 API is
available from <https://developers.google.com/display-video/api>.

## Prerequisites

- [`Java 8+`](http://java.com)
- [`Maven`](http://maven.apache.org)

## Announcements and updates

For API and client library updates and news, please follow our [Google Ads
Developers blog](https://ads-developers.googleblog.com/search/label/dv360_api).

## Running the examples

### Download the repository contents

To download the contents of the repository, you can use the command

```
git clone https://github.com/googleads/googleads-displayvideo-examples
```

or browse to <https://github.com/googleads/googleads-displayvideo-examples> and
download a zip.

### Authentication Setup

This API uses [OAuth 2.0](https://developers.google.com/accounts/docs/OAuth2).

Follow these steps to enable the API for your Google Cloud Platform project and
generate the necessary credentials:
1. Visit [Google Developers Console](https://console.developers.google.com) to
select or create your project.
2. From the API Manager &rarr; Google APIs screen, activate access to "Display
& Video 360 API".
3. Click on "Credentials" in the left navigation menu.
4. Click the button labeled "Create credentials" and select "OAuth Client ID".
5. Select "Desktop App" as the "Application type", then "Create".
6. From the Credentials page, click the "Download OAuth Client" icon under
"Actions" next to the client ID you just created and click "Download JSON".
7. Save the downloaded file as `client_secrets.json` in your home directory.

#### Authenticate using a service account

These samples support authentication using a [service account
key](https://cloud.google.com/iam/docs/service-account-overview). To run
samples using a service account, follow these steps after completing steps 1
and 2 from the section above:
1. Click on "Credentials" in the left navigation menu.
2. Click the button labeled "Create credentials" and select "Service Account".
3. Provide a "Service account name", "Service account ID", and "Service account
description", then "Create and Continue".
4. From the Credentials page, click the new service account email.
5. Navigate to the "Keys" tab, click "Add Key", and create a new JSON key,
which will download automatically.
6. Save the downloaded file as `service_account_key.json` in your home
directory.
7. When starting a sample, include the `--useServiceAccount` argument to
designate that you are authenticating with a service account.

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

