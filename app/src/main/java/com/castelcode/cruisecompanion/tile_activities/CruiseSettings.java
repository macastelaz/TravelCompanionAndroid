package com.castelcode.cruisecompanion.tile_activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.castelcode.cruisecompanion.agenda_entry.DateEntry;
import com.castelcode.cruisecompanion.HomePage;
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.adapters.InfoSerializerAdapter;
import com.castelcode.cruisecompanion.expenses.Expense;
import com.castelcode.cruisecompanion.log_entry_add_activity.LogEntry;
import com.castelcode.cruisecompanion.preferences.DatePreference;
import com.castelcode.cruisecompanion.preferences.SelectNotificationPreferences;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.Info;
import com.castelcode.cruisecompanion.utils.CruiseIO;
import com.castelcode.cruisecompanion.utils.DateStringUtil;
import com.castelcode.cruisecompanion.utils.InfoItemUtil;
import com.castelcode.cruisecompanion.utils.NotificationUtil;
import com.castelcode.cruisecompanion.utils.SettingsConstants;
import com.castelcode.cruisecompanion.utils.SharedPreferencesManager;
import com.castelcode.cruisecompanion.utils.TimeStringUtil;
import com.castelcode.cruisecompanion.utils.WakefulReceiver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

import io.gsonfire.GsonFireBuilder;

public class CruiseSettings extends PreferenceActivity {

    private static final String CRUISE_SETTINGS_TAG = "CRUISE_SETTINGS";

    private static final int SET_NOTIFICATION_PREFERENCES = 1;

