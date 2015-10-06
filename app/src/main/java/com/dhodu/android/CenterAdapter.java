package com.dhodu.android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by championswimmer on 26/8/15.
 */
public class CenterAdapter extends RecyclerView.Adapter<CenterAdapter.ItemHolder> {

    private AppCompatActivity activity;
    private List<ParseObject> arraylist;
    private Context context;

    public CenterAdapter(AppCompatActivity activity, List<ParseObject> arraylist, Context context) {
        this.activity = activity;
        this.arraylist = arraylist;
        this.context = context;
    }

    @Override
    public CenterAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            View v0 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
            return new ItemHolder(v0);
        }else {
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new ItemHolder(v2);
        }
    }

    @Override
    public void onBindViewHolder(CenterAdapter.ItemHolder holder, int position) {

        if (getItemViewType(position) == 0) {
            if (arraylist.size() == 0) {
                holder.orderCount.setText("Your order history will appear here.");
            } else {
                if (arraylist.size() == 1)
                    holder.orderCount.setText("1 order placed");
                else holder.orderCount.setText(arraylist.size() + " orders placed");
            }
        }  else {
            final ParseObject object = arraylist.get(position-1);
            holder.transactionId.setText("Order Id : "+object.getNumber("transaction_id").toString());

                String transaction_type = object.getString("service_type");
                String serviceString = null;
                if (transaction_type.contains("0")) {
                    serviceString = getServiceForType(0);
                }
                if (transaction_type.contains("1")) {
                    if (serviceString != null)
                        serviceString = serviceString + ", " + getServiceForType(1);
                    else
                        serviceString = getServiceForType(1);
                }
                if (transaction_type.contains("2")) {
                    if (serviceString != null)
                        serviceString = serviceString + ", " + getServiceForType(2);
                    else
                        serviceString = getServiceForType(2);
                }

            holder.orderType.setText(serviceString);
            if (object.getNumber("amount") != null) {
                holder.orderAmount.setText("Order amount : ₹ "+object.getNumber("amount").toString());
            } else holder.orderAmount.setVisibility(View.GONE);

            Date date = object.getCreatedAt();
            Format formatter = new SimpleDateFormat("dd MMM yyyy   HH:mm");
            holder.transactionDate.setText(formatter.format(date).toString());

            holder.viewBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,BillSummaryActivity.class);
                    intent.putExtra("transaction_id",object.getObjectId());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else return 1;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        TextView transactionId, transactionDate, orderCount, orderType, orderAmount;
        Button viewBill;

        public ItemHolder(View itemView) {
            super(itemView);
            transactionId = (TextView) itemView.findViewById(R.id.transaction_id);
            transactionDate = (TextView) itemView.findViewById(R.id.transaction_date);
            orderCount = (TextView) itemView.findViewById(R.id.order_count);
            orderType = (TextView) itemView.findViewById(R.id.order_type);
            orderAmount = (TextView) itemView.findViewById(R.id.order_amount);
            viewBill = (Button) itemView.findViewById(R.id.view_bill);
        }
    }

    private String getServiceForType(int type) {
        switch (type) {
            case 0:
                return "Press";
            case 1:
                return "Wash and Press";
            case 2:
                return "Dry Cleaning";
            default:
                return "";
        }
    }
}
