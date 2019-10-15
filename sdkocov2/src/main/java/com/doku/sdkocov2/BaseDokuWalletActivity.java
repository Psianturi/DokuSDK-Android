package com.doku.sdkocov2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doku.sdkocov2.dokupayment.ListDokuPayChan;
import com.doku.sdkocov2.dokupayment.SessionTimeOutFragment;
import com.doku.sdkocov2.dokupayment.WalletCCPayment;
import com.doku.sdkocov2.dokupayment.WalletPaymentFragment;
import com.doku.sdkocov2.interfaces.iSDKback;
import com.doku.sdkocov2.utils.ImageUtil;
import com.doku.sdkocov2.utils.SDKUtils;

import java.io.InputStream;

/**
 * Created by zaki on 2/25/16.
 */
public class BaseDokuWalletActivity extends FragmentActivity {
    public static ImageView backButton;
    public static CountDownTimer timeoutTimer;
    TextView toolbarValue, dokuName;
    ImageView profileView;
    Bundle bundleState;
    boolean sessionExpired = false;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_dokuwallet);

        setupLayout();


        timeoutTimer = new CountDownTimer((600 * 1000), 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                sessionExpired = true;
                openSessionTimeout();
            }
        }.start();


        //define layout
        toolbarValue = (TextView) findViewById(R.id.toolbarValue);
        backButton = (ImageView) findViewById(R.id.backButton);
        toolbarValue.setText(SDKUtils.EYDNumberFormat(DirectSDK.paymentItems.getDataAmount()));
        profileView = (ImageView) findViewById(R.id.profilePic);

        try {

            if (DirectSDK.userDetails.getAvatar() != null) {
                new DownloadImageTask(profileView).execute(DirectSDK.userDetails.getAvatar());
            }

        } catch (Exception e) {
            DirectSDK.callbackResponse.onException(e);
        }

        dokuName = (TextView) findViewById(R.id.dokuName);

        dokuName.setText(DirectSDK.userDetails.getCustomerName());

        selectItem(0);
    }

    private void selectItem(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Locate Position
        Fragment fragment = null;
        bundleState = new Bundle();

        switch (position) {
            case 0:
                fragment = new ListDokuPayChan();
                ft.replace(R.id.main_dokuWallet, fragment);
                bundleState.putInt("stateback", 0);
                fragment.setArguments(bundleState);
                break;
            case 1:
                fragment = new WalletPaymentFragment();
                ft.replace(R.id.main_dokuWallet, fragment);
                bundleState.putInt("stateback", 1);
                fragment.setArguments(bundleState);
                break;
            case 2:
                fragment = new WalletCCPayment();
                ft.replace(R.id.main_dokuWallet, fragment);
                bundleState.putInt("stateback", 2);
                fragment.setArguments(bundleState);
                break;
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    //setting layout
    private void setupLayout() {
        //define layout
        RelativeLayout toolbarTop;
        TextView textPayment, toolbarValue, idrValue, dokuName;
        RelativeLayout masterLayout;

        masterLayout = (RelativeLayout) findViewById(R.id.masterLayout);
        toolbarTop = (RelativeLayout) findViewById(R.id.toolbarTop);
        textPayment = (TextView) findViewById(R.id.textPayment);
        toolbarValue = (TextView) findViewById(R.id.toolbarValue);
        idrValue = (TextView) findViewById(R.id.idrValue);
        dokuName = (TextView) findViewById(R.id.dokuName);


        if (DirectSDK.layoutItems.getFontPath() != null) {
            SDKUtils.applyFont(DirectSDK.context, textPayment, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, toolbarValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, idrValue, DirectSDK.layoutItems.getFontPath());
            SDKUtils.applyFont(DirectSDK.context, dokuName, DirectSDK.layoutItems.getFontPath());
        } else {
            SDKUtils.applyFont(getApplicationContext(), textPayment, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getApplicationContext(), toolbarValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getApplicationContext(), idrValue, "fonts/dokuregular.ttf");
            SDKUtils.applyFont(getApplicationContext(), dokuName, "fonts/dokuregular.ttf");
        }

        //font color
        if (DirectSDK.layoutItems.getFontColor() != null) {
            dokuName.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
        }

        //set layout
        if (DirectSDK.layoutItems.getToolbarColor() != null) {
            toolbarTop.setBackgroundColor(Color.parseColor(DirectSDK.layoutItems.getToolbarColor()));
        }

        if (DirectSDK.layoutItems.getToolbarTextColor() != null) {
            try {
                textPayment.setTextColor(Color.parseColor(DirectSDK.layoutItems.getToolbarTextColor()));
                toolbarValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getToolbarTextColor()));
                idrValue.setTextColor(Color.parseColor(DirectSDK.layoutItems.getToolbarTextColor()));
            } catch (Exception e) {
                DirectSDK.callbackResponse.onException(e);
            }
        }

        if (DirectSDK.layoutItems.getBackgroundColor() != null) {
            masterLayout.setBackgroundColor(Color.parseColor(DirectSDK.layoutItems.getBackgroundColor()));
        }


    }

    @Override
    public void onBackPressed() {

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.main_dokuWallet);

        try {
            if (frag instanceof ListDokuPayChan) {
                if (timeoutTimer != null) {
                    timeoutTimer.cancel();
                    timeoutTimer = null;
                }

                if (doubleBackToExitPressedOnce) {
                    DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(200, "canceled by user"));
                    finish();
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to cancel Payment", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                final iSDKback fragment = (iSDKback) getSupportFragmentManager().findFragmentById(R.id.main_dokuWallet);
                if (fragment != null) {
                    fragment.doBack();
                } else {
                    super.onBackPressed();
                }
            }

        } catch (Exception e) {
            DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(200, "canceled by user"));
            finish();
        }

    }

    @Override
    protected void onResume() {
        openSessionTimeout();
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void openSessionTimeout() {

        if (sessionExpired) {
            bundleState.putInt("stateback", 3);
            Intent intent = new Intent(BaseDokuWalletActivity.this, SessionTimeOutFragment.class);
            intent.putExtra("stateback", 3);
            startActivity(intent);
            finish();
        } else {
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            try {
                bmImage.setImageBitmap(ImageUtil.getCircularBitmap(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}