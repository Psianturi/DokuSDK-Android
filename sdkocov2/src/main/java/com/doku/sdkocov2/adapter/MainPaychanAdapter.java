package com.doku.sdkocov2.adapter;

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
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.utils.SDKUtils;

import java.util.ArrayList;

/**
 * Created by zaki on 3/14/16.
 */
public class MainPaychanAdapter extends ArrayAdapter<String> {

    //declare variable
    private final Context context;

    private final ArrayList<String> textPayChan;
    private final ArrayList<Integer> imageID;

    //initiate adapter
    public MainPaychanAdapter(Context context, ArrayList<String> textPayChan,ArrayList<Integer> imageID) {
        super(context, R.layout.main_item, textPayChan);
        this.context = context;
        this.textPayChan = textPayChan;
        this.imageID = imageID;
    }

    //view function
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            //declare variable
            final ViewHolder holder;

            if (convertView == null) {

                //intiate view
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.main_item, parent, false);
                holder.textPay = (TextView) convertView.findViewById(R.id.textPay);
                holder.masterLayout = (RelativeLayout) convertView.findViewById(R.id.masterLayout);
                holder.imagePay = (ImageView) convertView
                        .findViewById(R.id.imagePay);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //get value
            holder.textPay.setText(textPayChan.get(position));
            holder.imagePay.setImageResource(imageID.get(position));

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
