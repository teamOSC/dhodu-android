package com.dhodu.android;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateCardFragment extends Fragment {

    public static final String MENS_WEAR = "MEN'S WEAR";
    public static final String WOMEN_WEAR = "WOMEN WEAR";
    public static final String HOUSEHOLD = "HOUSEHOLD";
    private List<ParseObject> clothesList;


    public RateCardFragment() {
        // Required empty public constructor
    }

    public static RateCardFragment newInstance(String category) {

        Bundle args = new Bundle();
        RateCardFragment fragment = new RateCardFragment();
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_rate_card, container, false);
        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        final Spinner serviceType = (Spinner) rootView.findViewById(R.id.service_type);
        final String[] serviceTypes = new String[]{"Wash & Press", "Drycleaning"};

        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayAdapter<String> servicesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Arrays.asList(serviceTypes));
        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceType.setAdapter(servicesAdapter);

        clothesList = new ArrayList<>();

        serviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                recyclerView.setAdapter(new RateCardAdapter(clothesList, i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ParseQuery<ParseObject> query = new ParseQuery<>("Clothes");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                progressBar.setVisibility(View.GONE);
                if (e == null) {
                    for (ParseObject cloth : objects) {
                        if (cloth.getString("category").equals(getArguments().getString("category")))
                            clothesList.add(cloth);
                    }
                    recyclerView.setAdapter(new RateCardAdapter(clothesList, serviceType.getSelectedItemPosition()));
                } else {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error fetching rate list", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return rootView;
    }

}
