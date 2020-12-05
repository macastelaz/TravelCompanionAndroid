package com.castelcode.travelcompanion.tile_activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.adapters.ExpenseItemsBaseAdapter;
import com.castelcode.travelcompanion.expenses.Expense;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

public class Expenses extends AppCompatActivity implements View.OnClickListener,
        TextView.OnEditorActionListener{

    private static final String EXPENSE_KEY = "Expenses";

    private double runningTotal;

    private FloatingActionButton expenseAddButton;
    private FloatingActionButton expenseConfirmButton;

    private EditText expenseDescriptionEditText;
    private EditText expenseValueEditText;

    private ArrayList<Expense> expenses = new ArrayList<>();

    private ListView expenseItemsView;
    private ExpenseItemsBaseAdapter adapter;

    private TextView totalExpenses;

    private Locale defaultLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        setAllUIElementsAboveListview();
        checkSharedPreferences();
        setUpListView();
        hideEditBoxes();
        setUpTotalExpenses();
    }

    private void setAllUIElementsAboveListview(){
        setFloatingButtons();
        setEditTextFields();
        expenseItemsView = (ListView) findViewById(R.id.expense_list_view);
    }

    private void setFloatingButtons(){
        expenseAddButton = (FloatingActionButton)
                findViewById(R.id.add_expense_button);

        expenseAddButton.setVisibility(View.VISIBLE);
        expenseAddButton.setOnClickListener(this);


        expenseConfirmButton = (FloatingActionButton)
                findViewById(R.id.confirm_expense_button);

        expenseConfirmButton.setVisibility(View.INVISIBLE);

        expenseConfirmButton.setOnClickListener(this);
    }

    private void setEditTextFields(){
        expenseDescriptionEditText = (EditText) findViewById(R.id.expense_description);
        expenseValueEditText = (EditText) findViewById(R.id.expense_value);
        expenseValueEditText.setOnEditorActionListener(this);
    }

    private void checkSharedPreferences(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Type listOfExpenses = new TypeToken<ArrayList<Expense>>(){}.getType();
        Gson gson = new Gson();
        String json = sharedPref.getString(getString(R.string.expense_items), "");
        ArrayList<Expense> readItems = gson.fromJson(json, listOfExpenses);
        if(readItems != null && readItems.size() > 0){
            expenses.clear();
            for (Expense expense:readItems) {
                expenses.add(expense);
                runningTotal += expense.getCost();
            }
        }
        else{
            runningTotal = 0;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        Type listOfExpenses = new TypeToken<ArrayList<Expense>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(expenses, listOfExpenses);
        editor.putString(getString(R.string.expense_items), json);
        editor.apply();
    }

    private void hideEditBoxes(){
        final Context context = this;
        expenseItemsView.setOnItemClickListener((AdapterView<?> parent, View view, int position,
                                                 long id) ->
                clickHandler(position, context));
        expenseDescriptionEditText.setVisibility(View.INVISIBLE);
        expenseValueEditText.setVisibility(View.INVISIBLE);
    }

    private void showEditBoxes(){
        expenseItemsView.setOnItemClickListener(null);
        expenseDescriptionEditText.setVisibility(View.VISIBLE);
        expenseValueEditText.setVisibility(View.VISIBLE);
        expenseDescriptionEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(expenseDescriptionEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void clickHandler(int position, Context context){
        Object o = expenseItemsView.getItemAtPosition(position);
        final Expense expense = (Expense)o;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete the " +
                expense.getDescription().toLowerCase() + " expense?")
                .setTitle("Delete")
                .setPositiveButton("Delete", (DialogInterface dialog, int which) ->
                        deleteExpense(expense))
                .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> {
                        //Do Nothing
                });
        builder.create().show();
    }

    private void setUpListView(){
        adapter = new ExpenseItemsBaseAdapter(this, expenses);
        expenseItemsView.setAdapter(adapter);

        final Context context = this;
        expenseItemsView.setOnItemClickListener((AdapterView<?> parent, View view, int position,
            long id) ->
            clickHandler(position, context));
    }

    private void setUpTotalExpenses(){
        totalExpenses = (TextView) findViewById(R.id.total_expenses);
        defaultLocale = Locale.getDefault();
        String totalExpenseText = getResources().getString(
                R.string.total_expenses_prefix) +
                String.format(defaultLocale, "%.2f", runningTotal);
        totalExpenses.setText(totalExpenseText);
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }

        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if(v == expenseAddButton){
            expenseAddButton.setVisibility(View.INVISIBLE);
            expenseConfirmButton.setVisibility(View.VISIBLE);
            showEditBoxes();
        }
        else if(v == expenseConfirmButton){
            addExpense();
        }
    }

    private void deleteExpense(Expense expense){
        expenses.remove(expense);
        runningTotal -= expense.getCost();
        updateTotalExpenseText();
        adapter.notifyDataSetChanged();
    }

    private void updateTotalExpenseText(){
        String expenseText = getResources().getString(
                R.string.total_expenses_prefix) +
                String.format(defaultLocale, "%.2f", runningTotal);
        totalExpenses.setText(expenseText);
    }

    private void addExpense(){
        expenseAddButton.setVisibility(View.VISIBLE);
        expenseConfirmButton.setVisibility(View.INVISIBLE);
        Expense expense;
        String descriptionText = expenseDescriptionEditText.getText().toString();
        String valueText = expenseValueEditText.getText().toString();
        if(!descriptionText.equals("") && !valueText.equals("")){
            try{
                double value = Double.valueOf(valueText);
                runningTotal += value;
                updateTotalExpenseText();
                expense = new Expense(descriptionText, value);
                expenses.add(expense);
                adapter.notifyDataSetChanged();
            }
            catch (Exception ex){
                Log.e(EXPENSE_KEY, ex.toString());
            }
        }
        expenseDescriptionEditText.setText("");
        expenseValueEditText.setText("");
        hideKeyboard();
        hideEditBoxes();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            addExpense();
            return true;
        }
        return false;
    }
}
