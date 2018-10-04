package com.instamojo.android.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * CardOptions object that holds the Card transaction information received from Instamojo server
 * for a particular order.
 */
public class CardOptions implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CardOptions> CREATOR = new Parcelable.Creator<CardOptions>() {
        @Override
        public CardOptions createFromParcel(Parcel in) {
            return new CardOptions(in);
        }

        @Override
        public CardOptions[] newArray(int size) {
            return new CardOptions[size];
        }
    };
    private final String orderID;
    private final String url;
    private final String merchantID;

    /**
     * Constructor for CardOptions.
     *
     * @param orderID    Order ID of the Order. Should not be Null.
     * @param merchantID Merchant ID of the transaction. Should not be Null.
     * @param url        Juspay Url to get the final 3D-secure Url.
     */
    public CardOptions(@NonNull String orderID, @NonNull String merchantID, @NonNull String url) {
        this.orderID = orderID;
        this.url = url;
        this.merchantID = merchantID;
    }

    protected CardOptions(Parcel in) {
        orderID = in.readString();
        url = in.readString();
        merchantID = in.readString();
    }

    /**
     * @return orderId of the current transaction.
     */
    public String getOrderID() {
        return orderID;
    }

    /**
     * @return Juspay url for the current transaction.
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return Merchant ID for the current transaction.
     */
    public String getMerchantID() {
        return merchantID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderID);
        dest.writeString(url);
        dest.writeString(merchantID);
    }
}
