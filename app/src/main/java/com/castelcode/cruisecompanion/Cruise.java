package com.castelcode.cruisecompanion;


import android.content.Context;
import android.util.Log;

import com.castelcode.cruisecompanion.NotificationPreference.NotificationPreferenceWrapper;
import com.castelcode.cruisecompanion.agenda_entry.DateEntry;
import com.castelcode.cruisecompanion.expenses.Expense;
import com.castelcode.cruisecompanion.log_entry_add_activity.LogEntry;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.Info;
import com.castelcode.cruisecompanion.utils.CruiseIO;
import com.castelcode.cruisecompanion.utils.SettingsConstants;
import com.castelcode.cruisecompanion.utils.SharedPreferencesManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

public class Cruise implements Serializable {

    private final int DEFAULT_YEAR = 1990;
    private final int DEFAULT_MONTH = 1;
    private final int DEFAULT_DAY = 1;

    private Calendar cruiseDateTime;
    private int numDrinksConsumed;
    private ArrayList<Info> tripInfo;
    private String cruiseName;
    private ArrayList<LogEntry> logEntries;
    private TreeMap<String, ArrayList<DateEntry>> agendaEntries;
    private ArrayList<Expense> expenses;
    private ArrayList<NotificationPreferenceWrapper> notificationPreferences = new ArrayList<>();

    public void setCruiseDateTime(Calendar dateTime){
        cruiseDateTime = dateTime;
    }

    public void setNumDrinksConsumed(int numDrinks){
        numDrinksConsumed = numDrinks;
    }

    public void setTripInfo(ArrayList<Info> infoItems){
        tripInfo = infoItems;
    }

    public void setCruiseName(String name){
        cruiseName = name;
    }

    public void setLogEntries(ArrayList<LogEntry> logItems){
        logEntries = logItems;
    }

    public void setAgendaEntries(TreeMap<String, ArrayList<DateEntry>> agendaItems){
        agendaEntries = agendaItems;
    }

    public void setExpenses(ArrayList<Expense> expenseItems){
        expenses = expenseItems;
    }

    public void setCruiseHour(int hour){
        cruiseDateTime.set(Calendar.HOUR, hour);
    }

    public void setCruiseMinute(int minute){
        cruiseDateTime.set(Calendar.MINUTE, minute);
    }

    public boolean save(CruiseIO io, String name, Context context){
        notificationPreferences.add(new NotificationPreferenceWrapper
                ("cruise_notifications", SharedPreferencesManager.getBooleanFromSP(
                        SettingsConstants.CRUISE_NOTIFICATION_PREFERENCE_TAG, context)));
        notificationPreferences.add(new NotificationPreferenceWrapper
                ("hotel_notifications", SharedPreferencesManager.getBooleanFromSP(
                        SettingsConstants.HOTEL_NOTIFICATION_PREFERENCE_TAG, context)));
        notificationPreferences.add(new NotificationPreferenceWrapper
                ("flight_notifications", SharedPreferencesManager.getBooleanFromSP(
                        SettingsConstants.FLIGHT_NOTIFICATION_PREFERENCE_TAG, context)));
        notificationPreferences.add(new NotificationPreferenceWrapper
                ("bus_notifications", SharedPreferencesManager.getBooleanFromSP(
                        SettingsConstants.BUS_NOTIFICATION_PREFERENCE_TAG, context)));
        if(this.getCruiseDateTime() == null){
            Calendar defaultDate = Calendar.getInstance();
            defaultDate.clear();
            defaultDate.set(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY);
            this.setCruiseDateTime(defaultDate);
        }
        return io.saveCruise(this, name);
    }

    public boolean delete(CruiseIO io, String name){
        return io.deleteCruise(name);
    }

    private void adjustMonthFromLoad(){
        cruiseDateTime.set(Calendar.MONTH, cruiseDateTime.get(Calendar.MONTH));
    }

    //TODO remove supressing when using open
    @SuppressWarnings("unused")
    public boolean open(CruiseIO io, String name, Context context) {
        Cruise readCruise = io.readCruise(name);
        if(readCruise != null){
            Calendar defaultDate = Calendar.getInstance();
            defaultDate.clear();
            defaultDate.set(DEFAULT_YEAR, DEFAULT_MONTH, DEFAULT_DAY);
            if(readCruise.getCruiseDateTime().equals(defaultDate)){
                this.setCruiseDateTime(null);
            }
            this.setCruiseDateTime(readCruise.getCruiseDateTime());
            this.adjustMonthFromLoad();
            this.setNumDrinksConsumed(readCruise.getNumDrinksConsumed());
            this.setTripInfo(readCruise.getTripInfo());
            this.setAgendaEntries(readCruise.getAgendaEntries());
            this.setCruiseName(readCruise.getCruiseName());
            this.setLogEntries(readCruise.getLogEntries());
            this.setExpenses(readCruise.getExpenses());
            for ( NotificationPreferenceWrapper wrapper  : readCruise.getNotificationPreferences())
            {
                switch (wrapper.getPreferenceName()) {
                    case "cruise_notifications":
                        SharedPreferencesManager.saveBooleanInSP(
                                SettingsConstants.CRUISE_NOTIFICATION_PREFERENCE_TAG,
                                wrapper.getPreferenceValue(),
                                context);
                        break;
                    case "flight_notifications":
                        SharedPreferencesManager.saveBooleanInSP(
                                SettingsConstants.FLIGHT_NOTIFICATION_PREFERENCE_TAG,
                                wrapper.getPreferenceValue(),
                                context);
                        break;
                    case "bus_notifications":
                        SharedPreferencesManager.saveBooleanInSP(
                                SettingsConstants.BUS_NOTIFICATION_PREFERENCE_TAG,
                                wrapper.getPreferenceValue(),
                                context);
                        break;
                    case "hotel_notifications":
                        SharedPreferencesManager.saveBooleanInSP(
                                SettingsConstants.HOTEL_NOTIFICATION_PREFERENCE_TAG,
                                wrapper.getPreferenceValue(),
                                context);
                        break;
                    default:
                        Log.i("CRUISE", "Preference type not recognized on read.");
                }
            }
            return true;
        }
        else{
            return false;
        }
    }

    public int getNumDrinksConsumed(){
        return numDrinksConsumed;
    }

    public Calendar getCruiseDateTime(){

        return cruiseDateTime;
    }

    public ArrayList<Info> getTripInfo(){ return tripInfo; }

    public String getCruiseName(){ return cruiseName; }

    public ArrayList<LogEntry> getLogEntries(){ return logEntries; }

    public TreeMap<String, ArrayList<DateEntry>> getAgendaEntries(){ return agendaEntries; }

    public ArrayList<Expense> getExpenses(){ return expenses; }

    public ArrayList<NotificationPreferenceWrapper> getNotificationPreferences()
    {
        return notificationPreferences;
    }

}
