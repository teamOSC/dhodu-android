package com.dhodu.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by naman on 06/09/15.
 */
public class CurrentOrderFragment extends Fragment {

    FrameLayout rootContainer;

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

        rootContainer.addView(cardView);;
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
