package com.doku.sdkocov2.dokupayment;

import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.doku.sdkocov2.BaseDokuWalletActivity;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.adapter.MainPaychanAdapter;
import com.doku.sdkocov2.utils.SDKUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by zaki on 2/17/16.
 */
public class ListDokuPayChan extends Fragment {
    View view;
    ListView list;
    ArrayList<String> textPay = new ArrayList<>();
    ArrayList<Integer> imagePay = new ArrayList<>();
    Bundle bundleState;
    String channelCode = "00";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_paychan, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupLayout();
        textPay.clear();
        imagePay.clear();
        setupArraylistPaychan();
        MainPaychanAdapter adapter = new MainPaychanAdapter(getActivity(), textPay, imagePay);

        list = view.findViewById(R.id.list);
        list.setAdapter(adapter);

        TextView title = view.findViewById(R.id.title);
        title.setText("Select DOKU Channel");
        BaseDokuWalletActivity.backButton.setVisibility(View.GONE);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setMenu(position, "haveCC");
            }
        });
    }

    private void setMenu(int position, String noCC) {
        FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        bundleState = new Bundle();
        switch (position) {
            case 0:
                fragment = new WalletPaymentFragment();
                t.replace(R.id.main_dokuWallet, fragment);
                if (noCC.equals("NoCC")) {
                    bundleState.putInt("stateback", 1);
                } else {
                    bundleState.putInt("stateback", 0);
                }
                fragment.setArguments(bundleState);
                break;
            case 1:
                fragment = new WalletCCPayment();
                t.replace(R.id.main_dokuWallet, fragment);
                bundleState.putInt("stateback", 0);
                fragment.setArguments(bundleState);
                break;
        }
        t.replace(R.id.main_dokuWallet, fragment);
        t.commit();
    }

    private void setupArraylistPaychan() {
        textPay.add("Cash Balance");
        imagePay.add(R.drawable.ico_pc_wallet);

        try {
            JSONArray ListPaymentData = new JSONArray(DirectSDK.userDetails.getPaymentChannel());

            for (int i = 0; i < ListPaymentData.length(); i++) {
                JSONObject data = ListPaymentData.getJSONObject(i);

                String channelCodeDefault = data.getString("channelCode");
                if (channelCodeDefault.equalsIgnoreCase("02")) {
                    imagePay.add(R.drawable.ico_pc_cc);
                    textPay.add("Credit Card");
                    channelCode = "02";
                }
            }

            if (channelCode.equalsIgnoreCase("00")) {
                setMenu(0, "NoCC");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupLayout() {

        TextView title;
        RelativeLayout masterLayout;

        masterLayout = view.findViewById(R.id.masterLayout);
        title = view.findViewById(R.id.title);

        if (DirectSDK.layoutItems.getFontPath() != null) {
            SDKUtils.applyFont(DirectSDK.context, title, DirectSDK.layoutItems.getFontPath());
        } else {
            SDKUtils.applyFont(getActivity(), title, "fonts/dokuregular.ttf");
        }

        if (DirectSDK.layoutItems.getFontColor() != null) {
            title.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
        }

        if (DirectSDK.layoutItems.getBackgroundColor() != null) {
            masterLayout.setBackgroundColor(Color.parseColor(DirectSDK.layoutItems.getBackgroundColor()));
        }
    }
}