package com.doku.samplesdkapps;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zaki on 3/15/16.
 */
public class ResultPayment extends AppCompatActivity {

    Bundle bundle;
    JSONObject data = null;
    TextView resultText;
    ImageView imageVar;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);

        setupLayout();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ico_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        resultText = (TextView) findViewById(R.id.resultText);
        imageVar = (ImageView) findViewById(R.id.imageVar);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        bundle = getIntent().getExtras();
        if (bundle != null) {

            try {
                data = new JSONObject(bundle.getString("data"));


                if (data.getString("res_response_code").equalsIgnoreCase("0000")) {
                    resultText.setText("Payment Success");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        imageVar.setBackground(getResources().getDrawable(R.drawable.ico_payment_success));
                    } else {
                        imageVar.setBackgroundResource(R.drawable.ico_payment_success);
                    }
                } else {
                    resultText.setText("Payment Failed");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        imageVar.setBackground(getResources().getDrawable(R.drawable.ico_payment_failed));
                    } else {
                        imageVar.setBackgroundResource(R.drawable.ico_payment_failed);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupLayout() {
        TextView resultText;
        Button btnSubmit;

        resultText = (TextView) findViewById(R.id.resultText);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        AppsUtil.applyFont(getApplicationContext(), resultText, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), btnSubmit, "fonts/dokuregular.ttf");
    }
}
