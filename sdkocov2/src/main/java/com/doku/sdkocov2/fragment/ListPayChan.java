package com.doku.sdkocov2.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.doku.sdkocov2.BaseSDKOCO;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.adapter.MainPaychanAdapter;
import com.doku.sdkocov2.utils.SDKUtils;
import java.util.ArrayList;

/**
 * Created by zaki on 2/17/16.
 */
public class ListPayChan extends Fragment {
    View view;
    ListView list;
    ArrayList<String> textPay = new ArrayList<>();
    ArrayList<Integer> imagePay = new ArrayList<>();
    Bundle bundleState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_paychan, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLayout();
        imagePay.clear();
        textPay.clear();
        setupArraylistPaychan();
        MainPaychanAdapter adapter = new MainPaychanAdapter(getActivity(), textPay, imagePay);
        list = view.findViewById(R.id.list);
        list.setAdapter(adapter);
        BaseSDKOCO.backButton.setVisibility(View.GONE);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = null;
                bundleState = new Bundle();
                switch (position) {
                    case 0:
                        fragment = new CCPayment();
                        t.replace(R.id.main_frame, fragment);
                        bundleState.putInt("stateback", 0);
                        fragment.setArguments(bundleState);
                        break;
                    case 1:
                        fragment = new DokuWalletLogin();
                        t.replace(R.id.main_frame, fragment);
                        bundleState.putInt("stateback", 0);
                        fragment.setArguments(bundleState);
                        break;
                }
                t.replace(R.id.main_frame, fragment);
                t.commit();
            }
        });
    }

    private void setupLayout() {
        TextView title;
        RelativeLayout masterLayout;
        title = view.findViewById(R.id.title);
        masterLayout = view.findViewById(R.id.masterLayout);

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

    private void setupArraylistPaychan() {
        textPay.add("Doku Wallet");
        imagePay.add(R.drawable.ico_pc_wallet);
        textPay.add("Kartu Kredit");
        imagePay.add(R.drawable.ico_pc_doku);
    }
}