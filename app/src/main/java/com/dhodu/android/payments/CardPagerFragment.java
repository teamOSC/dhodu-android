package com.dhodu.android.payments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dhodu.android.R;
import com.vinaygaba.creditcardview.CreditCardView;

/**
 * Created by naman on 05/11/15.
 */
public class CardPagerFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "pageNumber";

    int numOfCards;
    int position;

    CreditCardView cardView;
    FrameLayout addCardView;

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

        numOfCards = ((CardsFragment) getParentFragment()).cards.length();
        position = getArguments().getInt(ARG_PAGE_NUMBER);

        cardView = (CreditCardView) rootView.findViewById(R.id.credit_card);
        addCardView = (FrameLayout) rootView.findViewById(R.id.add_card);

        if (position <= numOfCards) {
            cardView.setVisibility(View.VISIBLE);
            addCardView.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.GONE);
            addCardView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }


}
