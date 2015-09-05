package com.dhodu.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by naman on 06/09/15.
 */
public class CurrentOrderFragment extends Fragment {

    RecyclerView recyclerView;
    CurrentOrderAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view= inflater.inflate(R.layout.fragment_current_order, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list_current_order);

        return view;
    }

}
