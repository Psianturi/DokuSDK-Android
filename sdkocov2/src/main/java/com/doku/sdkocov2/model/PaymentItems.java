package com.doku.sdkocov2.model;

/**
 * Created by zaki on 2/17/16.
 */


public class PaymentItems {

    String dataMerchantCode;
    String dataWords;
    String dataEmail;
    String dataTransactionID;
    String dataAmount;
    String dataCurrency;
    String dataMerchantChain;
    String dataBasket;
    String dataSessionID;
    String dataImei;
    String mobilePhone;
    String publicKey;
    String customerID;
    String tokenPayment;
    Boolean isProduction = null;

    public PaymentItems() {}

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getTokenPayment() {
        return tokenPayment;
    }

    public void setTokenPayment(String tokenPayment) {
        this.tokenPayment = tokenPayment;
    }

    public Boolean getIsProduction() {
        return isProduction;
    }

    public void isProduction(Boolean isProduction) {
        this.isProduction = isProduction;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getDataImei() {
        return dataImei;
    }

    public void setDataImei(String dataImei) {
        this.dataImei = dataImei;
    }

    public String getDataMerchantCode() {
        return dataMerchantCode;
    }

    public void setDataMerchantCode(String datamerchantCode) {
        this.dataMerchantCode = datamerchantCode;
    }

    public String getDataWords() {
        return dataWords;
    }

    public void setDataWords(String dataWords) {
        this.dataWords = dataWords;
    }

    public String getDataTransactionID() {
        return dataTransactionID;
    }

    public void setDataTransactionID(String datatransactionID) {
        this.dataTransactionID = datatransactionID;
    }

    public String getDataAmount() {
        return dataAmount;
    }

    public void setDataAmount(String dataAmount) {
        this.dataAmount = dataAmount;
    }

    public String getDataCurrency() {
        return dataCurrency;
    }

    public void setDataCurrency(String dataCurrency) {
        this.dataCurrency = dataCurrency;
    }

    public String getDataMerchantChain() {
        return dataMerchantChain;
    }

    public void setDataMerchantChain(String dataMerchantChain) {
        this.dataMerchantChain = dataMerchantChain;
    }

    public String getDataBasket() {
        return dataBasket;
    }

    public void setDataBasket(String dataBasket) {
        this.dataBasket = dataBasket;
    }

    public String getDataSessionID() {
        return dataSessionID;
    }

    public void setDataSessionID(String dataSessionID) {
        this.dataSessionID = dataSessionID;
    }

    public String getDataEmail() {
        return dataEmail;
    }

    public void setDataEmail(String dataEmail) {
        this.dataEmail = dataEmail;
    }
}
