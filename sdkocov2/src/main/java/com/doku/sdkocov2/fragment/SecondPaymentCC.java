package com.doku.sdkocov2.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.doku.sdkocov2.BaseSDKOCO;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.interfaces.DrawableClickListener;
import com.doku.sdkocov2.interfaces.iSDKback;
import com.doku.sdkocov2.model.CCItem;
import com.doku.sdkocov2.utils.Constants;
import com.doku.sdkocov2.utils.CustomEditText;
import com.doku.sdkocov2.utils.SDKConnections;
import com.doku.sdkocov2.utils.SDKUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by zaki on 12/8/15.
 */
public class SecondPaymentCC extends Fragment implements iSDKback {

    View view;
    int stateback;
    private Bundle bundle;
    CustomEditText cvvValue;
    Button btnSubmit;
    public static final ArrayList<CCItem> ccItem = new ArrayList<CCItem>();
    String conResult;
    TextView cardNumber;
    String getCvv;
    String pairingCode, getEmail, getPhoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.second_payment, container, false);

        setupLayout();

        btnSubmit = view.findViewById(R.id.btnSubmit);
        cvvValue = view.findViewById(R.id.cvvValue);
        cardNumber = view.findViewById(R.id.cardNumber);

        inquiryCard();

        ccItem.clear();

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
            getCvv = cvvValue.getText().toString();

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

            if (!cancel) {
                new RequestSecondPayToken().execute();
            } else {
                focusView.requestFocus();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RequestSecondPayToken extends AsyncTask<String, String, JSONObject> {
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
                String dataJson;
                dataJson = SDKUtils.createRequestSecondPay(DirectSDK.paymentItems.getDataMerchantCode(), DirectSDK.paymentItems.getDataTransactionID(), "15",
                        DirectSDK.paymentItems.getDataAmount(), DirectSDK.paymentItems.getDataCurrency(), SDKUtils.Encrypt(getCvv, DirectSDK.paymentItems.getPublicKey()),
                        DirectSDK.paymentItems.getDataMerchantChain(), DirectSDK.paymentItems.getDataBasket(), DirectSDK.paymentItems.getDataWords(),
                        DirectSDK.paymentItems.getDataSessionID(), DirectSDK.paymentItems.getDataImei(), pairingCode, DirectSDK.paymentItems.getTokenPayment());

                ContentValues data = new ContentValues();
                data.put("data", dataJson);

                if (DirectSDK.paymentItems.getIsProduction() == true) {
                    conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_getTokenProd, data);
                } else {
                    conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_getTokenDev, data);
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

                String dataJson = SDKUtils.createRequest3D(jsonResponse.getString("res_token_id"), jsonResponse.getString("res_pairing_code"), DirectSDK.paymentItems.getDataWords());

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


    private void setupLayout() {
        TextView title, cvvText;
        CustomEditText cvvValue;
        ScrollView masterLayout;
        Button btnSubmit;

        btnSubmit = view.findViewById(R.id.btnSubmit);
        masterLayout = view.findViewById(R.id.masterLayout);
        title = view.findViewById(R.id.title);
        cvvText = view.findViewById(R.id.cvvText);
        cvvValue = view.findViewById(R.id.cvvValue);

        if (DirectSDK.layoutItems.getFontPath() != null) {
            SDKUtils.applyFont(DirectSDK.context, title, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, cvvText, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, cvvValue, DirectSDK.layoutItems.getFontPath());
        } else {
            SDKUtils.applyFont(getActivity(), title, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), cvvText, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), cvvValue, "fonts/dokuregular.ttf");
        }

        if (DirectSDK.layoutItems.getFontColor() != null) {
            title.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            cvvValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
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
            cvvText.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
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

    @SuppressLint("StaticFieldLeak")
    private void inquiryCard() {
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
                    String checkMerchant = SDKUtils.cardInquirySecond(DirectSDK.paymentItems.getDataAmount(), DirectSDK.paymentItems.getDataCurrency(), DirectSDK.paymentItems.getDataImei(),
                            DirectSDK.paymentItems.getDataTransactionID(), DirectSDK.paymentItems.getDataMerchantCode(), DirectSDK.paymentItems.getDataMerchantChain(),
                            "15", SDKUtils.Encrypt(DirectSDK.paymentItems.getCustomerID(), DirectSDK.paymentItems.getPublicKey()), DirectSDK.paymentItems.getDataWords(),
                            SDKUtils.Encrypt(DirectSDK.paymentItems.getTokenPayment(), DirectSDK.paymentItems.getPublicKey()));

                    ContentValues data = new ContentValues();
                    data.put("data", checkMerchant);

                    if (DirectSDK.paymentItems.getIsProduction() == true) {
                        conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_doCheckStatusProd, data);
                    } else {
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

                        cardNumber.setText(result.getString("res_cc_number"));
                        pairingCode = result.getString("res_pairing_code");
                        getEmail = result.getString("res_data_email");
                        getPhoneNumber = result.getString("res_data_mobile_phone");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(null, null, null);
    }

    private void responseJSON(JSONObject json) {
        String responseCallBack;
        try {
            if (json.has("res_result_3d")) {
                json.remove("res_result_3d");
                responseCallBack = SDKUtils.createResponseCCSecond(json.getString("res_token_id"), json.getString("res_pairing_code"), json.getString("res_response_msg"),
                        json.getString("res_response_code"), DirectSDK.paymentItems.getDataImei(), DirectSDK.paymentItems.getDataAmount(),
                        json.getString("res_token_code"), DirectSDK.paymentItems.getDataTransactionID(), getEmail, DirectSDK.paymentItems.getMobilePhone());

            } else {
                responseCallBack = SDKUtils.createResponseCCSecond(json.getString("res_token_id"), json.getString("res_pairing_code"), json.getString("res_response_msg"),
                        json.getString("res_response_code"), DirectSDK.paymentItems.getDataImei(), DirectSDK.paymentItems.getDataAmount(),
                        json.getString("res_token_code"), DirectSDK.paymentItems.getDataTransactionID(), getEmail, DirectSDK.paymentItems.getMobilePhone());
            }

            DirectSDK.callbackResponse.onSuccess(responseCallBack);
            getActivity().finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}