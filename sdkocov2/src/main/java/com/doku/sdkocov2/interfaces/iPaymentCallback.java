package com.doku.sdkocov2.interfaces;

/**
 * Created by zaki on 2/16/16.
 */
public interface iPaymentCallback {
    void onSuccess(String text);
    void onError(String text);
    void onException(Exception e);
}
