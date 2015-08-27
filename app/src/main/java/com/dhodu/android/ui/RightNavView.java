package com.dhodu.android.ui;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.dhodu.android.R;

/**
 * Created by championswimmer on 27/8/15.
 */
public class RightNavView extends NavigationView{
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

    public void setUpOrderList () {
        ordersView = (RecyclerView) getRootView().findViewById(R.id.list_order_history);
    }
}
