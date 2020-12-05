package com.castelcode.travelcompanion.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.media.Rating;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.log_entry_add_activity.LogEntry;
import com.castelcode.travelcompanion.trip_checklists.Checklist;
import com.castelcode.travelcompanion.trip_checklists.ChecklistItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChecklistItemsBaseAdapter extends BaseAdapter {
    private static ArrayList<ChecklistItem> checklistItemsArrayList;

    private LayoutInflater mInflater;
    private Context mContext;
    private Boolean isRateable;

    public ChecklistItemsBaseAdapter(Context context,
                                     ArrayList<ChecklistItem> results,
                                     Boolean isRateable) {
        checklistItemsArrayList = results;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.isRateable = isRateable;
    }

    public int getCount() {
        return checklistItemsArrayList.size();
    }

    public Object getItem(int position) {
        return checklistItemsArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        ChecklistItemsBaseAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.checklist_item_list_view_item, null);
            holder = new ChecklistItemsBaseAdapter.ViewHolder();
            holder.title = convertView.findViewById(R.id.checklist_item_title);
            holder.checkBox = convertView.findViewById(R.id.checklist_item_checkbox);
            holder.ratingBar = convertView.findViewById(R.id.ratingBar);
            convertView.setTag(holder);
        } else {
            holder = (ChecklistItemsBaseAdapter.ViewHolder) convertView.getTag();
        }
        String title = checklistItemsArrayList.get(position).getItemTitle();
        holder.title.setText(title);
        holder.title.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Delete Item");
            builder.setMessage("Are you sure you want to delete the item: "
                    + checklistItemsArrayList.get(position).getItemTitle());
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                checklistItemsArrayList.remove(
                        checklistItemsArrayList.get(position));
                notifyDataSetChanged();
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
            builder.show();
            return true;
        });
        holder.checkBox.setChecked(checklistItemsArrayList.get(position).getCheckedState());
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isPressed()) {
                checklistItemsArrayList.get(position).toggleCheckedState();
            }
        });
        if (isRateable) {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(checklistItemsArrayList.get(position).getRating());
            LayerDrawable layerDrawable = (LayerDrawable) holder.ratingBar.getProgressDrawable();

            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(0)),
                    Color.WHITE);  // Empty star
            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(1)),
                    Color.rgb(255,216,16)); // Partial star
            DrawableCompat.setTint(DrawableCompat.wrap(layerDrawable.getDrawable(2)),
                    Color.rgb(255,216,16));
            holder.ratingBar.setOnRatingBarChangeListener((ratingBar, value, fromTouch) -> {
                if (fromTouch) {
                    checklistItemsArrayList.get(position).setRating(value);
                }
            });
        }
        else {
            holder.ratingBar.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        CheckBox checkBox;
        RatingBar ratingBar;
    }

}
