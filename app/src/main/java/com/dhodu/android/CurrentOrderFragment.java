package com.dhodu.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dhodu.android.ui.StepsView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by naman on 06/09/15.
 */
public class CurrentOrderFragment extends Fragment {

    FrameLayout rootContainer;
    StepsView stepsView;


    private final String[] statuses= {"", "", "", "", "", ""};

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_current_order, container, false);

        rootContainer = (FrameLayout) view.findViewById(R.id.rootContainer);

        fetchCurrentOrders();
        return view;
    }

    private void fetchCurrentOrders() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
        query.whereEqualTo("customer", ParseUser.getCurrentUser());
        query.include("Agent");
        query.whereLessThanOrEqualTo("status", 5);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    setUpOrderCard(object);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUpOrderCard(ParseObject object) {
        int layoutId =getLayoutIdForStatus(object.getInt("status"));

        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView =  inflater.inflate(layoutId, null);
        stepsView = (StepsView) cardView.findViewById(R.id.stepsView);
        rootContainer.addView(cardView);
        setCardDetails(object,cardView);
    }

    private void setCardDetails(ParseObject transaction, View cardView) {

        int statusCode = transaction.getInt("status");

        TextView transactionId =(TextView) cardView.findViewById(R.id.transaction_id);
        TextView transactiondate =(TextView) cardView.findViewById(R.id.transaction_date);
        TextView agentname = (TextView) cardView.findViewById(R.id.agentName);
        TextView agentVehicle = (TextView) cardView.findViewById(R.id.agentVehicle);
        TextView pickaddress =(TextView) cardView.findViewById(R.id.pick_address);
        TextView serviceType = (TextView) cardView.findViewById(R.id.service_type);

        RatingBar ratingBar = (RatingBar) cardView.findViewById(R.id.rating);

        switch (statusCode) {
            case 0:
                setStatusToHeader("Order placed. Confirmation awaited.",R.drawable.ic_cancel_white_48dp);
                GoogleMapOptions options = new GoogleMapOptions();

                SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);

                FragmentManager fragmentManager = getChildFragmentManager();
                fragmentManager.beginTransaction().add(R.id.map, mapFragment).commitAllowingStateLoss();

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        addToMap(new LatLng(28.542,77.259), googleMap);
                    }
                });

                break;
            case 1:
                setStatusToHeader("Booking confirmed.",R.drawable.ic_cancel_white_48dp);
                break;
            case 2:
                setStatusToHeader("Laundry picked. Being washed",R.drawable.ic_cancel_white_48dp);
                break;
            case 3:
                setStatusToHeader("Laundry ready. Ready to be delivered",R.drawable.phone);
                break;
            case 4:
                setStatusToHeader("Laundry out for delivery.",R.drawable.phone);
                break;
            case 5:
                setStatusToHeader("Laundry delivered",R.drawable.phone);
                break;
            default:
                break;
        }

        if (stepsView!=null) {
            stepsView.setLabels(statuses)
                    .setBarColorIndicator(getContext().getResources().getColor(R.color.material_blue_grey_800))
                    .setProgressColorIndicator(Color.parseColor("#FF9800"))
                    .setLabelColorIndicator(getContext().getResources().getColor(R.color.dhodu_primary_dark))
                    .setCompletedPosition(statusCode)
                    .drawView();
        }

        if (ratingBar!=null){
            ratingBar.setRating(4);
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.dhodu_primary_dark), PorterDuff.Mode.SRC_ATOP);
        }

        if (serviceType!=null) {
            JSONArray array = transaction.getJSONArray("clothes_data");
            if (array!=null) {
                String serviceString = "";
                for (int i = 0; i < array.length(); i++) {
                    try {
                        serviceString = serviceString + getServiceForType(array.getJSONObject(i).getInt("service_type")) + " ";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                serviceType.setText(serviceString);
            }
        }

        if (agentname!=null) {
            ParseObject object = transaction.getParseObject("agent_pick");
            try {
                agentname.setText(object.fetchIfNeeded().getString("name"));
                agentVehicle.setText(object.fetchIfNeeded().getString("vehicle"));
            } catch (ParseException e){
                e.printStackTrace();
            }
        }

        if (transactionId!=null)
        transactionId.setText(String.valueOf(transaction.getInt("transaction_id")));

        if (pickaddress!=null) {
            ParseUser currentuser = ParseUser.getCurrentUser();
            JSONArray addresses = currentuser.getJSONArray("address");
            try {
                JSONObject address = addresses.getJSONObject(transaction.getInt("address_index"));
                pickaddress.setText(address.getString("name") + "\n" + address.getString("house") + " , " + address.getString("locality"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (transactiondate!=null){
            transactiondate.setText(transaction.getCreatedAt().toString());

        }
//        if (agentdetails!=null){
//            ParseObject manager = transaction.getParseObject("assigned_manager");
//            if (manager!=null)
//            agentdetails.setText(manager.getString("name"));
//        }

    }

    private int getLayoutIdForStatus(int statusCode) {
        switch (statusCode) {
            case 0:
                return R.layout.item_current_order_0;
            case 1:
                return R.layout.item_current_order_1;
            case 2:
                return R.layout.item_current_order_2;
            case 3:
                return R.layout.item_current_order_3;
            case 4:
                return R.layout.item_current_order_4;
            case 5:
                return R.layout.item_current_order_5;
            default:
                return R.layout.item_current_order_0;
        }
    }

    private String getServiceForType(int type) {
        switch (type) {
            case 0:
                return "Press";
            case 1:
                return "Wash and Press";
            case 2:
                return "Dry Cleaning";
            default:
                return "";
        }
    }

    private void setStatusToHeader(String status, int imageResId) {
        ((MainActivity)getActivity()).setStatusToHeader(status, imageResId);
    }

    private void addToMap(LatLng latLng,GoogleMap mMap){

        MarkerOptions markerOptions;
        markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.addMarker(markerOptions);

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, 13.0f);
        mMap.animateCamera(cameraPosition);

    }
}
