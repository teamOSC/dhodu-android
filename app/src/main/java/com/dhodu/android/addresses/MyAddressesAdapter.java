package com.dhodu.android.addresses;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhodu.android.R;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by naman on 05/09/15.
 */
public class MyAddressesAdapter extends RecyclerView.Adapter<MyAddressesAdapter.ItemHolder> {

    private JSONArray array;
    private Context context;
    private boolean isChooseAddress;

    public MyAddressesAdapter(Context context, boolean isChooseAddress) {
        this.context = context;
        this.isChooseAddress = isChooseAddress;
    }

    @Override
    public MyAddressesAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_addnew_address, parent, false);
            return new ItemHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
            return new ItemHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(MyAddressesAdapter.ItemHolder holder, int position) {

        if (getItemViewType(position) == 0) {
            //do nothing
        } else {
            try {
                JSONObject address = array.getJSONObject(position - 1);
                holder.name.setText(address.getString("name"));
                holder.flat.setText(address.getString("house"));
                holder.locality.setText(address.getString("locality"));
                holder.street.setText(address.getString("street"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (array != null)
            return array.length() + 1;
        else return 0;
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

    public class ItemHolder extends RecyclerView.ViewHolder {

        protected TextView name, flat, street, locality;
        protected ImageView editAddress, deleteAddress;

        public ItemHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            flat = (TextView) itemView.findViewById(R.id.address_flat);
            locality = (TextView) itemView.findViewById(R.id.address_locality);
            street = (TextView) itemView.findViewById(R.id.address_street);

            editAddress = (ImageView) itemView.findViewById(R.id.edit_address);
            deleteAddress = (ImageView) itemView.findViewById(R.id.delete_address);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == 0) {
                        Intent intent = new Intent(context, AddAddressActivity.class);
                        context.startActivity(intent);
                    } else {
                        if (isChooseAddress) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("address_index", getAdapterPosition() - 1);
                            returnIntent.putExtra("address_name", flat.getText().toString());
                            ((Activity) context).setResult(Activity.RESULT_OK, returnIntent);
                            ((Activity) context).finish();
                        }
                    }
                }
            });

            if (editAddress != null && deleteAddress != null) {

                editAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, AddAddressActivity.class);
                        context.startActivity(intent);
                    }
                });

                deleteAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context)
                                .setCancelable(true).setMessage("Are you sure you want to delete this adddress ?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteAddress(getAdapterPosition()-1);
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        dialog.create().show();
                    }
                });
            }
        }
    }

    private void deleteAddress(int index) {
        ParseUser user = ParseUser.getCurrentUser();
    }
}