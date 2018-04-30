package com.castelcode.cruisecompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.castelcode.cruisecompanion.adapters.GridViewAdapter;
import com.castelcode.cruisecompanion.utils.DateStringUtil;
import com.castelcode.cruisecompanion.utils.SharedPreferencesManager;
import com.castelcode.cruisecompanion.utils.TimeStringUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Period;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomePage extends AppCompatActivity{

    private static final String CRUISE_COMPANION_TAG = "CRUISE_COMPANION";

    private static final String STRING_PREFERENCE_NOT_FOUND = "";
    private static final int DAYS_PER_WEEK = 7;
    public static Calendar cruiseDateTime = Calendar.getInstance();

    public static final int UNIT_CONVERTER_ID = 0;
    public static final int TRIP_LOG_ID = 1;
    public static final int TRIP_AGENDA_ID = 2;
    public static final int DRINK_COUNTER_ID = 3;
    public static final int TRIP_INFORMATION_ID = 4;
    public static final int EXPENSES_ID = 5;
    public static final int SETTINGS_ID = 6;

    private TextView tripCountDown;
    private GridViewAdapter adapter;
    private CountDownTimer cdt;

    private TileController unitConverter;
    private TileController tripLog;
    private TileController tripAgenda;
    private TileController drinkCounter;
    private TileController tripInformation;
    private TileController expenses;
    private TileController settings;

    public static Cruise cruise;

    private static Context applicationContext;

    public static Context getContext() {
        return applicationContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        applicationContext = this.getApplicationContext();
        GridView tileGrid = (GridView) findViewById(R.id.tile_grid);
        tripCountDown = (TextView) findViewById(R.id.trip_countdown);
        adapter = new GridViewAdapter(this);
        tileGrid.setAdapter(adapter);

        Context appContext = getApplicationContext();
        setupTiles(appContext);
        addTiles();

        cruise = new Cruise();
        String cruiseDate = SharedPreferencesManager.getCruiseDate(this);
        Calendar cal = Calendar.getInstance();
        if(!cruiseDate.equals("")) {
            cal.set(Calendar.MONTH, DateStringUtil.getMonth(cruiseDate));
            cal.set(Calendar.DAY_OF_MONTH, DateStringUtil.getDay(cruiseDate));
            cal.set(Calendar.YEAR, DateStringUtil.getYear(cruiseDate));
            cruise.setCruiseDateTime(cal);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String dateString = sharedPref.getString(getString(R.string.date_key),
                STRING_PREFERENCE_NOT_FOUND);

        String timeString = sharedPref.getString(getString(R.string.time_key),
                STRING_PREFERENCE_NOT_FOUND);

        if (!dateString.equals(STRING_PREFERENCE_NOT_FOUND) &&
                !timeString.equals(STRING_PREFERENCE_NOT_FOUND)){
            cruiseDateTime.clear();
            Date newDate = DateStringUtil.stringToDate(dateString);
            cruiseDateTime.setTime(newDate);
            int amOrPm = TimeStringUtil.getAMorPM(timeString);
            if (amOrPm != -1) {
                cruiseDateTime.set(Calendar.AM_PM, TimeStringUtil.getAMorPM(timeString));
                cruiseDateTime.set(Calendar.HOUR, TimeStringUtil.getHour(timeString));
            } else {
                cruiseDateTime.set(Calendar.HOUR_OF_DAY, TimeStringUtil.getHour(timeString));
            }
            cruiseDateTime.set(Calendar.MINUTE, TimeStringUtil.getMinute(timeString));
            cruiseDateTime.set(Calendar.SECOND, 0);
            cruise.setCruiseDateTime(cruiseDateTime);
            startCountdownTimer();
        }
        else{
            Toast.makeText(this, getResources().getString(R.string.date_not_set),
                    Toast.LENGTH_LONG).show();
            String countDownText = getResources().getString(R.string.trip_countdown_prefix) + ' ' +
                    getResources().getString(R.string.not_applicable);
            tripCountDown.setText(countDownText);
        }
        String cruiseName = sharedPref.getString(getResources().getString(R.string.current_name),
                "");
        cruise.setCruiseName(cruiseName);
    }

    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferencesManager.saveCruiseDate(this);
        try{
            cdt.cancel();
        }
        catch (NullPointerException ex){
            Log.e(CRUISE_COMPANION_TAG, "No count down timer running " + ex.toString());
        }
    }

    private void startCountdownTimer(){
        final long currTimeInMillis = Calendar.getInstance().getTimeInMillis();
        final long tripTimeInMillis = cruiseDateTime.getTimeInMillis();
        long timeDiff = tripTimeInMillis - currTimeInMillis;
        if(timeDiff < 0){
            Toast.makeText(this, getResources().getString(R.string.date_in_past),
                    Toast.LENGTH_LONG).show();
        }
        else{
            cdt = new CountDownTimer(timeDiff, 1000) {

                public void onTick(long millisUntilFinished) {
                    // TODO Auto-generated method stub
                    long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
                    DateTime cruiseDate =
                            new DateTime(Long.valueOf(tripTimeInMillis), DateTimeZone.UTC);
                    DateTime currentDate =
                            new DateTime(Long.valueOf(currentTimeInMillis), DateTimeZone.UTC);

                    int mDay = Days.daysBetween(
                            currentDate.toLocalDate(), cruiseDate.toLocalDate()).getDays();

                    Period period = new Period(currentTimeInMillis, tripTimeInMillis);

                    long mHour = period.getHours();
                    long mMin = period.getMinutes();
                    long mSec = period.getSeconds();
                    Locale locale = Locale.getDefault();
                    String countDownString =
                            mDay + "d " +
                            mHour + "h " +
                            mMin + "min " +
                            mSec + "s";
                    String countDownText = getResources().getString(R.string.trip_countdown_prefix)
                            + '\n' + countDownString;
                    tripCountDown.setText(countDownText);
                }

                public void onFinish() {
                    // TODO Auto-generated method stub

                }
            }.start();
        }
    }

    private void setupTiles(Context appContext){
        unitConverter = new TileController(this, "Unit Converter",
                ContextCompat.getDrawable(appContext, R.drawable.unit_converter),
                UNIT_CONVERTER_ID);
        tripLog = new TileController(this, "Trip Log",
                ContextCompat.getDrawable(appContext, R.drawable.trip_log),
                TRIP_LOG_ID);
        tripAgenda = new TileController(this, "Agenda",
                ContextCompat.getDrawable(appContext, R.drawable.trip_agenda),
                TRIP_AGENDA_ID);
        drinkCounter = new TileController(this, "Drink Counter",
                ContextCompat.getDrawable(appContext, R.drawable.drink_counter),
                DRINK_COUNTER_ID);
        tripInformation = new TileController(this, "Trip Info",
                ContextCompat.getDrawable(appContext, R.drawable.trip_information),
                TRIP_INFORMATION_ID);
        expenses = new TileController(this, "Expenses",
                ContextCompat.getDrawable(appContext, R.drawable.expenses),
                EXPENSES_ID);
        settings = new TileController(this, "Settings",
                ContextCompat.getDrawable(appContext, R.drawable.settings),
                SETTINGS_ID);
    }

    private void addTiles(){
        adapter.addTile(unitConverter);
        adapter.addTile(tripLog);
        adapter.addTile(tripAgenda);
        adapter.addTile(drinkCounter);
        adapter.addTile(tripInformation);
        adapter.addTile(expenses);
        adapter.addTile(settings);
    }

    public GridViewAdapter getAdapter(){
        return this.adapter;
    }

}
