package com.dhodu.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RateCardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ParseObject> clothesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_card);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final Spinner serviceType = (Spinner) findViewById(R.id.service_type);
        final Spinner category = (Spinner) findViewById(R.id.category);

        final String[] categories = new String[]{"MEN'S WEAR", "WOMEN WEAR", "KIDS WEAR", "HOUSEHOLD", "OTHERS"};
        final String[] serviceTypes = new String[]{"Press", "Wash & Press", "Drycleaning"};

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(categories));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        ArrayAdapter<String> servicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(serviceTypes));
        servicesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceType.setAdapter(servicesAdapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<ParseObject> list = new ArrayList<>();
                if (clothesList != null) {
                    for (ParseObject cloth : clothesList) {
                        if (cloth.getString("category").equals(categories[i]))
                            list.add(cloth);
                    }
                    recyclerView.setAdapter(new RateCardAdapter(list, serviceType.getSelectedItemPosition()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        serviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<ParseObject> list = new ArrayList<>();
                if (clothesList != null) {
                    for (ParseObject cloth : clothesList) {
                        if (cloth.getString("category").equals(categories[category.getSelectedItemPosition()]))
                            list.add(cloth);
                    }
                }
                recyclerView.setAdapter(new RateCardAdapter(list, i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ParseQuery<ParseObject> query = new ParseQuery<>("Clothes");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    clothesList = objects;
                    recyclerView.setAdapter(new RateCardAdapter(clothesList, serviceType.getSelectedItemPosition()));
                } else
                    e.printStackTrace();
            }
        });

    }
}
