package com.instamojo.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
        int code = intent.getIntExtra("code", 0);
        if (code == 1) {
            mCallback.onInstamojoPaymentSuccess();

        } else {
            mCallback.onInstamojoPaymentFailure();
        }
    }

    public enum Environment {
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
                    mCallback.onInstamojoPaymentFailure();
                }
            }

            @Override
            public void onFailure(Call<GatewayOrder> call, Throwable t) {
                mCallback.onInstamojoPaymentFailure();
            }
        });
    }
}
