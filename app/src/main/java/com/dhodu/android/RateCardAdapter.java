package com.dhodu.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by prempal on 12/9/15.
 */
public class RateCardAdapter extends RecyclerView.Adapter<RateCardAdapter.ViewHolder> {

    List<ParseObject> arrayList;
    int serviceType;

    public RateCardAdapter(List<ParseObject> arrayList, int serviceType) {
        this.serviceType = serviceType;
        this.arrayList = arrayList;
    }

    @Override
    public RateCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rate_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RateCardAdapter.ViewHolder holder, int position) {
        holder.clothName.setText(arrayList.get(position).getString("name"));
        Number rate = 0;
        switch (serviceType) {
            case 0:
                rate = arrayList.get(position).getNumber("press_sp");
                break;
            case 1:
                rate = arrayList.get(position).getNumber("wash_press_sp");
                break;
            case 2:
                rate = arrayList.get(position).getNumber("dry_clean_sp");
                break;
        }
        holder.clothRate.setText(rate.intValue() == 0 ? "-" : "₹ " + rate.toString());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView clothName, clothRate;

        public ViewHolder(View itemView) {
            super(itemView);
            clothName = (TextView) itemView.findViewById(R.id.cloth_name);
            clothRate = (TextView) itemView.findViewById(R.id.rate);
        }
    }
}
