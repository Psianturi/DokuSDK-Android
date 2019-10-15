package com.doku.samplesdkapps;

/**
 * Created by zaki on 3/28/16.
 */
public class Constants {

    //staging server mis
    //public static String ROOT_URL = "http://54.255.202.202/workspace/doku-library-staging/example-payment-mobile/";
    public static String ROOT_URL = "https://uat.api.doku.com/doku-library/example-payment-mobile/";

//    public static String ROOT_URL = "http://192.168.11.245:8081/example-payment-mobile/";

    //staging
    public static final String URL_CHARGING_DOKU_DAN_CC = ROOT_URL + "merchant-example.php";
    //public static final String URL_CHARGING_MANDIRI_CLICKPAY = ROOT_URL + "merchant-mandiri-example.php";
    public static final String URL_CHARGING_MANDIRI_CLICKPAY = ROOT_URL + "merchant-example.php";
    public static String URL_REQUEST_VACODE = ROOT_URL + "va_generate_staging.php";

}
