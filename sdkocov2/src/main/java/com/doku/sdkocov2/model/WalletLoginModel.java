package com.doku.sdkocov2.model;

/**
 * Created by zaki on 2/25/16.
 */
public class WalletLoginModel {
    String tokenID;
    String pairingCode;
    String responseCode;
    String responseMsg;
    String dataWallet;
    String tokenCode;
    String paymentChannelLogin;


    public String getPaymentChannelLogin() {
        return paymentChannelLogin;
    }

    public void setPaymentChannelLogin(String paymentChannelLogin) {
        this.paymentChannelLogin = paymentChannelLogin;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public String getPairingCode() {
        return pairingCode;
    }

    public void setPairingCode(String pairingCode) {
        this.pairingCode = pairingCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getDataWallet() {
        return dataWallet;
    }

    public void setDataWallet(String dataWallet) {
        this.dataWallet = dataWallet;
    }

    public String getTokenCode() {
        return tokenCode;
    }

    public void setTokenCode(String tokenCode) {
        this.tokenCode = tokenCode;
    }

}
