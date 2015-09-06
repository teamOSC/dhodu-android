package com.dhodu.android;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dhodu.android.ui.StepsView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
        query.whereLessThanOrEqualTo("status", 4);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size()>0)
                    setUpOrderCard(list);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUpOrderCard(List<ParseObject> list) {
        int layoutId =getLayoutIdForStatus(list.get(0).getInt("status"));

        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView =  inflater.inflate(layoutId, null);
        stepsView = (StepsView) cardView.findViewById(R.id.stepsView);
        rootContainer.addView(cardView);
        setCardDetails(list.get(0),cardView);
    }

    private void setCardDetails(ParseObject transaction, View cardView) {

        int statusCode = transaction.getInt("status");

        stepsView.setLabels(statuses)
                .setBarColorIndicator(getContext().getResources().getColor(R.color.material_blue_grey_800))
                .setProgressColorIndicator(Color.parseColor("#FF9800"))
                .setLabelColorIndicator(getContext().getResources().getColor(R.color.dhodu_primary_dark))
                .setCompletedPosition(statusCode)
                .drawView();

        TextView transactionId =(TextView) cardView.findViewById(R.id.transaction_id);
        TextView transactiondate =(TextView) cardView.findViewById(R.id.transaction_date);
        TextView pickaddress =(TextView) cardView.findViewById(R.id.pick_address);
        TextView agentdetails =(TextView) cardView.findViewById(R.id.agent_detail);

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
}
