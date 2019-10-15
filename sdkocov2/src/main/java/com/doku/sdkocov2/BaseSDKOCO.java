package com.doku.sdkocov2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doku.sdkocov2.fragment.CCPayment;
import com.doku.sdkocov2.fragment.DokuWalletLogin;
import com.doku.sdkocov2.fragment.ListPayChan;
import com.doku.sdkocov2.fragment.SecondPaymentCC;
import com.doku.sdkocov2.interfaces.iSDKback;
import com.doku.sdkocov2.utils.SDKUtils;

/**
 * Created by zaki on 2/17/16.
 */
public class BaseSDKOCO extends FragmentActivity {

    public static ImageView backButton;
    Bundle bundle;
    Bundle bundleState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        setupLayout();
        //check layout status
        bundle = getIntent().getExtras();
        if (bundle != null) {

            //define layout
            backButton = (ImageView) findViewById(R.id.backButton);

            //select payment method
            if (DirectSDK.posMenu > 2 || DirectSDK.posMenu < 0) {
                selectItem(0);
            } else {
                if (DirectSDK.paymentItems.getTokenPayment() != null && DirectSDK.posMenu == 1) {
                    selectItem(3);
                } else {
                    selectItem(DirectSDK.posMenu);
                }
            }
        }
    }

    private void selectItem(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Locate Position
        Fragment fragment = null;
        bundleState = new Bundle();
        switch (position) {
            case 0:
                fragment = new ListPayChan();
                ft.replace(R.id.main_frame, fragment);
                bundleState.putInt("stateback", 0);
                fragment.setArguments(bundleState);
                break;
            case 1:
                fragment = new CCPayment();
                ft.replace(R.id.main_frame, fragment);
                bundleState.putInt("stateback", 1);
                fragment.setArguments(bundleState);
                break;
            case 2:
                fragment = new DokuWalletLogin();
                ft.replace(R.id.main_frame, fragment);
                bundleState.putInt("stateback", 2);
                fragment.setArguments(bundleState);
                break;
            case 3:
                fragment = new SecondPaymentCC();
                ft.replace(R.id.main_frame, fragment);
                bundleState.putInt("stateback", 3);
                fragment.setArguments(bundleState);
                break;
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    //when activity destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.main_frame);

        try {
            if (frag instanceof ListPayChan) {

                finish();
            } else {
                final iSDKback fragment = (iSDKback) getSupportFragmentManager().findFragmentById(R.id.main_frame);
                if (fragment != null) {
                    fragment.doBack();
                } else {
                    super.onBackPressed();
                }
            }
        } catch (Exception e) {
            finish();
        }
    }

    //setting layout
    private void setupLayout() {
        //define layout
        RelativeLayout toolbarTop;
        TextView textPayment;
        toolbarTop = (RelativeLayout) findViewById(R.id.toolbarTop);
        textPayment = (TextView) findViewById(R.id.textPayment);

        if (DirectSDK.layoutItems.getFontPath() != null) {
            SDKUtils.applyFont(DirectSDK.context, textPayment, DirectSDK.layoutItems.getFontPath());
        } else {
            SDKUtils.applyFont(getApplicationContext(), textPayment, "fonts/dokuregular.ttf");
        }
        //set layout
        if (DirectSDK.layoutItems.getToolbarColor() != null) {
            toolbarTop.setBackgroundColor(Color.parseColor(DirectSDK.layoutItems.getToolbarColor()));
        }

        if (DirectSDK.layoutItems.getToolbarTextColor() != null) {
            try {
                textPayment.setTextColor(Color.parseColor(DirectSDK.layoutItems.getToolbarTextColor()));
            } catch (Exception e) {
                DirectSDK.callbackResponse.onException(e);
            }
        }


    }
}
