package com.turel.jewishday.fragments;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.turel.jewishday.R;

/**
 * Created by CHAIMT on 11/13/2014.
 * Build cards for zmanim
 */
public class ZmanimCard extends ZmanimCardLight {


    public ZmanimCard(Context context, CardType cardType) {
        super(context, cardType);
    }


    @Override
    public int getChildLayoutId() {
        return R.layout.card_zmanim;
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {
        View view = super.setupChildView(childPosition, object, convertView, parent);
        TextView description = (TextView) convertView.findViewById(R.id.zmanim_description);

        ZmanimObject zman = (ZmanimObject) object;
        description.setText(zman.getDescription());

        return view;
    }


}