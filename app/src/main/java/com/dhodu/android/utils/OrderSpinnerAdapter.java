package com.dhodu.android.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dhodu.android.R;
import com.parse.ParseObject;

import java.util.List;


public class OrderSpinnerAdapter extends ArrayAdapter<String> {

    Activity context;
    List<ParseObject> objects;

    public OrderSpinnerAdapter(Activity ctx, int txtViewResourceId, String[] ids, List<ParseObject> objects) {
        super(ctx, txtViewResourceId, ids);
        this.context = ctx;
        this.objects = objects;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_order_spinner, parent, false);

        ParseObject object = objects.get(position);

        TextView ordernumber = (TextView) view.findViewById(R.id.orderId);
        TextView orderDetails = (TextView) view.findViewById(R.id.order_detail);

        ordernumber.setText(object.getObjectId());
        orderDetails.setText(getTextForStatus(object.getNumber("status").intValue()));
        return view;
    }

    private String getTextForStatus(int status) {

        switch (status) {
            case 1:
                return "Booking confirmed.";
            case 2:
                return "Laundry picked. Being washed";
            case 3:
                return "Laundry ready. Ready to be delivered";
            case 4:
                return "Laundry out for delivery.";
            case 5:
                return "Laundry delivered";
            default:
                return "";

        }
    }
}
