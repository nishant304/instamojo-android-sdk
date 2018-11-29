package com.instamojo.android.network;

import android.util.Log;

import com.instamojo.android.helpers.Logger;

/**
 * SDK URL Class.
 */

public class Urls {

    private static final String PRODUCTION_BASE_URL = "https://api.instamojo.com/";
    private static String baseUrl = PRODUCTION_BASE_URL;

    /**
     * @return default redirect URL
     */
    public static String getDefaultRedirectUrl() {
        return baseUrl + "integrations/android/redirect/";
    }


    /**
     * Set the base url
     *
     * @param baseUrl Base url for all network calls
     */
    public static void setBaseUrl(String baseUrl) {
        baseUrl = sanitizeURL(baseUrl);

        if (baseUrl.contains("test")) {
            Log.d("Urls", "Using a test base url. Use https://api.instamojo.com/ for production");
        }

        Urls.baseUrl = baseUrl;
        Logger.d("Urls", "Base URL - " + Urls.baseUrl);
    }

    private static String sanitizeURL(String baseUrl) {

        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = PRODUCTION_BASE_URL;
        }

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        return baseUrl;
    }

}
