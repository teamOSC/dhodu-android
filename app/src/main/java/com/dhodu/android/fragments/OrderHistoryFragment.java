package com.dhodu.android.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.dhodu.android.CenterAdapter;
import com.dhodu.android.R;
import com.dhodu.android.ui.SpacesItemDecoration;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by naman on 16/09/15.
 */
public class OrderHistoryFragment extends Fragment {

    RecyclerView centerRecyclerview;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        centerRecyclerview = (RecyclerView) view.findViewById(R.id.center_recyclerview);
        progressBar=(ProgressBar) view.findViewById(R.id.progressBar);

        setUpAdapter();

        return view;
    }

    public void setUpAdapter() {
        centerRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        fetchOrderHistory();
    }


    private void fetchOrderHistory() {
        ParseQuery<ParseObject> query = new ParseQuery<>("Transaction");
        query.whereEqualTo("customer", ParseUser.getCurrentUser());
        query.whereEqualTo("status", 6);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && getActivity() != null) {
                    int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.spacing_card_order_history);
                    centerRecyclerview.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
                    centerRecyclerview.setAdapter(new CenterAdapter((AppCompatActivity)getActivity(), list, getActivity()));
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }
        });
    }
}
