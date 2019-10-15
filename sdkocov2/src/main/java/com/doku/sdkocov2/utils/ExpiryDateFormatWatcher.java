package com.doku.sdkocov2.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by zaki on 2/29/16.
 */
public class ExpiryDateFormatWatcher implements TextWatcher {

    private static final char space = '/';

    EditText validValue;

    public ExpiryDateFormatWatcher(EditText validValue) {
        this.validValue = validValue;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String working = s.toString();
        String working2;
        boolean isValid = true;
        if (working.length() == 2 && before == 0) {
            if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 12) {
                isValid = false;
            } else {
                working += "/";
                validValue.setText(working);
                validValue.setSelection(working.length());

            }
        } else if (working.length() == 1 && Integer.parseInt(working) > 1) {
            working = "0" + working;
            working += "/";
            validValue.setText(working);
            validValue.setSelection(working.length());
        }


        if (!isValid) {
            validValue.setError("Enter a valid date: MM/YY");
        } else {
            validValue.setError(null);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }
}
