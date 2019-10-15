package com.doku.sdkocov2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doku.sdkocov2.DirectSDK;
import com.doku.sdkocov2.R;
import com.doku.sdkocov2.model.CCItem;
import com.doku.sdkocov2.utils.SDKUtils;

import java.util.ArrayList;

/**
 * Created by zaki on 3/14/16.
 */
public class DokuPayChanAdapter extends ArrayAdapter<CCItem> {

    //declare variable
    public static int selectedPosition = 0;
    private final Context context;
    private ArrayList<CCItem> ccItem2;

    //function initiate adapter
    public DokuPayChanAdapter(Context context, ArrayList<CCItem> ccItem) {
        super(context, 0, ccItem);
        this.context = context;
        this.ccItem2 = ccItem;
    }

    //view function
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            //cash to CCItem model
            CCItem item = ccItem2.get(position);

            //declare variable
            final ViewHolder holder;

            if (convertView == null) {
                //initiate view
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.doku_cc_item, null);
                holder.cardNumber = (TextView) convertView.findViewById(R.id.cardNumber);
                holder.bankLogo = (ImageView) convertView.findViewById(R.id.imageBank);
                holder.choosenLogo = (ImageView) convertView.findViewById(R.id.choosen);
                holder.masterLayout = (RelativeLayout) convertView.findViewById(R.id.masterLayout);
                holder.radioButton = (RadioButton) convertView.findViewById(R.id.radioButton);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //get value
            holder.cardNumber.setText(item.getCardNoMasked());

            if (item.getTypeCard().equalsIgnoreCase("VISA")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.bankLogo.setBackground(context.getResources().getDrawable(R.drawable.paychan_visa));

                } else {
                    holder.bankLogo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.paychan_visa));

                }

            } else if (item.getTypeCard().equalsIgnoreCase("JCB")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.bankLogo.setBackground(context.getResources().getDrawable(R.drawable.paychan_jcb));

                } else {
                    holder.bankLogo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.paychan_jcb));

                }

            } else if (item.getTypeCard().equalsIgnoreCase("MASTERCARD")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.bankLogo.setBackground(context.getResources().getDrawable(R.drawable.paychan_mastercard));

                } else {
                    holder.bankLogo.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.paychan_mastercard));
                }

            }
            if (DirectSDK.layoutItems.getFontPath() != null) {
                SDKUtils.applyFont(DirectSDK.context, holder.cardNumber, DirectSDK.layoutItems.getFontPath());
            } else {
                SDKUtils.applyFont(context, holder.cardNumber, "fonts/dokuregular.ttf");
            }

            if (DirectSDK.layoutItems.getFontColor() != null) {
                holder.cardNumber.setTextColor(Color.parseColor(DirectSDK.layoutItems.getFontColor()));
            }

            if (DirectSDK.layoutItems.getBackgroundColor() != null) {
                holder.masterLayout.setBackgroundColor(Color.parseColor(DirectSDK.layoutItems.getBackgroundColor()));
            }


            holder.radioButton.setChecked(position == selectedPosition);
            holder.radioButton.setTag(position);
            holder.masterLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            });

            holder.radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            });


        } catch (Exception e) {
            Log.i(getClass().getSimpleName(),
                    "Exception message [" + e.getMessage() + "]");
        }
        return convertView;
    }

    private static class ViewHolder {
        // Declare Variables
        TextView cardNumber;
        ImageView bankLogo;
        ImageView choosenLogo;
        RelativeLayout masterLayout;
        RadioButton radioButton;
    }

}
