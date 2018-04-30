package com.castelcode.cruisecompanion.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.castelcode.cruisecompanion.agenda_entry.DateEntry;
import com.castelcode.cruisecompanion.agenda_entry.DateString;
import com.castelcode.cruisecompanion.R;

import java.util.ArrayList;
import java.util.TreeMap;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private CallbackInterface mCallback;

    private Context mContext;
    private ArrayList<DateString> mListDataHeader; //Header title dates

    private TreeMap<DateString, ArrayList<DateEntry>> mListDataChild;

    public interface CallbackInterface{

        /**
         * Callback invoked when clicked
         */
        void onHandleSelection(String dateText, int groupPos);
        void onHandleExpansionContraction(int pos);
        void onHandleItemSelection(int groupPos, DateEntry entry);
    }

    public ExpandableListAdapter(Context context, ArrayList<DateString> listDataHeader,
                                 TreeMap<DateString, ArrayList<DateEntry>> listChildDate){
        mContext = context;
        mListDataHeader = listDataHeader;
        mListDataChild = listChildDate;
        mCallback = (CallbackInterface)context;
    }

    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListDataChild.get(mListDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListDataChild.get(mListDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final DateString headerTitleAsDateString = (DateString) getGroup(groupPosition);
        final String headerTitle = headerTitleAsDateString.getDateString();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.list_group, parent, false);
        }

        TextView labelListHeader = (TextView) convertView.findViewById(R.id.date_header);
        labelListHeader.setText(headerTitle);
        labelListHeader.setOnClickListener((View v) ->
                mCallback.onHandleExpansionContraction(groupPosition));
        ImageButton button = (ImageButton) convertView.findViewById(R.id.add_agenda_entry_button);
        button.setOnClickListener((View v) ->
                mCallback.onHandleSelection(headerTitle, groupPosition));

        if(getChildrenCount(groupPosition) != 0){
            labelListHeader.setTypeface(null, Typeface.BOLD_ITALIC);
        }
        else{
            labelListHeader.setTypeface(null, Typeface.NORMAL);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final DateEntry dateEntry = (DateEntry) getChild(groupPosition, childPosition);
        final int groupPosToSend = groupPosition;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        LinearLayout contentContainer =
                (LinearLayout) convertView.findViewById(R.id.content_container);

        contentContainer.setOnClickListener((View v) ->
                mCallback.onHandleItemSelection(groupPosToSend, dateEntry));

        TextView title = (TextView) convertView.findViewById(R.id.title_item);
        TextView time = (TextView) convertView.findViewById(R.id.time_item);
        TextView location = (TextView) convertView.findViewById(R.id.location_item);
        TextView description = (TextView) convertView.findViewById(R.id.description_item);
        title.setText(String.format(mContext.getResources().getString(R.string.title_prefix),
                dateEntry.getTitle()));
        time.setText(String.format(mContext.getResources().getString(R.string.time_prefix),
                dateEntry.getTime()));
        description.setText(String.format(mContext.getResources().getString(
                R.string.description_prefix), dateEntry.getDescription()));
        location.setText(String.format(mContext.getResources().getString(R.string.location_prefix),
                dateEntry.getLocation()));

        return  convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
