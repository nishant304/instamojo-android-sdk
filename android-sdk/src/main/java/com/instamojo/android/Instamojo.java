package com.instamojo.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.helpers.Logger;
import com.instamojo.android.models.GatewayOrder;
import com.instamojo.android.network.ImojoService;
import com.instamojo.android.network.ServiceGenerator;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Instamojo SDK
 */
public class Instamojo extends BroadcastReceiver {

    public static final String TAG = Instamojo.class.getSimpleName();
    private static Instamojo mInstance;
    private Context mContext;

    private InstamojoPaymentCallback mCallback;

    private Instamojo() {
        // Default private constructor
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle returnData = intent.getExtras();
        if (returnData == null) {
            mCallback.onInitiatePaymentFailure("Unknown error. ");
        }

        switch (returnData.getInt("code")) {
            case Activity.RESULT_OK:
                String orderID = returnData.getString(Constants.ORDER_ID);
                String transactionID = returnData.getString(Constants.TRANSACTION_ID);
                String paymentID = returnData.getString(Constants.PAYMENT_ID);
                String paymentStatus = returnData.getString(Constants.PAYMENT_STATUS);
                mCallback.onInstamojoPaymentComplete(orderID, transactionID, paymentID, paymentStatus);
                break;

            case Activity.RESULT_CANCELED:
                mCallback.onPaymentCancelled();
                break;

        }
    }

    public enum Environment {
        TEST, PRODUCTION
    }

    public interface InstamojoPaymentCallback {
        void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus);

        void onPaymentCancelled();

        void onInitiatePaymentFailure(String errorMessage);
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
        ServiceGenerator.initialize(environment);
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Initiate an Instamojo payment with an orderID
     *
     * @param orderID  Identifier of an Gateway Order instance created in the server (developer)
     * @param callback Callback interface to receive the response from Instamojo SDK
     */
    public void initiatePayment(final Activity activity, final String orderID, final InstamojoPaymentCallback callback) {

        mCallback = callback;
        ImojoService imojoService = ServiceGenerator.getImojoService();
        Call<GatewayOrder> gatewayOrderCall = imojoService.getPaymentOptions(orderID);
        gatewayOrderCall.enqueue(new Callback<GatewayOrder>() {
            @Override
            public void onResponse(Call<GatewayOrder> call, Response<GatewayOrder> response) {
                if (response.isSuccessful()) {

                    IntentFilter filter = new IntentFilter("com.instamojo.android.sdk");
                    activity.registerReceiver(Instamojo.this, filter);

                    Intent intent = new Intent(mContext, PaymentDetailsActivity.class);
                    intent.putExtra(Constants.ORDER, response.body());
                    activity.startActivity(intent);

                } else {
                    if (response.errorBody() != null) {
                        try {
                            Logger.d(TAG, "Error response from server while fetching order details.");
                            Logger.e(TAG, "Error: " + response.errorBody().string());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mCallback.onInitiatePaymentFailure("Error fetching order details");
                }
            }

            @Override
            public void onFailure(Call<GatewayOrder> call, Throwable t) {
                mCallback.onInitiatePaymentFailure("Failed to fetch order details");
            }
        });
    }
}
