package com.dhodu.android.ui;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.dhodu.android.OrderListAdapter;
import com.dhodu.android.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by championswimmer on 27/8/15.
 */
public class RightNavView extends NavigationView {
    public static final String TAG = "RightNavView";
    RecyclerView ordersView;

    public RightNavView(Context context) {
        super(context);
    }

    public RightNavView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RightNavView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setUpOrderList() {
        ordersView = (RecyclerView) getRootView().findViewById(R.id.list_order_history);
        ordersView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchOrderHistory();
    }

    private void fetchOrderHistory() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
        query.whereEqualTo("customer", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ordersView.setAdapter(new OrderListAdapter(list));
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
