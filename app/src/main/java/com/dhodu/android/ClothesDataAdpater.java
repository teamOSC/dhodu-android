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
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView clothName, clothRate, clothQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            clothName = (TextView) itemView.findViewById(R.id.cloth_name);
            clothRate = (TextView) itemView.findViewById(R.id.rate);
            clothQuantity = (TextView) itemView.findViewById(R.id.cloth_quantity);
        }
    }
}
