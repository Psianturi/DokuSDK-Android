package com.doku.samplesdkapps;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zaki on 3/28/16.
 */
public class VirtualAccount extends AppCompatActivity {
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.va_payment);

        setupLayout();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Payment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ico_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VirtualAccountPayment().execute();
            }
        });
    }

    private void setupLayout() {
        TextView textVa;
        Button btnSubmit;

        textVa = findViewById(R.id.textVa);
        btnSubmit = findViewById(R.id.btnSubmit);

        AppsUtil.applyFont(getApplicationContext(), textVa, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), btnSubmit, "fonts/dokuregular.ttf");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private class VirtualAccountPayment extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VirtualAccount.this);
            pDialog.setMessage("Mohon Tunggu ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONObject defResp = null;
            try {
                JSONObject jGroup = new JSONObject();
                try {
                    jGroup.put("req_device_id", "DEMOSDK-"+ AppsUtil.generateInvoiceId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("request VA", jGroup.toString());
                ContentValues data = new ContentValues();
                data.put("data", jGroup.toString());

                String conResult = ApiConnection.httpsConnection(VirtualAccount.this, Constants.URL_REQUEST_VACODE, data);
                Log.d("DATA PAYMENT", conResult);

                defResp = new JSONObject(conResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return defResp;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            pDialog.dismiss();
            if (json != null) {
                Log.d("json", json.toString());
                try {
                    if (json.getString("res_response_code").equalsIgnoreCase("0000") && json != null) {
                        Intent intent = new Intent(getApplicationContext(), VAResult.class);
                        intent.putExtra("data", json.toString());
                        startActivity(intent);
                        finish();
                    } else {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_CANCELED, returnIntent);
                        finish();
                    }
                } catch (JSONException e) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();

                    e.printStackTrace();
                }
            }
        }
    }
}
