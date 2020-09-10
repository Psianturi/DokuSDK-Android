package com.doku.samplesdkapps;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.interfaces.iPaymentCallback;
import com.doku.sdkocov2.model.LayoutItems;
import com.doku.sdkocov2.model.PaymentItems;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    DirectSDK directSDK;
    String invoiceNumber;
    JSONObject respongetTokenSDK;
    String jsonRespon;
    Button buttonCC, buttonDOKU, buttonMandiri, buttonVA, buttonAlfa, buttonFirstPay, buttonSecondPay;
    int REQUEST_CODE_MANDIRI = 3;
    int REQUEST_CODE_VIRTUALACCOUNT = 4;
    TelephonyManager telephonyManager;
    String responseToken, Challenge1, Challenge2, Challenge3, debitCard;
    String tokenPayment = null;
    String customerID = null;
    String deviceID = "";
    EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        deviceID = "DEMOSDK-"+ AppsUtil.generateInvoiceId();
        System.out.println("DEVICE ID " + deviceID);
        initiateToolbar();
        initiateLayout();
    }

    private void initiateToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ico_merchant);
        toolbar.setTitle("Payment");
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        AppsUtil.applyFont(getApplicationContext(), mTitle, "fonts/dokuregular.ttf");
        setSupportActionBar(toolbar);
    }

    private void initiateLayout() {
        emailInput = findViewById(R.id.emailInput);
        emailInput.setText("jauhap@doku.com");
        buttonCC = findViewById(R.id.buttonCCRegular);
        buttonFirstPay = findViewById(R.id.buttonCCFirstPay);
        buttonSecondPay = findViewById(R.id.buttonCCSecondPay);
        buttonDOKU = findViewById(R.id.buttonDOKU);
        buttonMandiri = findViewById(R.id.buttonMandiriClick);
        buttonVA = findViewById(R.id.buttonVA);
        buttonAlfa = findViewById(R.id.buttonAlfaVA);

        buttonCC.setOnClickListener(this);
        buttonFirstPay.setOnClickListener(this);
        buttonSecondPay.setOnClickListener(this);
        buttonDOKU.setOnClickListener(this);
        buttonMandiri.setOnClickListener(this);
        buttonVA.setOnClickListener(this);
        buttonAlfa.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCCRegular:
                connectSDK(1);
                break;
            case R.id.buttonCCFirstPay:
                connectFirstPay(1);
                break;
            case R.id.buttonCCSecondPay:
                connectSecondPay(1);
                break;
            case R.id.buttonDOKU:
                connectSDK(2);
                break;
            case R.id.buttonMandiriClick:
                Intent intentMandiriPay = new Intent(MainActivity.this, MandiriClickPay.class);
                startActivityForResult(intentMandiriPay, REQUEST_CODE_MANDIRI);
                break;
            case R.id.buttonVA:
                Intent intentVA = new Intent(MainActivity.this, VirtualAccount.class);
                startActivityForResult(intentVA, REQUEST_CODE_VIRTUALACCOUNT);
                break;
            case R.id.buttonAlfaVA:
                Intent intentAlfaVA = new Intent(MainActivity.this, VirtualAccountAlfa.class);
                startActivityForResult(intentAlfaVA, REQUEST_CODE_VIRTUALACCOUNT);
                break;
        }
    }

    private void BackToMainPage(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void connectSDK(int menuPaymentChannel) {
        invoiceNumber = String.valueOf(AppsUtil.nDigitRandomNo(10));
        directSDK = new DirectSDK();
        System.out.println("invoiceNumber regular" + invoiceNumber);

        PaymentItems cardDetails;
        cardDetails = InputCard();
        directSDK.setCart_details(cardDetails);
        directSDK.setPaymentChannel(menuPaymentChannel);
        directSDK.getResponse(new iPaymentCallback() {

            @Override
            public void onSuccess(final String text) {
                try {
                    respongetTokenSDK = new JSONObject(text);
                    if (respongetTokenSDK.getString("res_response_code").equalsIgnoreCase("0000")) {
                        jsonRespon = text;
                        Log.d("json", text);
                        new RequestPayment().execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final String text) {
                Log.d("MAIN ACTIVITY", "ON ERORR : "+text);
                BackToMainPage();
            }

            @Override
            public void onException(Exception eSDK) {
                eSDK.printStackTrace();
            }
        }, getApplicationContext());
    }

    private void connectFirstPay(int menuPaymentChannel) {
        invoiceNumber = String.valueOf(AppsUtil.nDigitRandomNo(10));

        Log.d("invoice connectFirstPay", invoiceNumber);
        customerID = String.valueOf(AppsUtil.nDigitRandomNo(8));

        Log.d("customerID", customerID);
        directSDK = new DirectSDK();

        PaymentItems paymentItems = new PaymentItems();
        paymentItems.setDataBasket("[{\"name\":\"sayur\",\"amount\":\"10000.00\",\"quantity\":\"1\",\"subtotal\":\"10000.00\"},{\"name\":\"buah\",\"amount\":\"10000.00\",\"quantity\":\"1\",\"subtotal\":\"10000.00\"}]");
        paymentItems.setDataCurrency("360");
        paymentItems.setDataAmount("15000.00");
        paymentItems.setDataWords(AppsUtil.SHA1("15000.00" + "3019" + "Z13iYrqTb78J" + invoiceNumber + 360 + deviceID));
        paymentItems.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAudXdaD8Z57GQ445NnzU8UKvfMFeXeBCF9KdiyfDIr74uKy7gtQS45CiQiN+4d8HbZvDI7/Q6EpGleXu5JYKrhEVGqMHptsWAvNKEIBBeKXcuxzA4f8vbORHU8WZJ5mdNQ/QIOA/Q+WT1njt2NehKQMFqh92/oqFij5eByTuF8dk62btjPJOdNGlpucH0OefPfDtbfYzIZBDsbqG1TbzFxPFK58MQ5jDhqjk7/JBpbh4aCf/z22XDoa87d2hd5rnGtsXqLIo5nG8PuMVfG2UwlvSbVowCOLCUwa0ZHEklnE4e5TqGoGBUE6X+6EDuA4ZwpOA77gRBu6ceDoMDnWnL7QIDAQAB");
        paymentItems.setDataMerchantCode("3019");
        paymentItems.setDataEmail(emailInput.getText().toString());
        paymentItems.setDataMerchantChain("NA");
        paymentItems.setDataSessionID(String.valueOf(AppsUtil.nDigitRandomNo(9)));
        paymentItems.setDataTransactionID(invoiceNumber);
        paymentItems.setDataImei(deviceID);
        paymentItems.setMobilePhone("08123123112");
        paymentItems.isProduction(false);
        paymentItems.setCustomerID(customerID);
        directSDK.setCart_details(paymentItems);
        directSDK.setPaymentChannel(menuPaymentChannel);

        directSDK.getResponse(new iPaymentCallback() {
            @Override
            public void onSuccess(final String text) {
                try {
                    respongetTokenSDK = new JSONObject(text);
                    if (respongetTokenSDK.getString("res_response_code").equalsIgnoreCase("0000")) {
                        jsonRespon = text;
                        Log.d("json", text);
                        new RequestPayment().execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final String text) {
                Toast.makeText(getApplicationContext(), "from SDK " + text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Exception eSDK) {
                eSDK.printStackTrace();
            }
        }, getApplicationContext());

        tokenPayment = null;
        customerID = null;
    }

    private void connectSecondPay(int menuPaymentChannel) {
        invoiceNumber = String.valueOf(AppsUtil.nDigitRandomNo(10));
        directSDK = new DirectSDK();
        Log.d(" connectSecondPay", invoiceNumber);

        PaymentItems paymentItems = new PaymentItems();
        paymentItems.setDataBasket("[{\"name\":\"sayur\",\"amount\":\"10000.00\",\"quantity\":\"1\",\"subtotal\":\"10000.00\"},{\"name\":\"buah\",\"amount\":\"10000.00\",\"quantity\":\"1\",\"subtotal\":\"10000.00\"}]");
        paymentItems.setDataCurrency("360");
        paymentItems.setDataAmount("15000.00");
        paymentItems.setDataWords(AppsUtil.SHA1("15000.00" + "3019" + "Z13iYrqTb78J" + invoiceNumber + 360 + deviceID));
        paymentItems.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAudXdaD8Z57GQ445NnzU8UKvfMFeXeBCF9KdiyfDIr74uKy7gtQS45CiQiN+4d8HbZvDI7/Q6EpGleXu5JYKrhEVGqMHptsWAvNKEIBBeKXcuxzA4f8vbORHU8WZJ5mdNQ/QIOA/Q+WT1njt2NehKQMFqh92/oqFij5eByTuF8dk62btjPJOdNGlpucH0OefPfDtbfYzIZBDsbqG1TbzFxPFK58MQ5jDhqjk7/JBpbh4aCf/z22XDoa87d2hd5rnGtsXqLIo5nG8PuMVfG2UwlvSbVowCOLCUwa0ZHEklnE4e5TqGoGBUE6X+6EDuA4ZwpOA77gRBu6ceDoMDnWnL7QIDAQAB");
        paymentItems.setDataMerchantCode("3019");

        paymentItems.setDataMerchantChain("NA");
        paymentItems.setDataSessionID(String.valueOf(AppsUtil.nDigitRandomNo(9)));
        paymentItems.setDataTransactionID(invoiceNumber);
        paymentItems.setDataImei(deviceID);
        paymentItems.setMobilePhone("08123123112");
        paymentItems.isProduction(false);
        paymentItems.setCustomerID("63994583");
        paymentItems.setTokenPayment("32a381a0e37771a047af9a1ee870f1e6effaf2ad");
        directSDK.setCart_details(paymentItems);

        directSDK.setPaymentChannel(menuPaymentChannel);
        directSDK.getResponse(new iPaymentCallback() {
            @Override
            public void onSuccess(final String text) {
                try {
                    respongetTokenSDK = new JSONObject(text);

                    if (respongetTokenSDK.getString("res_response_code").equalsIgnoreCase("0000")) {
                        jsonRespon = text;
                        Log.d("json", text);
                        new RequestPayment().execute();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final String text) {
                Toast.makeText(getApplicationContext(), "from SDK " + text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Exception eSDK) {
                eSDK.printStackTrace();
            }
        }, getApplicationContext());

        tokenPayment = null;
        customerID = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 3 && data.getStringExtra("responseValue") != null) {
                responseToken = data.getStringExtra("responseValue");
                Challenge1 = data.getStringExtra("challenge1");
                Challenge2 = data.getStringExtra("challenge2");
                Challenge3 = data.getStringExtra("challenge3");
                debitCard = data.getStringExtra("debitCard");
                new MandiriPayment().execute();
            } else if (requestCode == 4) {
                Toast.makeText(getApplicationContext(), data.getStringExtra("data"), Toast.LENGTH_SHORT).show();
                Log.d("Memilih : ", "payment Channel Virtual Account");
                Log.d("data VA", data.getStringExtra("data"));
            } else {
                Log.d("data", "KOSONG");
            }
        }
    }

    private PaymentItems InputCard() {
        PaymentItems paymentItems = new PaymentItems();
        paymentItems.setDataAmount("15000.00");
        paymentItems.setDataBasket("[{\"name\":\"sayur\",\"amount\":\"10000.00\",\"quantity\":\"1\",\"subtotal\":\"10000.00\"},{\"name\":\"buah\",\"amount\":\"10000.00\",\"quantity\":\"1\",\"subtotal\":\"10000.00\"}]");
        paymentItems.setDataCurrency("360");
        paymentItems.setDataEmail(emailInput.getText().toString());

        //paymentItems.setDataWords(AppsUtil.SHA1("15000.00" + "3019" + "Z13iYrqTb78J" + invoiceNumber + 360 + deviceID));
        //paymentItems.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAudXdaD8Z57GQ445NnzU8UKvfMFeXeBCF9KdiyfDIr74uKy7gtQS45CiQiN+4d8HbZvDI7/Q6EpGleXu5JYKrhEVGqMHptsWAvNKEIBBeKXcuxzA4f8vbORHU8WZJ5mdNQ/QIOA/Q+WT1njt2NehKQMFqh92/oqFij5eByTuF8dk62btjPJOdNGlpucH0OefPfDtbfYzIZBDsbqG1TbzFxPFK58MQ5jDhqjk7/JBpbh4aCf/z22XDoa87d2hd5rnGtsXqLIo5nG8PuMVfG2UwlvSbVowCOLCUwa0ZHEklnE4e5TqGoGBUE6X+6EDuA4ZwpOA77gRBu6ceDoMDnWnL7QIDAQAB");
        //paymentItems.setDataMerchantCode("3019");

        paymentItems.setDataWords(AppsUtil.SHA1("15000.00" + "5262" + "zqH26B7cYo0J" + invoiceNumber + 360 + deviceID));
        paymentItems.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAivRSxN2wgd0ienmdEs3ZiBbmsC9Wul1cpIb5PJbR0cwSy4HDoX6UeLYkM4IAwKaVkx5L52tNYn1gBDaK3BWvlhQ97z7olKmRjTNYaQSkY9I+KyQ9zsjJIWAkVsVHBxb1GV5rx6KaRW84ieLPy/ds5snPcTUyRkrnHVWRGSw7fA/0b4hEgRWRXKW2VdSkqswo84PrB3xo1Dx/lJM3TNHjmXO4quM70s6RfHnKfbQLm8BdQjYQAzwNcb2LnO1xYZ33zANy9LE6D/VHdl6R8eCH9EJPNc+n+G/4UPoJIQ7fKs1DOwEQsPIJdoCGEXL4KsyEOdMCAfnQgmHyjW7vEg2Q/wIDAQAB");
        paymentItems.setDataMerchantCode("5262");

        paymentItems.setDataMerchantChain("NA");
        paymentItems.setDataSessionID(String.valueOf(AppsUtil.nDigitRandomNo(9)));
        paymentItems.setDataTransactionID(invoiceNumber);
        paymentItems.setDataImei(deviceID);
        paymentItems.setMobilePhone("08123123112");
        paymentItems.setDataEmail("JhonDoe@doku.com");
        paymentItems.isProduction(false);
        return paymentItems;
    }


    private LayoutItems setLayout() {

        LayoutItems layoutItems = new LayoutItems();
        layoutItems.setFontPath("fonts/dinbold.ttf");
        layoutItems.setToolbarColor("#289c64");
        layoutItems.setToolbarTextColor("#FFFFFF");
        layoutItems.setFontColor("#121212");
        layoutItems.setBackgroundColor("#eaeaea");
        layoutItems.setLabelTextColor("#9a9a9a");
        layoutItems.setButtonBackground(getResources().getDrawable(R.drawable.button_orange));
        layoutItems.setButtonTextColor("#FFFFFF");

        return layoutItems;
    }

    private class RequestPayment extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Mohon Tunggu ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject defResp = null;
            try {
                ContentValues data = new ContentValues();
                data.put("data", jsonRespon);

                String conResult = ApiConnection.httpsConnection(MainActivity.this, Constants.URL_CHARGING_DOKU_DAN_CC, data);

                System.out.println("CON RESULT " + conResult);
                defResp = new JSONObject(conResult);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return defResp;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();

            if (json != null) {
                try {
                    if (json.getString("res_response_code").equalsIgnoreCase("0000") && json != null) {
                        Intent intent = new Intent(getApplicationContext(), ResultPayment.class);
                        intent.putExtra("data", json.toString());
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), " PAYMENT SUKSES", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultPayment.class);
                        intent.putExtra("data", json.toString());
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "PAYMENT ERROR", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }
        }
    }

    private class MandiriPayment extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Mohon Tunggu ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject defResp = null;

            try {
                JSONObject jGroup = new JSONObject();// /main Object
                invoiceNumber = String.valueOf(AppsUtil.nDigitRandomNo(10));

                jGroup.put("req_transaction_id", invoiceNumber);
                jGroup.put("req_payment_channel", "02");
                jGroup.put("req_card_number", debitCard);
                jGroup.put("req_device_id", deviceID);
                jGroup.put("req_challenge_code_1", Challenge1);
                jGroup.put("req_challenge_code_2", Challenge2);
                jGroup.put("req_challenge_code_3", Challenge3);
                jGroup.put("req_response_token", responseToken);

                ContentValues data = new ContentValues();
                data.put("data", jGroup.toString());

                // Getting JSON from URL
                String conResult = ApiConnection.httpsConnection(MainActivity.this, Constants.URL_CHARGING_MANDIRI_CLICKPAY, data);
                Log.d("DATA PAYMENT", conResult);

                defResp = new JSONObject(conResult);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return defResp;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();

            if (json != null) {
                try {
                    if (json.getString("res_response_code").equalsIgnoreCase("0000") && json != null) {
                        Intent intent = new Intent(getApplicationContext(), ResultPayment.class);
                        intent.putExtra("data", json.toString());
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), " PAYMENT SUKSES", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultPayment.class);
                        intent.putExtra("data", json.toString());
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "PAYMENT ERROR", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
    }
}
