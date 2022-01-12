# Display & Video 360 API PHP Samples

This is a collection of samples written in PHP which provide a starting place
for your experimentation into the Display & Video 360 API.

## Technical Requirements

  - PHP 7.2+
  - [Composer](https://getcomposer.org/)

From the example directory, run `composer install` to install all dependencies.

## Setup Authentication

This API uses OAuth 2.0. Learn more about Google APIs and OAuth 2.0 here:
https://developers.google.com/accounts/docs/OAuth2

If you've already set up
[Application Default Credentials](https://cloud.google.com/docs/authentication/production#providing_credentials_to_your_application)
at the environmental variable `GOOGLE_APPLICATION_CREDENTIALS`, the sample
suite will automatically use those credentials for authentication.

If not, follow these steps:
 - Visit https://console.developers.google.com to register your application.
 - From the API Manager -> Overview screen, activate access to
   "Display & Video 360 API".
 - Click on "Credentials" in the left navigation menu
 - Click the button labeled "Create credentials" ->  "OAuth2 client ID"
 - Select "Web Application" as the "Application type"
 - Configure javascript origins and redirect URIs
   - Authorized Javascript Origins: http://localhost
   - Authorized Redirect URIs: http://localhost/path/to/index.php
 - Click "Create client ID"
 - Click "Download JSON" and save the file as `client_secrets.json` in your
   examples directory

If you are using a service account for authentication,
[create and download a JSON service account key](https://cloud.google.com/iam/docs/creating-managing-service-account-keys#creating),
save the file as `service_account_key.json` in your examples, and include the query
parameter `service_account=true` when opening the sample.

> #### Security alert!

> Always ensure that your `client_secrets.json` and `service_account_key.json`
> files are not publicly accessible. These files have credential information
> which could allow unauthorized access to your Display & Video 360 data. For
> security purposes, these files have been listed in `.gitignore` file in this
> directory.

## Running the Examples

Once you've checked out the code and installed dependencies using Composer, you
can run the examples locally:
1. Via the command line, execute the following command in this directory:
```
php -S localhost:8000 -t ./
```
2. Open the sample (http://localhost:8000/index.php) in your browser.
3. Click ```Connect Me``` to start an authentication flow, redirect back to your
server, and then run the samples against your Display & Video 360 account.

**Note**: To use a service account for authentication, open the sample using the
following url: `http://localhost:8000/index.php?service_account=true`