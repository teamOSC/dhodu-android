package com.dhodu.android.payments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhodu.android.R;
import com.dhodu.android.ui.MultiViewPager;

/**
 * Created by naman on 05/11/15.
 */
public class CardsFragment extends Fragment {

    MultiViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_payment_cards, container, false);

        final MultiViewPager pager = (MultiViewPager) rootView.findViewById(R.id.cardspager);

        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return 5;
            }

            @Override
            public Fragment getItem(int position) {
                return CardPagerFragment.newInstance(position);
            }

        };
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);


        return rootView;
    }

}
