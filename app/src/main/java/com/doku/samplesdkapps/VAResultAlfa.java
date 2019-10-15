package com.doku.samplesdkapps;

import android.content.Intent;
import android.net.Uri;
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
public class VAResultAlfa extends AppCompatActivity {

    Button btnMini;
    TextView txtInvoice, TxtTotal, txtKodePembayaran;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.va_result_alfa);

        setupLayout();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ico_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("data", bundle.getString("data"));
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        btnMini = (Button) findViewById(R.id.btnMini);
        txtKodePembayaran = (TextView) findViewById(R.id.kodePembayaran);
        txtInvoice = (TextView) findViewById(R.id.invoiceValue);
        TxtTotal = (TextView) findViewById(R.id.totalValue);


        bundle = getIntent().getExtras();
        if (bundle != null) {

            Log.d("bundle", bundle.getString("data"));

            try {
                JSONObject jsonObject = new JSONObject(bundle.getString("data"));

                txtInvoice.setText(String.valueOf(AppsUtil.nDigitRandomNo(10)));
                TxtTotal.setText("Rp. 15.000");
                txtKodePembayaran.setText(jsonObject.getString("res_pay_code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        btnMini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://www.google.com");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

    private void setupLayout() {
        TextView txtKode, kodePembayaran, invoiceTxt, invoiceValue, totalTxt, totalValue, ketTxt;
        Button btnATM, btnMini;

        txtKode = (TextView) findViewById(R.id.txtKode);
        kodePembayaran = (TextView) findViewById(R.id.kodePembayaran);
        invoiceTxt = (TextView) findViewById(R.id.invoiceTxt);
        invoiceValue = (TextView) findViewById(R.id.invoiceValue);
        totalTxt = (TextView) findViewById(R.id.totalTxt);
        totalValue = (TextView) findViewById(R.id.totalValue);
        ketTxt = (TextView) findViewById(R.id.ketTxt);

        btnATM = (Button) findViewById(R.id.btnATM);
        btnMini = (Button) findViewById(R.id.btnMini);


        AppsUtil.applyFont(getApplicationContext(), txtKode, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), kodePembayaran, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), invoiceTxt, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), invoiceValue, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), totalTxt, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), totalValue, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), ketTxt, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), btnMini, "fonts/dokuregular.ttf");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("data", bundle.getString("data"));
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
