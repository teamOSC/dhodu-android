package com.dhodu.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dhodu.android.ui.StepsView;
import com.dhodu.android.ui.WashingMachineView;
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
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by naman on 06/09/15.
 */
public class CurrentOrderFragment extends Fragment {

    private final String[] statuses = {"", "", "", "", "", ""};
    FrameLayout rootContainer;
    StepsView stepsView;
    View loadingView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_current_order, container, false);

        rootContainer = (FrameLayout) view.findViewById(R.id.rootContainer);
        loadingView = view.findViewById(R.id.loadingView);

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
                    setUpOrderCard(null);
                }
            }
        });
    }

    private void setUpOrderCard(ParseObject object) {
        int layoutId;
        if (object != null)
            layoutId = getLayoutIdForStatus(object.getInt("status"));
        else
            layoutId = getLayoutIdForStatus(-1);
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView = inflater.inflate(layoutId, null);
        stepsView = (StepsView) cardView.findViewById(R.id.stepsView);
        rootContainer.addView(cardView);
        loadingView.setVisibility(View.GONE);
        setCardDetails(object, cardView);
    }

    private void setCardDetails(final ParseObject transaction, View cardView) {
        int statusCode;
        try {
            statusCode = transaction.getInt("status");
        } catch (Exception e) {
            statusCode = -1;
            e.printStackTrace();
        }

        TextView transactionId = (TextView) cardView.findViewById(R.id.transaction_id);
        TextView transactiondate = (TextView) cardView.findViewById(R.id.transaction_date);
        TextView agentName = (TextView) cardView.findViewById(R.id.agentName);
        TextView agentVehicle = (TextView) cardView.findViewById(R.id.agentVehicle);
        ImageView agentPhoto = (ImageView) cardView.findViewById(R.id.profile_pic);
        TextView pickaddress = (TextView) cardView.findViewById(R.id.pick_address);
        TextView serviceType = (TextView) cardView.findViewById(R.id.service_type);
        ImageView agentCall = (ImageView) cardView.findViewById(R.id.call_agent);
        TextView pickupTime = (TextView) cardView.findViewById(R.id.eta_pick);
        TextView deliveryTime = (TextView) cardView.findViewById(R.id.delivery_time);
        TextView pickTime = (TextView) cardView.findViewById(R.id.pickup_time);
        TextView dropTime = (TextView) cardView.findViewById(R.id.eta_drop);
        WashingMachineView washingMachineView = (WashingMachineView) cardView.findViewById(R.id.wave_view);

        final TextView noOrderText = (TextView) cardView.findViewById(R.id.no_order_text);

        TextView totalItems = (TextView) cardView.findViewById(R.id.total_items);
        TextView totalAmount = (TextView) cardView.findViewById(R.id.total_amount);

        final EditText feedback = (EditText) cardView.findViewById(R.id.et_feedback);
        Button feedbackSubmit = (Button) cardView.findViewById(R.id.submit_feedback);

        final RatingBar ratingBar = (RatingBar) cardView.findViewById(R.id.rating);

        switch (statusCode) {
            case 0:
                setStatusToHeader("Order placed. Confirmation awaited.", R.drawable.ic_cancel_white_48dp);
                GoogleMapOptions options = new GoogleMapOptions();

                SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);

                FragmentManager fragmentManager = getChildFragmentManager();
                fragmentManager.beginTransaction().add(R.id.map, mapFragment).commitAllowingStateLoss();

                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        addToMap(new LatLng(28.542, 77.259), googleMap);
                    }
                });

                break;
            case 1:
                setStatusToHeader("Booking confirmed.", R.drawable.ic_cancel_white_48dp);
                break;
            case 2:
                setStatusToHeader("Laundry picked. Being washed", R.drawable.ic_cancel_white_48dp);
                break;
            case 3:
                setStatusToHeader("Laundry ready. Ready to be delivered", 0);
                break;
            case 4:
                setStatusToHeader("Laundry out for delivery.", 0);
                break;
            case 5:
                setStatusToHeader("Laundry delivered", 0);
                break;
            default:
                break;
        }

        if (stepsView != null) {
            stepsView.setLabels(statuses)
                    .setBarColorIndicator(getContext().getResources().getColor(R.color.material_blue_grey_800))
                    .setProgressColorIndicator(Color.parseColor("#FF9800"))
                    .setLabelColorIndicator(getContext().getResources().getColor(R.color.dhodu_primary_dark))
                    .setCompletedPosition(statusCode)
                    .drawView();
        }

        if (ratingBar != null) {
            ratingBar.setRating(4);
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.dhodu_primary_dark), PorterDuff.Mode.SRC_ATOP);
        }

        if (serviceType != null) {
            String transaction_type = transaction.getString("service_type");
            String serviceString = null;
            if (transaction_type.contains("0")) {
                serviceString = getServiceForType(0);
            }
            if (transaction_type.contains("1")) {
                if (serviceString != null)
                    serviceString = serviceString + ", " + getServiceForType(1);
                else
                    serviceString = getServiceForType(1);
            }
            if (transaction_type.contains("2")) {
                if (serviceString != null)
                    serviceString = serviceString + ", " + getServiceForType(2);
                else
                    serviceString = getServiceForType(2);
            }
            serviceType.setText(serviceString);
        }

        if(noOrderText != null){
            ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
            query.whereEqualTo("customer", ParseUser.getCurrentUser());
            query.orderByDescending("updatedAt");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        Date today = new Date();
                        Date orderPlaced = object.getCreatedAt();
                        long diffInDays = (today.getTime() - orderPlaced.getTime())/(24 * 60 * 60 * 1000);  //FIXME
                        noOrderText.setText("Hey there! Looks like you haven't done your laundry in " + diffInDays + " days.");
                    } else if(e.getCode() == ParseException.OBJECT_NOT_FOUND){
                        noOrderText.setText("Congratulations on saying goodbye to your laundry hassles. Welcome to Dhodu!");
                    }
                    else {
                        e.printStackTrace();
                    }
                }
            });
        }

        if (agentName != null) {
            final ParseObject object = transaction.getParseObject("agent_pick");
            if (object != null) {
                try {
                    final String agentPhone = object.fetchIfNeeded().getString("contact");
                    agentName.setText(object.fetchIfNeeded().getString("name"));
                    agentVehicle.setText(object.fetchIfNeeded().getString("vehicle"));
                    if (agentCall != null) {
                        agentCall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + agentPhone));
                                startActivity(intent);
                            }
                        });
                    }
                    Picasso.with(getActivity()).load(object.fetchIfNeeded().getString("agent_photo")).into(agentPhoto);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        if (transactionId != null)
            transactionId.setText(String.valueOf(transaction.getInt("transaction_id")));

        if (totalAmount != null && totalItems != null) {
            totalAmount.setText("₹ " + transaction.getNumber("amount").toString());
            totalItems.setText(transaction.getJSONArray("clothes_data").length() + "");
        }

        if (pickaddress != null) {
            ParseUser currentuser = ParseUser.getCurrentUser();
            JSONArray addresses = currentuser.getJSONArray("address");
            try {
                JSONObject address = addresses.getJSONObject(transaction.getInt("address_index"));
                pickaddress.setText(address.getString("house") + ", " + address.getString("street"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (pickupTime != null) {
            pickupTime.setText(transaction.getString("time_pick") + ", " + transaction.getString("pick_date"));
        }

        if (pickTime != null) {
            pickTime.setText(transaction.getString("time_pick") + ", " + transaction.getString("pick_date"));
        }

        if (deliveryTime != null) {
            deliveryTime.setText(transaction.getString("drop_date"));
        }

        if (dropTime != null) {
            dropTime.setText(transaction.getString("drop_date"));
        }

        if (transactiondate != null) {
            transactiondate.setText(transaction.getCreatedAt().toString());

        }

        if (feedback != null) {
            feedbackSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    transaction.put("rating", ratingBar.getNumStars());
                    transaction.put("feedback", feedback.getText().toString());
                    transaction.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getActivity(), "Feedback submitted!", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
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
                return R.layout.item_no_order;
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
        ((MainActivity) getActivity()).setStatusToHeader(status, imageResId);
    }

    private void addToMap(LatLng latLng, GoogleMap mMap) {

        MarkerOptions markerOptions;
        markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.addMarker(markerOptions);

        CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(latLng, 13.0f);
        mMap.animateCamera(cameraPosition);

    }
}
