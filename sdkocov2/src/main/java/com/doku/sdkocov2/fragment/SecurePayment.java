package com.doku.sdkocov2.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;

import com.doku.sdkocov2.R;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by zaki on 3/3/16.
 */
public class SecurePayment extends FragmentActivity {
    String ACSURL, TERMURL, PAREQ, MD;
    private WebView webView;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.secure_layout);

        webView = findViewById(R.id.webView1);

        bundle = getIntent().getExtras();

        if (bundle != null) {
            ACSURL = bundle.getString("ACSURL");
            TERMURL = bundle.getString("TERMURL");
            PAREQ = bundle.getString("PAREQ");
            MD = bundle.getString("MD");
            final RelativeLayout loadpage = findViewById(R.id.loadpage);

            webView.getSettings().setJavaScriptEnabled(true);
            if (Build.VERSION.SDK_INT >= 21) {
                webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            webView.getSettings().setDomStorageEnabled(true);
            webView.clearCache(true);
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                public void onPageStarted(WebView view, String url, Bitmap favicon) {}

                @Override
                public void onPageFinished(WebView view, final String url) {
                    try {
                        loadpage.setVisibility(View.GONE);
                        if (url.trim().equalsIgnoreCase(TERMURL)) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result", "doRequestResponse");
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                        view.clearCache(true);
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
            });

            String postData;
            try {
                postData = String.format(Locale.US, "MD=%1$s&TermUrl=%2$s&PaReq=%3$s",
                        URLEncoder.encode(MD, "UTF-8"),
                        URLEncoder.encode(TERMURL, "UTF-8"),
                        URLEncoder.encode(PAREQ, "UTF-8")
                );
                webView.postUrl(ACSURL, postData.getBytes());
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "propertyNull");
            setResult(RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}

