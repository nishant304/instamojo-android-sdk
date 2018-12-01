package com.instamojo.android.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EMIOption {

    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("bank_code")
    private String bankCode;

    @SerializedName("rates")
    private List<EMIRate> emiRates;

}
