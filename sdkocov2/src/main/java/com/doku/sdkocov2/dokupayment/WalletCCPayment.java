package com.doku.sdkocov2.dokupayment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.doku.sdkocov2.BaseDokuWalletActivity;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.adapter.DokuPayChanAdapter;
import com.doku.sdkocov2.fragment.SecurePayment;
import com.doku.sdkocov2.interfaces.DrawableClickListener;
import com.doku.sdkocov2.interfaces.iSDKback;
import com.doku.sdkocov2.model.CCItem;
import com.doku.sdkocov2.utils.Constants;
import com.doku.sdkocov2.utils.CustomEditText;
import com.doku.sdkocov2.utils.SDKConnections;
import com.doku.sdkocov2.utils.SDKUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zaki on 2/25/16.
 */
public class WalletCCPayment extends Fragment implements iSDKback {

    public static final ArrayList<CCItem> ccItem = new ArrayList<CCItem>();
    //declare variable
    View view;
    CustomEditText cvvValue;
    Button btnSubmit;
    int stateback;
    DokuPayChanAdapter mAdapter;
    String cvv;
    String channelCode;
    private Bundle bundle;
    String conResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.doku_cc_payment, container, false);

        //setting layout
        setupLayout();

        //initiate view
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        cvvValue = (CustomEditText) view.findViewById(R.id.cvvValue);

        //clear arraylist
        ccItem.clear();

        //parsing json data
        try {
            JSONArray ListPaymentData = new JSONArray(DirectSDK.userDetails.getPaymentChannel());
            for (int i = 0; i < ListPaymentData.length(); i++) {
                JSONObject data = ListPaymentData.getJSONObject(i);
                if (data.getString("channelCode").equalsIgnoreCase("02")) {

                    channelCode = data.getString("channelCode");
                    if (data.has("details")) {
                        JSONArray dataCC = data.getJSONArray("details");

                        for (int cc = 0; dataCC.length() > cc; cc++) {
                            JSONObject listCC = dataCC.getJSONObject(cc);

                            if (listCC.has("cardEmail") && listCC.has("cardZipCode")) {

                                //add data to model
                                ccItem.add(new CCItem(listCC.getString("linkId"),
                                        listCC.getString("cardName"),
                                        listCC.getString("cardZipCode"),
                                        listCC.getString("cardNoEncrypt"),
                                        listCC.getString("cardCountry"),
                                        listCC.getString("cardCity"),
                                        listCC.getString("cardEmail"),
                                        listCC.getString("cardExpiryDateEncrypt"),
                                        listCC.getString("cardPhone"),
                                        listCC.getString("cardNoMasked"), listCC.getString("cardType")));
                            } else {
                                //add data to model
                                ccItem.add(new CCItem(listCC.getString("linkId"),
                                        listCC.getString("cardName"),
                                        listCC.getString("cardNoEncrypt"),
                                        DirectSDK.userDetails.getCustomerEmail(),
                                        listCC.getString("cardExpiryDateEncrypt"),
                                        listCC.getString("cardNoMasked"), listCC.getString("cardType")));
                            }
                        }
                    } else {
                        Bundle bundle = new Bundle();
                        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                        Fragment fragment = new WalletCCRegister();
                        bundle.putString("channelCode", channelCode);
                        fragment.setArguments(bundle);
                        t.replace(R.id.main_dokuWallet, fragment);
                        t.commit();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //set adapter
        mAdapter = new DokuPayChanAdapter(getActivity().getApplicationContext(), ccItem);
        ListView list = (ListView) view.findViewById(R.id.list);
        list.setAdapter(mAdapter);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSubmit();
            }
        });


        bundle = getArguments();
        if (bundle != null) {
            stateback = bundle.getInt("stateback");

            if (stateback == 0) {
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
            }
        }


        cvvValue.setDrawableClickListener(new DrawableClickListener() {


            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //Do something here
                        Toast.makeText(getContext(), "Insert last 3 number from back of your credit card", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        break;
                }
            }

        });

        return view;
    }

    //form validation
    private void attemptSubmit() {

        try {
            //declare variable
            boolean cancel = false;
            View focusView = null;
            String vldtRslt;

            cvv = cvvValue.getText().toString();

            vldtRslt = SDKUtils.validateValue(cvv, 'C');
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

            if (!cancel) {
                new PrePaymentProcess().execute();

            } else {
                focusView.requestFocus();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get result activity
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

    @Override
    public void doBack() {
        if (stateback == 0) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_dokuWallet, new ListDokuPayChan());
            ft.addToBackStack(null);
            ft.commit();
        } else {
            DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(200, "canceled by user"));
            getActivity().finish();
        }

    }

    private void setupLayout() {
        //declare variable
        TextView title, cvvText;
        CustomEditText cvvValue;
        ScrollView masterLayout;
        Button btnSubmit;

        //initiate view
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        masterLayout = (ScrollView) view.findViewById(R.id.masterLayout);
        title = (TextView) view.findViewById(R.id.title);
        cvvText = (TextView) view.findViewById(R.id.cvvText);
        cvvValue = (CustomEditText) view.findViewById(R.id.cvvValue);

        if (DirectSDK.layoutItems.getFontPath() != null) {
            SDKUtils.applyFont(DirectSDK.context, title, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, cvvText, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, cvvValue, DirectSDK.layoutItems.getFontPath());
        } else {
            SDKUtils.applyFont(getActivity(), title, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), cvvText, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), cvvValue, "fonts/dokuregular.ttf");
        }

        //font color
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

    private class PrePaymentProcess extends AsyncTask<String, String, JSONObject> {

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

            try {
                String dataJSON = SDKUtils.createRequestCCWallet(channelCode, DirectSDK.userDetails.getInquiryCode(), SDKUtils.Encrypt(DirectSDK.userDetails.getDokuID(),
                        DirectSDK.paymentItems.getPublicKey()), ccItem.get(DokuPayChanAdapter.selectedPosition).getLinkID(),
                        ccItem.get(DokuPayChanAdapter.selectedPosition).getCardNumberEncrypt(), ccItem.get(DokuPayChanAdapter.selectedPosition).getCardExpiryDateEncrypt(),
                        SDKUtils.Encrypt(cvv, DirectSDK.paymentItems.getPublicKey()), DirectSDK.loginModel.getTokenID(), DirectSDK.loginModel.getPairingCode(),
                        DirectSDK.paymentItems.getDataWords(), SDKUtils.Encrypt(DirectSDK.userDetails.getCustomerEmail(), DirectSDK.paymentItems.getPublicKey()),
                        DirectSDK.userDetails.getCustomerName());

                List<NameValuePair> data = new ArrayList<NameValuePair>(3);
                data.add(new BasicNameValuePair("data", dataJSON));

                if (DirectSDK.paymentItems.getIsProduction() == true) {

                    // Getting JSON from URL
                    conResult = SDKConnections.httpsConnection(getActivity(),
                            Constants.URL_prePaymentProd, data);
                } else {

                    // Getting JSON from URL
                    conResult = SDKConnections.httpsConnection(getActivity(),
                            Constants.URL_prePaymentDev, data);
                }

                if (conResult != null) {
                    defResp = new JSONObject(conResult);

                    return defResp;
                } else {
                    Log.d("DATA JSON CC WALLET", "DATA NULL");
                }


            } catch (JSONException e) {
                DirectSDK.callbackResponse.onException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            pDialog.dismiss();

            if (json != null) {

                try {
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
                                    DirectSDK.userDetails.getCustomerEmail(), DirectSDK.paymentItems.getMobilePhone());

                            DirectSDK.callbackResponse.onSuccess(responseCallBack);
                            getActivity().finish();

                        }
                    } else {
                        DirectSDK.callbackResponse.onError(json.getString("res_response_msg"));
                        getActivity().finish();
                    }

                } catch (JSONException e) {
                    DirectSDK.callbackResponse.onException(e);
                    getActivity().finish();
                }

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

                //create json request
                String dataJson = SDKUtils.createRequest3D(DirectSDK.loginModel.getTokenID(), DirectSDK.loginModel.getPairingCode(), DirectSDK.paymentItems.getDataWords());

                List<NameValuePair> data = new ArrayList<NameValuePair>(3);
                data.add(new BasicNameValuePair("data", dataJson));

                if (DirectSDK.paymentItems.getIsProduction() == true) {
                    conResult = SDKConnections.httpsConnection(getActivity(),
                            Constants.URL_CHECK3dStatusProd, data);
                } else {
                    conResult = SDKConnections.httpsConnection(getActivity(),
                            Constants.URL_CHECK3dStatusDev, data);
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
            ;
            try {
                if (json.getString("res_response_code").equalsIgnoreCase("0000")) {

                    String responseCallBack = SDKUtils.createResponseCashWallet(DirectSDK.loginModel.getTokenID(), DirectSDK.loginModel.getPairingCode(),
                            json.getString("res_response_msg"), json.getString("res_response_code"), DirectSDK.paymentItems.getDataImei(),
                            DirectSDK.paymentItems.getDataAmount(), DirectSDK.loginModel.getTokenCode(), DirectSDK.paymentItems.getDataTransactionID(),
                            DirectSDK.userDetails.getCustomerName(), DirectSDK.userDetails.getCustomerEmail(), DirectSDK.paymentItems.getMobilePhone());

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
