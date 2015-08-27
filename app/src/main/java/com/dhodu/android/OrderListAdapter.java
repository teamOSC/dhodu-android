package com.dhodu.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by championswimmer on 26/8/15.
 */
public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ItemHolder> {

    private List<ParseObject> arraylist;

    public OrderListAdapter(List<ParseObject> arraylist) {
        this.arraylist = arraylist;
    }

    @Override
    public OrderListAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, null);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(OrderListAdapter.ItemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);
        }
    }
}
