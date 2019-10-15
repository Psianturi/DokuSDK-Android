package com.doku.sdkocov2.utils;

import android.view.View;
import android.widget.ScrollView;

/**
 * Created by zaki on 4/4/16.
 */
public class EditTextFocusChangeListener implements View.OnFocusChangeListener {

    private ScrollView scrollView;

    public EditTextFocusChangeListener(ScrollView scrollView) {
        this.scrollView = scrollView;
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            int left = view.getLeft();
            int top = view.getTop();
            int bottom = view.getBottom();
            int keyboardHeight = scrollView.getHeight() / 3;

            // if the bottom of edit text is greater than scroll view height divide by 3,
            // it means that the keyboard is visible
            if (bottom > keyboardHeight) {
                // increase scroll view with padding
                scrollView.setPadding(0, 0, 0, keyboardHeight);
                // scroll to the edit text position
                scrollView.scrollTo(left, top);
            }
        }
    }
}