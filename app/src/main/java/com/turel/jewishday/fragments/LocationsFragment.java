package com.turel.jewishday.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.turel.jewishday.R;
import com.turel.jewishday.data.AddressInfo;
import com.turel.jewishday.utils.AppSettings;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Created by Haim.Turkel on 9/6/2015.
 */
public class LocationsFragment extends Fragment implements CardUpdate {

    private List<AddressInfo> list = new ArrayList<>();
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_locations, container,
                false);
        AppSettings.getInstance().updateLocalLanguage(getActivity());

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setTitle(R.string.menu_locations);

        initCards(rootView);
        return rootView;
    }


    public static Fragment newInstance() {
        return new LocationsFragment();
    }


    protected void initCards(final View rootView) {

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(AddLocationFragment.FragTag).replace(R.id.container,
                        AddLocationFragment.newInstance(), AddLocationFragment.FragTag)
                        .commit();
            }
        });

        refreshList(rootView);
    }

    public void refreshList(final View rootView) {
        ArrayList<Card> cards = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locations = preferences.getString(AddressInfo.LOCATIONS_KEY, "");
        try {
            list = AddressInfo.fromJson(locations);
            for (AddressInfo add : list) {
                Card card = new LocationCard(this, getContext(), add);
                cards.add(card);
            }

            CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
            CardListView listView = (CardListView) rootView.findViewById(R.id.myList);
            if (listView != null) {
                listView.setAdapter(mCardArrayAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleted(LocationCard card) {
        for (AddressInfo add : list) {
            if (add.getTitle().equals(card.getTitle())) {
                list.remove(add);
                break;
            }
        }
        saveList();
        refreshList(rootView);
    }

    private void saveList() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        try {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(AddressInfo.LOCATIONS_KEY, AddressInfo.toJson(list));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
