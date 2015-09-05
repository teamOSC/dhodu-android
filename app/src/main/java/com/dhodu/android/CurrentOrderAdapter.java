package com.dhodu.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by naman on 06/09/15.
 */
public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.ItemHolder> {

    private Context context;

    public CurrentOrderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CurrentOrderAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addnew_address, parent,false);
            return new ItemHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent,false);
            return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(CurrentOrderAdapter.ItemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
       return 0;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {


        public ItemHolder(View itemView) {
            super(itemView);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else return 1;
    }

}
