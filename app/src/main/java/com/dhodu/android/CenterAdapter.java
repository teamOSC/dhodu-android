package com.dhodu.android;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by championswimmer on 26/8/15.
 */
public class CenterAdapter extends RecyclerView.Adapter<CenterAdapter.ItemHolder> {

    private AppCompatActivity activity;
    private List<ParseObject> arraylist;

    public CenterAdapter(AppCompatActivity activity, List<ParseObject> arraylist) {
        this.activity = activity;
        this.arraylist = arraylist;
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
            holder.transactionId.setText(arraylist.get(position - 1).getNumber("transaction_id").toString());
            holder.transactionDate.setText(arraylist.get(position - 1).getCreatedAt().toString());
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

        TextView transactionId, transactionDate, orderCount;

        public ItemHolder(View itemView) {
            super(itemView);
            transactionId = (TextView) itemView.findViewById(R.id.transaction_id);
            transactionDate = (TextView) itemView.findViewById(R.id.transaction_date);
            orderCount = (TextView) itemView.findViewById(R.id.order_count);
        }
    }
}
