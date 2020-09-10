package com.doku.sdkocov2.utils;

/**
 * Created by zaki on 12/18/15.
 */
public class Constants {

    public static final String VALIDATE_SUCCESS = "00";
    public static final String VALIDATE_EMPTY_VALUE = "01";
    public static final String VALIDATE_INVALID_FORMAT = "02";
    public static final String VALIDATE_UE = "03";

    //staging
    public static String ConfigUrlDev = "https://staging.doku.com";
    //production
    public static String ConfigUrlProd = "https://pay.doku.com";

    //staging
    public static final String URL_getTokenDev = ConfigUrlDev + "/api/payment/getToken";
    public static final String URL_CHECK3dStatusDev = ConfigUrlDev + "/api/payment/doCheck3DStatus";
    public static final String URL_prePaymentDev = ConfigUrlDev + "/api/payment/PrePayment";

    //prod
    public static final String URL_getTokenProd = ConfigUrlProd + "/api/payment/getToken";
    public static final String URL_CHECK3dStatusProd = ConfigUrlProd + "/api/payment/doCheck3DStatus";
    public static final String URL_prePaymentProd = ConfigUrlProd + "/api/payment/PrePayment";

    //tokenStatus
    public static final String URL_doCheckStatus = ConfigUrlDev + "/api/payment/doGetDataMerchantTokenization";
    public static final String URL_doCheckStatusProd = ConfigUrlProd + "/api/payment/doGetDataMerchantTokenization";

}
