package com.turel.jewishday.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.turel.jewishday.MainActivity;
import com.turel.jewishday.R;
import com.turel.jewishday.data.AddressInfo;
import com.turel.jewishday.utils.AppSettings;

import net.sourceforge.zmanim.ComplexZmanimCalendar;
import net.sourceforge.zmanim.hebrewcalendar.Daf;
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;
import net.sourceforge.zmanim.hebrewcalendar.JewishDate;
import net.sourceforge.zmanim.hebrewcalendar.YomiCalculator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.prototypes.CardWithList;

/**
 * Created by Haim.Turkel on 7/28/2015.
 */
public class ZmanimCardLight extends CardWithList {

    public static final String OPTIONS_SUNSET = "options.sunset";
    public static final String OPTIONS_PLAG_MINCHA = "options.plag.mincha";
    public static final String OPTIONS_ZMAIN_TEPHILA = "options.zmain.tephila";
    public static final String OPTIONS_SUNRISE = "options.sunrise";
    public static final String OPTIONS_ALOTHASHACR = "options.alothashacr";

    public enum CardType {
        Global, Morning, Afternoon, Shabbat, Special
    }

    private java.text.DateFormat userTimeFormat = new SimpleDateFormat("HH:mm");
    private CardType cardType;
    private AddressInfo currentAddress = null;

    public ZmanimCardLight(Context context, CardType cardType) {
        super(context);
        this.cardType = cardType;
        init();
    }

    @Override
    public int getChildLayoutId() {
        return R.layout.card_zmanim_light;
    }

    @Override
    protected CardHeader initCardHeader() {
        //Add Header
        final List<AddressInfo> addressList = AppSettings.getInstance().getAddressInfoList();
        CardHeader header = new CardHeader(getContext());
        switch (cardType) {
            case Global:
                //Add a popup menu. This method sets OverFlow button to visibile
                header.setPopupMenu(R.menu.main, new CardHeader.OnClickCardHeaderPopupMenuListener() {
                    @Override
                    public void onMenuItemClick(BaseCard card, MenuItem item) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(AddressInfo.LOCATIONS_INDEX_KEY, item.getItemId());
                        editor.commit();
                        if (item.getItemId() == AppSettings.CURRENT_LOCATION_INDEX) {
                            //current location
                            currentAddress = new AddressInfo(getContext().getString(R.string.current_location), AppSettings.getInstance().getAddress(), AppSettings.getInstance().getLatitude(), AppSettings.getInstance().getLongitude(), AppSettings.getInstance().getAltitude(),AppSettings.getInstance().getTimeZone());

                        } else if (item.getItemId() >= 0) {
                            //use location
                            currentAddress = addressList.get(item.getItemId());
                        }
                        Toast.makeText(getContext(), getContext().getString(R.string.using_location) + " " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        AppSettings.getInstance().refreshDisplayDateNotification();
                        ((MainActivity) getContext()).
                                getSupportFragmentManager()
                                .beginTransaction().replace(R.id.container,
                                new ZmanimFragment())
                                .commit();


                    }
                });
                header.setPopupMenuPrepareListener(new CardHeader.OnPrepareCardHeaderPopupMenuListener() {
                    @Override
                    public boolean onPreparePopupMenu(BaseCard card, PopupMenu popupMenu) {
                        popupMenu.getMenu().add(0, AppSettings.CURRENT_LOCATION_INDEX, 0, R.string.current_location);
                        int pos = 0;
                        for (AddressInfo info : addressList) {
                            popupMenu.getMenu().add(0, pos, pos, info.getTitle());
                            pos++;
                        }
                        return true;
                    }
                });
        }
        return header;
    }

    @Override
    public CardHeader getCardHeader() {
        return super.getCardHeader();
    }

    @Override
    protected void initCard() {

        setSwipeable(true);
    }

    private String displayDate(Date date){
        return date==null ? "NaN" : userTimeFormat.format(date);
    }

    @Override
    protected List<ListObject> initChildren() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        currentAddress = AppSettings.getInstance().getAddressInfo();
        List<ListObject> mObjects = new ArrayList<>();

