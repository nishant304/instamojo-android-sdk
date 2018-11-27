package com.instamojo.android;

import android.content.Context;
import android.util.Log;

/**
 * SDK Base Class.
 */

public class Instamojo {

    private static Instamojo instance;
    private Context appContext;

    public Instamojo(Context appContext) {
        this.appContext = appContext;
    }

    /**
     * Initialises the previous session if exists
     *
     * @param appContext Application Context
     */
    public static void initialize(Context appContext) {
        instance = new Instamojo(appContext);
    }

    public static boolean isInitialised() {
        if (instance != null) {
            return true;
        }

        Log.e("Instamojo SDK", "Initialise the SDK with Application Context.");
        return false;
    }

    /**
     * @return Current instance
     */
    public static Instamojo getInstance() {
        return instance;
    }

    /**
     * @return Application context
     */
    public Context getAppContext() {
        return appContext;
    }
}
