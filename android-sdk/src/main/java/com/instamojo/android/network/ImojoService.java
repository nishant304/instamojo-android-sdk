package com.instamojo.android.network;

import com.instamojo.android.models.CardPaymentRequest;
import com.instamojo.android.models.CardPaymentResponse;
import com.instamojo.android.models.Order;
import com.instamojo.android.models.UPIPaymentRequest;
import com.instamojo.android.models.UPIStatusResponse;
import com.instamojo.android.models.UPISubmissionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ImojoService {

    @GET("v2/gateway/orders/{orderID}/checkout-options/")
    Call<Order> getPaymentOptions(@Path("orderID") String orderID);

    @POST
    Call<CardPaymentResponse> collectCardPayment(@Url String url, @Body CardPaymentRequest cardPaymentRequest);

    @POST("v2/gateway/orders/{orderID})/upi/")
    Call<UPISubmissionResponse> collectUPIPayment(@Path("orderID") String orderID, @Body UPIPaymentRequest upiPaymentRequest);

    @GET
    Call<UPIStatusResponse> getUPIStatus(@Url String url);

}
