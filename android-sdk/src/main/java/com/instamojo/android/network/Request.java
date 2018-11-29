package com.instamojo.android.network;

import android.support.annotation.NonNull;

import com.instamojo.android.callbacks.OrderRequestCallback;
import com.instamojo.android.helpers.Logger;
import com.instamojo.android.models.CardOptions;
import com.instamojo.android.models.EMIBank;
import com.instamojo.android.models.EMIOptions;
import com.instamojo.android.models.Errors;
import com.instamojo.android.models.NetBankingOptions;
import com.instamojo.android.models.Order;
import com.instamojo.android.models.UPIOptions;
import com.instamojo.android.models.Wallet;
import com.instamojo.android.models.WalletOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Network Request Class.
 */

public class Request {

    private static final String TAG = Request.class.getSimpleName();
    private Order order;
    private OrderRequestCallback orderRequestCallback;
    private MODE mode;
    private String orderID;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .build();

    /**
     * Network request to fetch the order
     *
     * @param orderID              String
     * @param orderRequestCallback {@link OrderRequestCallback}
     */
    public void getPaymentOptions(@NonNull String orderID, @NonNull final OrderRequestCallback orderRequestCallback) {
        this.mode = MODE.FetchOrder;
        this.orderID = orderID;
        this.orderRequestCallback = orderRequestCallback;

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Urls.getOrderFetchURL(orderID))
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(this.getClass().getSimpleName(), "Error while making Instamojo request - " + e.getMessage());
                orderRequestCallback.onFinish(null, new Errors.ConnectionError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                String responseBody = "";
                try {
                    responseBody = r.body().string();
                    r.body().close();
                    parseCheckoutOptions(responseBody);
                    orderRequestCallback.onFinish(order, null);
                } catch (IOException e) {
                    Logger.e(this.getClass().getSimpleName(), "Error while making Instamojo request - " + e.getMessage());
                    orderRequestCallback.onFinish(order, new Errors.ServerError(e.getMessage()));
                } catch (JSONException e) {
                    Logger.e(this.getClass().getSimpleName(), "Error while making Instamojo request - " + e.getMessage());
                    orderRequestCallback.onFinish(order, Errors.getAppropriateError(responseBody));
                }
            }
        });
    }

    /**
     * Executes the call to the server and calls the callback with  {@link Exception} if failed.
     */
    public void execute() {
        switch (this.mode) {
            case FetchOrder:
                executeFetchOrder();
                break;
            default:
                throw new RuntimeException("Unknown Mode");
        }
    }

    private void executeFetchOrder() {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Urls.getOrderFetchURL(orderID))
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.e(this.getClass().getSimpleName(), "Error while making Instamojo request - " + e.getMessage());
                orderRequestCallback.onFinish(null, new Errors.ConnectionError(e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response r) throws IOException {
                String responseBody = "";
                try {
                    responseBody = r.body().string();
                    r.body().close();
                    parseCheckoutOptions(responseBody);
                    orderRequestCallback.onFinish(order, null);
                } catch (IOException e) {
                    Logger.e(this.getClass().getSimpleName(), "Error while making Instamojo request - " + e.getMessage());
                    orderRequestCallback.onFinish(order, new Errors.ServerError(e.getMessage()));
                } catch (JSONException e) {
                    Logger.e(this.getClass().getSimpleName(), "Error while making Instamojo request - " + e.getMessage());
                    orderRequestCallback.onFinish(order, Errors.getAppropriateError(responseBody));
                }
            }
        });
    }

    private void parseCheckoutOptions(String responseBody) throws JSONException {
        JSONObject responseObject = new JSONObject(responseBody);
        JSONObject orderObject = responseObject.getJSONObject("order");
        String amount = orderObject.getString("amount");
        order = new Order();
        order.setAmount(amount);
        updateTransactionDetails(responseObject);
    }

    private void updateTransactionDetails(JSONObject responseObject) throws JSONException {

        JSONObject paymentOptionsObject = responseObject.getJSONObject("payment_options");
        if (paymentOptionsObject.has("card_options") && !paymentOptionsObject.isNull("card_options")) {
            JSONObject cardOptions = paymentOptionsObject.getJSONObject("card_options");
            JSONObject submissionData = cardOptions.getJSONObject("submission_data");
            String merchantID = submissionData.getString("merchant_id");
            String orderID = submissionData.getString("order_id");
            String paymentURL = cardOptions.getString("submission_url");
            order.setCardOptions(new CardOptions(orderID, merchantID, paymentURL));
        }

        if (paymentOptionsObject.has("netbanking_options") && !paymentOptionsObject.isNull("netbanking_options")) {
            JSONObject netbankingOptions = paymentOptionsObject.getJSONObject("netbanking_options");
            String nbURL = netbankingOptions.getString("submission_url");
            JSONArray banksArray = netbankingOptions.getJSONArray("choices");
            LinkedHashMap<String, String> banks = new LinkedHashMap<>();
            JSONObject bank;
            for (int i = 0; i < banksArray.length(); i++) {
                bank = banksArray.getJSONObject(i);
                banks.put(bank.getString("name"), bank.getString("id"));
            }
            if (banks.size() > 0) {
                order.setNetBankingOptions(new NetBankingOptions(nbURL, banks));
            }
        }

        if (paymentOptionsObject.has("emi_options") && !paymentOptionsObject.isNull("emi_options")) {
            JSONObject emiOptionsRaw = paymentOptionsObject.getJSONObject("emi_options");
            JSONArray emiListRaw = emiOptionsRaw.getJSONArray("emi_list");
            EMIBank emiBank;
            JSONObject emiOptionRaw;
            JSONArray ratesRaw;
            JSONObject rateRaw;
            ArrayList<EMIBank> emis = new ArrayList<>();
            for (int i = 0; i < emiListRaw.length(); i++) {
                emiOptionRaw = emiListRaw.getJSONObject(i);
                String bankName = emiOptionRaw.getString("bank_name");
                String bankCode = emiOptionRaw.getString("bank_code");
                Map<Integer, BigDecimal> rates = new HashMap<>();
                ratesRaw = emiOptionRaw.getJSONArray("rates");
                for (int j = 0; j < ratesRaw.length(); j++) {
                    rateRaw = ratesRaw.getJSONObject(j);
                    int tenure = rateRaw.getInt("tenure");
                    String interest = rateRaw.getString("interest");
                    rates.put(tenure, new BigDecimal(interest));
                }
                LinkedList<Map.Entry<Integer, BigDecimal>> ratesList = new LinkedList<>(rates.entrySet());
                Collections.sort(ratesList, new Comparator<Map.Entry<Integer, BigDecimal>>() {
                    @Override
                    public int compare(Map.Entry<Integer, BigDecimal> lhs, Map.Entry<Integer, BigDecimal> rhs) {
                        return lhs.getKey() - rhs.getKey();
                    }
                });
                LinkedHashMap<Integer, BigDecimal> sortedRates = new LinkedHashMap<>();
                for (Map.Entry<Integer, BigDecimal> entry : ratesList) {
                    sortedRates.put(entry.getKey(), entry.getValue());
                }

                if (sortedRates.size() > 0) {
                    emiBank = new EMIBank(bankName, bankCode, sortedRates);
                    emis.add(emiBank);
                }
            }
            String url = emiOptionsRaw.getString("submission_url");
            JSONObject submissionData = emiOptionsRaw.getJSONObject("submission_data");
            String merchantID = submissionData.getString("merchant_id");
            String orderID = submissionData.getString("order_id");
            if (emis.size() > 0) {
                order.setEmiOptions(new EMIOptions(merchantID, orderID, url, emis));
            }
        }

        if (paymentOptionsObject.has("wallet_options") && !paymentOptionsObject.isNull("wallet_options")) {
            JSONObject walletOptionsObject = paymentOptionsObject.getJSONObject("wallet_options");
            JSONArray walletChoices = walletOptionsObject.getJSONArray("choices");
            JSONObject walletObject;
            ArrayList<Wallet> wallets = new ArrayList<>();
            for (int i = 0; i < walletChoices.length(); i++) {
                walletObject = walletChoices.getJSONObject(i);
                String name = walletObject.getString("name");
                String walletID = walletObject.getString("id");
                String walletImage = walletObject.getString("image");
                wallets.add(new Wallet(name, walletImage, walletID));
            }

            String url = walletOptionsObject.getString("submission_url");

            if (wallets.size() > 0) {
                order.setWalletOptions(new WalletOptions(url, wallets));
            }
        }

        if (paymentOptionsObject.has("upi_options") && !paymentOptionsObject.isNull("upi_options")) {
            JSONObject upiOptionsRaw = paymentOptionsObject.getJSONObject("upi_options");
            UPIOptions upiOptions = new UPIOptions(upiOptionsRaw.getString("submission_url"));
            order.setUpiOptions(upiOptions);
        }
    }

    private enum MODE {
        FetchOrder
    }
}
