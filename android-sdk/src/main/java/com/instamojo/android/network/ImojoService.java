package com.instamojo.android.network;

import com.instamojo.android.models.Order;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ImojoService {

    @GET("v2/gateway/orders/{orderID}/checkout-options/")
    public Call<Order> getPaymentOptions(@Path("orderID") String orderID);

    @POST
    public Call<Order> collectCardPayment(@Url String url, @Body Order order);

    @POST("v2/gateway/orders/{orderID})/upi/")
    public Call<Order> collectUPIPayment(@Path("orderID") String orderID, @Body RequestBody body);

    @GET
    public Call<Order> getUPIStatus(@Url String url);

}
