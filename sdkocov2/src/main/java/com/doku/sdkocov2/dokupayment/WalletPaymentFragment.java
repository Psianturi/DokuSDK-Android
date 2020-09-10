package com.doku.sdkocov2.dokupayment;

import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.doku.sdkocov2.BaseDokuWalletActivity;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.interfaces.DrawableClickListener;
import com.doku.sdkocov2.interfaces.iSDKback;
import com.doku.sdkocov2.model.PromoItem;
import com.doku.sdkocov2.utils.Constants;
import com.doku.sdkocov2.utils.CustomEditText;
import com.doku.sdkocov2.utils.SDKConnections;
import com.doku.sdkocov2.utils.SDKUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zaki on 2/25/16.
 */
public class WalletPaymentFragment extends Fragment implements iSDKback {

    public static final ArrayList<PromoItem> promoItem = new ArrayList<>();
    public static final List<String> amountSpin = new ArrayList<>();
    View view, line1, line2;
    TextView saldoWallet, totalTxt, totalValue, discountTxt, discountValue, lastTotal, lastTotalValue;
    CustomEditText pinValue;
    Button btnSubmit;
    String userPin, channelCode;
    int stateback;
    RelativeLayout promoLayout;
    String promoID;
    int spinPosition;
    private Bundle bundle;
    String conResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.wallet_payment, container, false);

        setupLayout();

        saldoWallet = view.findViewById(R.id.saldoWallet);
        totalTxt = view.findViewById(R.id.totalTxt);
        totalValue = view.findViewById(R.id.totalValue);
        discountTxt = view.findViewById(R.id.discountTxt);
        discountValue = view.findViewById(R.id.discountValue);
        lastTotal = view.findViewById(R.id.lastTotal);
        lastTotalValue = view.findViewById(R.id.lastTotalValue);

        line1 = view.findViewById(R.id.line1);
        line2 = view.findViewById(R.id.line2);
        pinValue = view.findViewById(R.id.pinValue);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        promoLayout = view.findViewById(R.id.promoLayout);
        final Spinner spinVoucher = view.findViewById(R.id.spinVoucher);

        promoItem.clear();
        amountSpin.clear();

        JSONArray listPromotion;
        try {
            if (DirectSDK.userDetails.getListPromotion() != null) {
                totalValue.setText("Rp " + SDKUtils.EYDNumberFormat(DirectSDK.paymentItems.getDataAmount()));
                listPromotion = new JSONArray(DirectSDK.userDetails.getListPromotion());
                promoLayout.setVisibility(View.VISIBLE);
                amountSpin.add(0, "Pilih");

                for (int i = 0; i < listPromotion.length(); i++) {
                    JSONObject data = listPromotion.getJSONObject(i);
                    amountSpin.add(data.getString("name") + "( " + data.getString("amount") + " )");
                    promoItem.add(new PromoItem(data.getString("id"), data.getString("name"), data.getString("amount"), data.getString("dateStart"), data.getString("dateEnd")));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, amountSpin);
                spinVoucher.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinVoucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinPosition = position;
                if (position > 0) {
                    discountTxt.setVisibility(View.VISIBLE);
                    discountValue.setVisibility(View.VISIBLE);
                    lastTotalValue.setVisibility(View.VISIBLE);
                    lastTotal.setVisibility(View.VISIBLE);
                    line1.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.VISIBLE);

                    double aDouble = Double.parseDouble(promoItem.get(position - 1).getPromoAmount());
                    int endTotalValue = (int) aDouble;

                    double bDouble = Double.parseDouble(DirectSDK.paymentItems.getDataAmount());
                    int firstTotalValue = (int) bDouble;


                    discountValue.setText("- Rp " + SDKUtils.EYDNumberFormat(promoItem.get(position - 1).getPromoAmount()));
                    int totalAkhir = firstTotalValue - endTotalValue;

                    if (totalAkhir <= 0) {
                        lastTotalValue.setText("Rp " + 0);
                    } else {
                        lastTotalValue.setText("Rp " + SDKUtils.EYDNumberFormat(String.valueOf(totalAkhir)));
                    }
                } else {
                    discountTxt.setVisibility(View.GONE);
                    discountValue.setVisibility(View.GONE);
                    lastTotalValue.setVisibility(View.GONE);
                    lastTotal.setVisibility(View.GONE);
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        pinValue.setDrawableClickListener(new DrawableClickListener() {
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        Toast.makeText(getContext(), "image question click action", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });

        try {
            JSONArray ListPaymentData = new JSONArray(DirectSDK.userDetails.getPaymentChannel());
            for (int i = 0; i < ListPaymentData.length(); i++) {
                JSONObject data = ListPaymentData.getJSONObject(i);

                if (data.getString("channelCode").equalsIgnoreCase("01")) {
                    channelCode = data.getString("channelCode");
                    JSONObject arrayList = data.getJSONObject("details");
                    double saldoAwal = Double.parseDouble(arrayList.getString("lastBalance"));
                    saldoWallet.setText("Rp " + SDKUtils.EYDNumberFormat(String.valueOf((int) saldoAwal)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        return view;
    }

    private void attemptSubmit() {
        try {
            boolean cancel = false;
            View focusView = null;
            String vldtRslt;
            userPin = pinValue.getText().toString();
            vldtRslt = SDKUtils.validateValue(userPin, 'N');
            if (!vldtRslt.equals(Constants.VALIDATE_SUCCESS)) {

                pinValue
                        .setError(getString(vldtRslt
                                .equals(Constants.VALIDATE_EMPTY_VALUE) ? R.string.error_field_required
                                : (vldtRslt
                                .equals(Constants.VALIDATE_INVALID_FORMAT) ? R.string.error_invalid_format
                                : R.string.error_invalid_format)));
                focusView = pinValue;
                cancel = true;
            }

            if (DirectSDK.userDetails.getListPromotion() != null) {
                if (spinPosition > 0) {
                    promoID = promoItem.get(spinPosition - 1).getPromoName();
                } else {
                    promoID = "";
                }
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
        TextView saldoText, pinText, saldoWallet, totalTxt, totalValue, discountTxt, discountValue, lastTotal, lastTotalValue, txtVoucher;
        CustomEditText pinValue;
        ScrollView masterLayout;
        Button btnSubmit;

        totalTxt = view.findViewById(R.id.totalTxt);
        totalValue = view.findViewById(R.id.totalValue);
        discountTxt = view.findViewById(R.id.discountTxt);
        discountValue = view.findViewById(R.id.discountValue);
        lastTotal = view.findViewById(R.id.lastTotal);
        lastTotalValue = view.findViewById(R.id.lastTotalValue);
        txtVoucher = view.findViewById(R.id.txtVoucher);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        masterLayout = view.findViewById(R.id.masterLayout);
        saldoText = view.findViewById(R.id.saldoText);
        pinText = view.findViewById(R.id.pinText);
        saldoWallet = view.findViewById(R.id.saldoWallet);
        pinValue = view.findViewById(R.id.pinValue);

        if (DirectSDK.layoutItems.getFontPath() != null) {

            SDKUtils.applyFont(DirectSDK.context, saldoText, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, pinText, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, saldoWallet, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, pinValue, DirectSDK.layoutItems.getFontPath());

            SDKUtils.applyFont(DirectSDK.context, totalTxt, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, totalValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, discountTxt, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, discountValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, lastTotal, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, lastTotalValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, txtVoucher, DirectSDK.layoutItems.getFontPath());
        } else {

            SDKUtils.applyFont(getActivity(), saldoText, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), pinText, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), saldoWallet, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), pinValue, "fonts/dokuregular.ttf");

            SDKUtils.applyFont(getActivity(), totalTxt, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), totalValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), discountTxt, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), discountValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), lastTotal, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), lastTotalValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getActivity(), txtVoucher, "fonts/dokuregular.ttf");
        }

        if (DirectSDK.layoutItems.getFontColor() != null) {
            saldoWallet.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            pinValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            totalTxt.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            totalValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            discountTxt.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            discountValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            lastTotal.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            lastTotalValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            txtVoucher.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
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

            saldoText.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            pinText.setTextColor(Color.parseColor(DirectSDK.layoutItems.getLabelTextColor()));
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
            ContentValues data;
            try {
                if (DirectSDK.userDetails.getListPromotion() != null && !promoID.equalsIgnoreCase("")) {
                    String dataJSON = SDKUtils.createRequestCashWallet(channelCode, SDKUtils.Encrypt(userPin, DirectSDK.paymentItems.getPublicKey()), DirectSDK.userDetails.getInquiryCode(),
                            DirectSDK.userDetails.getCustomerName(), SDKUtils.Encrypt(DirectSDK.userDetails.getCustomerEmail(), DirectSDK.paymentItems.getPublicKey()),
                            SDKUtils.Encrypt(DirectSDK.userDetails.getDokuID(), DirectSDK.paymentItems.getPublicKey()), DirectSDK.loginModel.getTokenID(), DirectSDK.loginModel.getPairingCode(),
                            DirectSDK.paymentItems.getDataWords(), promoItem.get(spinPosition - 1).getPromoID());

                    data = new ContentValues();
                    data.put("data", dataJSON);

                } else {
                    String dataJSON = SDKUtils.createRequestCashWallet(channelCode, SDKUtils.Encrypt(userPin, DirectSDK.paymentItems.getPublicKey()), DirectSDK.userDetails.getInquiryCode(),
                            DirectSDK.userDetails.getCustomerName(), SDKUtils.Encrypt(DirectSDK.userDetails.getCustomerEmail(), DirectSDK.paymentItems.getPublicKey()),
                            SDKUtils.Encrypt(DirectSDK.userDetails.getDokuID(), DirectSDK.paymentItems.getPublicKey()), DirectSDK.loginModel.getTokenID(),
                            DirectSDK.loginModel.getPairingCode(), DirectSDK.paymentItems.getDataWords());

                    data = new ContentValues();
                    data.put("data", dataJSON);
                }


                if (DirectSDK.paymentItems.getIsProduction() == true) {
                    conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_prePaymentProd, data);
                } else {
                    conResult = SDKConnections.httpsConnection(getActivity(), Constants.URL_prePaymentDev, data);
                }

                if (conResult != null) {
                    defResp = new JSONObject(conResult);
                    return defResp;
                } else {
                    Log.d("DATA JSON WALLET", "DATA NULL");
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
                        String responseCallBack = SDKUtils.createResponseCashWallet(DirectSDK.loginModel.getTokenID(), DirectSDK.loginModel.getPairingCode(), json.getString("res_response_msg"),
                                json.getString("res_response_code"), DirectSDK.paymentItems.getDataImei(), DirectSDK.paymentItems.getDataAmount(), DirectSDK.loginModel.getTokenCode(),
                                DirectSDK.paymentItems.getDataTransactionID(), DirectSDK.userDetails.getCustomerName(),
                                DirectSDK.userDetails.getCustomerEmail(), DirectSDK.paymentItems.getMobilePhone());
                        DirectSDK.callbackResponse.onSuccess(responseCallBack);
                        getActivity().finish();
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
}
