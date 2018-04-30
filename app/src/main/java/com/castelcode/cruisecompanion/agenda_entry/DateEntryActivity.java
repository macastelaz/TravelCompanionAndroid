package com.castelcode.cruisecompanion.agenda_entry;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.utils.TimeStringUtil;

public class DateEntryActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mTitle;
    Button mTime;
    EditText mLocation;
    EditText mDescription;

    TextView header;

    FloatingActionButton confirmButton;
    FloatingActionButton deleteButton;

    String dateString;
    int groupPos;

    DateEntry date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_entry);

        mTitle = (EditText) findViewById(R.id.date_entry_title_text);
        mTime = (Button) findViewById(R.id.date_entry_time_picker_launcher);
        mLocation = (EditText) findViewById(R.id.date_entry_location_text);
        mDescription = (EditText) findViewById(R.id.date_entry_description_text);

        header = (TextView) findViewById(R.id.date_entry_header);

        Intent intent = getIntent();
        dateString = intent.getStringExtra(
                getResources().getString(R.string.date_entry_date));
        String headerText = "New event for " + dateString;
        header.setText(headerText);

        groupPos = intent.getIntExtra(getResources().getString(R.string.group_pos), -1);

        mTime.setOnClickListener(this);

        confirmButton = (FloatingActionButton) findViewById(R.id.entry_confirm_button);
        confirmButton.setOnClickListener(this);
        deleteButton = (FloatingActionButton) findViewById(R.id.entry_delete_button);
        deleteButton.setOnClickListener(this);

        date = (DateEntry) intent.getSerializableExtra(
                getResources().getString(R.string.agenda_entry_item));
        if(date != null) {
            mTitle.setText(date.getTitle());
            if(!date.getTime().equals("")){
                mTime.setText(date.getTime());
            }
            mLocation.setText(date.getLocation());
            mDescription.setText(date.getDescription());
            deleteButton.setVisibility(View.VISIBLE);
        }
        else{
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    private void createTimePicker(String title, String positiveMessage, String negativeMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final TimePicker picker = new TimePicker(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
           picker.setMinute(0);
        }
        else {
            picker.setCurrentMinute(0);
        }
        builder.setTitle(title)
                .setView(picker)
                .setPositiveButton(positiveMessage, (DialogInterface dialog, int which) -> {
                        int hour;
                        int minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = picker.getHour();
                            minute = picker.getMinute();
                        }
                        else {
                            hour = picker.getCurrentHour();
                            minute = picker.getCurrentMinute();
                        }
                        setTime(hour, minute);
                })
                .setNegativeButton(negativeMessage, null);
        builder.show();
    }

    private void setTime(int hour, int minute){
        String timeString = TimeStringUtil.getSummaryString(
                TimeStringUtil.createTimeString(hour, minute));
        mTime.setText(timeString);
    }

    @Override
    public void onClick(View v) {
        if(v == mTime){
            createTimePicker(getResources().getString(R.string.time_picker_label),
                    getResources().getString(R.string.select),
                    getResources().getString(R.string.cancel));
        }
        else if(v == confirmButton){
            if(mTitle.getText().toString().equals("")){
                Toast.makeText(this, "This event must have a title",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Intent resultIntent = new Intent();
            String time = mTime.getText().toString().equals(
                    getResources().getString(R.string.time_picker_label)) ? "" :
                    mTime.getText().toString();
            DateEntry entry = new DateEntry(mTitle.getText().toString(), time,
                    mLocation.getText().toString(), mDescription.getText().toString(), dateString);
            resultIntent.putExtra(getResources().getString(R.string.agenda_entry_item), entry);
            resultIntent.putExtra(getResources().getString(R.string.group_pos), groupPos);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        else if(v == deleteButton){
            // delete here
            Intent resultIntent = new Intent();
            resultIntent.putExtra(getResources().getString(R.string.agenda_entry_item), date);
            resultIntent.putExtra(getResources().getString(R.string.group_pos), groupPos);
            resultIntent.putExtra(getResources().getString(R.string.delete), true);

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}
