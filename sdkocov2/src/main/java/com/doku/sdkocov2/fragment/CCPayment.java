package com.doku.sdkocov2.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.doku.sdkocov2.BaseSDKOCO;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.interfaces.DrawableClickListener;
import com.doku.sdkocov2.interfaces.iSDKback;
import com.doku.sdkocov2.utils.CardModel;
import com.doku.sdkocov2.utils.Constants;
import com.doku.sdkocov2.utils.CustomEditText;
import com.doku.sdkocov2.utils.ExpiryDateFormatWatcher;
import com.doku.sdkocov2.utils.FourDigitCardFormatWatcher;
import com.doku.sdkocov2.utils.SDKConnections;
import com.doku.sdkocov2.utils.SDKUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zaki on 12/8/15.
 */
public class CCPayment extends Fragment implements iSDKback {

    //declare variable
    View view;
    EditText mobilePhoneValue, emailValue, cardHoldervalue, cardNumber;
    Button btnSubmit;
    CustomEditText cvvValue;
    EditText validValue;
    StringBuilder sb = new StringBuilder(19);
    String getCardHolder, getCardNumber, getCvv, getEmail, getPhoneNumber, getValidValue;
    int stateback;
    private Bundle bundle;
    String conResult;
    CheckBox checkSave;
    String saveCard = "SAVE";
    String pairingCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cc_payment, container, false);
        sb.setLength(0);

        //define layout
        mobilePhoneValue = (EditText) view.findViewById(R.id.mobilePhoneValue);
        emailValue = (EditText) view.findViewById(R.id.emailValue);
        cardHoldervalue = (EditText) view.findViewById(R.id.cardHoldervalue);
        cardNumber = (EditText) view.findViewById(R.id.cardNumber);
        cvvValue = (CustomEditText) view.findViewById(R.id.cvvValue);
        validValue = (EditText) view.findViewById(R.id.validValue);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        checkSave = (CheckBox) view.findViewById(R.id.check_save);

        if (DirectSDK.paymentItems.getMobilePhone() != null) {
            mobilePhoneValue.setText(DirectSDK.paymentItems.getMobilePhone().trim());
        }

        if (!TextUtils.isEmpty(DirectSDK.paymentItems.getDataEmail())) {
            emailValue.setText(DirectSDK.paymentItems.getDataEmail());
        }

        if (DirectSDK.paymentItems.getCustomerID() != null) {
            checkMerchant();
        }

        //setup layout
        setupLayout();

        //function validation for valid value date MM/YY and add 'slash' after month
        validValue.addTextChangedListener(new ExpiryDateFormatWatcher(validValue));

        //cardNumber separate after 4 digits
        cardNumber.addTextChangedListener(new FourDigitCardFormatWatcher());

        checkSave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    saveCard = "SAVE";
                } else {
                    saveCard = "UNSAVE";
                }
            }
        });

        //set button submit action
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();

            }
        });

        cvvValue.setDrawableClickListener(new DrawableClickListener() {


            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //Do something here
                        Toast.makeText(getContext(), "Insert last 3 number from back of your card", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }

        });

        bundle = getArguments();
        if (bundle != null) {
            stateback = bundle.getInt("stateback");

            BaseSDKOCO.backButton.setVisibility(View.VISIBLE);
            BaseSDKOCO.backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (stateback == 0) {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.main_frame, new ListPayChan());
                        ft.addToBackStack(null);
                        ft.commit();
                    } else {
                        getActivity().finish();
                    }

                }
            });
        }

        return view;
    }

    //validation input credit card
    private void attemptSubmit() {
        try {
            //declare variable validation
            boolean cancel = false;
            View focusView = null;
            String vldtRslt;

            //get value from edittext
            getCardHolder = cardHoldervalue.getText().toString().trim();
            getCardNumber = cardNumber.getText().toString().replace("-", "");
            getCvv = cvvValue.getText().toString();
            getEmail = emailValue.getText().toString();
            getPhoneNumber = mobilePhoneValue.getText().toString();
            getValidValue = validValue.getText().toString();


            //begin validation
            vldtRslt = SDKUtils.validateValue(getCardHolder, 'S', 0, 1024);
            if (!vldtRslt.equals(Constants.VALIDATE_SUCCESS)) {

                cardHoldervalue
                        .setError(getString(vldtRslt
                                .equals(Constants.VALIDATE_EMPTY_VALUE) ? R.string.error_field_required
                                : (vldtRslt
                                .equals(Constants.VALIDATE_INVALID_FORMAT) ? R.string.error_invalid_format
                                : R.string.error_invalid_format)));
                focusView = cardHoldervalue;
                cancel = true;
            }

            //get Card Type
            CardModel cardType = CardModel.fromCardNumber(getCardNumber.replace("-", ""));
            String getCardType = cardType.getDisplayName(getCardNumber.replace("-", ""));

            vldtRslt = SDKUtils.validateValue(getCardNumber.replace("-", ""), 'C');
            if (!vldtRslt.equals(Constants.VALIDATE_SUCCESS)) {

                cardNumber
                        .setError(getString(vldtRslt
                                .equals(Constants.VALIDATE_EMPTY_VALUE) ? R.string.error_field_required
                                : (vldtRslt
                                .equals(Constants.VALIDATE_INVALID_FORMAT) ? R.string.error_invalid_format
                                : R.string.error_invalid_format)));
                focusView = cardNumber;
                cancel = true;
            } else if (getCardType == null) {
                cardNumber.setError(getString(R.string.cc_not_valid));
                focusView = cardNumber;
                cancel = true;

            }


            vldtRslt = SDKUtils.validateValue(getCvv, 'C');
            if (!vldtRslt.equals(Constants.VALIDATE_SUCCESS)) {

                cvvValue
                        .setError(getString(vldtRslt
                                .equals(Constants.VALIDATE_EMPTY_VALUE) ? R.string.error_field_required
                                : (vldtRslt
                                .equals(Constants.VALIDATE_INVALID_FORMAT) ? R.string.error_invalid_format
                                : R.string.error_invalid_format)));
                focusView = cvvValue;
                cancel = true;
            }

            vldtRslt = SDKUtils.validateValue(getEmail, 'E');
            if (!vldtRslt.equals(Constants.VALIDATE_SUCCESS)) {
                emailValue
                        .setError(getString(vldtRslt
                                .equals(Constants.VALIDATE_EMPTY_VALUE) ? R.string.error_field_required
                                : (vldtRslt
                                .equals(Constants.VALIDATE_INVALID_FORMAT) ? R.string.error_invalid_email
                                : R.string.error_invalid_email)));

                focusView = emailValue;
                cancel = true;
            }

            vldtRslt = SDKUtils.validateValue(getPhoneNumber, 'M');
            if (!vldtRslt.equals(Constants.VALIDATE_SUCCESS)) {
                mobilePhoneValue
                        .setError(getString(vldtRslt
                                .equals(Constants.VALIDATE_EMPTY_VALUE) ? R.string.error_field_required
                                : (vldtRslt
                                .equals(Constants.VALIDATE_INVALID_FORMAT) ? R.string.error_invalid_email
                                : R.string.error_invalid_phone_number)));

                focusView = mobilePhoneValue;
                cancel = true;
            }

            List<String> values = null;
            values = Arrays.asList(getValidValue.split("/"));

            DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
            String formattedDate = df.format(Calendar.getInstance().getTime());

            DateFormat df2 = new SimpleDateFormat("MM");
            String formattedMonth = df2.format(Calendar.getInstance().getTime());

            if (TextUtils.isEmpty(getValidValue)) {
                validValue.setError(getString(R.string.error_field_required));
                focusView = validValue;
                cancel = true;
            } else if (Integer.parseInt(values.get(1)) < Integer.parseInt(formattedDate)) {
                validValue.setError("your card is expired!");
                focusView = validValue;
                cancel = true;
            } else if (getValidValue.length() != 5) {
                validValue.setError(getString(R.string.error_invalid_format));
                focusView = validValue;
                cancel = true;
            } else if (Integer.parseInt(values.get(1)) == Integer.parseInt(formattedDate)) {
                if (Integer.parseInt(values.get(0)) < (Integer.parseInt(formattedMonth))) {
                    validValue.setError("your card is expired!");
                    focusView = validValue;
                    cancel = true;
                }
            }
            //end validation

            //start new background process
            if (!cancel) {
                new RequestToken().execute();

            } else {
                focusView.requestFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void responseJSON(JSONObject json) {
        String responseCallBack;
        try {
            if (json.has("res_result_3d")) {
                json.remove("res_result_3d");
                responseCallBack = SDKUtils.createResponseCCReguler(json.getString("res_token_id"), json.getString("res_pairing_code"), json.getString("res_response_msg"),
                        json.getString("res_response_code"), DirectSDK.paymentItems.getDataImei(), DirectSDK.paymentItems.getDataAmount(),
                        json.getString("res_token_code"), DirectSDK.paymentItems.getDataTransactionID(), getEmail, getCardHolder, getPhoneNumber);

            } else {
                responseCallBack = SDKUtils.createResponseCCReguler(json.getString("res_token_id"), json.getString("res_pairing_code"), json.getString("res_response_msg"),
                        json.getString("res_response_code"), DirectSDK.paymentItems.getDataImei(), DirectSDK.paymentItems.getDataAmount(),
                        json.getString("res_token_code"), DirectSDK.paymentItems.getDataTransactionID(), getEmail, getCardHolder, getPhoneNumber);
            }

            DirectSDK.callbackResponse.onSuccess(responseCallBack);
            getActivity().finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (data.getStringExtra("result").equalsIgnoreCase("doRequestResponse")) {
                //do magical request response
                new check3dSecure().execute();
            } else if (data.getStringExtra("result").equalsIgnoreCase("propertyNull")) {
                //handle error request
                DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(300, "data null"));
            }
        }
    }

    private void setupLayout() {

        //define layout
        Button btnSubmit;
        EditText cardNumber, cvvValue, cardHoldervalue, validValue, emailValue, mobilePhoneValue;
        TextView cardNumberTxt, cvvText, cardHolderTxt, validTxt, emailTxt, mobilePhoneTxt;
        ScrollView masterLayout;

        masterLayout = (ScrollView) view.findViewById(R.id.masterLayout);

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        cardNumber = (EditText) view.findViewById(R.id.cardNumber);
        cvvValue = (EditText) view.findViewById(R.id.cvvValue);
        cardHoldervalue = (EditText) view.findViewById(R.id.cardHoldervalue);
        validValue = (EditText) view.findViewById(R.id.validValue);
        emailValue = (EditText) view.findViewById(R.id.emailValue);
        mobilePhoneValue = (EditText) view.findViewById(R.id.mobilePhoneValue);

        cardNumberTxt = (TextView) view.findViewById(R.id.cardNumberTxt);
        cvvText = (TextView) view.findViewById(R.id.cvvText);
        cardHolderTxt = (TextView) view.findViewById(R.id.cardHolderTxt);
        validTxt = (TextView) view.findViewById(R.id.validTxt);
        emailTxt = (TextView) view.findViewById(R.id.emailTxt);
        mobilePhoneTxt = (TextView) view.findViewById(R.id.mobilePhoneTxt);

        //apply font
        if (DirectSDK.layoutItems.getFontPath() != null) {
            SDKUtils.applyFont(DirectSDK.context, btnSubmit, DirectSDK.layoutItems.getFontPath());

            SDKUtils.applyFont(DirectSDK.context, cardNumber, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, cvvValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, cardHoldervalue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, validValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, emailValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, mobilePhoneValue, DirectSDK.layoutItems.getFontPath());

            SDKUtils.applyFont(DirectSDK.context, cardNumberTxt, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, cvvText, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, cardHolderTxt, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, validTxt, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, emailTxt, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, mobilePhoneTxt, DirectSDK.layoutItems.getFontPath());
        } else {
            SDKUtils.applyFont(getActivity(), btnSubmit, "fonts/dokuregular.ttf");

            SDKUtils.applyFont(getActivity(), cardNumber, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), cvvValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), cardHoldervalue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), validValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), emailValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), mobilePhoneValue, "fonts/dokuregular.ttf");

            SDKUtils.applyFont(getActivity(), cardNumberTxt, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), cvvText, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), cardHolderTxt, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), validTxt, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), emailTxt, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), mobilePhoneTxt, "fonts/dokuregular.ttf");


        }

        //font color
        if (DirectSDK.layoutItems.getFontColor() != null) {
            cardNumber.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            cvvValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            cardHoldervalue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            validValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            emailValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            mobilePhoneValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
        }

        if (DirectSDK.layoutItems.getBackgroundColor() != null) {
            masterLayout.setBackgroundColor(Color.parseColor(DirectSDK.layoutItems.getBackgroundColor()));
        }

        if (DirectSDK.layoutItems.getButtonBackground() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btnSubmit.setBackground(DirectSDK.layoutItems.getButtonBackground());
            } else {
                btnSubmit.setBackgroundDrawable(DirectSDK.layoutItems.getButtonBackground());
            }
        }

        if (DirectSDK.layoutItems.getButtonTextColor() != null) {
            btnSubmit.setTextColor(Color.parseColor(DirectSDK.layoutItems.getButtonTextColor()));
        }

        if (DirectSDK.layoutItems.getLabelTextColor() != null) {

            cardNumberTxt.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
            cvvText.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
            cardHolderTxt.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
            validTxt.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
            emailTxt.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
            mobilePhoneTxt.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
        }
    }

    @Override
    public void doBack() {

        if (stateback == 0) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, new ListPayChan());
            ft.addToBackStack(null);
            ft.commit();
        } else {
            getActivity().finish();
        }

    }

    //background process
    private class RequestToken extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Mohon Tunggu ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject defResp;

            List<String> values = null;
            values = Arrays.asList(getValidValue.split("/"));
            String expiryDate = values.get(1) + values.get(0);

            try {
                String dataJson;
                if (DirectSDK.paymentItems.getCustomerID() != null) {

                    //create json request
                    dataJson = SDKUtils.createRequestFirstPay(DirectSDK.paymentItems.getDataMerchantCode(), DirectSDK.paymentItems.getDataTransactionID(), "15", DirectSDK.paymentItems.getDataAmount(),
                            DirectSDK.paymentItems.getDataCurrency(), SDKUtils.Encrypt(expiryDate, DirectSDK.paymentItems.getPublicKey()),
                            SDKUtils.Encrypt(getCardNumber, DirectSDK.paymentItems.getPublicKey()), getCardHolder, SDKUtils.Encrypt(getCvv, DirectSDK.paymentItems.getPublicKey()),
                            DirectSDK.paymentItems.getDataMerchantChain(), DirectSDK.paymentItems.getDataBasket(),
                            getPhoneNumber, getEmail, DirectSDK.paymentItems.getDataWords(), DirectSDK.paymentItems.getDataSessionID(), DirectSDK.paymentItems.getDataImei(),
                            pairingCode, saveCard);
                } else {
                    //create json request
                    dataJson = SDKUtils.createRequestTokenCC(DirectSDK.paymentItems.getDataMerchantCode(), DirectSDK.paymentItems.getDataTransactionID(), "15", DirectSDK.paymentItems.getDataAmount(),
                            DirectSDK.paymentItems.getDataCurrency(), SDKUtils.Encrypt(expiryDate, DirectSDK.paymentItems.getPublicKey()),
                            SDKUtils.Encrypt(getCardNumber, DirectSDK.paymentItems.getPublicKey()), getCardHolder, SDKUtils.Encrypt(getCvv, DirectSDK.paymentItems.getPublicKey()),
                            DirectSDK.paymentItems.getDataMerchantChain(), DirectSDK.paymentItems.getDataBasket(),
                            getPhoneNumber, getEmail, DirectSDK.paymentItems.getDataWords(), DirectSDK.paymentItems.getDataSessionID(), DirectSDK.paymentItems.getDataImei());
                }

                List<NameValuePair> data = new ArrayList<NameValuePair>(3);
                data.add(new BasicNameValuePair("data", dataJson));


                if (DirectSDK.paymentItems.getIsProduction() == true) {
                    // Getting JSON from URL
                    conResult = SDKConnections.httpsConnection(getActivity(),
                            Constants.URL_getTokenProd, data);
                } else {
                    // Getting JSON from URL
                    conResult = SDKConnections.httpsConnection(getActivity(),
                            Constants.URL_getTokenDev, data);
                }

                if (conResult != null) {
                    defResp = new JSONObject(conResult);
                    DirectSDK.jsonResponse = conResult;
                    return defResp;
                } else {
                    DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(200, "can't get data from server"));
                }
            } catch (JSONException e) {
                DirectSDK.callbackResponse.onException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            try {
                if (json != null) {
                    if (json.has("res_response_code") && json.getString("res_response_code").equalsIgnoreCase("0000")) {

                        if (json.has("res_result_3D")) {

                            JSONObject secureData = new JSONObject(json.getString("res_result_3D"));
                            String ACSURL = secureData.getString("ACSURL");
                            String TERMURL = secureData.getString("TERMURL");
                            String PAREQ = secureData.getString("PAREQ");
                            String MD = secureData.getString("MD");

                            Intent i = new Intent(getActivity(), SecurePayment.class);
                            i.putExtra("ACSURL", ACSURL);
                            i.putExtra("TERMURL", TERMURL);
                            i.putExtra("PAREQ", PAREQ);
                            i.putExtra("MD", MD);

                            startActivityForResult(i, 1);
                        } else {
                            responseJSON(json);

                        }
                    } else {
                        DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(Integer.parseInt(json.getString("res_response_code")), json.getString("res_response_msg")));
                        getActivity().finish();
                    }
                }
            } catch (JSONException e) {
                DirectSDK.callbackResponse.onException(e);
                getActivity().finish();
            }
        }

    }

    //background process
    private class check3dSecure extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Mohon Tunggu ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject defResp;

            try {

                JSONObject jsonResponse = new JSONObject(DirectSDK.jsonResponse);

                //create json request
                String dataJson = SDKUtils.createRequest3D(jsonResponse.getString("res_token_id"), jsonResponse.getString("res_pairing_code"), DirectSDK.paymentItems.getDataWords());

                List<NameValuePair> data = new ArrayList<NameValuePair>(3);
                data.add(new BasicNameValuePair("data", dataJson));

                if (DirectSDK.paymentItems.getIsProduction() == true) {

                    // Getting JSON from URL
                    conResult = SDKConnections.httpsConnection(getActivity(),
                            Constants.URL_CHECK3dStatusProd, data);
                } else {

                    // Getting JSON from URL
                    conResult = SDKConnections.httpsConnection(getActivity(),
                            Constants.URL_CHECK3dStatusDev, data);
                }


                if (!"null".equalsIgnoreCase(conResult)) {
                    defResp = new JSONObject(conResult);
                    return defResp;
                } else {
                    DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(200, "can't get data from server"));
                }
            } catch (JSONException e) {
                DirectSDK.callbackResponse.onException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            if (json != null) {
                pDialog.dismiss();

                try {
                    if (json.getString("res_response_code").equalsIgnoreCase("0000")) {

                        JSONObject jsonObject = new JSONObject(DirectSDK.jsonResponse);
                        responseJSON(jsonObject);

                    } else {

                        DirectSDK.callbackResponse.onError(json.toString());
                        getActivity().finish();

                    }
                } catch (JSONException e) {
                    DirectSDK.callbackResponse.onException(e);
                }
            }

        }
    }

    private void checkMerchant() {
        new AsyncTask<String, String, JSONObject>() {

            private ProgressDialog pDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Mohon Tunggu ...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(false);
                pDialog.show();

            }

            @Override
            protected JSONObject doInBackground(String... params) {
                JSONObject defResp;
                try {

                    String checkMerchant = SDKUtils.checkMerchantStatus(DirectSDK.paymentItems.getDataAmount(), DirectSDK.paymentItems.getDataCurrency(), DirectSDK.paymentItems.getDataImei(),
                            DirectSDK.paymentItems.getDataTransactionID(), DirectSDK.paymentItems.getDataMerchantCode(), DirectSDK.paymentItems.getDataMerchantChain(),
                            "15", SDKUtils.Encrypt(DirectSDK.paymentItems.getCustomerID(), DirectSDK.paymentItems.getPublicKey()), DirectSDK.paymentItems.getDataWords());

                    List<NameValuePair> data = new ArrayList<NameValuePair>(3);
                    data.add(new BasicNameValuePair("data", checkMerchant));

                    if (DirectSDK.paymentItems.getIsProduction() == true) {

                        // Getting JSON from URL
                        conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_doCheckStatusProd, data);
                    } else {

                        // Getting JSON from URL
                        conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_doCheckStatus, data);
                    }

                    if (conResult != null) {
                        defResp = new JSONObject(conResult);
                        return defResp;
                    } else {
                        DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(200, "can't get data from server"));
                    }
                } catch (JSONException e) {
                    DirectSDK.callbackResponse.onException(e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                pDialog.dismiss();

                try {
                    if (result.getString("res_response_code").equalsIgnoreCase("0000")) {

                        if (result.getString("res_service_two_click").equalsIgnoreCase("true")) {
                            checkSave.setVisibility(View.VISIBLE);
                            pairingCode = result.getString("res_pairing_code");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(null, null, null);
    }
}