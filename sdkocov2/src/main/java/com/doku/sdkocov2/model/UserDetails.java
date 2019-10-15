package com.doku.sdkocov2.model;

/**
 * Created by zaki on 2/25/16.
 */
public class UserDetails {


    String responseCode;
    String responseMsg;
    String dpMallID;
    String transIDMerchant;
    String dokuID;
    String customerName;
    String customerEmail;
    String paymentChannel;
    String inquiryCode;
    String listPromotion;
    String avatar;

    public String getListPromotion() {
        return listPromotion;
    }

    public void setListPromotion(String listPromotion) {
        this.listPromotion = listPromotion;
    }

    public String getInquiryCode() {
        return inquiryCode;
    }

    public void setInquiryCode(String inquiryCode) {
        this.inquiryCode = inquiryCode;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getDpMallID() {
        return dpMallID;
    }

    public void setDpMallID(String dpMallID) {
        this.dpMallID = dpMallID;
    }

    public String getTransIDMerchant() {
        return transIDMerchant;
    }

    public void setTransIDMerchant(String transIDMerchant) {
        this.transIDMerchant = transIDMerchant;
    }

    public String getDokuID() {
        return dokuID;
    }

    public void setDokuID(String dokuID) {
        this.dokuID = dokuID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

}
