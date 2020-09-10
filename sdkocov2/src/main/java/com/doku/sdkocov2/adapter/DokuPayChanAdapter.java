package com.doku.sdkocov2.adapter;

import android.content.Context;
import android.graphics.Color;
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
    public static int selectedPosition = 0;
    private final Context context;
    private ArrayList<CCItem> ccItem2;

    public DokuPayChanAdapter(Context context, ArrayList<CCItem> ccItem) {
        super(context, 0, ccItem);
        this.context = context;
        this.ccItem2 = ccItem;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        try {
            CCItem item = ccItem2.get(position);
            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.doku_cc_item, null);
                holder.cardNumber = convertView.findViewById(R.id.cardNumber);
                holder.bankLogo = convertView.findViewById(R.id.imageBank);
                holder.choosenLogo = convertView.findViewById(R.id.choosen);
                holder.masterLayout = convertView.findViewById(R.id.masterLayout);
                holder.radioButton = convertView.findViewById(R.id.radioButton);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.cardNumber.setText(item.getCardNoMasked());

            if (item.getTypeCard().equalsIgnoreCase("VISA")) {

                holder.bankLogo.setBackground(context.getResources().getDrawable(R.drawable.paychan_visa));

            } else if (item.getTypeCard().equalsIgnoreCase("JCB")) {

                holder.bankLogo.setBackground(context.getResources().getDrawable(R.drawable.paychan_jcb));

            } else if (item.getTypeCard().equalsIgnoreCase("MASTERCARD")) {

                holder.bankLogo.setBackground(context.getResources().getDrawable(R.drawable.paychan_mastercard));

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
            Log.i(getClass().getSimpleName(), "Exception message [" + e.getMessage() + "]");
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView cardNumber;
        ImageView bankLogo;
        ImageView choosenLogo;
        RelativeLayout masterLayout;
        RadioButton radioButton;
    }
}
