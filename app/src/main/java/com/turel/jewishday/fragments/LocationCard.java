package com.turel.jewishday.fragments;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.turel.jewishday.R;
import com.turel.jewishday.data.AddressInfo;

import java.util.TimeZone;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;

/**
 * Created by Haim.Turkel on 9/6/2015.
 */
public class LocationCard extends Card {
    protected TextView titleTextView;
    protected TextView addressTextView;
    protected TextView lontitudeTextView;
    protected TextView latitudeTextView;
    protected TextView timezoneTextView;
    protected ImageView imageView;

    private String address;
    private double longitude;
    private double latitude;
    private TimeZone zone;

    public LocationCard(final CardUpdate cardUpdate, Context context, String title, String address, double longitude, double latitude, TimeZone zone) {
        super(context, R.layout.card_location);

        final LocationCard holder = this;
        CardHeader header = new CardHeader(context);
        header.setButtonExpandVisible(false);
        header.setOtherButtonVisible(false);
        header.setButtonOverflowVisible(false);
        header.setOverflowSelected(false);
        header.setTitle(title);
        header.setPopupMenu(R.menu.main, new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                if (cardUpdate != null) {
                    cardUpdate.deleted(holder);
                }
            }
        });
        header.setPopupMenuPrepareListener(new CardHeader.OnPrepareCardHeaderPopupMenuListener() {
            @Override
            public boolean onPreparePopupMenu(BaseCard card, PopupMenu popupMenu) {
                popupMenu.getMenu().add(R.string.delete_location);
                return true;
            }
        });

        addCardHeader(header);
        setTitle(title);
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.zone = zone;
    }

    public LocationCard(CardUpdate cardUpdate, Context context, AddressInfo add) {
        this(cardUpdate, context, add.getTitle(), add.getAddress(), add.getLongitude(), add.getLatitude(),add.getZone());
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        addressTextView = (TextView) parent.findViewById(R.id.card_address);
        if (addressTextView != null) {
            addressTextView.setText(address);
        }

        lontitudeTextView = (TextView) parent.findViewById(R.id.card_longitude);
        if (lontitudeTextView != null) {
            lontitudeTextView.setText(longitude + "  " + getContext().getString(R.string.preferences_longitude));
        }

        latitudeTextView = (TextView) parent.findViewById(R.id.card_latitude);
        if (latitudeTextView != null) {
            latitudeTextView.setText(latitude + "  " + getContext().getString(R.string.preferences_latitude));
        }
        timezoneTextView = (TextView) parent.findViewById(R.id.card_timezone);
        if (timezoneTextView != null) {
            timezoneTextView.setText(zone.getID() + "  " + getContext().getString(R.string.prefs_timezone_title));
        }

        imageView = (ImageView) parent.findViewById(R.id.card_image);
        if (imageView != null) {
            //imageView.setImageResource(R.drawable.app_phone_icon);
            imageView.setTag(this);
        }
    }
}
