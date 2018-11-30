package com.instamojo.android;

import android.content.Context;
import android.util.Log;

/**
 * SDK Base Class.
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
}
