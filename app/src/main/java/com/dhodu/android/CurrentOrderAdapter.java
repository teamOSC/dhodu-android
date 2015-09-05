package com.dhodu.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by naman on 06/09/15.
 */
public class CurrentOrderAdapter extends RecyclerView.Adapter<CurrentOrderAdapter.ItemHolder> {

    private Context context;
    private List<ParseObject> arraylist;

    public CurrentOrderAdapter(Context context, List<ParseObject> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
    }

    @Override
    public CurrentOrderAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order_0, parent, false);
                return new ItemHolder(v);
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order_1, parent, false);
                return new ItemHolder(v);
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order_2, parent, false);
                return new ItemHolder(v);
            case 3:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order_3, parent, false);
                return new ItemHolder(v);
            case 4:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order_4, parent, false);
                return new ItemHolder(v);
            case 5:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order_5, parent, false);
                return new ItemHolder(v);
            case 6:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order_6, parent, false);
                return new ItemHolder(v);
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_current_order_6, parent, false);
                return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(CurrentOrderAdapter.ItemHolder holder, int position) {

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

    @Override
    public int getItemViewType(int position) {
        switch (arraylist.get(position).getInt("status")) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            default:
                return 6;
        }
    }

}
