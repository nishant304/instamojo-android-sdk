package com.instamojo.android.models;

import com.google.gson.annotations.SerializedName;

public class PaymentOptions {

    @SerializedName("card_options")
    private CardOptions cardOptions;

    @SerializedName("netbanking_options")
    private NetBankingOptions netBankingOptions;

    @SerializedName("emi_options")
    private EMIOptions emiOptions;

    @SerializedName("wallet_options")
    private WalletOptions walletOptions;

    @SerializedName("upi_options")
    private UPIOptions upiOptions;

    public CardOptions getCardOptions() {
        return cardOptions;
    }

    public void setCardOptions(CardOptions cardOptions) {
        this.cardOptions = cardOptions;
    }

    public NetBankingOptions getNetBankingOptions() {
        return netBankingOptions;
    }

    public void setNetBankingOptions(NetBankingOptions netBankingOptions) {
        this.netBankingOptions = netBankingOptions;
    }

    public EMIOptions getEmiOptions() {
        return emiOptions;
    }

    public void setEmiOptions(EMIOptions emiOptions) {
        this.emiOptions = emiOptions;
    }

    public WalletOptions getWalletOptions() {
        return walletOptions;
    }

    public void setWalletOptions(WalletOptions walletOptions) {
        this.walletOptions = walletOptions;
    }

    public UPIOptions getUpiOptions() {
        return upiOptions;
    }

    public void setUpiOptions(UPIOptions upiOptions) {
        this.upiOptions = upiOptions;
    }
}
