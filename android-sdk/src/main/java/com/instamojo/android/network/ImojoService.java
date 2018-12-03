package com.instamojo.android.network;

import com.instamojo.android.models.CardPaymentResponse;
import com.instamojo.android.models.GatewayOrder;
import com.instamojo.android.models.UPIPaymentRequest;
import com.instamojo.android.models.UPIStatusResponse;
import com.instamojo.android.models.UPISubmissionResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ImojoService {

    @GET("v2/gateway/orders/{orderID}/checkout-options/")
    Call<GatewayOrder> getPaymentOptions(@Path("orderID") String orderID);

    @FormUrlEncoded
    @POST
    Call<CardPaymentResponse> collectCardPayment(@Url String url, @FieldMap Map<String, String> cardPaymentRequest);

    @POST("v2/gateway/orders/{orderID})/upi/")
    Call<UPISubmissionResponse> collectUPIPayment(@Path("orderID") String orderID, @Body UPIPaymentRequest upiPaymentRequest);

    @GET
    Call<UPIStatusResponse> getUPIStatus(@Url String url);

}
