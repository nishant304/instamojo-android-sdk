# Instamojo Android SDK Sample App

## Why Sample App?
1. The Sample app implements the complete scope of the Android SDK.
2. To help you kick start your own application using the Sample App as a base with Instamojo payments integrated.

## What does Sample App do now?
1. Create orders and make payments on either Test or Production Environments.
2. Check for the Order status.
3. Refund the Payment for a completed Order.

## Sample server
The sample app needs a backend server to do operations that require a secure environment and use credentials like `client_id` and `client_secret`. These operations include fetching an oauth access token, creating payment order and initiating a refund. For the purpose of testing the sample app, we have hosted a [sample backend server](https://github.com/Instamojo/sample-sdk-server) at `https://sample-sdk-server.instamojo.com`. Do checkout the [readme](https://github.com/Instamojo/sample-sdk-server/blob/master/Readme.md) to see how to implement your own backend server for Instamojo Android SDK.

## How it works?
1. The user chooses the environment (Test or Production) and provides the order details like buyer name, email, amount etc.
3. The sample app sends the details to the sample backend server.
4. The backend server talks to Instamojo servers and creates a payment order using the order details as well as the client credentials.
5. The backend server returns the `orderID` to the sample app.
6. The sample app initiates a payment by passing the `orderID` to the SDK.
3. The SDK then provides a view consisting of multiple payment options (Credit Card, Net Banking, UPI etc.) for the user to choose from.
4. User makes a payment using one of the options.
5. Once the payment is done, the SDK returns the payment status and details to the sample app.
6. If the payment is successful, the sample app initiates a full refund for the order through the sample backend server.

## I have few more queries
If you still have queries regarding sample app, please send a mail to our support id: [support@instamojo.com](mailto:support@instamojo.com). We will respond ASAP.
