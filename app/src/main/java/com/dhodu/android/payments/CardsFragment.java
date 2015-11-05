package com.dhodu.android.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dhodu.android.R;
import com.dhodu.android.ui.MultiViewPager;
import com.parse.ParseUser;

import org.json.JSONArray;

/**
 * Created by naman on 05/11/15.
 */
public class CardsFragment extends Fragment {

    public JSONArray cards;
    FrameLayout addcard;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_payment_cards, container, false);

        final MultiViewPager pager = (MultiViewPager) rootView.findViewById(R.id.cardspager);
        addcard = (FrameLayout) rootView.findViewById(R.id.add_card);

        ParseUser currentuser = ParseUser.getCurrentUser();
        cards = currentuser.getJSONArray("cards");

        if (cards != null && cards.length() != 0) {
            final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
                @Override
                public int getCount() {
                    return cards.length() + 1;
                }

                @Override
                public Fragment getItem(int position) {
                    return CardPagerFragment.newInstance(position);
                }

            };
            pager.setAdapter(adapter);
            pager.setOffscreenPageLimit(3);

        } else {
            addcard.setVisibility(View.VISIBLE);
            addcard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),AddCardActivity.class);

                }
            });
        }


        return rootView;
    }

}
