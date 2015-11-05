package com.dhodu.android.payments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhodu.android.R;

/**
 * Created by naman on 05/11/15.
 */
public class CardPagerFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "pageNumber";

    public static CardPagerFragment newInstance(int pageNumber) {
        CardPagerFragment fragment = new CardPagerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_pager, container, false);

        return rootView;
    }


}
