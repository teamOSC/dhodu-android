package com.dhodu.android;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragment extends android.support.v4.app.Fragment {

    private int layoutResId;

    public IntroFragment() {
        // Required empty public constructor
    }

    public static IntroFragment newInstance(int layout) {

        Bundle args = new Bundle();

        IntroFragment fragment = new IntroFragment();
        args.putInt("layout", layout);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("layout"))
            layoutResId = getArguments().getInt("layout");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false);
    }


}
