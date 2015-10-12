package com.dhodu.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by naman on 16/09/15.
 */
public class ClothesDataAdpater extends RecyclerView.Adapter<ClothesDataAdpater.ViewHolder> {

    JSONArray arrayList;

    public ClothesDataAdpater(JSONArray clothesArray) {
        this.arrayList = clothesArray;
    }

    @Override
    public ClothesDataAdpater.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_cloth, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ClothesDataAdpater.ViewHolder holder, int position) {
        try {
            JSONObject jsonObject = arrayList.getJSONObject(position);
            holder.clothName.setText(jsonObject.getString("cloth_name"));
            holder.clothQuantity.setText(jsonObject.getString("cloth_qty"));
            holder.serviceType.setText(getServiceTypeForCode(jsonObject.getInt("service_type")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.length();
    }

    private String getServiceTypeForCode(int code) {
        switch (code) {
            case 0:
                return "Press";
            case 1:
                return "Wash and Press";
            case 2:
                return "Dry Cleaning";
        }
        return "";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView clothName, clothRate, clothQuantity, serviceType;

        public ViewHolder(View itemView) {
            super(itemView);
            clothName = (TextView) itemView.findViewById(R.id.cloth_name);
            clothRate = (TextView) itemView.findViewById(R.id.rate);
            clothQuantity = (TextView) itemView.findViewById(R.id.cloth_quantity);
            serviceType = (TextView) itemView.findViewById(R.id.service);
        }
    }
}