    static Preference saveButton;
    static Preference resetButton;
    static Preference loadButton;
    static Preference deleteButton;
    static Preference datePreference;
    static Preference timePreference;
    static Preference notificationPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new CruiseSettingsFragment()).commit();

    }

    public static class CruiseSettingsFragment extends PreferenceFragment implements
            Preference.OnPreferenceClickListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.cruise_preferences);
            getActivity().setTheme(R.style.prefScreen);
            getActivity().findViewById(android.R.id.list).setBackgroundColor(
                    ContextCompat.getColor(getActivity(), android.R.color.black));
            saveButton = findPreference(getString(R.string.cruise_save_key));
            resetButton = findPreference(getString(R.string.cruise_reset_key));
            loadButton = findPreference(getString(R.string.cruise_load_key));
            deleteButton = findPreference(getString(R.string.cruise_delete_key));
            datePreference = findPreference(getString(R.string.date_key));
            timePreference = findPreference(getString(R.string.time_key));
            notificationPreferences = findPreference("notificationPreferences");


            saveButton.setOnPreferenceClickListener(this);
            resetButton.setOnPreferenceClickListener(this);
            loadButton.setOnPreferenceClickListener(this);
            deleteButton.setOnPreferenceClickListener(this);
            notificationPreferences.setOnPreferenceClickListener(this);
        }

        private void setupCruiseWithCurrentInformation(SharedPreferences pref, Gson gson,
                                                       String name){
            HomePage.cruise.setCruiseName(name);
            int drinksConsumed = pref.getInt(getString(R.string.beverages_consumed), 0);
            HomePage.cruise.setNumDrinksConsumed(drinksConsumed);

            Type listOfExpenses = new TypeToken<ArrayList<Expense>>(){}.getType();
            String expensesString = pref.getString(getString(R.string.expense_items), "");
            ArrayList<Expense> expenses = gson.fromJson(expensesString, listOfExpenses);
            HomePage.cruise.setExpenses(expenses);

            Type hashMapOfDateEnties = new TypeToken<TreeMap<String, ArrayList<DateEntry>>>(){}
                    .getType();
            String agendaString = pref.getString(getString(R.string.date_entries_hash_map), "");
            TreeMap<String, ArrayList<DateEntry>> agendaItems =
                    gson.fromJson(agendaString, hashMapOfDateEnties);
            HomePage.cruise.setAgendaEntries(agendaItems);

            GsonFireBuilder builder = new GsonFireBuilder().registerTypeSelector(Info.class,
                    (JsonElement readElement) -> {
                        String type = readElement.getAsJsonObject().get("type").getAsString();
                        switch (type){
                            case "Hotel":
                                return HotelInfo.class;
                            case "Cruise":
                                return CruiseInfo.class;
                            case "Flight":
                                return FlightInfo.class;
                            default:
                                return null;
                        }
                    });
            Gson gsonForTripInfo = builder.createGson();
            String tripInfoString = pref.getString(getString(R.string.info_items), "");
            Type tripInfoType = new TypeToken<ArrayList<Info>>(){}.getType();
            ArrayList<Info> tripInformation = gsonForTripInfo.fromJson(tripInfoString,
                    tripInfoType);
            HomePage.cruise.setTripInfo(tripInformation);

            Type logEntriesType = new TypeToken<ArrayList<LogEntry>>(){}.getType();
            String logEntriesString = pref.getString(getString(R.string.log_entry), "");
            ArrayList<LogEntry> logEntries = gson.fromJson(logEntriesString, logEntriesType);
            HomePage.cruise.setLogEntries(logEntries);

            String dateString = pref.getString(getString(R.string.date_key),
                    "");
            String timeString = pref.getString(getString(R.string.time_key),
                    "");
            Calendar cruiseDateTime = Calendar.getInstance();
            if (!dateString.equals("") &&
                    !timeString.equals("")) {
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
                HomePage.cruise.setCruiseDateTime(cruiseDateTime);
            }
        }

        private void saveButtonClicked() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                    R.style.CustomAlertDialog);
            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            input.setTextColor(ContextCompat.getColor(getActivity(), R.color.textColor));
            input.setText(HomePage.cruise.getCruiseName());
            builder.setView(input);
            builder.setMessage(R.string.cruise_save_message)
                    .setPositiveButton(R.string.save_confirm,(DialogInterface dialog, int which) -> {
                            final SharedPreferences pref =
                                    PreferenceManager.getDefaultSharedPreferences(
                                            getActivity());
                            final Gson gson = new Gson();
                            final String loadedJsonText = pref.getString(getResources().getString(
                                    R.string.cruise_names), "");
                            final ArrayList<String> cruiseNames;
                            if (!loadedJsonText.equals("")) {
                                cruiseNames = new ArrayList<>(Arrays.asList(
                                        gson.fromJson(loadedJsonText, String[].class)));
                            } else {
                                cruiseNames = new ArrayList<>();
                            }
                            final String name = input.getText().toString();
                            if (cruiseNames.contains(name)) {
                                AlertDialog.Builder overwriteBuilder =
                                        new AlertDialog.Builder(getActivity(),
                                                R.style.CustomAlertDialog);
                                overwriteBuilder.setTitle("Overwrite?");
                                overwriteBuilder.setMessage("Do you wish to overwrite " +
                                        name);
                                overwriteBuilder.setPositiveButton(getString(R.string.yes),
                                        (DialogInterface dialogInnerYes, int whichInnerYes) -> {
                                    if(!deleteCruise(name,
                                            gson, loadedJsonText, pref,
                                            false)) {
                                        Toast.makeText(getActivity(),
                                                "Cruise could not be overwritten",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    cruiseNames.remove(name);
                                    completeSave(pref, gson, name, cruiseNames);
                                });
                                overwriteBuilder.setNegativeButton(getString(R.string.no),
                                        (DialogInterface dialogInnerNo, int whichInnerNo) -> {
                                    //We dont want to save so lets exit the flow.
                                    Toast.makeText(getActivity(), "Save cancelled",
                                            Toast.LENGTH_SHORT).show();
                                    Log.i(CRUISE_SETTINGS_TAG, getResources().getString(
                                            R.string.save_cancelled));
                                });
                                overwriteBuilder.create().show();
                            }
                            else {
                                completeSave(pref, gson, name, cruiseNames);
                            }
                    })
                    .setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) -> {
                        Toast.makeText(getActivity(), "Save cancelled",
                                Toast.LENGTH_SHORT).show();
                        Log.i(CRUISE_SETTINGS_TAG, getResources().getString(
                                R.string.save_cancelled));
                    });
            builder.create().show();
        }

        private void completeSave(SharedPreferences pref, Gson gson, String name,
                                  ArrayList<String> cruiseNames ){
            setupCruiseWithCurrentInformation(pref, gson, name);

            boolean saveSuccessful =
                    HomePage.cruise.save(
                            new CruiseIO(getActivity().getFilesDir()), name,
                            this.getActivity().getApplicationContext());
            if (saveSuccessful) {
                cruiseNames.add(name);
                String saveJsonText = gson.toJson(cruiseNames);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(getResources().getString(
                        R.string.cruise_names), saveJsonText);
                editor.putString(getResources().getString(
                        R.string.current_name), name);
                editor.apply();
                Toast.makeText(getActivity(), name + " " +
                                getResources().getString(R.string.save_success),
                        Toast.LENGTH_SHORT).show();
            } else {
                HomePage.cruise.setCruiseName("");
                Toast.makeText(getActivity(), name + " " +
                                getResources().getString(R.string.save_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }

        private void resetButtonClicked(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                    R.style.CustomAlertDialog);

            builder.setMessage(R.string.cruise_reset_message)
                    .setPositiveButton(R.string.reset_confirm,
                            (DialogInterface dialog, int which) -> {
                                SharedPreferences pref =
                                        PreferenceManager.getDefaultSharedPreferences(
                                                getActivity());
                                String loadedJsonText = pref.getString(getResources().getString(
                                        R.string.cruise_names), "");
                                Gson gson = new Gson();
                                ArrayList<String> cruiseNamesArray = null;
                                if(gson.fromJson(loadedJsonText, String[].class) != null){
                                    cruiseNamesArray = new ArrayList<>(
                                            Arrays.asList(gson.fromJson(loadedJsonText,
                                                    String[].class)));
                                }
                                SharedPreferencesManager.resetSharedPreferences(getActivity());
                                if(cruiseNamesArray != null) {
                                    String saveJsonText = gson.toJson(cruiseNamesArray);
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString(getResources().getString(
                                            R.string.cruise_names), saveJsonText);
                                    editor.apply();
                                }
                                datePreference.setSummary(getString(R.string.date_summary));
                                ((DatePreference)datePreference).clearDate();
                                timePreference.setSummary(getString(R.string.time_summary));
                                TripInformation.resetTripInformationItems();
                                Toast.makeText(getActivity(), "This cruise has been reset",
                                        Toast.LENGTH_SHORT).show();
                            })
                    .setNegativeButton(R.string.cancel, (DialogInterface dialog, int which) ->
                            Log.i(CRUISE_SETTINGS_TAG, "Reset Cancelled"));
            builder.create().show();
        }

        private void updateSharedPreferencesWithNewCruise(){
            Gson genericGson = new Gson();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(
                    getActivity());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.beverages_consumed),
                    HomePage.cruise.getNumDrinksConsumed());

            Type listOfExpenses = new TypeToken<ArrayList<Expense>>(){}.getType();
            String expensesJson = genericGson.toJson(HomePage.cruise.getExpenses(), listOfExpenses);
            editor.putString(getString(R.string.expense_items), expensesJson);

            Type hashMapOfDateEnties = new TypeToken<TreeMap<String, ArrayList<DateEntry>>>(){}
                    .getType();
            TreeMap<String, ArrayList<DateEntry>> agendaItems = HomePage.cruise.getAgendaEntries();
            String agendaEntriesJson = genericGson.toJson(agendaItems, hashMapOfDateEnties);
            editor.putString(getString(R.string.date_entries_hash_map), agendaEntriesJson);

            Gson infoGson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Info.class,
                    new InfoSerializerAdapter()).create();
            String informationJson = infoGson.toJson(HomePage.cruise.getTripInfo());
            editor.putString(getString(R.string.info_items), informationJson);

            Type listOfLogEntry = new TypeToken<ArrayList<LogEntry>>(){}.getType();

            String logJson = genericGson.toJson(HomePage.cruise.getLogEntries(), listOfLogEntry);
            editor.putString(getString(R.string.log_entry), logJson);

            //Handle Time and cruise name

            Calendar calendar = HomePage.cruise.getCruiseDateTime();
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            String dateString = DateStringUtil.calendarToString(calendar);

            editor.putString(getString(R.string.date_key), DateStringUtil.slashToDot(dateString));
            int hour = calendar.get(Calendar.HOUR);
            if(calendar.get(Calendar.AM_PM) == Calendar.PM){
                hour += 12;
            }
            String timeString = TimeStringUtil.createTimeString(hour,
                    calendar.get(Calendar.MINUTE));

            editor.putString(getString(R.string.time_key), timeString);

            editor.putString(getResources().getString(
                    R.string.current_name), HomePage.cruise.getCruiseName());

            editor.apply();
        }

        private void loadButtonClicked(){
            SharedPreferences pref =
                    PreferenceManager.getDefaultSharedPreferences(
                            getActivity());
            Gson gson = new Gson();
            String loadedJsonText = pref.getString(getResources().getString(
                    R.string.cruise_names), "");
            final CharSequence[] cruiseNames;
            if (!loadedJsonText.equals("")) {
                cruiseNames = gson.fromJson(loadedJsonText, String[].class);
            }
            else{
                Toast.makeText(getActivity(), getString(R.string.no_saved_cruises),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                    R.style.CustomAlertDialog);
            builder.setTitle(getResources().getString(R.string.cruise_load_title))
                    .setItems(cruiseNames, (DialogInterface dialog, int which) -> {
                        //Handle save
                        String name = String.valueOf(cruiseNames[which]);
                        if(HomePage.cruise.open(new CruiseIO(getActivity().getFilesDir()),
                                name, this.getActivity().getApplicationContext())){
                            updateSharedPreferencesWithNewCruise();
                            Calendar calendar = HomePage.cruise.getCruiseDateTime();
                            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                            ((DatePreference)datePreference).setCruiseDate(
                                    DateStringUtil.calendarToString(calendar));
                            ((DatePreference)datePreference).cruiseLoaded();
                            int hour = calendar.get(Calendar.HOUR);
                            if(calendar.get(Calendar.AM_PM) == Calendar.PM){
                                hour += 12;
                            }
                            timePreference.setSummary(TimeStringUtil.getSummaryString(
                                    TimeStringUtil.createTimeString(hour,
                                            calendar.get(Calendar.MINUTE))));
                        }
                        else {
                            Toast.makeText(getActivity(), name + " " +
                                    getResources().getString(R.string.load_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.create().show();

        }

        private void deleteButtonClicked(){
            final SharedPreferences pref =
                    PreferenceManager.getDefaultSharedPreferences(
                            getActivity());
            final Gson gson = new Gson();
            final String loadedJsonText = pref.getString(getResources().getString(
                    R.string.cruise_names), "");
            final CharSequence[] cruiseNames;
            if (!loadedJsonText.equals("")) {
                cruiseNames = gson.fromJson(loadedJsonText, String[].class);
            }
            else{
                Toast.makeText(getActivity(), getString(R.string.no_saved_cruises),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                    R.style.CustomAlertDialog);
            builder.setTitle(getResources().getString(R.string.cruise_delete_title))
                    .setItems(cruiseNames, (DialogInterface dialog, int which) ->
                        deleteCruise(cruiseNames[which].toString(),
                                gson, loadedJsonText, pref, true));
            builder.create().show();
        }

        private boolean deleteCruise(String name, Gson gson, String loadedJsonText,
                                  SharedPreferences pref, boolean showToast) {
            //Handle Delete
            if(HomePage.cruise.delete(new CruiseIO(
                    getActivity().getFilesDir()), name)){
                ArrayList<String> cruiseNamesArray = new ArrayList<>(Arrays.asList(
                        gson.fromJson(loadedJsonText, String[].class)));
                cruiseNamesArray.remove(name);
                String saveJsonText = gson.toJson(cruiseNamesArray);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(getResources().getString(
                        R.string.cruise_names), saveJsonText);
                if(HomePage.cruise.getCruiseName().equals(name)){

                    HomePage.cruise.setCruiseName("");
                    editor.putString(getResources().getString(
                            R.string.current_name), "");
                }
                editor.apply();
                if(showToast) {
                    Toast.makeText(getActivity(), name + " " +
                                    getString(R.string.delete_successful),
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            else{
                if(showToast) {
                    Toast.makeText(getActivity(), name + " " +
                                    getString(R.string.delete_failed),
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        }

        private void notificationPreferencesClicked() {
            Log.d("TEST", "NOTIFICATION PREFERENCES CLICKED");
            Intent launchNotificationPage = new Intent(this.getActivity(),
                    SelectNotificationPreferences.class);
            startActivityForResult(launchNotificationPage, SET_NOTIFICATION_PREFERENCES);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(requestCode == SET_NOTIFICATION_PREFERENCES && resultCode == RESULT_OK ) {
                Context context = this.getActivity().getApplicationContext();
                boolean cruiseNotification =
                        data.getBooleanExtra(SettingsConstants.CRUISE_NOTIFICATION_KEY,
                                false);
                boolean hotelNotification =
                        data.getBooleanExtra(SettingsConstants.HOTEL_NOTIFICATION_KEY,
                                false);
                boolean busNotification =
                        data.getBooleanExtra(SettingsConstants.BUS_NOTIFICATION_KEY,
                                false);
                boolean flightNotification =
                        data.getBooleanExtra(SettingsConstants.FLIGHT_NOTIFICATION_KEY,
                                false);
                if(cruiseNotification) {
                    boolean oldCruiseNotifications = SharedPreferencesManager.getBooleanFromSP(
                            SettingsConstants.CRUISE_NOTIFICATION_PREFERENCE_TAG, context);
                    if (!oldCruiseNotifications) {
                        ArrayList<Info> cruiseInfoItems = InfoItemUtil.getAllCruiseInfoItems();
                        for(Info item : cruiseInfoItems) {
                            Calendar cal = NotificationUtil.getNotificationTimeForInfoItem(item);
                            if(cal == null) {
                                return;
                            }
                            WakefulReceiver.setAlarm(
                                    context,
                                    cal,
                                    NotificationUtil.getNotificationIdForInfoItem(item),
                                    NotificationUtil.getNotificationMessageForInfoItem(item));
                        }
                    }
                }
                else {
                    ArrayList<Info> cruiseInfoItems = InfoItemUtil.getAllCruiseInfoItems();
                    for(Info item : cruiseInfoItems) {
                        WakefulReceiver.cancelAlarm(context,
                                NotificationUtil.getNotificationIdForInfoItem(item));
                    }
                }
                if (hotelNotification) {
                    boolean oldHotelNotifications = SharedPreferencesManager.getBooleanFromSP(
                            SettingsConstants.HOTEL_NOTIFICATION_PREFERENCE_TAG, context);
                    if (!oldHotelNotifications) {
                        ArrayList<Info> hotelInfoItems = InfoItemUtil.getAllHotelInfoItems();
                        for(Info item : hotelInfoItems) {
                            Calendar cal = NotificationUtil.getNotificationTimeForInfoItem(item);
                            if(cal == null) {
                                return;
                            }
                            WakefulReceiver.setAlarm(
                                    context,
                                    cal,
                                    NotificationUtil.getNotificationIdForInfoItem(item),
                                    NotificationUtil.getNotificationMessageForInfoItem(item));
                        }
                    }
                }
                else {
                    ArrayList<Info> hotelInfoItems = InfoItemUtil.getAllHotelInfoItems();
                    for(Info item : hotelInfoItems) {
                        WakefulReceiver.cancelAlarm(context,
                                NotificationUtil.getNotificationIdForInfoItem(item));
                    }
                }
                if(busNotification) {
                    boolean oldBusNotifications = SharedPreferencesManager.getBooleanFromSP(
                            SettingsConstants.BUS_NOTIFICATION_PREFERENCE_TAG, context);
                    if (!oldBusNotifications) {
                        ArrayList<Info> busInfoItems = InfoItemUtil.getAllBusInfoItems();
                        for(Info item : busInfoItems) {
                            Calendar cal = NotificationUtil.getNotificationTimeForInfoItem(item);
                            if(cal == null) {
                                return;
                            }
                            WakefulReceiver.setAlarm(
                                    context,
                                    cal,
                                    NotificationUtil.getNotificationIdForInfoItem(item),
                                    NotificationUtil.getNotificationMessageForInfoItem(item));
                        }
                    }
                }
                else {
                    ArrayList<Info> busInfoItems = InfoItemUtil.getAllBusInfoItems();
                    for(Info item : busInfoItems) {
                        WakefulReceiver.cancelAlarm(context,
                                NotificationUtil.getNotificationIdForInfoItem(item));
                    }
                }
                if(flightNotification) {
                    boolean oldFlightNotifications = SharedPreferencesManager.getBooleanFromSP(
                            SettingsConstants.FLIGHT_NOTIFICATION_PREFERENCE_TAG, context);
                    if (!oldFlightNotifications) {
                        ArrayList<Info> flightInfoItems = InfoItemUtil.getAllFlightInfoItems();
                        for(Info item : flightInfoItems) {
                            Calendar cal = NotificationUtil.getNotificationTimeForInfoItem(item);
                            if(cal == null) {
                                return;
                            }
                            WakefulReceiver.setAlarm(
                                    context,
                                    cal,
                                    NotificationUtil.getNotificationIdForInfoItem(item),
                                    NotificationUtil.getNotificationMessageForInfoItem(item));
                        }
                    }
                }
                else {
                    ArrayList<Info> flightInfoItems = InfoItemUtil.getAllFlightInfoItems();
                    for(Info item : flightInfoItems) {
                        WakefulReceiver.cancelAlarm(context,
                                NotificationUtil.getNotificationIdForInfoItem(item));
                    }
                }
                SharedPreferencesManager.saveBooleanInSP(
                        SettingsConstants.CRUISE_NOTIFICATION_PREFERENCE_TAG,
                        cruiseNotification,
                        context);
                SharedPreferencesManager.saveBooleanInSP(
                        SettingsConstants.HOTEL_NOTIFICATION_PREFERENCE_TAG,
                        hotelNotification,
                        context);
                SharedPreferencesManager.saveBooleanInSP(
                        SettingsConstants.BUS_NOTIFICATION_PREFERENCE_TAG,
                        busNotification,
                        context);
                SharedPreferencesManager.saveBooleanInSP(
                        SettingsConstants.FLIGHT_NOTIFICATION_PREFERENCE_TAG,
                        flightNotification,
                        context);
            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference == saveButton){
                saveButtonClicked();
                return true;
            }
            else if(preference == loadButton){
                loadButtonClicked();
                return true;
            }
            else if(preference == resetButton){
                resetButtonClicked();
                return true;
            }
            else if(preference == deleteButton){
                deleteButtonClicked();
                return true;
            }
            else if(preference == notificationPreferences) {
                notificationPreferencesClicked();
                return true;
            }
            return false;
        }
    }
}
