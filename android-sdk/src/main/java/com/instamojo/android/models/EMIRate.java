package com.instamojo.android.models;

import com.google.gson.annotations.SerializedName;

public class EMIRate {

    @SerializedName("tenure")
    private int tenure;

    @SerializedName("interest")
    private String interest;
}
