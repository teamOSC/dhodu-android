package com.dhodu.android.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dhodu.android.R;
import com.dhodu.android.ui.card.CreditCardView;

import org.json.JSONException;
import org.json.JSONObject;

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

        if (position < numOfCards) {
            cardView.setVisibility(View.VISIBLE);
            addCardView.setVisibility(View.GONE);
            setCardDetails();
        } else {
            cardView.setVisibility(View.GONE);
            addCardView.setVisibility(View.VISIBLE);
        }

        addCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCardActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void setCardDetails() {
        try {
            cardView.setIsEditable(true);
            JSONObject card = ((CardsFragment) getParentFragment()).cards.getJSONObject(position);
            Log.d("lol",card.getString("card_name"));
            cardView.setCardName(card.getString("card_name"));
            cardView.setCardNumber(card.getString("card_number"));
            cardView.setExpiryDate(card.getString("card_expiry"));
            cardView.setIsEditable(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
