package com.castelcode.cruisecompanion.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.utils.DeviceItem;

import java.util.ArrayList;

public class BluetoothDevicesAdapter extends BaseAdapter {
    private static ArrayList<DeviceItem> deviceItemsArrayList;

    private LayoutInflater mInflater;

    public BluetoothDevicesAdapter(Context context, ArrayList<DeviceItem> results) {
        deviceItemsArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return deviceItemsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceItemsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BluetoothDevicesAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bluetooth_device_item, null);
            holder = new BluetoothDevicesAdapter.ViewHolder();
            holder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
            holder.deviceAddress = (TextView)
                    convertView.findViewById(R.id.device_address);
            convertView.setTag(holder);
        } else {
            holder = (BluetoothDevicesAdapter.ViewHolder) convertView.getTag();
        }
        String nameText = "Name: " + deviceItemsArrayList.get(position).getDeviceName();
        holder.deviceName.setText(nameText);
        String addressText = "Address: " +
                deviceItemsArrayList.get(position).getAddress();
        holder.deviceAddress.setText(addressText);
        return convertView;
    }

    private static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }
}
