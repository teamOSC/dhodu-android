package com.dhodu.android.addresses;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dhodu.android.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by naman on 05/09/15.
 */
public class MyAddressesAdapter extends RecyclerView.Adapter<MyAddressesAdapter.ItemHolder> {

    private JSONArray array;

    public MyAddressesAdapter(JSONArray array) {
        this.array= array;
    }

    @Override
    public MyAddressesAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, null);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(MyAddressesAdapter.ItemHolder holder, int position) {

        try {
            JSONObject address = array.getJSONObject(position);
            holder.flat.setText(address.getString("flat"));
            holder.locality.setText(address.getString("locality"));
            holder.pincode.setText(address.getString("pin"));
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return array.length();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        protected TextView flat, locality, pincode;

        public ItemHolder(View itemView) {
            super(itemView);

            flat= (TextView) itemView.findViewById(R.id.address_flat);
            locality = (TextView) itemView.findViewById(R.id.address_locality);
            pincode = (TextView) itemView.findViewById(R.id.address_pincode);
        }
    }
}