# Display & Video 360 API Python Samples

This is a collection of samples written in Python which provide a starting
place for your experimentation into the Display & Video 360 API.

**WARNING**: Display & Video 360 API v1 and v2 are **sunset**.

## Prerequisites

Please make sure that you're running at least Python 3.8 and have pip
installed. Use the following command from the samples directory to install all
dependencies:

```Batchfile
$ python -m pip install -r requirements.txt
```

## Setup Authentication

This API uses [OAuth 2.0](https://developers.google.com/accounts/docs/OAuth2).

Follow these steps to enable the API for your Google Cloud Platform project and
generate the necessary credentials:
1. Visit [Google Developers Console](https://console.developers.google.com) to
   select or create your project.
2. From the API Manager &rarr; Google APIs screen, activate access to "Display
   & Video 360 API".
3. Click on "Credentials" in the left navigation menu
4. Click the button labeled "Create credentials" and select "OAuth Client ID"
5. Select "Desktop App" as the "Application type", then "Create"
6. From the Credentials page, click the "Download OAuth Client" icon under
   "Actions" next to the client ID you just created and click "Download JSON".
7. Save the downloaded file as `client_secrets.json` in the samples project
   directory.

### Authenticating with a Service Account

**Note**: Use the default sample argument `--use_service_account` to use
service account authentication when running v2 samples. v1 samples must be
edited directly to use service account authentication. User service samples
(`create_user.py`, `edit_user_access.py`, and `retrieve_users.py`) require
service account authentication to return successfully.

These samples support authentication using a [service account
key](https://cloud.google.com/iam/docs/service-account-overview).

To run samples using a service account, follow these steps after completing
steps 1 and 2 from the section above:
1. Click on "Credentials" in the left navigation menu
2. Click the button labeled "Create credentials" and select "Service Account"
3. Provide a "Service account name", "Service account ID", and "Service account
   description", then "Create and Continue".
4. From the Credentials page, click the new service account email.
5. Navigate to the "Keys" tab, click "Add Key", and create a new JSON key,
   which will download automatically.
6. Save the file as `service_account_key.json` in the samples project directory.
7. When starting a sample, include the `--use_service_account` flag to
   designate that you are authenticating with a service account.

## Running the Examples

This directory assumes you've checked out the code and are reading this from a
local directory.

1. Start up a sample, e.g.

   ```
   $ python create_campaign.py <advertiser_id> <display_name>
   ```
2. Complete the authorization steps on your browser.

3. Examine your shell output, be inspired and start hacking an amazing new app!