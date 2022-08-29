package com.doku.sdkocov2.dokupayment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.doku.sdkocov2.BaseDokuWalletActivity;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.fragment.SecurePayment;
import com.doku.sdkocov2.interfaces.DrawableClickListener;
import com.doku.sdkocov2.interfaces.iSDKback;
import com.doku.sdkocov2.utils.CardModel;
import com.doku.sdkocov2.utils.Constants;
import com.doku.sdkocov2.utils.CustomEditText;
import com.doku.sdkocov2.utils.ExpiryDateFormatWatcher;
import com.doku.sdkocov2.utils.FourDigitCardFormatWatcher;
import com.doku.sdkocov2.utils.SDKConnections;
import com.doku.sdkocov2.utils.SDKUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zaki on 2/25/16.
 */
public class WalletCCRegister extends Fragment implements iSDKback {
    View view;
    EditText mobilePhoneValue, emailValue, cardHoldervalue, cardNumber;
    Button btnSubmit;
    CustomEditText cvvValue;
    EditText validValue;
    StringBuilder sb = new StringBuilder(19);
    String getCardHolder, getCardNumber, getCvv, getEmail, getPhoneNumber, getValidValue;
    String channelCode;
    private Bundle bundle;
    String conResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cc_payment, container, false);

        BaseDokuWalletActivity.backButton.setVisibility(View.VISIBLE);
        BaseDokuWalletActivity.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_dokuWallet, new ListDokuPayChan());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        bundle = getArguments();
        if (bundle != null) {
            channelCode = bundle.getString("channelCode");
        }
        sb.setLength(0);

        mobilePhoneValue = view.findViewById(R.id.mobilePhoneValue);
        emailValue = view.findViewById(R.id.emailValue);
        cardHoldervalue = view.findViewById(R.id.cardHoldervalue);
        cardNumber = view.findViewById(R.id.cardNumber);
        cvvValue = view.findViewById(R.id.cvvValue);
        validValue = view.findViewById(R.id.validValue);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        if (DirectSDK.paymentItems.getMobilePhone() != null) {
            mobilePhoneValue.setText(DirectSDK.paymentItems.getMobilePhone());
        }

        setupLayout();

        validValue.addTextChangedListener(new ExpiryDateFormatWatcher(validValue));

        cardNumber.addTextChangedListener(new FourDigitCardFormatWatcher());

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
                        Toast.makeText(getContext(), "Insert last 3 number from back of your credit card", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    private void attemptSubmit() {
        try {
            boolean cancel = false;
            View focusView = null;
            String vldtRslt;

            getCardHolder = cardHoldervalue.getText().toString().trim();
            getCardNumber = cardNumber.getText().toString().replace("-", "");
            getCvv = cvvValue.getText().toString();
            getEmail = emailValue.getText().toString();
            getPhoneNumber = mobilePhoneValue.getText().toString();
            getValidValue = validValue.getText().toString();

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

            CardModel cardType = CardModel.fromCardNumber(getCardNumber.replace("-", ""));
            String getCardType = cardType.getDisplayName(getCardNumber.replace("-", ""));

            vldtRslt = SDKUtils.validateValue(getCardNumber.replace("-", ""),'C');
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
                cvvValue.setError(getString(vldtRslt
                                .equals(Constants.VALIDATE_EMPTY_VALUE) ? R.string.error_field_required
                                : (vldtRslt
                                .equals(Constants.VALIDATE_INVALID_FORMAT) ? R.string.error_invalid_format
                                : R.string.error_invalid_format)));
                focusView = cvvValue;
                cancel = true;
            }

            vldtRslt = SDKUtils.validateValue(getEmail, 'E');
            if (!vldtRslt.equals(Constants.VALIDATE_SUCCESS)) {
                emailValue.setError(getString(vldtRslt
                                .equals(Constants.VALIDATE_EMPTY_VALUE) ? R.string.error_field_required
                                : (vldtRslt
                                .equals(Constants.VALIDATE_INVALID_FORMAT) ? R.string.error_invalid_email
                                : R.string.error_invalid_email)));

                focusView = emailValue;
                cancel = true;
            }

            vldtRslt = SDKUtils.validateValue(getPhoneNumber, 'M');
            if (!vldtRslt.equals(Constants.VALIDATE_SUCCESS)) {
                mobilePhoneValue.setError(getString(vldtRslt
                                .equals(Constants.VALIDATE_EMPTY_VALUE) ? R.string.error_field_required
                                : (vldtRslt
                                .equals(Constants.VALIDATE_INVALID_FORMAT) ? R.string.error_invalid_email
                                : R.string.error_invalid_email)));

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

            if (!cancel) {
                new PrepaymentCCRegister().execute();

            } else {
                focusView.requestFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data.getStringExtra("result").equalsIgnoreCase("doRequestResponse")) {
                new check3dSecure().execute();
            } else if (data.getStringExtra("result").equalsIgnoreCase("propertyNull")) {
                DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(300, "data null"));
            }
        }
    }

    private void setupLayout() {
        Button btnSubmit;
        EditText cardNumber, cvvValue, cardHoldervalue, validValue, emailValue, mobilePhoneValue;
        TextView cardNumberTxt, cvvText, cardHolderTxt, validTxt, emailTxt, mobilePhoneTxt;
        ScrollView masterLayout;

        masterLayout =  view.findViewById(R.id.masterLayout);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        cardNumber = view.findViewById(R.id.cardNumber);
        cvvValue = view.findViewById(R.id.cvvValue);
        cardHoldervalue = view.findViewById(R.id.cardHoldervalue);
        validValue = view.findViewById(R.id.validValue);
        emailValue = view.findViewById(R.id.emailValue);
        mobilePhoneValue = view.findViewById(R.id.mobilePhoneValue);

        cardNumberTxt = view.findViewById(R.id.cardNumberTxt);
        cvvText = view.findViewById(R.id.cvvText);
        cardHolderTxt = view.findViewById(R.id.cardHolderTxt);
        validTxt = view.findViewById(R.id.validTxt);
        emailTxt = view.findViewById(R.id.emailTxt);
        mobilePhoneTxt = view.findViewById(R.id.mobilePhoneTxt);

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
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_dokuWallet, new ListDokuPayChan());
        ft.addToBackStack(null);
        ft.commit();
    }

    private class PrepaymentCCRegister extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Mohon Tunggu ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            if (BaseDokuWalletActivity.timeoutTimer != null) {
                BaseDokuWalletActivity.timeoutTimer.cancel();
                BaseDokuWalletActivity.timeoutTimer = null;
            }
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject defResp;
            List<String> values = null;
            values = Arrays.asList(getValidValue.split("/"));
            String expiryDate = values.get(1) + values.get(0);

            try {
                String dataJson = SDKUtils.createRegisterCCWallet(DirectSDK.loginModel.getTokenID(), DirectSDK.loginModel.getPairingCode(), DirectSDK.paymentItems.getDataWords(),
                        SDKUtils.Encrypt(expiryDate, DirectSDK.paymentItems.getPublicKey()), SDKUtils.Encrypt(getCardNumber, DirectSDK.paymentItems.getPublicKey()), getCardHolder,
                        SDKUtils.Encrypt(getCvv, DirectSDK.paymentItems.getPublicKey()), getPhoneNumber, SDKUtils.Encrypt(getEmail, DirectSDK.paymentItems.getPublicKey()), channelCode,
                        DirectSDK.userDetails.getInquiryCode(), SDKUtils.Encrypt(DirectSDK.userDetails.getDokuID(), DirectSDK.paymentItems.getPublicKey()));

                ContentValues data = new ContentValues();
                data.put("data", dataJson);

                if (DirectSDK.paymentItems.getIsProduction() == true) {
                     conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_prePaymentProd, data);
                } else {
                     conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_prePaymentDev, data);
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
                    if (json.getString("res_response_code").equalsIgnoreCase("0000")) {
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
                            String responseCallBack = SDKUtils.createResponseCashWallet(DirectSDK.loginModel.getTokenID(), DirectSDK.loginModel.getPairingCode(), json.getString("res_response_msg"),
                                    json.getString("res_response_code"), DirectSDK.paymentItems.getDataImei(), DirectSDK.paymentItems.getDataAmount(), DirectSDK.loginModel.getTokenCode(),
                                    DirectSDK.paymentItems.getDataTransactionID(), DirectSDK.userDetails.getCustomerName(),
                                    DirectSDK.userDetails.getCustomerEmail(), getPhoneNumber);
                            DirectSDK.callbackResponse.onSuccess(responseCallBack);
                            getActivity().finish();

                        }
                    } else {
                        DirectSDK.callbackResponse.onError(json.getString("res_response_msg"));
                        getActivity().finish();
                    }

                } else {
                    DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(Integer.parseInt(json.getString("res_response_code")), json.getString("res_response_msg")));
                    getActivity().finish();
                }

            } catch (JSONException e) {
                DirectSDK.callbackResponse.onException(e);
                getActivity().finish();
            }

        }
    }

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
                String dataJson = SDKUtils.createRequest3D(DirectSDK.loginModel.getTokenID(), DirectSDK.loginModel.getPairingCode(), DirectSDK.paymentItems.getDataWords());

                ContentValues data = new ContentValues();
                data.put("data", dataJson);

                if (DirectSDK.paymentItems.getIsProduction() == true) {
                    conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_CHECK3dStatusProd, data);

                } else {
                    conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_CHECK3dStatusDev, data);
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
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();

            try {
                if (json.getString("res_response_code").equalsIgnoreCase("0000")) {

                    String responseCallBack = SDKUtils.createResponseCashWallet(DirectSDK.loginModel.getTokenID(), DirectSDK.loginModel.getPairingCode(), json.getString("res_response_msg"),
                            json.getString("res_response_code"), DirectSDK.paymentItems.getDataImei(), DirectSDK.paymentItems.getDataAmount(), DirectSDK.loginModel.getTokenCode(),
                            DirectSDK.paymentItems.getDataTransactionID(), DirectSDK.userDetails.getCustomerName(),
                            DirectSDK.userDetails.getCustomerEmail(), getPhoneNumber);
                    DirectSDK.callbackResponse.onSuccess(responseCallBack);
                    getActivity().finish();


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
