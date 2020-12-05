package com.castelcode.travelcompanion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.Info;

import java.util.ArrayList;

public class InfoItemsBaseAdapter extends BaseAdapter {
    private static ArrayList<Info> infoItemsArrayList;

    private LayoutInflater mInflater;

    public InfoItemsBaseAdapter(Context context, ArrayList<Info> results) {
        infoItemsArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return infoItemsArrayList.size();
    }

    public Object getItem(int position) {
        return infoItemsArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.info_list_view_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.confirmationNumber = (TextView)
                    convertView.findViewById(R.id.confirmation_number);
            holder.originDisplay = (TextView)
                    convertView.findViewById(R.id.origin_display);
            holder.destinationDisplay = (TextView)
                    convertView.findViewById(R.id.destination_display);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String nameText = "Name: " + infoItemsArrayList.get(position).getPrimaryName();
        holder.name.setText(nameText);
        String confNumberText = "Conf #: " +
                infoItemsArrayList.get(position).getConfirmationNumber();
        holder.confirmationNumber.setText(confNumberText);
        if(infoItemsArrayList.get(position) instanceof FlightInfo){
            String originText = "Origin: " +
                    ((FlightInfo) infoItemsArrayList.get(position)).getOrigin();
            holder.originDisplay.setText(originText);
            String destinationText = "Destination: " +
                    ((FlightInfo) infoItemsArrayList.get(position)).getDestination();
            holder.destinationDisplay.setText(destinationText);
        }
        else if(infoItemsArrayList.get(position) instanceof BusInfo){
            String originText = "Origin: " +
                    ((BusInfo) infoItemsArrayList.get(position)).getOrigin();
            holder.originDisplay.setText(originText);
            String destinationText = "Destination: " +
                    ((BusInfo) infoItemsArrayList.get(position)).getDestination();
            holder.destinationDisplay.setText(destinationText);
        }
        else{
            holder.originDisplay.setVisibility(View.GONE);
            holder.destinationDisplay.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView confirmationNumber;
        TextView originDisplay;
        TextView destinationDisplay;
    }
}
