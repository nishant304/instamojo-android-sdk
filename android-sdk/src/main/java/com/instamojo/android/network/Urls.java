package com.instamojo.android.network;

import android.util.Log;

import com.instamojo.android.helpers.Constants;
import com.instamojo.android.helpers.Logger;

/**
 * SDK URL Class.
 *
 * @author vedhavyas
 * @version 1.0
 * @since 14/03/16
 */

public class Urls {

    private static String baseUrl = Constants.DEFAULT_BASE_URL;

    /**
     * @return Order Create URL.
     */
    public static String getOrderCreateUrl() {
        return baseUrl + "v2/gateway/orders/";
    }

    /**
     * @return default redirect URL
     */
    public static String getDefaultRedirectUrl() {
        return baseUrl + "integrations/android/redirect/";
    }

    /**
     * @return base url
     */
    public static String getBaseUrl() {
        return baseUrl;
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
        Logger.logDebug("Urls", "Base URL - " + Urls.baseUrl);
    }

    private static String sanitizeURL(String baseUrl) {

        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = Constants.DEFAULT_BASE_URL;
        }

        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }

        return baseUrl;
    }
}
