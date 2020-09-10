package com.doku.sdkocov2.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.TextView;
import com.doku.sdkocov2.DirectSDK;
import org.bouncycastle.util.encoders.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.crypto.Cipher;

/**
 * Created by zaki on 12/16/15.
 */
public class SDKUtils {
    private static String regexEmail = "^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?$";
    private static String regexSafeString = "^[\\p{L}\\p{N} .'-]+${0,1024}";
    private static String regexDWPIN = "^[0-9]{4}";
    private static String regexNumber = "[0-9]+";
    private static String regexNumberCC = "^(d+.)?d+$";
    private static String regexCvv = "^[0-9]+${3,4}";

    public static void applyFont(Context context, TextView textView, String fontPath) {
        try {
            textView.setTypeface(Typeface.createFromAsset(context.getAssets(), fontPath));
        } catch (Exception e) {
            Log.e("Font Apply", String.format("Error occured when trying to apply %s font for %s view", fontPath, textView));
            e.printStackTrace();
        }
    }

    public static String validateValue(String input, Character type, Integer minLgt, Integer maxLgt) {
        try {
            if (input == null || input.equals(""))
                return Constants.VALIDATE_EMPTY_VALUE;

            Pattern pattern;
            if (type.equals('S')) {
                // E FOR SAFE STRING
                pattern = Pattern.compile(regexSafeString);
                if (minLgt != null)
                    if (input.length() < minLgt) {
                        return Constants.VALIDATE_INVALID_FORMAT;
                    }
                if (maxLgt != null)
                    if (input.length() > maxLgt) {
                        return Constants.VALIDATE_INVALID_FORMAT;
                    }
                if (pattern.matcher(input).matches()) {
                    return Constants.VALIDATE_SUCCESS;
                } else {
                    return Constants.VALIDATE_INVALID_FORMAT;
                }

            } else if (type.equals('N')) { // N for ONLY NUMBER

                pattern = Pattern.compile(regexDWPIN);

                if (pattern.matcher(input).matches()) {
                    return Constants.VALIDATE_SUCCESS;
                } else {
                    return Constants.VALIDATE_INVALID_FORMAT;
                }
            } else if (type.equals('C')) { // N for ONLY NUMBER

                pattern = Pattern.compile(regexCvv);

                if (pattern.matcher(input).matches()) {
                    return Constants.VALIDATE_SUCCESS;
                } else {
                    return Constants.VALIDATE_INVALID_FORMAT;
                }
            } else if (type.equals('E')) {// E FOR EMAIL
                pattern = Pattern.compile(regexEmail);
                if (pattern.matcher(input).matches()) {
                    return Constants.VALIDATE_SUCCESS;
                } else {
                    return Constants.VALIDATE_INVALID_FORMAT;
                }
            } else if (type.equals('M')) {// M FOR PHONE NUMBER
                if (PhoneNumberUtils.isGlobalPhoneNumber(input)) {
                    return Constants.VALIDATE_SUCCESS;
                } else {
                    return Constants.VALIDATE_INVALID_FORMAT;
                }
            } else if (type.equals('P')) {// P FOR PIN
                pattern = Pattern.compile(regexDWPIN);
                if (pattern.matcher(input).matches()) {
                    return Constants.VALIDATE_SUCCESS;
                } else {
                    return Constants.VALIDATE_INVALID_FORMAT;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Constants.VALIDATE_UE;
    }

    public static String validateValue(String input, Character type) {
        return validateValue(input, type, null, null);
    }

    public static String SHA1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (Throwable th) {
            th.printStackTrace();
            return "";
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String createClientResponse(int code, String msg) {
        return "{\"res_response_code\":\"" + code + "\",\"res_response_msg\":\"" + msg
                + "\"}";
    }

    public static String createErrorResponse(int code, String msg) {
        JSONObject jGroup = new JSONObject();// /sub Object
        JSONObject jResult = new JSONObject();// main object
        JSONArray jArray = new JSONArray();// /ItemDetail jsonArray

        try {
            jGroup.put("errorCode", code);
            jGroup.put("errorMessage", msg);

            jArray.put(jGroup);

            // itemDetail Name is JsonArray Name
            jResult.put("responseMessage", jArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jResult.toString();
    }

    public static String createRequestTokenCC(String data_merchant_code, String data_transaction_id, String data_payment_channel,
                                              String data_amount, String data_currency, String data_date, String data_number,
                                              String data_name, String data_secret, String data_chain_merchant, String data_basket,
                                              String data_mobile_phone, String data_email, String data_words, String data_session_id, String data_device_id) {

        JSONObject jGroup = new JSONObject();// /main Object
        try {
            jGroup.put("req_merchant_code", data_merchant_code);
            jGroup.put("req_transaction_id", data_transaction_id);
            jGroup.put("req_payment_channel", data_payment_channel);
            jGroup.put("req_amount", data_amount);
            jGroup.put("req_currency", data_currency);
            jGroup.put("req_date", data_date);
            jGroup.put("req_number", data_number);
            jGroup.put("req_name", data_name);
            jGroup.put("req_secret", data_secret);
            jGroup.put("req_chain_merchant", data_chain_merchant);
            jGroup.put("req_access_type", "M");
            jGroup.put("req_basket", data_basket);
            jGroup.put("req_mobile_phone", data_mobile_phone);
            jGroup.put("req_email", data_email);
            jGroup.put("req_words", data_words);
            jGroup.put("req_session_id", data_session_id);
            jGroup.put("req_device_id", data_device_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jGroup.toString();
    }

    public static String createRequestFirstPay(String data_merchant_code, String data_transaction_id, String data_payment_channel,
                                               String data_amount, String data_currency, String data_date, String data_number,
                                               String data_name, String data_secret, String data_chain_merchant, String data_basket,
                                               String data_mobile_phone, String data_email, String data_words, String data_session_id, String data_device_id, String pairing_code,
                                               String save_customer) {

        JSONObject jGroup = new JSONObject();// /main Object
        try {
            jGroup.put("req_merchant_code", data_merchant_code);
            jGroup.put("req_transaction_id", data_transaction_id);
            jGroup.put("req_payment_channel", data_payment_channel);
            jGroup.put("req_amount", data_amount);
            jGroup.put("req_currency", data_currency);
            jGroup.put("req_date", data_date);
            jGroup.put("req_number", data_number);
            jGroup.put("req_name", data_name);
            jGroup.put("req_secret", data_secret);
            jGroup.put("req_chain_merchant", data_chain_merchant);
            jGroup.put("req_access_type", "M");
            jGroup.put("req_basket", data_basket);
            jGroup.put("req_mobile_phone", data_mobile_phone);
            jGroup.put("req_email", data_email);
            jGroup.put("req_words", data_words);
            jGroup.put("req_session_id", data_session_id);
            jGroup.put("req_device_id", data_device_id);
            jGroup.put("req_pairing_code", pairing_code);
            jGroup.put("req_save_customer", save_customer);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jGroup.toString();
    }

    public static String createRequestSecondPay(String data_merchant_code, String data_transaction_id, String data_payment_channel,
                                                String data_amount, String data_currency, String data_secret, String data_chain_merchant, String data_basket,
                                                String data_words, String data_session_id, String data_device_id, String pairing_code, String token_payment) {

        JSONObject jGroup = new JSONObject();// /main Object
        try {
            jGroup.put("req_merchant_code", data_merchant_code);
            jGroup.put("req_transaction_id", data_transaction_id);
            jGroup.put("req_payment_channel", data_payment_channel);
            jGroup.put("req_amount", data_amount);
            jGroup.put("req_currency", data_currency);
            jGroup.put("req_secret", data_secret);
            jGroup.put("req_chain_merchant", data_chain_merchant);
            jGroup.put("req_access_type", "M");
            jGroup.put("req_basket", data_basket);
            jGroup.put("req_token_payment", token_payment);
            jGroup.put("req_words", data_words);
            jGroup.put("req_session_id", data_session_id);
            jGroup.put("req_device_id", data_device_id);
            jGroup.put("req_pairing_code", pairing_code);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jGroup.toString();
    }

    public static String createRequestTokenWallet(String data_merchant_code, String data_transaction_id, String data_payment_channel,
                                                  String data_amount, String data_currency, String data_chain_merchant, String data_basket,
                                                  String data_words, String data_session_id, String data_device_id, String username, String password) {


        JSONObject jGroup = new JSONObject();// /main Object

        try {
            jGroup.put("req_merchant_code", data_merchant_code);
            jGroup.put("req_transaction_id", data_transaction_id);
            jGroup.put("req_payment_channel", data_payment_channel);
            jGroup.put("req_amount", data_amount);
            jGroup.put("req_currency", data_currency);
            jGroup.put("req_chain_merchant", data_chain_merchant);
            jGroup.put("req_access_type", "M");
            jGroup.put("req_basket", data_basket);
            jGroup.put("req_words", data_words);
            jGroup.put("req_session_id", data_session_id);
            jGroup.put("req_device_id", data_device_id);
            jGroup.put("req_doku_id", username);
            jGroup.put("req_doku_pass", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jGroup.toString();

    }

    public static String createRequestCashWallet(String channelCode, String customerPin, String inquiryCode, String custName,
                                                 String custEmail, String dokuID, String tokenID, String pairingCode, String merchantWords) {
        JSONObject jGroup = new JSONObject();// /main Object

        JSONObject jSub = new JSONObject();// /main Object

        try {


            jSub.put("req_token_id", tokenID);
            jSub.put("req_pairing_code", pairingCode);
            jSub.put("req_words", merchantWords);

            jGroup.put("req_channel_code", channelCode);
            jGroup.put("req_customer_pin", customerPin);
            jGroup.put("req_inquiry_code", inquiryCode);
            jGroup.put("req_customer_name", custName);
            jGroup.put("req_customer_email", custEmail);
            jGroup.put("req_doku_id", dokuID);

            jSub.put("req_dokuwallet", jGroup);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return jSub.toString();
    }

    public static String createRequestCashWallet(String channelCode, String customerPin, String inquiryCode, String custName,
                                                 String custEmail, String dokuID, String tokenID, String pairingCode, String merchantWords, String promoID) {
        JSONObject jGroup = new JSONObject();// /main Object

        JSONObject jSub = new JSONObject();// /main Object

        try {


            jSub.put("req_token_id", tokenID);
            jSub.put("req_pairing_code", pairingCode);
            jSub.put("req_words", merchantWords);

            jGroup.put("req_channel_code", channelCode);
            jGroup.put("req_customer_pin", customerPin);
            jGroup.put("req_inquiry_code", inquiryCode);
            jGroup.put("req_customer_name", custName);
            jGroup.put("req_customer_email", custEmail);
            jGroup.put("req_doku_id", dokuID);
            jGroup.put("req_promotion_id", promoID);


            jSub.put("req_dokuwallet", jGroup);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return jSub.toString();
    }

    public static String createRegisterCCWallet(String tokenID, String pairingCode, String merchantWords, String expiryDate,
                                                String cardNumber, String cardHolder, String secretNumber, String mobilePhone,
                                                String emailUser,
                                                String channelCode, String inquiryCode, String dokuID) {
        JSONObject jGroup = new JSONObject();// /main Object

        JSONObject jSub = new JSONObject();// /main Object

        try {


            jSub.put("req_token_id", tokenID);
            jSub.put("req_pairing_code", pairingCode);
            jSub.put("req_words", merchantWords);

            jGroup.put("CC_EXPIRYDATE", expiryDate);
            jGroup.put("CC_CARDNUMBER", cardNumber);
            jGroup.put("CC_NAME", cardHolder);
            jGroup.put("CC_CVV", secretNumber);
            jGroup.put("CC_MOBILEPHONE", mobilePhone);
            jGroup.put("CC_EMAIL", emailUser);

            jGroup.put("req_channel_code", channelCode);
            jGroup.put("req_inquiry_code", inquiryCode);
            jGroup.put("req_doku_id", dokuID);


            jSub.put("req_dokuwallet", jGroup);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return jSub.toString();
    }

    public static String createResponseCashWallet(String tokenID, String pairingCode, String responseMsg, String responseCode, String deviceID, String dataAmount,
                                                  String tokenCode, String transactionID, String name, String email, String mobilePhone) {

        JSONObject jGroup = new JSONObject();// /main Object

        try {
            jGroup.put("res_token_id", tokenID);
            jGroup.put("res_pairing_code", pairingCode);
            jGroup.put("res_response_msg", responseMsg);
            jGroup.put("res_response_code", responseCode);
            jGroup.put("res_device_id", deviceID);
            jGroup.put("res_amount", dataAmount);
            jGroup.put("res_token_code", tokenCode);
            jGroup.put("res_transaction_id", transactionID);
            jGroup.put("res_name", name);
            jGroup.put("res_data_email", email);
            jGroup.put("res_payment_channel", DirectSDK.loginModel.getPaymentChannelLogin());
            jGroup.put("res_data_mobile_phone", mobilePhone);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return jGroup.toString();
    }

    public static String createRequestCCWallet(String channelCode, String inquiryCode, String dokuID,
                                               String linkID, String number, String date, String cvv,
                                               String tokenID, String pairingCode, String merchantWords, String custEmail, String custName) {

        JSONObject jGroup = new JSONObject();// /main Object

        JSONObject jSub = new JSONObject();// /main Object
        try {

            jSub.put("req_token_id", tokenID);
            jSub.put("req_pairing_code", pairingCode);
            jSub.put("req_words", merchantWords);

            jGroup.put("req_channel_code", channelCode);
            jGroup.put("req_inquiry_code", inquiryCode);
            jGroup.put("req_doku_id", dokuID);
            jGroup.put("req_link_id", linkID);
            jGroup.put("req_number", number);
            jGroup.put("req_date", date);
            jGroup.put("req_cvv", cvv);
            jGroup.put("req_customer_email", custEmail);
            jGroup.put("req_customer_name", custName);


            jSub.put("req_dokuwallet", jGroup);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return jSub.toString();
    }

    public static String createRequest3D(String tokenID, String pairingCode, String words) {
        JSONObject jGroup = new JSONObject();// /main Object

        try {
            jGroup.put("req_token_id", tokenID);
            jGroup.put("req_pairing_code", pairingCode);
            jGroup.put("req_words", words);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return jGroup.toString();
    }

    public static String createResponseCCReguler(String tokenID, String pairingCode, String responseMsg, String responseCode, String deviceID, String dataAmount,
                                                 String tokenCode, String transactionID, String emailUser, String nameUser, String mobilePhone) {

        JSONObject jGroup = new JSONObject();// /main Object

        try {
            jGroup.put("res_token_id", tokenID);
            jGroup.put("res_pairing_code", pairingCode);
            jGroup.put("res_response_msg", responseMsg);
            jGroup.put("res_response_code", responseCode);
            jGroup.put("res_device_id", deviceID);
            jGroup.put("res_amount", dataAmount);
            jGroup.put("res_token_code", tokenCode);
            jGroup.put("res_transaction_id", transactionID);
            jGroup.put("res_data_email", emailUser);
            jGroup.put("res_name", nameUser);
            jGroup.put("res_payment_channel", "15");
            jGroup.put("res_data_mobile_phone", mobilePhone);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return jGroup.toString();
    }

    public static String createResponseCCSecond(String tokenID, String pairingCode, String responseMsg, String responseCode, String deviceID, String dataAmount,
                                                String tokenCode, String transactionID, String emailUser, String mobilePhone) {

        JSONObject jGroup = new JSONObject();// /main Object

        try {
            jGroup.put("res_token_id", tokenID);
            jGroup.put("res_pairing_code", pairingCode);
            jGroup.put("res_response_msg", responseMsg);
            jGroup.put("res_response_code", responseCode);
            jGroup.put("res_device_id", deviceID);
            jGroup.put("res_amount", dataAmount);
            jGroup.put("res_token_code", tokenCode);
            jGroup.put("res_transaction_id", transactionID);
            jGroup.put("res_data_email", emailUser);
            jGroup.put("res_payment_channel", "15");
            jGroup.put("res_data_mobile_phone", mobilePhone);


        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return jGroup.toString();
    }

    public static String checkMerchantStatus(String amount, String currency, String device_id, String transaction_id, String merchant_code,
                                             String chain_merchant, String payment_channel, String customer_id, String words) {


        JSONObject jGroup = new JSONObject();// /main Object

        try {
            jGroup.put("req_amount", amount);
            jGroup.put("req_currency", currency);
            jGroup.put("req_device_id", device_id);
            jGroup.put("req_transaction_id", transaction_id);
            jGroup.put("req_merchant_code", merchant_code);
            jGroup.put("req_chain_merchant", chain_merchant);
            jGroup.put("req_payment_channel", payment_channel);
            jGroup.put("req_customer_id", customer_id);
            jGroup.put("req_words", words);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jGroup.toString();

    }

    public static String cardInquirySecond(String amount, String currency, String device_id, String transaction_id, String merchant_code,
                                           String chain_merchant, String payment_channel, String customer_id, String words, String token_payment) {


        JSONObject jGroup = new JSONObject();// /main Object

        try {
            jGroup.put("req_amount", amount);
            jGroup.put("req_currency", currency);
            jGroup.put("req_device_id", device_id);
            jGroup.put("req_transaction_id", transaction_id);
            jGroup.put("req_merchant_code", merchant_code);
            jGroup.put("req_chain_merchant", chain_merchant);
            jGroup.put("req_payment_channel", payment_channel);
            jGroup.put("req_customer_id", customer_id);
            jGroup.put("req_words", words);
            jGroup.put("req_token_payment", token_payment);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jGroup.toString();

    }

    public static String EYDNumberFormat(String amount) {

        BigDecimal newtext = new BigDecimal(amount);
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###,###.##", otherSymbols);
        String newAmount = df.format(newtext);
        return newAmount;
    }


    public static PublicKey getPublicKey(String publicKeyString) {
        byte[] sigBytes = Base64.decode(publicKeyString);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(sigBytes);
        KeyFactory keyFact = null;

        try {
            keyFact = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException var7) {
            var7.printStackTrace();
        }

        try {
            return keyFact.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public static String Encrypt(String plaintext, String publicKeyString) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        try {
            byte[] encodedStr = plaintext.getBytes();
            PublicKey e = getPublicKey(publicKeyString);
            Cipher cipher = Cipher.getInstance("RSA/None/OAEPWithSHA1AndMGF1Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, e);

            byte[] plainText = cipher.doFinal(encodedStr);
            String text = SDKBase64.encode(plainText);
            return text;
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

}
