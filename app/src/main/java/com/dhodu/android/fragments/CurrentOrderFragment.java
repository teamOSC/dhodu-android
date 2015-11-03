package com.dhodu.android.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dhodu.android.BillSummaryActivity;
import com.dhodu.android.CreateOrderActivity;
import com.dhodu.android.CreateOrderDialog;
import com.dhodu.android.R;
import com.dhodu.android.RateCardActivity;
import com.dhodu.android.ui.StepsView;
import com.dhodu.android.ui.WashingMachineView;
import com.dhodu.android.utils.OrderSpinnerAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by naman on 06/09/15.
 */
public class CurrentOrderFragment extends Fragment {

    private final String[] statuses = {"", "", "", "", ""};
    FrameLayout rootContainer;
    StepsView stepsView;
    View loadingView;
    ParsePushBroadcastReceiver pushReceiver = null;
    IntentFilter intentFilter;
    FloatingActionButton createFab;
    Spinner orderSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_current_order, container, false);

        rootContainer = (FrameLayout) view.findViewById(R.id.rootContainer);
        loadingView = view.findViewById(R.id.loadingView);
        createFab = (FloatingActionButton) view.findViewById(R.id.neworder_fab);
        createFab.setVisibility(View.GONE);

        orderSpinner = (Spinner) view.findViewById(R.id.orders_spinner);

        fetchCurrentOrders();

        pushReceiver = new ParsePushBroadcastReceiver() {

            @Override
            protected void onPushReceive(Context context, Intent intent) {
                updateStatusCard();
            }
        };

        intentFilter = new IntentFilter("com.parse.push.intent.RECEIVE");
        getActivity().registerReceiver(pushReceiver, intentFilter);

        createFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateOrderDialog createOrderDialog = new CreateOrderDialog();
                createOrderDialog.show(getChildFragmentManager(), "Dialog fragment");
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (pushReceiver != null) {
            try {
                getActivity().unregisterReceiver(pushReceiver);
            } catch (Exception e) {
                Log.e("Current Order", "onPause ", e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pushReceiver != null)
            getActivity().registerReceiver(pushReceiver, intentFilter);
    }

    private void fetchCurrentOrders() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
        query.whereEqualTo("customer", ParseUser.getCurrentUser());
        query.include("Agent");
        query.whereLessThanOrEqualTo("status", 5);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() != 0) {
                    if (objects.size()==1) {
                        orderSpinner.setVisibility(View.GONE);
                        setUpOrderCard(objects.get(0));
                    } else {
                        orderSpinner.setVisibility(View.VISIBLE);
                        setupSpinner(objects);
                    }
                    createFab.setVisibility(View.VISIBLE);
                    loadingView.setVisibility(View.GONE);
                } else {
                    createFab.setVisibility(View.GONE);
                    setUpOrderCard(null);
                    loadingView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupSpinner(final List<ParseObject> objects) {
        List<String> orderIds = new ArrayList<>();
        for (ParseObject object: objects) {
            orderIds.add(object.getObjectId());
        }
        String[] ids = new String[orderIds.size()];
        ids = orderIds.toArray(ids);
        OrderSpinnerAdapter adapter = new OrderSpinnerAdapter(getActivity(),R.layout.item_order_spinner,ids,objects);
        orderSpinner.setAdapter(adapter);

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setUpOrderCard(objects.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpOrderCard(ParseObject object) {
        rootContainer.removeAllViews();
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
        TextView serviceType = (TextView) cardView.findViewById(R.id.service_type);
        ImageView agentCall = (ImageView) cardView.findViewById(R.id.call_agent);
        TextView pickupTime = (TextView) cardView.findViewById(R.id.eta_pick);
        TextView deliveryTime = (TextView) cardView.findViewById(R.id.delivery_time);
        TextView pickTime = (TextView) cardView.findViewById(R.id.pickup_time);
        TextView dropTime = (TextView) cardView.findViewById(R.id.eta_drop);
        WashingMachineView washingMachineView = (WashingMachineView) cardView.findViewById(R.id.wave_view);
        Button scheduleBooking = (Button) cardView.findViewById(R.id.schedule_booking);
        Button rateCardButton = (Button) cardView.findViewById(R.id.rate_card);
        Button viewBill = (Button) cardView.findViewById(R.id.view_bill);
        Button payNow = (Button) cardView.findViewById(R.id.pay_now);
        TextView totalAmount5 = (TextView) cardView.findViewById(R.id.total_amount_5);
        final LinearLayout slotWashPress = (LinearLayout) cardView.findViewById(R.id.slot1);
        final LinearLayout slotDryClean = (LinearLayout) cardView.findViewById(R.id.slot2);
        final ImageView noOrderImage = (ImageView) cardView.findViewById(R.id.imageView);

        final TextView noOrderText = (TextView) cardView.findViewById(R.id.no_order_text);

        TextView totalItems = (TextView) cardView.findViewById(R.id.total_items);
        TextView totalAmount = (TextView) cardView.findViewById(R.id.total_amount);

        final EditText feedback = (EditText) cardView.findViewById(R.id.et_feedback);
        Button feedbackSubmit = (Button) cardView.findViewById(R.id.submit_feedback);

        final RatingBar ratingBar = (RatingBar) cardView.findViewById(R.id.rating);

        switch (statusCode) {
            case 1:
                ImageView cancelOrder = (ImageView) cardView.findViewById(R.id.cancel_order);
                cancelOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder cancelDialog = new AlertDialog.Builder(getActivity());
                        cancelDialog.setTitle("Cancel Booking");
                        cancelDialog.setCancelable(true);
                        cancelDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog pDialog = new ProgressDialog(getActivity());
                                pDialog.setMessage("Canceling order...");
                                pDialog.setCancelable(false);
                                pDialog.show();
                                transaction.put("status", 13);
                                transaction.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(getActivity(), "Order canceled", Toast.LENGTH_SHORT).show();
                                            pDialog.dismiss();
                                            updateStatusCard();
                                        } else {
                                            pDialog.dismiss();
                                            Toast.makeText(getActivity(), "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                        cancelDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        cancelDialog.setMessage("Are you sure you want to cancel the booking?");
                        cancelDialog.show();
                    }
                });
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            default:
                slotWashPress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (slotWashPress.isSelected())
                            slotWashPress.setSelected(false);
                        else slotWashPress.setSelected(true);
                    }
                });
                slotDryClean.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (slotDryClean.isSelected())
                            slotDryClean.setSelected(false);
                        else slotDryClean.setSelected(true);
                    }
                });
                scheduleBooking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String serviceType = "";
                        if (slotDryClean.isSelected()) serviceType += 2;
                        if (slotWashPress.isSelected()) serviceType += 1;
                        if (serviceType.length() < 1) {
                            Toast.makeText(getActivity(), "Please select the type of service", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent i = new Intent(getActivity(), CreateOrderActivity.class);
                            i.putExtra("servicetype", serviceType);
                            startActivity(i);
                        }
                    }
                });
                rateCardButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(), RateCardActivity.class));
                    }
                });
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("App");
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null && getActivity() != null && noOrderImage != null) {
                            String url = object.getString("image_url");
                            if (!url.equals(""))
                                Picasso.with(getActivity()).load(url).into(noOrderImage);
                        }
                    }
                });
                break;
        }

        if (stepsView != null) {
            stepsView.setLabels(statuses)
                    .setBarColorIndicator(getContext().getResources().getColor(R.color.material_blue_grey_800))
                    .setProgressColorIndicator(Color.parseColor("#FF9800"))
                    .setLabelColorIndicator(getContext().getResources().getColor(R.color.dhodu_primary_dark))
                    .setCompletedPosition(statusCode - 1)
                    .drawView();
        }

        if (ratingBar != null && Build.VERSION.SDK_INT >= 21) {
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

        if (noOrderText != null) {
            ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
            query.whereEqualTo("customer", ParseUser.getCurrentUser());
            query.orderByDescending("updatedAt");
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        Date today = new Date();
                        Date orderPlaced = object.getCreatedAt();
                        long diffInDays = (today.getTime() - orderPlaced.getTime()) / (24 * 60 * 60 * 1000);  //FIXME
                        noOrderText.setText("Hey there! Looks like you haven't done your laundry in " + diffInDays + " days.");
                    } else if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        noOrderText.setText("Congratulations on saying goodbye to your laundry hassles. Welcome to Dhodu!");
                    } else {
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
            transactionId.setText(transaction.getObjectId().toUpperCase());

        if (totalAmount != null && totalItems != null) {
            totalAmount.setText("₹ " + transaction.getNumber("amount").toString());
            totalItems.setText(transaction.getJSONArray("clothes_data").length() + "");
        }

//        if (pickaddress != null) {
//            ParseUser currentuser = ParseUser.getCurrentUser();
//            JSONArray addresses = currentuser.getJSONArray("address");
//            try {
//                JSONObject address = addresses.getJSONObject(transaction.getInt("address_index"));
//                pickaddress.setText(address.getString("house") + ", " + address.getString("street"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        if (pickupTime != null) {
            pickupTime.setText(transaction.getString("time_pick") + ", " + transaction.getString("pick_date"));
        }

        if (deliveryTime != null) {
//            deliveryTime.setText(transaction.getString("drop_date"));
//            set pickup time + 48 hours for now
            String date = transaction.getString("pick_date");
            String increaseddate = String.valueOf(Integer.parseInt(date.substring(0, 2)) + 2);
            deliveryTime.setText(transaction.getString("time_pick") + ", " +
                    increaseddate + date.substring(2, date.length()));
        }

        if (dropTime != null) {
            dropTime.setText(transaction.getString("drop_date"));
        }

        if (transactiondate != null) {
            transactiondate.setText(transaction.getCreatedAt().toString());

        }

        if (viewBill != null) {
            viewBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BillSummaryActivity.class);
                    intent.putExtra("transaction_id", transaction.getObjectId());
                    startActivity(intent);
                }
            });
        }

        if (payNow != null) {
            payNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Online payment coming soon", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (totalAmount5 != null) {
            totalAmount5.setText("Total = ₹ " + transaction.getNumber("amount").toString());
        }

        if (feedback != null && ratingBar != null) {
            feedbackSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ratingBar.getRating() < 1.0) {
                        Toast.makeText(getActivity(), "Please rate your transaction", Toast.LENGTH_SHORT).show();
                    } else {
                        transaction.put("rating", ratingBar.getRating());
                        final ProgressDialog pDialog = new ProgressDialog(getActivity());
                        pDialog.setMessage("Submitting Feedback...");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        transaction.put("rating", ratingBar.getNumStars());
                        transaction.put("feedback", feedback.getText().toString());
                        transaction.put("status", 6);
                        transaction.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getActivity(), "Feedback submitted!", Toast.LENGTH_SHORT).show();
                                    updateStatusCard();
                                    pDialog.dismiss();
                                } else {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                                    pDialog.dismiss();
                                }
                            }
                        });
                    }

                }
            });
        }
    }


//        if (agentdetails!=null){
//            ParseObject manager = transaction.getParseObject("assigned_manager");
//            if (manager!=null)
//            agentdetails.setText(manager.getString("name"));
//        }


    private int getLayoutIdForStatus(int statusCode) {
        switch (statusCode) {
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


    public void updateStatusCard() {
        Animation slidedown = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_down);
        final View view = rootContainer.getChildAt(1);
        slidedown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rootContainer.removeView(view);
                loadingView.setVisibility(View.VISIBLE);
                fetchCurrentOrders();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (view != null)
            view.startAnimation(slidedown);
    }

}
