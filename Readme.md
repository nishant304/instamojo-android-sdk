# Instamojo SDK Integration Documentation 

Table of Contents
=================

   * [Overview](#overview)
   * [Payment flow Via SDK](#payment-flow-via-sdk)
   * [Sample Application](#sample-application)
   * [Installation](#installation-----)
     * [Include SDK](#include-sdk)
     * [SDK Permissions](#sdk-permissions)
     * [Proguard rules](#proguard-rules)
   * [Generating Access Token](#generating-access-token)
   * [What is Transaction ID](#what-is-transaction-id)
   * [Simple Integration](#simple-integration)
     * [Initializing SDK](#initializing-sdk)
     * [Initiating Payment](#initiating-payment)
     * [Displaying Payment Forms](#displaying-payment-forms)
     * [Receiving Payment result in the main activity](#receiving-payment-result-in-the-main-activity)
   * [Using Custom Created UI](#using-custom-created-ui)
     * [Changing the Caller method](#changing-the-caller-method)
     * [Fetching order object in the CustomUIActivity](#fetching-order-object-in-the-customuiactivity)
     * [Collecting Card Details](#collecting-card-details)
       * [Validating Card Option](#validating-card-option)
       * [Creating and validating Card deatils](#creating-and-validating-card-deatils)
       * [Generating Juspay Bundle using Card](#generating-juspay-bundle-using-card)
     * [Collecting Netbanking Details](#collecting-netbanking-details)
       * [Validating Netbanking Option](#validating-netbanking-option)
       * [Displaying available Banks](#displaying-available-banks)
       * [Generating Juspay Bundle using Bank code](#generating-juspay-bundle-using-bank-code)
     * [Collecting EMI Details](#collecting-emi-details)
       * [Validating EMI Options](#validating-emi-options)
       * [Displaying available EMI Options](#displaying-available-emi-options)
       * [Updating Order and Collecting Card Details](#updating-order-and-collecting-card-details)
     * [Collecting Wallet Details](#collecting-wallet-details)
       * [Validating Wallet Options](#validating-wallet-options)
       * [Displaying available Wallets](#displaying-available-wallets)
       * [Generating Juspay Bundle using Wallet ID](#generating-juspay-bundle-using-wallet-id)
     * [Starting the payment Activity using the bundle](#starting-the-payment-activity-using-the-bundle)
     * [Passing the result back to main Activity](#passing-the-result-back-to-main-activity)
   * [Integration with Test Environment](#integration-with-test-environment)
   * [Verbose logging](#verbose-logging)
    
##***Note:SDK currently doesn't support MarketPlace Integration. MarketPlace API Documentation is available [here](https://docs.instamojo.com/v2/docs)***
##Overview
This SDK allows you to integrate payments via Instamojo into your Android app. It currently supports following modes of payments:

1. Credit / Debit Cards
2. EMI 
3. Netbanking
4. Wallets

This SDK also comes pre-integrated with Juspay Safe Browser, which makes payments on mobile easier, resulting in faster transaction time and improvement in conversions.

1. 1-click OTP: Auto-processing Bank SMS OTP for 1-Click experience.
2. Network optimizations: Smart 2G connection handling to reduce page load times.
3. Input & Keyboard Enhancements: Displays right keyboard with password viewing option.
4. Smooth User Experience: Aids the natural flow of users with features like Automatic Focus, Scroll/Zoom, Navigation buttons.

##Payment flow Via SDK
The section describes how the payment flow probably looks like, when you integrate with this SDK. Note that, this is just for reference and you are free to make changes to this flow that works well for you.

- When the buyer clicks on Buy button, your app makes a call to your backend to initiate a transaction in your system.
- Your backend systems create a transaction (uniquely identified by transaction_id). If required, it also obtains an access token from Instamojo servers. It passes back order details (like, buyer details, amount, transaction id etc.) and a valid Instamojo’s access token.
- Your Android app creates a new Order (from the SDK), by passing it order details and access token.
- If order is valid, the user is shown the payment modes, which will take him via the payment process as per mode selected.
- Once a payment is completed, a callback is called in your Android app with your transaction_id.
- Your Android app makes a request to your backend servers with the transaction_id.
- Your backend servers checks the status of the transaction by making a request to Instamojo’ servers. Your backend servers updates the status in the database, and passes back the result (successful / failure) to your app.
- Your app shows the success / failure screen based on the result if received from your backend.

## Sample Application 
Yes, we have a Sample app that is integrated with SDK. You can either use it as a base for your project or have a look at the integration in action.
Check out the documentation of the Sample App [here](https://github.com/Instamojo/android-sdk-sample-app/blob/master/ReadMe.md).

## Installation   [ ![Download](https://api.bintray.com/packages/dev-accounts/maven/sdks/images/download.svg) ](https://bintray.com/dev-accounts/maven/sdks/_latestVersion)
### Include SDK
The SDK currently supports Android Version >= ICS 4.0.3(14). Just add the following to your application’s `build.gradle` file, inside the dependencies section.
```
repositories {
    mavenCentral()
    maven {
        url "https://s3-ap-southeast-1.amazonaws.com/godel-release/godel/"
    }
}

dependencies {
    compile 'com.instamojo:android-sdk:+'
}

```

### SDK Permissions
The following are the minimum set of permissions required by the SDK. Add the following set of permissions in the application’s manifest file above the `<application>` tag.
```
//General permissions 
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

//required for Juspay to read the OTP from the SMS sent to the device
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_SMS" />
<uses-permission android:name="android.permission.RECEIVE_SMS" />
```

### Proguard rules
If you are using Proguard for code obfuscation, add following rules in the proguard configuration file `proguard-rules.pro`
```
# Rules for Instamojo SDK
-keep class com.instamojo.android.**{*;}
```

## Generating Access Token
A valid access token should be generated on your server using your `Client ID` and `Client Secret`.

Generate CLIENT_ID and CLIENT_SECRET for specific environments from the following links.
 - [Test Environment](https://test.instamojo.com/integrations)
 - [Production Environment](https://www.instamojo.com/integrations)

Related support article: [How Do I Get My Client ID And Client Secret?](https://support.instamojo.com/hc/en-us/articles/212214265-How-do-I-get-my-Client-ID-and-Client-Secret-)

Access token should be generated using CLIENT_ID and CLIENT_SECRET and will be valid for 10 hours after generation. Please check this [documentation](https://github.com/Instamojo/android-sdk-sample-app/blob/master/sample-sdk-server/Readme.md#generating-access-token) 
on how to generate Access Token using the credentials.

## What is Transaction ID
Well, transaction ID is a unique ID for an Order. Using this transaction ID, you can fetch Order status, get order details, and even initiate a refund for the Order attached to that transaction ID.

The transaction ID should be unique for every Order.

## Simple Integration
### Initializing SDK
Add the following `android:name="com.instamojo.android.InstamojoApplication"` key to `<application>` tag in manifest tag
```XML

    <application
            android:name="com.instamojo.android.InstamojoApplication"
            ..... >
    </application>        
```

What if there is a custom `Application` class already?
Then, add the following code snippet inside the `onCreate()` method of that custom application class.
```Java
    @Override
        public void onCreate() {
            super.onCreate();
            Instamojo.initialize(this);
            ...
        }
```

### Initiating Payment
To initiate a Payment, the following mandatory fields are required by the SDK.

1. Name of the buyer (Max 100 characters)&nbsp;
2. Email of the buyer (Max 75 characters)&nbsp;
3. Purpose of the transaction (Max 255 characters)&nbsp;
4. Phone number of the buyer &nbsp;
5. Transaction amount (Min of Rs. 9 and limited to 2 decimal points)&nbsp;
6. Access Token &nbsp;
7. Transaction ID (Max 64 characters)&nbsp;

With all the mandatory fields mentioned above, a `Order` object can created.
``` Java
Order order = new Order(accessToken, transactionID, name, email, phone, amount, purpose);
```

You can also set webhook for this particular order using the following code snippet
```java
order.setWebhook("http://your.server.com/webhook/");
```

`Order` object must be validated locally before creating Order with Instamojo.
Add the following code snippet to validate the `Order` object.
```Java
//Validate the Order
        if (!order.isValid()){
            //oops order validation failed. Pinpoint the issue(s).

            if (!order.isValidName()){
                Log.e("App", "Buyer name is invalid");
            }

            if (!order.isValidEmail()){
                Log.e("App", "Buyer email is invalid");
            }

            if (!order.isValidPhone()){
                Log.e("App", "Buyer phone is invalid");
            }

            if (!order.isValidAmount()){
                Log.e("App", "Amount is invalid");
            }

            if (!order.isValidDescription()){
                Log.e("App", "description is invalid");
            }

            if (!order.isValidTransactionID()){
                Log.e("App", "Transaction ID is invalid");
            }

            if (!order.isValidRedirectURL()){
                Log.e("App", "Redirection URL is invalid");
            }
            
            if (!order.isValidWebhook()) {
                showToast("Webhook URL is invalid");
            }

            return;
        }

        //Validation is successful. Proceed
```

Once `Order` is validated. Add the following code snippet to create an order with Instamojo.
``` Java
// Good time to show progress dialog to user
Request request = new Request(order, new OrderRequestCallBack() {
                    @Override
                    public void onFinish(Order order, Exception error) {
                        //dismiss the dialog if showed
                        
                        // Make sure the follwoing code is called on UI thread to show Toasts or to 
                        //update UI elements 
                        if (error != null) {
                            if (error instanceof Errors.ConnectionError) {
                                  Log.e("App", "No internet connection");
                            } else if (error instanceof Errors.ServerError) {
                                  Log.e("App", "Server Error. Try again");
                            } else if (error instanceof Errors.AuthenticationError){
                                  Log.e("App", "Access token is invalid or expired");
                            } else if (error instanceof Errors.ValidationError){
                                  // Cast object to validation to pinpoint the issue
                                  Errors.ValidationError validationError = (Errors.ValidationError) error;
                                  if (!validationError.isValidTransactionID()) {
                                         Log.e("App", "Transaction ID is not Unique");
                                         return;
                                  }
                                  if (!validationError.isValidRedirectURL()) {
                                         Log.e("App", "Redirect url is invalid");
                                         return;
                                  }
                                  
                                  if (!validationError.isValidWebhook()) {
                                         showToast("Webhook url is invalid");
                                         return;
                                  }
                                  
                                  if (!validationError.isValidPhone()) {
                                         Log.e("App", "Buyer's Phone Number is invalid/empty");
                                         return;
                                  }
                                  if (!validationError.isValidEmail()) {
                                         Log.e("App", "Buyer's Email is invalid/empty");
                                         return;
                                  }
                                  if (!validationError.isValidAmount()) {
                                         Log.e("App", "Amount is either less than Rs.9 or has more than two decimal places");
                                         return;
                                  }
                                  if (!validationError.isValidName()) {
                                         Log.e("App", "Buyer's Name is required");
                                         return;
                                  }
                            } else {
                                  Log.e("App", error.getMessage());
                            }
                        return;
                        }

                        startPreCreatedUI(order);
                    }
                });

                request.execute();
            }
        });
```

### Displaying Payment Forms
This SDK comes by default with payment forms (Cards and Netbanking) that can be used to collect payment details from the buyer.

Add the following code snippet to your application's activity/fragment to use Pre-created UI.
``` Java
private void startPreCreatedUI(Order order){
        //Using Pre created UI
        Intent intent = new Intent(getBaseContext(), PaymentDetailsActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
}
```

And you can call this once the order is created and validated, as per the above step:
```Java
startPreCreatedUI(order);
```

### Receiving Payment result in the main activity
Add the following code snippet in the main activity.
Note that TransactionID, OrderID, and paymentID maybe null. Please do a null check before proceeding.
``` Java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE && data != null) {
                    String orderID = data.getStringExtra(Constants.ORDER_ID);
                    String transactionID = data.getStringExtra(Constants.TRANSACTION_ID);
                    String paymentID = data.getStringExtra(Constants.PAYMENT_ID);
        
                    // Check transactionID, orderID, and orderID for null before using them to check the Payment status.
                    if (orderID != null && transactionID != null && paymentID != null) {
                         //Check for Payment status with Order ID or Transaction ID
                    } else {
                         //Oops!! Payment was cancelled
                    }
        }
}
```

## Using Custom Created UI
We know that every application is unique. If you choose to create your own UI to collect Payment information, SDK has necessary APIs to achieve this.
Use `CustomUIActivity` activity, which uses SDK APIs to collect Payment Information, to extend and modify as per your needs.
You can change the name of the activity to anything you like. Best way to do in Android Studio is by refactoring the name of the activity.

### Changing the Caller method
Replace `startPreCreatedUI` method wih the following one.
```Java
private void startCustomUI(Order order) {
        //Custom UI Implementation
        Intent intent = new Intent(getBaseContext(), CustomUIActivity.class);
        intent.putExtra(Constants.ORDER, order);
        startActivityForResult(intent, Constants.REQUEST_CODE);
}
```

### Fetching `order` object in the `CustomUIActivity`
To fetch the passed `order` object in the `CustomUIActivity`. Use the following snippet.
```Java
final Order order = getIntent().getParcelableExtra(Constants.ORDER);
```

### Collecting Card Details
#### Validating Card Option
Always validate whether the current order has card payment enabled.
```Java
if (order.getCardOptions() == null) {
   //seems like card payment is not enabled. Make the necessary UI Changes.
} else{
   // Card payment is enabled.
}
```

#### Creating and validating `Card` deatils
Once the user has typed in all the card details and ready to proceed, you can create the `Card` object.
```Java
Card card = new Card();
card.setCardNumber(cardNumber.getText().toString());
card.setDate(cardExpiryDate.getText().toString());
card.setCardHolderName(cardHoldersName.getText().toString());
card.setCvv(cvv.getText().toString());

//Validate the card now
if (!card.isCardValid()) {

   if (!card.isCardNameValid()) {
        Log.e("App", "Card Holders Name is invalid");
   }

   if (!card.isCardNumberValid()) {
        Log.e("App", "Card Number is invalid");
   }

## Where can I find the Documentation?

Integration documentation for the latest SDK is found [here](https://docs.instamojo.com/v1.1/page/devsupport-ai-android-integration).

## I have queries regarding the SDK Integration
If this documentation doesn't answer any specific question(s) pertaining to SDK integration,
please shoot out a mail to our [support](mailto:support@instamojo.com). We will respond ASAP.
