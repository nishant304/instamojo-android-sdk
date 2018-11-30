package com.instamojo.android;

import android.content.Context;
import android.util.Log;

/**
 * Instamojo SDK
 */
public class Instamojo {

    public static final String TAG = Instamojo.class.getSimpleName();
    public static final String PRODUCTION_BASE_URL = "https://api.instamojo.com/";
    public static final String TEST_BASE_URL = "https://test.instamojo.com/";

    private static Instamojo mInstance;
    private Context mContext;
    private static String mBaseUrl;

    private Instamojo() {
        // Default private constructor
    }

    enum Environment {
        TEST, PRODUCTION
    }

    public interface InstamojoPaymentCallback {
        void onInstamojoPaymentSuccess();

        void onInstamojoPaymentFailure();
    }

    public static Instamojo getInstance() {
        if (mInstance == null) {
            synchronized (Instamojo.class) {
                if (mInstance == null) {
                    mInstance = new Instamojo();
                }
            }
        }

        return mInstance;
    }

    /**
     * Initialize the SDK with application context and environment
     */
    public void initialize(Context context, Environment environment) {
        Log.e(TAG, "Initializing SDK...");

        mContext = context;
        mBaseUrl = (environment == Environment.PRODUCTION) ? PRODUCTION_BASE_URL : TEST_BASE_URL;

        Log.d(TAG, "Using base url: " + mBaseUrl);
    }

    public Context getContext() {
        return mContext;
    }

    public String getDefaultRedirectUrl() {
        return mBaseUrl + "integrations/android/redirect/";
    }

    /**
     * Initiate an Instamojo payment with an orderID
     *
     * @param orderID  Identifier of an Gateway Order instance created in the server (developer)
     * @param callback Callback interface to receive the response from Instamojo SDK
     */
    public void initiatePayment(String orderID, InstamojoPaymentCallback callback) {

    }
}
