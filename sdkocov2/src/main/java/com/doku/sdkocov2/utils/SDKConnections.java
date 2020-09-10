package com.doku.sdkocov2.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by zaki on 12/21/15.
 */
public class SDKConnections {
    private static Integer defTimeout = 65;

    public static boolean isConnectingToInternet(Context ctx) {
        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static String httpsConnection(Context ctx, String url, ContentValues data) {
        return httpsConnection(ctx, url, data, defTimeout);
    }

    public static String httpsConnection(Context ctx, String url, ContentValues data, Integer timeout) {
        String result = "";
        try {
            if (isConnectingToInternet(ctx)) {
                try {

                    X509TrustManager trustAllCerts = new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] chain,
                                String authType) {}

                        @Override
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] chain,
                                String authType) {}

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[0];
                        }
                    };

                    URL urlDist = new URL(url);
                    HttpsURLConnection conn = (HttpsURLConnection) urlDist.openConnection();

                    SSLSocketFactory sslSocketFactory = new TLSSocketFactory();
                    SSLContext sslContext;
                    sslContext = SSLContext.getInstance("TLS");

                    sslContext.init(null, new TrustManager[] {
                            trustAllCerts
                    }, new java.security.SecureRandom());

                    conn.setSSLSocketFactory(sslSocketFactory);

                    conn.setReadTimeout(timeout*1000);
                    conn.setConnectTimeout(timeout*1000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getFormData(data));
                    writer.flush();
                    writer.close();
                    os.close();

                    conn.connect();

                    conn.getResponseMessage();

                    BufferedReader br;
                    if (conn.getResponseCode() == 200) {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String strCurrentLine;
                        while ((strCurrentLine = br.readLine()) != null) {
                            result = strCurrentLine;
                        }
                    } else if (conn.getResponseCode() == 202) {
                        result = SDKUtils.createClientResponse(0000, "Request has accepted.");
                    } else if (conn.getResponseCode() == 404) {
                        result = SDKUtils.createClientResponse(9998, "Unable connect to server, please try again later.");
                    } else if (conn.getResponseCode() == 500) {
                        result = SDKUtils.createClientResponse(9997, "Internal server error, We're really sorry about this, and will work hard to get this resolved as soon as possible..");
                    } else {
                        result = SDKUtils.createClientResponse(9996, "Internal server error, We're really sorry about this, and will work hard to get this resolved as soon as possible.");
                    }

                } catch (SocketTimeoutException e) {
                    result = SDKUtils.createClientResponse(3001, "Connection timeout, please check your internet connection");
                } catch (UnknownHostException e) {
                    result = SDKUtils.createClientResponse(3002, "Connection timeout, please check your internet connection");
                } catch (Exception ex) {
                    result = SDKUtils.createClientResponse(3003, "Unable connect to server, please try again");
                }
            } else {
                result = SDKUtils.createClientResponse(3000, "Internet not available, please check your internet connection");
            }
        } catch (Exception ex) {
            result = SDKUtils.createClientResponse(3004, ex.getMessage());
        }
        return result;
    }

    private static String getFormData(ContentValues contentValues) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : contentValues.valueSet()) {
            if (first)
                first = false;
            else
                sb.append("&");

            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }
        return sb.toString();
    }
}
