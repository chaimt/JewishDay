package com.turel.jewishday.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.turel.jewishday.MainActivity;
import com.turel.jewishday.R;
import com.turel.jewishday.utils.AppSettings;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by CHAIMT on 10/30/2014.
 */
public class ZmanimFragment extends Fragment {

    PullToRefreshView mPullToRefreshView;
    static int REFRESH_DELAY = 700;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_zmanim, container,
                false);
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setTitle(R.string.menu_zmanim);

        AppSettings.getInstance().updateLocalLanguage(getActivity());
        mPullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppSettings.getInstance().removeLocationData();
                        ((MainActivity)getActivity()).startLocationFind();
//                        AppSettings.getInstance().startLocationFind();
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, REFRESH_DELAY);
            }
        });
        initCards(rootView);
        return rootView;
    }


    public static Fragment newInstance() {
        return new ZmanimFragment();
    }


    protected void initCards(View rootView) {
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new ZmanimCardLight(getActivity(), ZmanimCard.CardType.Global));
        cards.add(new ZmanimCard(getActivity(), ZmanimCard.CardType.Morning));
        cards.add(new ZmanimCard(getActivity(), ZmanimCard.CardType.Afternoon));
        cards.add(new ZmanimCard(getActivity(), ZmanimCard.CardType.Shabbat));
        cards.add(new ZmanimCardLight(getActivity(), ZmanimCard.CardType.Special));

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
        CardListView listView = (CardListView) rootView.findViewById(R.id.myList);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }
    }


}
