package com.doku.sdkocov2.interfaces;

/**
 * Created by zaki on 2/16/16.
 */
public interface iPaymentCallback {

    public void onSuccess(String text);

    public void onError(String text);

    public void onException(Exception e);
}
