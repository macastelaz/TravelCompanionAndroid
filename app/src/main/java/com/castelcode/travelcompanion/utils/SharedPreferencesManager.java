package com.castelcode.travelcompanion.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.castelcode.travelcompanion.HomePage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesManager {

    public static void resetSharedPreferences(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean hotel_preference = getBooleanFromSP(
                SettingsConstants.HOTEL_NOTIFICATION_PREFERENCE_TAG, context);
        boolean flight_preference = getBooleanFromSP(
                SettingsConstants.FLIGHT_NOTIFICATION_PREFERENCE_TAG, context);
        boolean cruise_preference = getBooleanFromSP(
                SettingsConstants.CRUISE_NOTIFICATION_PREFERENCE_TAG, context);
        boolean bus_preference = getBooleanFromSP(
                SettingsConstants.BUS_NOTIFICATION_PREFERENCE_TAG, context);
        editor.clear();
        editor.apply();
        saveBooleanInSP(
                SettingsConstants.HOTEL_NOTIFICATION_PREFERENCE_TAG, hotel_preference, context);
        saveBooleanInSP(
                SettingsConstants.FLIGHT_NOTIFICATION_PREFERENCE_TAG, flight_preference, context);
        saveBooleanInSP(
                SettingsConstants.CRUISE_NOTIFICATION_PREFERENCE_TAG, cruise_preference, context);
        saveBooleanInSP(
                SettingsConstants.BUS_NOTIFICATION_PREFERENCE_TAG, bus_preference, context);
    }

    public static void saveCruiseDate(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        Calendar cal = HomePage.cruise.getCruiseDateTime();
        String cruiseDate;
        if(cal != null){
            cruiseDate = DateStringUtil.intToDateString(cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.YEAR));
        } else {
            cruiseDate = "";
        }

        editor.putString("cruiseDateTime", cruiseDate);
        editor.apply();
    }

    public static void saveDrinksConsumed(Context context,
                                          HashMap<String, Integer> numDrinksConsumed) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        String hashMapString = gson.toJson(numDrinksConsumed);

        editor.putString("drinksConsumedPerType" , hashMapString);
        editor.apply();
    }

    public static HashMap<String, Integer> getDrinksConsumed(Context context) {
        Gson gson = new Gson();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String hashMapString = sharedPref.getString("drinksConsumedPerType", "");

        if(hashMapString.equals("")){
            return null;
        }

        java.lang.reflect.Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        return gson.fromJson(hashMapString, type);
    }

    public static void saveDrinkPrices(Context context,
                                          Map<String, Double> drinkPrices) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        String hashMapString = gson.toJson(drinkPrices);

        editor.putString("drinkPricesPerType" , hashMapString);
        editor.apply();
    }

    public static Map<String, Double> getDrinkPrices(Context context) {
        Gson gson = new Gson();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String mapString = sharedPref.getString("drinkPricesPerType", "");

        if(mapString.equals("")){
            return null;
        }

        java.lang.reflect.Type type = new TypeToken<Map<String, Double>>(){}.getType();
        return gson.fromJson(mapString, type);
    }

    public static String getCruiseDate(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString("cruiseDateTime", "");
    }

    public static boolean getBooleanFromSP(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return preferences.getBoolean(key, false);
    }

    public static void saveBooleanInSP(String key, boolean value, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
