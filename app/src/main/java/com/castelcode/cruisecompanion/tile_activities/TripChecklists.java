package com.castelcode.cruisecompanion.tile_activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.adapters.ChecklistBaseAdapter;
import com.castelcode.cruisecompanion.trip_checklists.Checklist;
import com.castelcode.cruisecompanion.trip_checklists.trip_checklist_edit_activity.TripChecklistEdit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TripChecklists extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = "TRIP_CHECKLIST";
    private final static int EDIT_CHECKLIST = 1;

    FloatingActionButton addChecklistButton;

    private ArrayList<Checklist> checklists = new ArrayList<>();

    private ListView checklistsItemView;

    private ChecklistBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_checklists);

        addChecklistButton = findViewById(R.id.add_checklist_button);
        addChecklistButton.setOnClickListener(this);

        checklistsItemView = findViewById(R.id.checklist_list_view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Type listOfChecklists = new TypeToken<ArrayList<Checklist>>(){}.getType();
        Gson gson = new Gson();
        String json = sharedPref.getString(getString(R.string.checklist_entry), "");
        ArrayList<Checklist> readItems = gson.fromJson(json, listOfChecklists);
        if(readItems != null && readItems.size() > 0){
            checklists.clear();
            checklists.addAll(readItems);
        }

        adapter = new ChecklistBaseAdapter(this, checklists);
        checklistsItemView.setAdapter(adapter);

        checklistsItemView.setOnItemClickListener((AdapterView<?> parent, View view, int position,
                                                  long id) -> {
            Intent editNewChecklistIntent =
                    new Intent(this, TripChecklistEdit.class);
            editNewChecklistIntent.putExtra(getString(R.string.checklist_instance),
                    (Parcelable)checklists.get(position));
            startActivityForResult(editNewChecklistIntent, EDIT_CHECKLIST);

        });
        checklistsItemView.setOnItemLongClickListener((AdapterView<?> parent, View view,
                                                      final int position, long id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Checklist");
                builder.setMessage("Are you sure you want to delete the checklist: "
                    + checklists.get(position).getChecklistName());
            builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                checklists.remove(checklists.get(position));
                adapter.notifyDataSetChanged();
            });
            builder.setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
            builder.show();
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        Type listOfChecklists = new TypeToken<ArrayList<Checklist>>(){}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(checklists, listOfChecklists);
        editor.putString(getString(R.string.checklist_entry), json);
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        if(v == addChecklistButton){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Checklist Name");
            final EditText checklistNameInput = new EditText(this);
            checklistNameInput.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            final CheckBox isRateable = new CheckBox(this);
            final TextView isRateableLabel = new TextView(this);
            isRateableLabel.setText(R.string.include_rating_scale_label);
            final LinearLayout isRateableLayout = new LinearLayout(this);
            isRateableLayout.setOrientation(LinearLayout.HORIZONTAL);
            isRateableLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

            isRateableLayout.addView(isRateable);
            isRateableLayout.addView(isRateableLabel);

            isRateableLayout.setPadding(30, 0,0,0);
            final LinearLayout parentLayout = new LinearLayout(this);
            parentLayout.setOrientation(LinearLayout.VERTICAL);
            parentLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            parentLayout.addView(checklistNameInput);
            parentLayout.addView(isRateableLayout);

            builder.setView(parentLayout);
            builder.setPositiveButton("Create", (dialogInterface, i) -> createNewChecklist(
                    checklistNameInput.getText().toString(),
                    isRateable.isChecked()));
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
            builder.show();
        }
    }

    private void createNewChecklist(String checklistTitle, Boolean isRateable) {
        if (!checklistTitle.equals("")) {
            Checklist checklist = new Checklist(checklistTitle, isRateable);
            if (!checklists.contains(checklist)) {
                checklists.add(checklist);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CHECKLIST) {
            if (resultCode == RESULT_OK) {
                Checklist checklist =
                        data.getParcelableExtra(getString(R.string.checklist_instance));
                if (checklists.contains(checklist)) {
                    int index = checklists.indexOf(checklist);
                    checklists.remove(checklist);
                    checklists.add(index, checklist);
                }
                else {
                    // Should never happen because we don't allow editing of the checklist name yet.
                    checklists.add(checklist);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
