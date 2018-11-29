package com.instamojo.android.models;

import com.google.gson.annotations.SerializedName;

public class CardPaymentRequest {

    @SerializedName("order_id")
    private String orderID;

    @SerializedName("merchant_id")
    private String merchantID;

    @SerializedName("payment_method_type")
    private String paymentMethod;

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("card_exp_month")
    private String cardExpiryMonth;

    @SerializedName("card_exp_year")
    private String cardExpiryYear;

    @SerializedName("card_security_code")
    private String cardSecurityCode;

    @SerializedName("save_to_locker")
    private boolean saveToLocker;

    @SerializedName("redirect_after_payment")
    private boolean redirectAfterPayment;

    @SerializedName("format")
    private String format;

    @SerializedName("name_on_card")
    private String nameOnCard;

    @SerializedName("is_emi")
    private boolean isEmi;

    @SerializedName("emi_bank")
    private String emiBank;

    @SerializedName("emi_tenure")
    private String emiTenure;

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpiryMonth() {
        return cardExpiryMonth;
    }

    public void setCardExpiryMonth(String cardExpiryMonth) {
        this.cardExpiryMonth = cardExpiryMonth;
    }

    public String getCardExpiryYear() {
        return cardExpiryYear;
    }

    public void setCardExpiryYear(String cardExpiryYear) {
        this.cardExpiryYear = cardExpiryYear;
    }

    public String getCardSecurityCode() {
        return cardSecurityCode;
    }

    public void setCardSecurityCode(String cardSecurityCode) {
        this.cardSecurityCode = cardSecurityCode;
    }

    public boolean isSaveToLocker() {
        return saveToLocker;
    }

    public void setSaveToLocker(boolean saveToLocker) {
        this.saveToLocker = saveToLocker;
    }

    public boolean isRedirectAfterPayment() {
        return redirectAfterPayment;
    }

    public void setRedirectAfterPayment(boolean redirectAfterPayment) {
        this.redirectAfterPayment = redirectAfterPayment;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public boolean isEmi() {
        return isEmi;
    }

    public void setEmi(boolean emi) {
        isEmi = emi;
    }

    public String getEmiBank() {
        return emiBank;
    }

    public void setEmiBank(String emiBank) {
        this.emiBank = emiBank;
    }

    public String getEmiTenure() {
        return emiTenure;
    }

    public void setEmiTenure(String emiTenure) {
        this.emiTenure = emiTenure;
    }
}
