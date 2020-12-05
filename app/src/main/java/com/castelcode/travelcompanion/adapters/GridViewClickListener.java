package com.castelcode.travelcompanion.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.castelcode.travelcompanion.tile_activities.CruiseSettings;
import com.castelcode.travelcompanion.tile_activities.DrinkCounter;
import com.castelcode.travelcompanion.tile_activities.Expenses;
import com.castelcode.travelcompanion.HomePage;
import com.castelcode.travelcompanion.tile_activities.TripAgenda;
import com.castelcode.travelcompanion.tile_activities.TripChecklists;
import com.castelcode.travelcompanion.tile_activities.TripInformation;
import com.castelcode.travelcompanion.tile_activities.TripLog;
import com.castelcode.travelcompanion.tile_activities.UnitConverter;

class GridViewClickListener implements View.OnClickListener
{
    private final int mPosition;
    private final Context mContext;

    GridViewClickListener(int position, Context context)
    {
        mPosition = position;
        mContext = context;
    }

    public void onClick(View v)
    {
        int id = ((HomePage)mContext).getAdapter().getItem(mPosition).getItemId();
        boolean validIntent = true;
        Intent intent = null;
        switch (id){
            case HomePage.UNIT_CONVERTER_ID:
                intent = new Intent(mContext, UnitConverter.class);
                break;
            case HomePage.TRIP_LOG_ID:
                intent = new Intent(mContext, TripLog.class);
                break;
            case HomePage.TRIP_AGENDA_ID:
                intent = new Intent(mContext, TripAgenda.class);
                break;
            case HomePage.DRINK_COUNTER_ID:
                intent = new Intent(mContext, DrinkCounter.class);
                break;
            case HomePage.TRIP_INFORMATION_ID:
                intent = new Intent(mContext, TripInformation.class);
                break;
            case HomePage.EXPENSES_ID:
                intent = new Intent(mContext, Expenses.class);
                break;
            case HomePage.SETTINGS_ID:
                intent = new Intent(mContext, CruiseSettings.class);
                break;
            case HomePage.TRIP_CHECKLIST_ID:
                intent = new Intent(mContext, TripChecklists.class);
                break;
            default:
                validIntent = false;
                break;
        }
        if(validIntent){
            mContext.startActivity(intent);
        }
    }
}
