package com.castelcode.travelcompanion.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.expenses.Expense;

import java.util.ArrayList;
import java.util.Locale;

public class ExpenseItemsBaseAdapter extends BaseAdapter {

    private static ArrayList<Expense> expenseItemsArrayList;

    private LayoutInflater mInflater;
    private Locale defaultLocale;

    public ExpenseItemsBaseAdapter(Context context, ArrayList<Expense> results) {
        expenseItemsArrayList = results;
        mInflater = LayoutInflater.from(context);
        defaultLocale = Locale.getDefault();
    }

    public int getCount() {
        return expenseItemsArrayList.size();
    }

    public Object getItem(int position) {
        return expenseItemsArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.expense_list_view_item, null);
            holder = new ViewHolder();
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.value = (TextView)
                    convertView.findViewById(R.id.value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.description.setText(expenseItemsArrayList.get(position).getDescription());
        String costString = "$" + String.format(defaultLocale, "%.2f",
                expenseItemsArrayList.get(position).getCost());
        holder.value.setText(costString);

        return convertView;
    }

    private static class ViewHolder {
        TextView description;
        TextView value;
    }
}
