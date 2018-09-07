package com.instamojo.android.callbacks;

import android.os.Bundle;

import com.instamojo.android.models.Card;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.Order;

/**
 * Callback listener that is passed along with {@link Order}
 * and {@link com.instamojo.android.models.Card} to
 * {@link com.instamojo.android.network.Request#Request(Order, Card, JuspayRequestCallback)}.
 */

public interface JuspayRequestCallback {
    /**
     * Called on finish of the network call to Juspay with card and order details.
     * Pass on the bundle only after validating error to be null.
     *
     * @param bundle - Payment bundle that needs to be passed to {@link com.instamojo.android.activities.PaymentActivity}
     *               with Key {@link com.instamojo.android.helpers.Constants#PAYMENT_BUNDLE}.
     * @param error  - Appropriate {@link Exception} extended exception.
     *               {@link Errors.ConnectionError} related to all network exceptions.
     */
    void onFinish(Bundle bundle, Exception error);
}
