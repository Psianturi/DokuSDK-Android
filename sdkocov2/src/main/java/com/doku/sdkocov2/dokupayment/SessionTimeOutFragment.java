package com.doku.sdkocov2.dokupayment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.doku.sdkocov2.BaseDokuWalletActivity;
import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.interfaces.iSDKback;
import com.doku.sdkocov2.utils.SDKUtils;

/**
 * Created by zaki on 2/25/16.
 */
public class SessionTimeOutFragment extends FragmentActivity implements iSDKback {
    int stateback;
    Button btnSubmit;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_timeout);

        setupLayout();
        bundle = getIntent().getExtras();

        if (bundle != null) {
            stateback = bundle.getInt("stateback");
            BaseDokuWalletActivity.backButton.setVisibility(View.VISIBLE);
            BaseDokuWalletActivity.backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(202, "Session Doku Wallet Expired"));
                    finish();
                }
            });
        }

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupLayout() {
        TextView timeout;
        RelativeLayout masterLayout;
        Button btnSubmit;

        btnSubmit = findViewById(R.id.btnSubmit);
        masterLayout = findViewById(R.id.masterLayout);
        timeout = findViewById(R.id.timeout);

        if (DirectSDK.layoutItems.getFontPath() != null) {
            SDKUtils.applyFont(DirectSDK.context, timeout, DirectSDK.layoutItems.getFontPath());
        } else {
            SDKUtils.applyFont(this, timeout, "fonts/dokuregular.ttf");
        }

        if (DirectSDK.layoutItems.getFontColor() != null) {
            timeout.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
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
    }

    @Override
    public void doBack() {
        DirectSDK.callbackResponse.onError(SDKUtils.createClientResponse(202, "Session Doku Wallet Timeout"));
        finish();
    }
}
