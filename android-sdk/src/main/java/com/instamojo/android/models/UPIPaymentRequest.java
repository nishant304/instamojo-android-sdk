package com.instamojo.android.models;

import com.google.gson.annotations.SerializedName;

public class UPIPaymentRequest {

    @SerializedName("virtual_address")
    private String upiAddress;

    public String getUpiAddress() {
        return upiAddress;
    }

    public void setUpiAddress(String upiAddress) {
        this.upiAddress = upiAddress;
    }
}
