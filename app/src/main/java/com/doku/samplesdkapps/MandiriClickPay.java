package com.doku.samplesdkapps;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by zaki on 3/28/16.
 */
public class MandiriClickPay extends AppCompatActivity {

    private static final char space = '-';
    Button btnSubmit;
    EditText debitCard, responValue;
    String mandiriCardNumber;
    TextView challengeValue1, challengeValue2, challengeValue3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mandiri_clickpay);

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
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });

        debitCard = findViewById(R.id.cardNumber);
        responValue = findViewById(R.id.responValue);
        btnSubmit = findViewById(R.id.btnSubmit);
        challengeValue1 = findViewById(R.id.challengeValue1);
        challengeValue2 = findViewById(R.id.challengeValue2);
        challengeValue3 = findViewById(R.id.challengeValue3);
        responValue.setText("000000");
        challengeValue2.setText("15000");
        challengeValue3.setText(String.valueOf(AppsUtil.nDigitRandomNo(8)));

        debitCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Remove spacing char
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }

                if (s.length() == 19) {
                    mandiriCardNumber = debitCard.getText().toString().replace("-", "");
                    challengeValue1.setText(mandiriCardNumber.substring(6, 16));
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(responValue.getText().toString())) {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    returnIntent.putExtra("responseValue", responValue.getText().toString());
                    returnIntent.putExtra("challenge1", challengeValue1.getText().toString());
                    returnIntent.putExtra("challenge2", challengeValue2.getText().toString());
                    returnIntent.putExtra("challenge3", challengeValue3.getText().toString());
                    returnIntent.putExtra("debitCard", debitCard.getText().toString().replace("-", ""));
                    finish();
                } else {
                    responValue.setError(getString(com.doku.sdkocov2.R.string.error_field_required));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    private void setupLayout() {
        TextView debitTxt, mandiriText, mandiriTextToken, challengeText1, challengeText2, challengeText3, challengeValue1, challengeValue2, challengeValue3, responTxt;
        Button btnSubmit;
        EditText cardNumber, responValue;

        debitTxt = findViewById(R.id.debitTxt);
        mandiriText = findViewById(R.id.mandiriText);
        mandiriTextToken = findViewById(R.id.mandiriTextToken);
        challengeText1 = findViewById(R.id.challengeText1);
        challengeText2 = findViewById(R.id.challengeText2);
        challengeText3 = findViewById(R.id.challengeText3);
        challengeValue1 = findViewById(R.id.challengeValue1);
        challengeValue2 = findViewById(R.id.challengeValue2);
        challengeValue3 = findViewById(R.id.challengeValue3);
        responTxt = findViewById(R.id.responTxt);
        cardNumber = findViewById(R.id.cardNumber);
        responValue = findViewById(R.id.responValue);
        btnSubmit = findViewById(R.id.btnSubmit);

        AppsUtil.applyFont(getApplicationContext(), debitTxt, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), mandiriText, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), mandiriTextToken, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), challengeText1, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), challengeText2, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), challengeText3, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), challengeValue1, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), challengeValue2, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), challengeValue3, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), responTxt, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), cardNumber, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), responValue, "fonts/dokuregular.ttf");
        AppsUtil.applyFont(getApplicationContext(), btnSubmit, "fonts/dokuregular.ttf");
    }
}
