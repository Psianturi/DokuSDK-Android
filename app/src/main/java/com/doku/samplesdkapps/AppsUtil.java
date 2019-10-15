package com.doku.samplesdkapps;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by zaki on 3/28/16.
 */
public class AppsUtil {


    public static void applyFont(Context context, TextView textView,
                                 String fontPath) {
        try {

            textView.setTypeface(Typeface.createFromAsset(
                    context.getAssets(), fontPath));
        } catch (Exception e) {
            Log.e("Font Apply", String.format(
                    "Error occured when trying to apply %s font for %s view",
                    fontPath, textView));
            e.printStackTrace();
        }
    }

    public static String SHA1(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (Throwable th) {
            th.printStackTrace();
            return "";
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String generateMoneyFormat(String number) {
        double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("####.00");
        return formatter.format(amount);
    }


    public static int nDigitRandomNo(int digits) {
        int max = (int) Math.pow(10, (digits)) - 1; //for digits =7, max will be 9999999
        int min = (int) Math.pow(10, digits - 1); //for digits = 7, min will be 1000000
        int range = max - min; //This is 8999999
        Random r = new Random();
        int x = r.nextInt(range);// This will generate random integers in range 0 - 8999999
        int nDigitRandomNo = x + min; //Our random rumber will be any random number x + min
        return nDigitRandomNo;
    }

}
