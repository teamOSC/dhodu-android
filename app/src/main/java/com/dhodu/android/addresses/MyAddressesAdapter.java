package com.dhodu.android.addresses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private Context context;

    public MyAddressesAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
        Log.d("lol",String.valueOf(array.length()));
    }

    @Override
    public MyAddressesAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            Log.d("lol","here");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addnew_address, null);
            return new ItemHolder(v);
        } else {
            Log.d("lol","here2");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, null);
            return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(MyAddressesAdapter.ItemHolder holder, int position) {

        if (getItemViewType(position) == 0) {
            //do nothing
        } else {
            try {
                JSONObject address = array.getJSONObject(position-1);
                holder.flat.setText(address.getString("house"));
                holder.locality.setText(address.getString("locality"));
                holder.pincode.setText(address.getString("pin"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return array.length() + 1;
    }

    public class ItemHolder extends RecyclerView.ViewHolder {

        protected TextView flat, locality, pincode;

        public ItemHolder(View itemView) {
            super(itemView);

            flat= (TextView) itemView.findViewById(R.id.address_flat);
            locality = (TextView) itemView.findViewById(R.id.address_locality);
            pincode = (TextView) itemView.findViewById(R.id.address_pincode);

         itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (getAdapterPosition() == 0) {
                     Intent intent = new Intent(context,AddAddressActivity.class);
                     context.startActivity(intent);
                 }
             }
         });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else return 1;
    }

    public void updateDataSet(JSONArray array) {
        this.array = array;
    }
}