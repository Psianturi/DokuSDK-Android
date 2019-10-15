package com.doku.samplesdkapps;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.utils.SDKUtils;

/**
 * Created by zaki on 3/28/16.
 */
public class PaymentChannelAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] textPayChan;
    private final Integer[] imageID;

    public PaymentChannelAdapter(Context context, String[] textPayChan,
                                 Integer[] imageID) {
        super(context, com.doku.sdkocov2.R.layout.main_item, textPayChan);
        this.context = context;
        this.textPayChan = textPayChan;
        this.imageID = imageID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        com.doku.sdkocov2.R.layout.main_item, parent, false);
                holder.textPay = (TextView) convertView.findViewById(R.id.textPay);
                holder.masterLayout = (RelativeLayout) convertView.findViewById(R.id.masterLayout);
                holder.imagePay = (ImageView) convertView
                        .findViewById(R.id.imagePay);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.textPay.setText(textPayChan[position]);
            holder.imagePay.setImageResource(imageID[position]);

            if (DirectSDK.layoutItems.getFontPath() != null) {
                SDKUtils.applyFont(DirectSDK.context, holder.textPay, DirectSDK.layoutItems.getFontPath());
            } else {
                SDKUtils.applyFont(context, holder.textPay, "fonts/dokuregular.ttf");
            }

            if (DirectSDK.layoutItems.getFontColor() != null) {
                holder.textPay.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            }

            if (DirectSDK.layoutItems.getBackgroundColor() != null) {
                holder.masterLayout.setBackgroundColor(Color.parseColor(DirectSDK.layoutItems.getBackgroundColor()));
            }
            this.notifyDataSetChanged();


        } catch (Exception e) {
            Log.i(getClass().getSimpleName(),
                    "Exception message [" + e.getMessage() + "]");
        }
        return convertView;
    }

    private static class ViewHolder {
        // Declare Variables
        TextView textPay;
        ImageView imagePay;
        RelativeLayout masterLayout;

    }

}
