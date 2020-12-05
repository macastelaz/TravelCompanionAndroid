package com.castelcode.travelcompanion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.log_entry_add_activity.LogEntry;
import com.castelcode.travelcompanion.trip_checklists.Checklist;

import java.util.ArrayList;

public class ChecklistBaseAdapter extends BaseAdapter {
    private static ArrayList<Checklist> checklistArrayList;

    private LayoutInflater mInflater;

    public ChecklistBaseAdapter(Context context, ArrayList<Checklist> results) {
        checklistArrayList = results;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return checklistArrayList.size();
    }

    public Object getItem(int position) {
        return checklistArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        ChecklistBaseAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.checklist_list_view_item, null);
            holder = new ChecklistBaseAdapter.ViewHolder();
            holder.title = convertView.findViewById(R.id.checklist_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ChecklistBaseAdapter.ViewHolder) convertView.getTag();
        }
        String title = checklistArrayList.get(position).getChecklistName();
        holder.title.setText(title);

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
    }
}
