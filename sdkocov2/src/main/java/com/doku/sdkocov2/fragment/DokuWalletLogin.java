package com.doku.sdkocov2.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.doku.sdkocov2.BaseDokuWalletActivity;
import com.doku.sdkocov2.BaseSDKOCO;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.interfaces.iSDKback;
import com.doku.sdkocov2.utils.Constants;
import com.doku.sdkocov2.utils.SDKConnections;
import com.doku.sdkocov2.utils.SDKUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zaki on 2/17/16.
 */
public class DokuWalletLogin extends Fragment implements iSDKback {
    View view;
    Button btnSubmit;
    EditText emailValue, passwordValue;
    String mEmail, mPassword;
    Bundle bundle;
    int stateback;
    String conResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.doku_wallet_login, container, false);
        setupLayout();
        btnSubmit = view.findViewById(R.id.btnSubmit);
        emailValue = view.findViewById(R.id.emailValue);
        passwordValue = view.findViewById(R.id.passwordValue);
        emailValue.requestFocus();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();
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

    private void attemptSubmit() {
        try {
            boolean cancel = false;
            View focusView = null;

            mEmail = emailValue.getText().toString();
            mPassword = passwordValue.getText().toString();

            if (TextUtils.isEmpty(mEmail)) {
                emailValue.setError(getString(R.string.error_field_required));
                focusView = emailValue;
                cancel = true;
            }

            if (TextUtils.isEmpty(mPassword)) {
                passwordValue.setError(getString(R.string.error_field_required));
                focusView = passwordValue;
                cancel = true;
            }

            if (!cancel) {
                new RequestToken().execute();
            } else {
                focusView.requestFocus();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupLayout() {
        TextView emailTxt, passwordTxt;
        RelativeLayout masterLayout;

        masterLayout = view.findViewById(R.id.masterLayout);

        btnSubmit = view.findViewById(R.id.btnSubmit);
        emailValue = view.findViewById(R.id.emailValue);
        passwordValue = view.findViewById(R.id.passwordValue);

        emailTxt = view.findViewById(R.id.emailTxt);
        passwordTxt = view.findViewById(R.id.passwordTxt);

        if (DirectSDK.layoutItems.getFontPath() != null) {
            SDKUtils.applyFont(DirectSDK.context, emailValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, passwordValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, btnSubmit, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, emailTxt, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, passwordTxt, DirectSDK.layoutItems.getFontPath());
        } else {
            SDKUtils.applyFont(getActivity(), emailValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), passwordValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), btnSubmit, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), emailTxt, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), passwordTxt, "fonts/dokuregular.ttf");
        }

        if (DirectSDK.layoutItems.getFontColor() != null) {
            emailValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            passwordValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
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
            emailTxt.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
            passwordTxt.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
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
            try {
                String dataJson = SDKUtils.createRequestTokenWallet(DirectSDK.paymentItems.getDataMerchantCode(), DirectSDK.paymentItems.getDataTransactionID(), "04", DirectSDK.paymentItems.getDataAmount(),
                        DirectSDK.paymentItems.getDataCurrency(), DirectSDK.paymentItems.getDataMerchantChain(), DirectSDK.paymentItems.getDataBasket(),
                        DirectSDK.paymentItems.getDataWords(), DirectSDK.paymentItems.getDataSessionID(), DirectSDK.paymentItems.getDataImei(),
                        SDKUtils.Encrypt(mEmail, DirectSDK.paymentItems.getPublicKey()),
                        SDKUtils.Encrypt(mPassword, DirectSDK.paymentItems.getPublicKey()));

                ContentValues data = new ContentValues();
                data.put("data", dataJson);

                if (DirectSDK.paymentItems.getIsProduction() == true) {
                    conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_getTokenProd, data);
                } else {
                    conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_getTokenDev, data);
                }

                defResp = new JSONObject(conResult);

                return defResp;
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

                        if (json.getString("res_data_dw") != null) {
                            DirectSDK.loginModel.setDataWallet(json.getString("res_data_dw"));
                            DirectSDK.loginModel.setTokenID(json.getString("res_token_id"));
                            DirectSDK.loginModel.setResponseMsg(json.getString("res_response_msg"));
                            DirectSDK.loginModel.setPairingCode(json.getString("res_pairing_code"));
                            DirectSDK.loginModel.setTokenCode(json.getString("res_token_code"));
                            DirectSDK.loginModel.setResponseCode(json.getString("res_response_code"));
                            DirectSDK.loginModel.setPaymentChannelLogin(json.getString("res_payment_channel"));
                            JSONObject jObj = new JSONObject(json.getString("res_data_dw"));

                            DirectSDK.userDetails.setResponseCode(jObj.getString("responseCode"));
                            DirectSDK.userDetails.setResponseMsg(jObj.getString("responseMsg"));
                            DirectSDK.userDetails.setDpMallID(jObj.getString("dpMallId"));
                            DirectSDK.userDetails.setTransIDMerchant(jObj.getString("transIdMerchant"));
                            DirectSDK.userDetails.setDokuID(jObj.getString("dokuId"));
                            DirectSDK.userDetails.setCustomerName(jObj.getString("customerName"));
                            DirectSDK.userDetails.setCustomerEmail(jObj.getString("customerEmail"));
                            DirectSDK.userDetails.setPaymentChannel(jObj.getString("listPaymentChannel"));
                            DirectSDK.userDetails.setInquiryCode(jObj.getString("inquiryCode"));
                            DirectSDK.userDetails.setAvatar(jObj.getString("avatar"));

                            if (jObj.has("listPromotion")) {
                                DirectSDK.userDetails.setListPromotion(jObj.getString("listPromotion"));
                            }
                        }

                        Intent intent = new Intent(getActivity(), BaseDokuWalletActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (json.getString("res_response_code").equalsIgnoreCase("4001")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Login Error")
                                .setMessage("incorrect username/password!")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {}
                                })
                                .show();
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
}