        ComplexZmanimCalendar czc = AppSettings.getInstance().getZmanimData(currentAddress.getTitle(),currentAddress.getLatitude(),currentAddress.getLongitude(),currentAddress.getAltitude(),currentAddress.getZone());
        JewishCalendar jewishCalendar = AppSettings.getInstance().getJewishCalendarByTime();
        HebrewDateFormatter formatter = AppSettings.getInstance().getHebrewDateFormatter();
        Resources res = getContext().getResources();

        switch (cardType) {
            case Global:
                getCardHeader().setTitle(res.getString(R.string.zmanim_global));

                mObjects.add(new ZmanimObject(this, res.getString(R.string.preferences_jewish_date), "", formatter.format(jewishCalendar)));
                Calendar friday = Calendar.getInstance();
                friday.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                JewishCalendar fridayJewish = new JewishCalendar(friday);
                fridayJewish.setInIsrael(AppSettings.getInstance().isHebrewFormat());
                mObjects.add(new ZmanimObject(this, res.getString(R.string.parsha_day), res.getString(R.string.parsha_day_description), formatter.formatParsha(fridayJewish)));
                if (jewishCalendar.isChanukah()) {
                    mObjects.add(new ZmanimObject(this, res.getString(R.string.chanuka_day), res.getString(R.string.chanuka_day_description), String.valueOf(jewishCalendar.getDayOfChanukah())));
                }
                if (jewishCalendar.getDayOfOmer() != -1) {
                    mObjects.add(new ZmanimObject(this, res.getString(R.string.omer_day), res.getString(R.string.omer_day_description), formatter.formatOmer(jewishCalendar)));
                }
                if (jewishCalendar.isYomTov()) {
                    mObjects.add(new ZmanimObject(this, res.getString(R.string.yomtov_day), res.getString(R.string.yomtov_day_description), formatter.formatYomTov(jewishCalendar)));
                }
                if (jewishCalendar.isRoshChodesh()) {
                    mObjects.add(new ZmanimObject(this, res.getString(R.string.rosh_hodesh_day), res.getString(R.string.rosh_hodesh_day_description), formatter.formatRoshChodesh(jewishCalendar)));
                }
                if (jewishCalendar.isTaanis()) {
                    if (jewishCalendar.getYomTovIndex() == JewishCalendar.TISHA_BEAV || jewishCalendar.getYomTovIndex() == JewishCalendar.YOM_KIPPUR) {
                        mObjects.add(new ZmanimObject(this, res.getString(R.string.tanis_start), res.getString(R.string.tanis_start_description), displayDate(czc.getSunset())));
                    } else {
                        mObjects.add(new ZmanimObject(this, res.getString(R.string.tanis_start), res.getString(R.string.tanis_start_description), displayDate(czc.getAlosHashachar())));
                    }
                    mObjects.add(new ZmanimObject(this, res.getString(R.string.tanis_end), res.getString(R.string.tanis_end_description), displayDate(czc.getTzaisGeonim5Point88Degrees())));
                }

                break;
            case Morning:
                getCardHeader().setTitle(res.getString(R.string.zmanim_morning));

                addAlotHashacr(preferences, mObjects, czc, res);
                mObjects.add(new ZmanimObject(this, res.getString(R.string.tephilin_text), res.getString(R.string.tephilin_description), displayDate(czc.getMisheyakir11Point5Degrees())));
                addSunrise(preferences, mObjects, czc, res);

                int zmainTephila = Integer.valueOf(preferences.getString(OPTIONS_ZMAIN_TEPHILA, "1"));
                if (zmainTephila==1){
                    mObjects.add(new ZmanimObject(this, res.getString(R.string.kiriyat_shema_text), res.getString(R.string.kiriyat_shema_description), displayDate(czc.getSofZmanShmaMGA())));
                    mObjects.add(new ZmanimObject(this, res.getString(R.string.shacarit_text), res.getString(R.string.shacarit_description), displayDate(czc.getSofZmanTfilaMGA())));
                    if (jewishCalendar.isErevYomTov() && jewishCalendar.getJewishMonth()==JewishDate.NISSAN) {
                        mObjects.add(new ZmanimObject(this, res.getString(R.string.achilas_chametz_text), res.getString(R.string.achilas_chametz_description), displayDate(czc.getSofZmanAchilasChametzGRA())));
                        mObjects.add(new ZmanimObject(this, res.getString(R.string.achilas_chametz_text), res.getString(R.string.achilas_chametz_description), displayDate(czc.getSofZmanBiurChametzGRA())));
                    }
                }
                else{
                    mObjects.add(new ZmanimObject(this, res.getString(R.string.kiriyat_shema_gra_text), res.getString(R.string.kiriyat_shema_gra_description), displayDate(czc.getSofZmanShmaGRA())));
                    mObjects.add(new ZmanimObject(this, res.getString(R.string.shacarit_text), res.getString(R.string.shacarit_description), displayDate(czc.getSofZmanTfilaGRA())));
                    if (jewishCalendar.isErevYomTov() && jewishCalendar.getJewishMonth()==JewishDate.NISSAN) {
                        mObjects.add(new ZmanimObject(this, res.getString(R.string.achilas_chametz_text), res.getString(R.string.achilas_chametz_description), displayDate(czc.getSofZmanAchilasChametzMGA72Minutes())));
                        mObjects.add(new ZmanimObject(this, res.getString(R.string.biur_chametz_text), res.getString(R.string.biur_chametz_description), displayDate(czc.getSofZmanBiurChametzMGA72Minutes())));
                    }
                }

                break;
            case Afternoon:
                getCardHeader().setTitle(res.getString(R.string.zmanim_afternoon));
                mObjects.add(new ZmanimObject(this, res.getString(R.string.chaztot_text), res.getString(R.string.chaztot_description), displayDate(czc.getChatzos())));
                mObjects.add(new ZmanimObject(this, res.getString(R.string.mincha_gedola_text), res.getString(R.string.mincha_gedola_description), displayDate(czc.getMinchaGedola())));
                addPlagMincha(preferences, mObjects, czc, res);
                addSunset(preferences, mObjects, czc, res);

                mObjects.add(new ZmanimObject(this, res.getString(R.string.tzet_hacochavim_text), res.getString(R.string.tzet_hacochavim_description), displayDate(czc.getTzaisGeonim5Point88Degrees())));
//                czc.getTzais()
//                czc.getTzais60()
//                czc.getTzais72()
//                czc.getTzais90()
//                czc.getTzais96()
//                czc.getTzais120()
                break;
            case Shabbat:
                getCardHeader().setTitle(res.getString(R.string.zmanim_shabbat));
                mObjects.add(new ZmanimObject(this, res.getString(R.string.candle_lighting_text), String.format(res.getString(R.string.candle_lighting_description), (int) czc.getCandleLightingOffset()), displayDate(czc.getCandleLighting())));
                addPlagMincha(preferences, mObjects, czc, res);
                addSunset(preferences, mObjects, czc, res);
                mObjects.add(new ZmanimObject(this, res.getString(R.string.tzet_hacochavim_text), res.getString(R.string.tzet_hacochavim_shabat_text), displayDate(czc.getTzaisGeonim8Point5Degrees())));
                break;
            case Special:
                getCardHeader().setTitle(res.getString(R.string.zmanim_special));

                Daf daf = YomiCalculator.getDafYomiBavli(jewishCalendar);
                //String masechta = Locale.getDefault().getLanguage().equals("he") ? daf.getMasechta() : daf.getMasechtaTransliterated();
                mObjects.add(new ZmanimObject(this, res.getString(R.string.daf_yomi), res.getString(R.string.daf_yomi_description), formatter.formatDafYomiBavli(daf)));

                DecimalFormat twoDForm = new DecimalFormat("#.00");
                mObjects.add(new ZmanimObject(this, res.getString(R.string.preferences_longitude), "", twoDForm.format(currentAddress.getLongitude())));
                mObjects.add(new ZmanimObject(this, res.getString(R.string.preferences_latitude), "", twoDForm.format(currentAddress.getLatitude())));
                mObjects.add(new ZmanimObject(this, res.getString(R.string.preferences_altitude), "", twoDForm.format(currentAddress.getAltitude())));
                mObjects.add(new ZmanimObject(this, res.getString(R.string.preferences_title), "", currentAddress.getTitle()));
                mObjects.add(new ZmanimObject(this, res.getString(R.string.preferences_address), "", currentAddress.getAddress()));
                break;
        }
        return mObjects;
    }

    private void addSunrise(SharedPreferences preferences, List<ListObject> mObjects, ComplexZmanimCalendar czc, Resources res) {
        int sunrisePos = Integer.valueOf(preferences.getString(OPTIONS_SUNRISE, "1"));
        if (sunrisePos==1){
            mObjects.add(new ZmanimObject(this, res.getString(R.string.netz_hachama_text), res.getString(R.string.netz_hachama_sea_description), displayDate(czc.getSeaLevelSunrise())));
        }
        else{
            mObjects.add(new ZmanimObject(this, res.getString(R.string.netz_hachama_text), res.getString(R.string.netz_hachama_observed_description), displayDate(czc.getSunrise())));
        }
    }

    private void addAlotHashacr(SharedPreferences preferences, List<ListObject> mObjects, ComplexZmanimCalendar czc, Resources res) {
        int alothashacr = Integer.valueOf(preferences.getString(OPTIONS_ALOTHASHACR, "60"));
        String description = String.format(res.getString(R.string.allot_hashchar_description), alothashacr);
        switch (alothashacr){
            case 60:
                mObjects.add(new ZmanimObject(this, res.getString(R.string.allot_hashchar_text), description, displayDate(czc.getAlos60())));
                break;
            case 72:
                mObjects.add(new ZmanimObject(this, res.getString(R.string.allot_hashchar_text), description, displayDate(czc.getAlos72())));
                break;
            case 90:
                mObjects.add(new ZmanimObject(this, res.getString(R.string.allot_hashchar_text), description, displayDate(czc.getAlos90())));
                break;
            case 120:
                mObjects.add(new ZmanimObject(this, res.getString(R.string.allot_hashchar_text), description, displayDate(czc.getAlos120())));
                break;
        }
    }

    private void addPlagMincha(SharedPreferences preferences, List<ListObject> mObjects, ComplexZmanimCalendar czc, Resources res) {
        String description;
        int plagMincha = Integer.valueOf(preferences.getString(OPTIONS_PLAG_MINCHA, "60"));
        description = String.format(res.getString(R.string.plag_mincha_full_description),plagMincha);
        switch (plagMincha){
            case 60:
                mObjects.add(new ZmanimObject(this, res.getString(R.string.plag_mincha_text), description, displayDate(czc.getPlagHamincha60Minutes())));
                break;
            case 72:
                mObjects.add(new ZmanimObject(this, res.getString(R.string.plag_mincha_text), description, displayDate(czc.getPlagHamincha72Minutes())));
                break;
            case 90:
                mObjects.add(new ZmanimObject(this, res.getString(R.string.plag_mincha_text), description, displayDate(czc.getPlagHamincha90Minutes())));
                break;
            case 96:
                mObjects.add(new ZmanimObject(this, res.getString(R.string.plag_mincha_text), description, displayDate(czc.getPlagHamincha96Minutes())));
                break;
            case 120:
                mObjects.add(new ZmanimObject(this, res.getString(R.string.plag_mincha_text), description, displayDate(czc.getPlagHamincha120Minutes())));
                break;
        }
    }

    private void addSunset(SharedPreferences preferences, List<ListObject> mObjects, ComplexZmanimCalendar czc, Resources res) {
        int sunset = Integer.valueOf(preferences.getString(OPTIONS_SUNSET, "1"));
        if (sunset==1){
            mObjects.add(new ZmanimObject(this, res.getString(R.string.shekia_text), res.getString(R.string.shekia_sea_description), displayDate(czc.getSeaLevelSunset())));
        }
        else{
            mObjects.add(new ZmanimObject(this, res.getString(R.string.shekia_text), res.getString(R.string.shekia_observed_description), displayDate(czc.getSunset())));
        }
    }

    @Override
    public View setupChildView(int childPosition, ListObject object, View convertView, ViewGroup parent) {

        TextView name = (TextView) convertView.findViewById(R.id.zmanim_name);
        TextView time = (TextView) convertView.findViewById(R.id.zmanim_time);

        ZmanimObject zman = (ZmanimObject) object;
        name.setText(zman.getName());
        time.setText(zman.getTime());

        return convertView;
    }

    public class ZmanimObject extends DefaultListObject {
        private String name;

        private String description;
        private String time;

        public ZmanimObject(Card parentCard, String name, String description, String time) {
            super(parentCard);
            this.name = name;
            this.time = time;
            this.description = description;
            setSwipeable(true);
        }

        public String getName() {
            return name;
        }

        public String getTime() {
            return time;
        }

        public String getDescription() {
            return description;
        }


    }

}
