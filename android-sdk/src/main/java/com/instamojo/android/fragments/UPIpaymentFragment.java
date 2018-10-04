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
import com.instamojo.android.callbacks.UPICallback;
import com.instamojo.android.helpers.Constants;
import com.instamojo.android.helpers.Logger;
import com.instamojo.android.helpers.Validators;
import com.instamojo.android.models.UPISubmissionResponse;
import com.instamojo.android.network.Request;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * A simple {@link Fragment} subclass. The {@link Fragment} to get Virtual Private Address from user.
 */

public class UPIpaymentFragment extends BaseFragment implements View.OnClickListener, UPICallback {

    private static final String FRAGMENT_NAME = "UPISubmission Form";
    private static final long DELAY_CHECK = 2000;

    private MaterialEditText virtualAddressBox;
    private PaymentDetailsActivity parentActivity;
    private View preVPALayout, postVPALayout, verifyPayment;
    private UPISubmissionResponse upiSubmissionResponse;
    private Handler handler = new Handler();
    private boolean continueCheck = true;


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
        continueCheck = false;
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        if (!virtualAddressBox.validate()) {
            return;
        }

        virtualAddressBox.setEnabled(false);
        verifyPayment.setEnabled(false);

        Request request = new Request(parentActivity.getOrder(), virtualAddressBox.getText().toString(), this);
        request.execute();
    }

    private void checkStatusOfTransaction() {
        Request request = new Request(parentActivity.getOrder(), this.upiSubmissionResponse, this);
        request.execute();
    }

    @Override
    public void onSubmission(final UPISubmissionResponse upiSubmissionResponse, final Exception e) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e != null || upiSubmissionResponse.getStatusCode() != Constants.PENDING_PAYMENT) {
                    virtualAddressBox.setEnabled(true);
                    verifyPayment.setEnabled(true);
                    Toast.makeText(getContext(), "please try again...", Toast.LENGTH_SHORT).show();
                    return;
                }

                preVPALayout.setVisibility(View.GONE);
                postVPALayout.setVisibility(View.VISIBLE);

                UPIpaymentFragment.this.upiSubmissionResponse = upiSubmissionResponse;
                checkStatusOfTransaction();
            }
        });
    }

    @Override
    public void onStatusCheckComplete(Bundle bundle, boolean paymentComplete, Exception e) {
        if (e != null || !paymentComplete) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (continueCheck) {
                        checkStatusOfTransaction();
                    }
                }
            }, DELAY_CHECK);
            return;
        }

        Logger.logDebug(UPIpaymentFragment.this.getClass().getSimpleName(), "Payment complete. Finishing activity...");
        parentActivity.returnResult(bundle, Activity.RESULT_OK);
    }
}
