package com.instamojo.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.instamojo.android.R;
import com.instamojo.android.activities.PaymentDetailsActivity;
import com.instamojo.android.helpers.MoneyUtils;
import com.instamojo.android.models.EMIBank;

import java.math.BigDecimal;
import java.util.Map;

public class EMIBankOptionsView extends BaseFragment {

    private PaymentDetailsActivity parentActivity;
    private LinearLayout optionsContainer;
    private EMIBank selectedBank;

    public EMIBankOptionsView() {
    }

    public static EMIBankOptionsView getInstance(EMIBank selectedBank) {
        EMIBankOptionsView optionsView = new EMIBankOptionsView();
        optionsView.setSelectedBank(selectedBank);
        return optionsView;
    }

    private static double getEmiAmount(String totalAmount, BigDecimal rate, int tenure) {
        double parsedAmount = Double.parseDouble(totalAmount);
        return MoneyUtils.getMonthlyEMI(parsedAmount, rate, tenure);
    }

    private static double getTotalAmount(double emiAmount, int tenure) {
        return MoneyUtils.getRoundedValue(emiAmount * tenure, 2);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emi_instamojo, container, false);
        parentActivity = (PaymentDetailsActivity) getActivity();
        inflateXML(view);
        loadOptions();
        return view;
    }

    @Override
    public void inflateXML(View view) {
        super.inflateXML(view);
        optionsContainer = view.findViewById(R.id.emi_view_container);
    }

    @Override
    public void onResume() {
        super.onResume();
        parentActivity.updateActionBarTitle(R.string.choose_an_emi_option);
    }

    public void setSelectedBank(EMIBank selectedBank) {
        this.selectedBank = selectedBank;
    }

    private void loadOptions() {
        optionsContainer.removeAllViews();
        String orderAmount = parentActivity.getOrder().getAmount();
        for (final Map.Entry<Integer, BigDecimal> option : selectedBank.getRates().entrySet()) {
            View optionView = LayoutInflater.from(getContext()).inflate(R.layout.emi_option_view,
                    optionsContainer, false);
            double emiAmount = getEmiAmount(orderAmount, option.getValue(), option.getKey());
            String emiAmountString = "₹" + emiAmount + " x " + option.getKey() + " Months";
            String finalAmountString = "Total ₹" + getTotalAmount(emiAmount, option.getKey()) + " @ "
                    + option.getValue() + "% pa";
            ((TextView) optionView.findViewById(R.id.emi_amount)).setText(emiAmountString);
            ((TextView) optionView.findViewById(R.id.final_emi_amount)).setText(finalAmountString);
            optionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentActivity.getOrder().getEmiOptions()
                            .setSelectedBankCode(selectedBank.getBankCode());
                    parentActivity.getOrder().getEmiOptions().setSelectedTenure(option.getKey());
                    parentActivity.loadFragment(CardForm.getCardForm(CardForm.Mode.EMI), true);
                }
            });
            optionsContainer.addView(optionView);
        }
    }
}
