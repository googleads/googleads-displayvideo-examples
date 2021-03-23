# Display & Video 360 API Python Samples

This is a collection of samples written in Python which provide a starting place
for your experimentation into the Display & Video 360 API.

## Prerequisites

Please make sure that you're running the latest version of Python 3 and have pip
installed. Use the following command from the samples directory to install all
dependencies:

```Batchfile
$ pip install --upgrade google-api-python-client
```

## Setup Authentication

This API uses OAuth 2.0. Learn more about Google APIs and OAuth 2.0 here:
https://developers.google.com/accounts/docs/OAuth2

Or, if you'd like to dive right in, follow these steps.
 - Visit https://console.developers.google.com to register your application.
 - From the API Manager -> Google APIs screen, activate access to
 "Display & Video 360 API".
 - Click on "Credentials" in the left navigation menu.
 - Click the button labeled "Create credentials" and select "OAuth Client ID"
 - Select "Other" as the "Application type", then "Create".
 - From the Credentials page, click "Download JSON" next to the client ID you
 just created and save the file as `client_secrets.json` in the project
 directory and update the `CREDENTIALS_FILE` variable in `samples_util.py`.

## Running the Examples

Before running the samples, check out the code to a local directory and follow
the instructions above to setup authentication.

1. Start up a sample, e.g.

        $ python create_campaign.py <advertiser_id> <display_name>

2. Complete the authorization steps on your browser.

3. Examine your shell output, be inspired and start hacking an amazing new app!