package com.instamojo.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.instamojo.android.R;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.helpers.Logger;
import com.instamojo.android.helpers.Validators;
import com.instamojo.android.models.Order;
import com.instamojo.android.models.UPIPaymentRequest;
import com.instamojo.android.models.UPIStatusResponse;
import com.instamojo.android.models.UPISubmissionResponse;
import com.instamojo.android.network.ImojoService;
import com.instamojo.android.network.ServiceGenerator;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass. The {@link Fragment} to get Virtual Private Address from user.
 */

public class UPIFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = UPIFragment.class.getSimpleName();
    private static final String FRAGMENT_NAME = "UPISubmission Form";
    private static final long DELAY_CHECK = 2000;

    private MaterialEditText virtualAddressBox;
    private PaymentDetailsActivity parentActivity;
    private View preVPALayout, postVPALayout, verifyPayment;
    private UPISubmissionResponse upiSubmissionResponse;
    private Handler handler = new Handler();

    private StatusPoller mStatusPoller = new StatusPoller("url");

    class StatusPoller implements Runnable {
        String statusURL;

        public StatusPoller(String statusURL) {
            this.statusURL = statusURL;
        }

        @Override
        public void run() {
            try {
                int statusCode = fetchUPIStatus(statusURL);
                // TODO instead check for SUCCESS
                if (statusCode != Constants.PENDING_PAYMENT) {
                    // Stop polling for status. Return to activity
                    returnResult();
                    return;
                }

            } catch (IOException e) {
                Logger.d(TAG, "Exception occurred while polling for status. Error: " + e.getMessage());

            } finally {
                // Continue for exceptions as well
                handler.postDelayed(mStatusPoller, DELAY_CHECK);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upi_instamojo, container, false);
        parentActivity = (PaymentDetailsActivity) getActivity();
        inflateXML(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int title = R.string.title_fragment_upi;
        parentActivity.updateActionBarTitle(title);
    }

    @Override
    public String getFragmentName() {
        return FRAGMENT_NAME;
    }

    @Override
    public void inflateXML(View view) {
        virtualAddressBox = view.findViewById(R.id.virtual_address_box);
        virtualAddressBox.addValidator(new Validators.EmptyFieldValidator());
        virtualAddressBox.addValidator(new Validators.VPAValidator());
        preVPALayout = view.findViewById(R.id.pre_vpa_layout);
        postVPALayout = view.findViewById(R.id.post_vpa_layout);
        verifyPayment = view.findViewById(R.id.verify_payment);
        verifyPayment.setOnClickListener(this);

        // Automatically open soft keyboard for VPA field (on display of this fragment).
        virtualAddressBox.post(new Runnable() {
            @Override
            public void run() {
                virtualAddressBox.requestFocus();
                InputMethodManager lManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (lManager != null) {
                    lManager.showSoftInput(virtualAddressBox, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
    }

    @Override
    public void onDetach() {
        handler.removeCallbacks(mStatusPoller);
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        if (!virtualAddressBox.validate()) {
            return;
        }

        virtualAddressBox.setEnabled(false);
        verifyPayment.setEnabled(false);

        UPIPaymentRequest upiPaymentRequest = new UPIPaymentRequest();
        upiPaymentRequest.setUpiAddress(virtualAddressBox.getText().toString());

        ImojoService imojoService = ServiceGenerator.getImojoService();
        Call<UPISubmissionResponse> upiPaymentCall =
                imojoService.collectUPIPayment(parentActivity.getOrder().getId(), upiPaymentRequest);

        upiPaymentCall.enqueue(new Callback<UPISubmissionResponse>() {
            @Override
            public void onResponse(Call<UPISubmissionResponse> call, final Response<UPISubmissionResponse> response) {
                if (response.isSuccessful()) {
                    parentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UPISubmissionResponse upiSubmissionResponse = response.body();
                            if (upiSubmissionResponse.getStatusCode() != Constants.PENDING_PAYMENT) {
                                virtualAddressBox.setEnabled(true);
                                verifyPayment.setEnabled(true);
                                Toast.makeText(getContext(), "please try again...", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            preVPALayout.setVisibility(View.GONE);
                            postVPALayout.setVisibility(View.VISIBLE);

                            UPIFragment.this.upiSubmissionResponse = upiSubmissionResponse;
                            startPollingForUPIStatus();
                        }
                    });

                } else {
                    String error = "Oops. Some error occurred. Please try again..";
                    if (response.code() == 400) {
                        try {
                            String errorBody = response.errorBody().string();
                            JSONObject responseObject = new JSONObject(errorBody);
                            JSONObject errors = responseObject.getJSONObject("errors");

                            virtualAddressBox.setEnabled(true);
                            verifyPayment.setEnabled(true);
                            error = errors.getString("virtual_address");

                        } catch (IOException | JSONException e) {
                            Logger.e(TAG, "Error while handling UPI error response - " + e.getMessage());
                        }
                    }

                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UPISubmissionResponse> call, Throwable t) {
                Logger.e(TAG, "Error while making UPI Submission request - " + t.getMessage());
                Toast.makeText(getContext(), "Oops. Some error occurred. Please try again..", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private int fetchUPIStatus(String statusURL) throws IOException {
        ImojoService imojoService = ServiceGenerator.getImojoService();
        Call<UPIStatusResponse> upiStatusCall = imojoService.getUPIStatus(statusURL);
        Response<UPIStatusResponse> response = upiStatusCall.execute();
        UPIStatusResponse upiStatusResponse = response.body();
        return upiStatusResponse.getStatusCode();
    }

    private void startPollingForUPIStatus() {
        mStatusPoller.run();
    }

    private void returnResult() {
        Bundle bundle = new Bundle();
        Order order = parentActivity.getOrder();
        bundle.putString(Constants.ORDER_ID, order.getId());
        bundle.putString(Constants.TRANSACTION_ID, order.getTransactionID());
        bundle.putString(Constants.PAYMENT_ID, upiSubmissionResponse.getPaymentID());
        Logger.d(TAG, "Payment complete. Finishing activity...");
        parentActivity.returnResult(bundle, Activity.RESULT_OK);
    }
}
