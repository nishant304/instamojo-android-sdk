package com.instamojo.android.network;

import android.app.Activity;
import android.os.Bundle;

import com.instamojo.android.activities.PaymentActivity;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.helpers.Logger;

/**
 * JavaScript interface to transfer control from Webview to Application.
 */
public class JavaScriptInterface {

    private Activity activity;

    /**
     * Constructor for ScriptInterface.
     *
     * @param activity This activity must be a subclass of {@link com.instamojo.android.activities.BaseActivity}.
     */
    public JavaScriptInterface(Activity activity) {
        this.activity = activity;
    }

    @android.webkit.JavascriptInterface
    public void onTransactionComplete(String orderID, String transactionID, String paymentID) {
        Logger.d(this.getClass().getSimpleName(), "Received Call to Javascript Interface");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.ORDER_ID, orderID);
        bundle.putString(Constants.TRANSACTION_ID, transactionID);
        bundle.putString(Constants.PAYMENT_ID, paymentID);
        Logger.d(this.getClass().getSimpleName(), "Returning result back to caller");
        ((PaymentActivity) activity).returnResult(bundle, Activity.RESULT_OK);
    }
}
