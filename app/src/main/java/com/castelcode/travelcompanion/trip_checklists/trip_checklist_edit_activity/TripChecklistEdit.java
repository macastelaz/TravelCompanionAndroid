package com.castelcode.travelcompanion.trip_checklists.trip_checklist_edit_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.adapters.ChecklistItemsBaseAdapter;
import com.castelcode.travelcompanion.trip_checklists.Checklist;
import com.castelcode.travelcompanion.trip_checklists.ChecklistItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TripChecklistEdit extends AppCompatActivity implements View.OnClickListener {

    TextView checklistTitle;
    ListView checklistItems;
    Button addChecklistItem;
    Checklist checklistBeingEdited;
    ArrayList<ChecklistItem> items;
    Spinner sortOptions;

    private ChecklistItemsBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip_checklist);
        checklistBeingEdited =
                getIntent().getParcelableExtra(getString(R.string.checklist_instance));
        items = checklistBeingEdited.getItems();
        checklistTitle = findViewById(R.id.checklist_title);
        checklistItems = findViewById(R.id.checklist_items_list_view);
        sortOptions = findViewById(R.id.sort_preference);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, R.layout.order_option_spinner);
        sortAdapter.setDropDownViewResource(R.layout.order_option_spinner_dropdown);
        sortOptions.setAdapter(sortAdapter);
        sortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        System.out.println("Original Order");
                        Collections.sort(items,
                                (item, t1) -> item.getOriginalPosition() - t1.getOriginalPosition());
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        System.out.println("Name Asc.");
                        Collections.sort(items,
                                (item, t1) -> item.getItemTitle().compareTo(t1.getItemTitle()));
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        System.out.println("Name Desc.");
                        Collections.sort(items,
                                (item, t1) -> t1.getItemTitle().compareTo(item.getItemTitle()));
                        adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        System.out.println("Rating Asc.");
                        Collections.sort(items,
                                (item, t1) -> {
                                    if (t1.getRating() == item.getRating()) {
                                        return item.getItemTitle().compareTo(t1.getItemTitle());
                                    }
                                    return Math.round(item.getRating() - t1.getRating());
                                });
                        adapter.notifyDataSetChanged();
                        break;
                    case 4:
                        System.out.println("Rating Desc.");
                        Collections.sort(items,
                                (item, t1) -> {
                                    if (t1.getRating() == item.getRating()) {
                                        return item.getItemTitle().compareTo(t1.getItemTitle());
                                    }
                                    return Math.round(t1.getRating() - item.getRating());
                                });
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        System.out.println("WTF: This shouldn't happen");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addChecklistItem = findViewById(R.id.add_checklist_item_button);
        addChecklistItem.setOnClickListener(this);

        checklistTitle.setText(checklistBeingEdited.getChecklistName());

        adapter = new ChecklistItemsBaseAdapter(this,
                items,
                checklistBeingEdited.getIsRateable());
        checklistItems.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(addChecklistItem)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Checklist Item");
            builder.setMessage("What is the name of the checklist item you wish to add?");
            final EditText checklistNameInput = new EditText(this);
            checklistNameInput.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(checklistNameInput);
            builder.setPositiveButton("Add",
                    (dialogInterface, i) ->
                            createNewChecklistItem(checklistNameInput.getText().toString()));
            builder.setNegativeButton("Cancel",
                    (dialogInterface, i) -> dialogInterface.cancel());
            builder.show();
        }
    }

    private void createNewChecklistItem(String checklistTitle) {
        if (!checklistTitle.equals("")) {
            ChecklistItem checklistItem = new ChecklistItem(checklistTitle,
                    items.size());
            if (checklistBeingEdited.addChecklistItem(checklistItem)) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.checklist_instance), (Parcelable)checklistBeingEdited);
        setResult(RESULT_OK, intent);
        finish();
    }
}
