package com.castelcode.travelcompanion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.log_entry_add_activity.LogEntry;
import java.util.ArrayList;

public class LogEntryItemsBaseAdapter extends BaseAdapter {
    private static ArrayList<LogEntry> logEntriesArrayList;

    private LayoutInflater mInflater;

    public LogEntryItemsBaseAdapter(Context context, ArrayList<LogEntry> results) {
        logEntriesArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return logEntriesArrayList.size();
    }

    public Object getItem(int position) {
        return logEntriesArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        LogEntryItemsBaseAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.log_entry_list_view_item, null);
            holder = new LogEntryItemsBaseAdapter.ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.logEntryTextPreview = (TextView)
                    convertView.findViewById(R.id.log_entry_preview);
            convertView.setTag(holder);
        } else {
            holder = (LogEntryItemsBaseAdapter.ViewHolder) convertView.getTag();
        }
        String dateText = logEntriesArrayList.get(position).getDateTimeAsString();
        holder.date.setText(dateText);
        String textPreview = logEntriesArrayList.get(position).getTextPreview();
        holder.logEntryTextPreview.setText(textPreview);

        return convertView;
    }

    private static class ViewHolder {
        TextView date;
        TextView logEntryTextPreview;
    }
}

