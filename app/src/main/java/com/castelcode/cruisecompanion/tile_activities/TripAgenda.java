package com.castelcode.cruisecompanion.tile_activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.castelcode.cruisecompanion.agenda_entry.DateEntry;
import com.castelcode.cruisecompanion.agenda_entry.DateEntryActivity;
import com.castelcode.cruisecompanion.agenda_entry.DateString;
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.adapters.ExpandableListAdapter;
import com.castelcode.cruisecompanion.share_activity.ShareCruiseItem;
import com.castelcode.cruisecompanion.share_activity.SupportedShareItemTypes;
import com.castelcode.cruisecompanion.utils.DateStringUtil;
import com.castelcode.cruisecompanion.utils.DeviceUuidFactory;
import com.castelcode.cruisecompanion.utils.TreeMapConverter;
import com.castelcode.cruisecompanion.utils.TripAgendaConstants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TripAgenda extends AppCompatActivity implements View.OnClickListener,
    DatePicker.OnDateChangedListener, ExpandableListAdapter.CallbackInterface{
    private static final String TRIP_AGENDA_TAG = "TripAgenda";

    private static final String STRING_PREFERENCE_NOT_FOUND = "";
    private static final int DATE_ENTRY_CREATION = 1;
    private static final int TRIP_AGENDA_SHARE = 2;

    public static final String ITEM_TO_SHARE_NAME = "itemToShareName";
    public static final String ITEM_TO_SHARE_VALUE = "itemToShareValue";
    private static final String ALL_ITEMS = "all items";

    private FirebaseDatabase database;

    private DatabaseReference ref;
    private String deviceUUID;

    Button startDatePickerLauncher;
    Button endDatePickerLauncher;

    Calendar startDate;
    Calendar endDate;

    boolean startDateSet = false;

    ExpandableListAdapter listAdapter;
    ExpandableListView expandableListView;
    static ArrayList<DateString> listDateHeader;
    public static TreeMap<DateString, ArrayList<DateEntry>> listDateChildren;

    Button expandCollapseButton;
    Button shareAllButton;
    boolean expand = true;
    Type hashMapOfDateEnties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trip_agenda);
        hashMapOfDateEnties = new TypeToken<TreeMap<String, ArrayList<DateEntry>>>(){}.getType();

        startDatePickerLauncher = findViewById(R.id.start_date_picker_launcher);
        endDatePickerLauncher = findViewById(R.id.end_date_picker_launhcer);

        startDatePickerLauncher.setOnClickListener(this);
        endDatePickerLauncher.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        deviceUUID = new DeviceUuidFactory(this).getDeviceUuidAsString();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String dateString = sharedPref.getString(getString(R.string.date_key),
                STRING_PREFERENCE_NOT_FOUND);
        if(!dateString.equals(STRING_PREFERENCE_NOT_FOUND)){
            int year = DateStringUtil.getYear(dateString);
            int month = DateStringUtil.getMonth(dateString);
            int day = DateStringUtil.getDay(dateString);
            startDate = createCalendarFrom(month, day, year); //adjust the month
            handleStartDateSet(dateString);
        }
        shareAllButton = findViewById(R.id.share_all_button);
        shareAllButton.setOnClickListener(this);
        expandCollapseButton = findViewById(R.id.expand_all_button);
        expandCollapseButton.setOnClickListener(this);

        listDateHeader = new ArrayList<>();

        listDateChildren = new TreeMap<>();

        expandableListView = findViewById(R.id.list_of_dates);

        Gson gson = new Gson();
        String json = sharedPref.getString(getString(R.string.date_entries_hash_map), "");
        TreeMap<String, ArrayList<DateEntry>> readItemsWithStringKey =
                gson.fromJson(json, hashMapOfDateEnties);
        if(readItemsWithStringKey != null && readItemsWithStringKey.size() > 0) {
            TreeMap<DateString, ArrayList<DateEntry>> readItems =
                    TreeMapConverter.toDateStringKeyed(readItemsWithStringKey);
            if (readItems != null && readItems.size() > 0) {
                listDateChildren.clear();
                listDateHeader.clear();

                for (Object o : readItems.entrySet()) {
                    Map.Entry pair = (Map.Entry) o;
                    @SuppressWarnings("unchecked")
                    ArrayList<DateEntry> value = (ArrayList<DateEntry>) pair.getValue();
                    listDateChildren.put((DateString) pair.getKey(), value);
                    listDateHeader.add((DateString) pair.getKey());
                }
                listAdapter = new ExpandableListAdapter(this, listDateHeader, listDateChildren);
                expandableListView.setAdapter(listAdapter);
                String startDateString = listDateHeader.get(0).getDateString();
                if(dateString.equals(STRING_PREFERENCE_NOT_FOUND)
                        || !dateString.equals(startDateString)) {
                    startDatePickerLauncher.setText(DateStringUtil.dotToSlash(dateString));
                    startDate = createCalendarFrom(DateStringUtil.getMonth(dateString),
                            DateStringUtil.getDay(dateString),
                            DateStringUtil.getYear(dateString)); //adjust the month
                }

                startDatePickerLauncher.setClickable(false);
                String endDateString = listDateHeader.get(listDateHeader.size() -1).getDateString();
                endDatePickerLauncher.setText(endDateString);
                endDate = createCalendarFrom(DateStringUtil.getMonth(endDateString),
                        DateStringUtil.getDay(endDateString),
                        DateStringUtil.getYear(endDateString)); //adjust the month

            }
            expandCollapseButton.setVisibility(View.VISIBLE);
        }
        else{
            expandCollapseButton.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        setupAndroidToIOS();
    }

    private Calendar createCalendarFrom(int month, int day, int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return calendar;
    }

    private void handleStartDateSet(String dateString){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getResources().getString(R.string.date_key),
                dateString);
        editor.apply();
        startDatePickerLauncher.setText(DateStringUtil.dotToSlash(dateString));
        startDateSet = true;
    }

    private void handleEndDateSet(String dateString){
        endDatePickerLauncher.setText(dateString);
        listDateHeader.clear();
        @SuppressWarnings("unchecked")
        TreeMap<DateString, ArrayList<DateEntry>> listDateChildrenCopy =
                (TreeMap<DateString, ArrayList<DateEntry>>) listDateChildren.clone();
        listDateChildren.clear();

        Calendar startDateIterator = Calendar.getInstance();
        startDateIterator.set(Calendar.DAY_OF_MONTH, startDate.get(Calendar.DAY_OF_MONTH));
        startDateIterator.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
        startDateIterator.set(Calendar.YEAR, startDate.get(Calendar.YEAR));
        while(startDateIterator.before(endDate)){
            DateString keyString =
                    new DateString(DateStringUtil.calendarToString(startDateIterator));
            listDateHeader.add(keyString);
            if(listDateChildrenCopy.get(keyString) == null){
                listDateChildren.put(keyString, new ArrayList<>());
            }
            else {
                listDateChildren.put(keyString, listDateChildrenCopy.get(keyString));
            }
            startDateIterator.add(Calendar.DAY_OF_MONTH, 1);
        }
        DateString keyString =
                new DateString(DateStringUtil.calendarToString(startDateIterator));
        listDateHeader.add(keyString);
        if(listDateChildren.get(keyString) == null) {
            listDateChildren.put(keyString, new ArrayList<>());
        }

        listAdapter = new ExpandableListAdapter(this, listDateHeader, listDateChildren);
        expandableListView.setAdapter(listAdapter);

        expandCollapseButton.setVisibility(View.VISIBLE);
    }

    private void endDatePostCheck(Calendar calendar, String dateString){
        endDate = calendar;
        if(endDate.before(startDate)){
            endDate = null;
            Toast.makeText(this, "End date must be after the start date",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        handleEndDateSet(dateString);
    }

    private void setDate(int day, int month, int year, String dateType){
        final Calendar calendar = createCalendarFrom(month, day, year); //no adjustment to calendar (0 indexed)
        final String dateString = DateStringUtil.intToDateString(month, day, year); //adjust the text (1 indexed)
        if(dateType.equals(getResources().getString(R.string.select_start_date))){
            startDate = calendar;
            startDate.set(Calendar.MONTH, startDate.get(Calendar.MONTH));
            handleStartDateSet(dateString);
        }
        else{
            if(endDate != null){
                if(calendar.before(endDate)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.end_date_change_warning)
                        .setTitle(R.string.end_date_change_title)
                        .setPositiveButton(R.string.yes, (DialogInterface dialog, int which) ->
                                endDatePostCheck(calendar, dateString))
                        .setNegativeButton(R.string.no, (DialogInterface dialog, int which) -> {
                        });
                    builder.create().show();

                }
                else{
                    endDatePostCheck(calendar, dateString);
                }
            }
            else{
                endDatePostCheck(calendar, dateString);
            }
        }
    }

    private void createPickerDialog(String title, String positiveMessage, String negativeMessage,
                                    Calendar dateOfReference){
        final String dateType = title;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DatePicker picker = new DatePicker(this);
        if(endDate != null){
            picker.init(endDate.get(Calendar.YEAR),
                    endDate.get(Calendar.MONTH),
                    endDate.get(Calendar.DAY_OF_MONTH), this);
        }
        else if(dateOfReference != null){
            picker.init(dateOfReference.get(Calendar.YEAR),
                    dateOfReference.get(Calendar.MONTH) - 1,
                    dateOfReference.get(Calendar.DAY_OF_MONTH), this);
        }
        builder.setTitle(title)
                .setView(picker)
                .setPositiveButton(positiveMessage, (DialogInterface dialog, int which) -> {
                        int day = picker.getDayOfMonth();
                        int month = picker.getMonth() + 1;
                        int year = picker.getYear();
                        setDate(day, month, year, dateType);
                })
                .setNegativeButton(negativeMessage, null);
        builder.show();
    }

    @Override
    public void onClick(View v) {
        if(v == startDatePickerLauncher && startDate == null){
            createPickerDialog(getResources().getString(R.string.select_start_date),
                    getResources().getString(R.string.select),
                    getResources().getString(R.string.cancel), null);
        }
        else if(v == startDatePickerLauncher) {
            Toast.makeText(this, "Please change the start date in settings.",
                    Toast.LENGTH_SHORT).show();
        }
        else if(v == endDatePickerLauncher)
        {
            if(!startDateSet){
                Toast.makeText(this, "Please set the start date first",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                createPickerDialog(getResources().getString(R.string.select_end_date),
                        getResources().getString(R.string.select),
                        getResources().getString(R.string.cancel), startDate);
            }
        }
        else if(v == expandCollapseButton){
            if(expand){
                for(int i = 0; i < listDateChildren.size(); i++){
                    expandableListView.expandGroup(i);
                }
                expandCollapseButton.setText(getResources().getString(R.string.collapse_all_label));
            }
            else{
                for(int i = 0; i < listDateChildren.size(); i++){
                    expandableListView.collapseGroup(i);
                }
                expandCollapseButton.setText(getResources().getString(R.string.expand_all_label));
            }
            expand = !expand;
        }
        else if(v == shareAllButton) {
            Intent shareTripAgendaIntent = new Intent(this, ShareCruiseItem.class);
            shareTripAgendaIntent.putExtra(ITEM_TO_SHARE_NAME, ALL_ITEMS);
            shareTripAgendaIntent.putExtra(ShareCruiseItem.SHARE_ITEM_TYPE_NAME,
                    SupportedShareItemTypes.TRIP_AGENDA);
            StringBuilder builder = new StringBuilder();
            String stringToShare;
            for (Map.Entry<DateString, ArrayList<DateEntry>> entry: listDateChildren.entrySet()) {
                String dateString = entry.getKey().getDateString();
                builder.append(dateString).append("~");
                for(DateEntry dateEntry: entry.getValue()) {
                    builder.append(dateEntry.toShareableString()).append("~");
                }
                builder.append("~");
            }

            stringToShare = builder.toString();
            if(stringToShare.length() > 0) {
                stringToShare = stringToShare.substring(0, stringToShare.length() - 1);
            }
            shareTripAgendaIntent.putExtra(ITEM_TO_SHARE_VALUE, stringToShare);
            startActivityForResult(shareTripAgendaIntent, TRIP_AGENDA_SHARE);
        }
    }


    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onHandleSelection(String dateText, int groupPos) {
        Intent intent = new Intent(this, DateEntryActivity.class);
        intent.putExtra(getResources().getString(R.string.date_entry_date), dateText);
        intent.putExtra(getResources().getString(R.string.group_pos), groupPos);
        startActivityForResult(intent, DATE_ENTRY_CREATION);
    }

    @Override
    public void onHandleItemSelection(int groupPos, DateEntry dateEntry) {
        Intent intent = new Intent(this, DateEntryActivity.class);
        intent.putExtra(getResources().getString(R.string.date_entry_date), dateEntry.getDate());
        intent.putExtra(getResources().getString(R.string.agenda_entry_item), dateEntry);
        intent.putExtra(getResources().getString(R.string.group_pos), groupPos);
        startActivityForResult(intent, DATE_ENTRY_CREATION);
    }

    @Override
    public void onHandleExpansionContraction(int pos) {
        if(expandableListView.isGroupExpanded(pos)){
            expandableListView.collapseGroup(pos);
        }
        else{
            expandableListView.expandGroup(pos);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();

        Gson gson = new Gson();
        TreeMap<String, ArrayList<DateEntry>> stringKeyed =
                TreeMapConverter.toStringKeyed(listDateChildren);
        String json = gson.toJson(stringKeyed, hashMapOfDateEnties);
        editor.putString(getString(R.string.date_entries_hash_map), json);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == DATE_ENTRY_CREATION) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if(data.getBooleanExtra(getResources().getString(R.string.delete), false)){
                    DateEntry entryToDelete = (DateEntry) data.getSerializableExtra(
                            getResources().getString(R.string.agenda_entry_item));
                    DateString date = new DateString(entryToDelete.getDate());
                    listDateChildren.get(date).remove(entryToDelete);
                    listAdapter.notifyDataSetChanged();
                }
                else{
                    DateEntry newEntry = (DateEntry) data.getSerializableExtra(
                            getResources().getString(R.string.agenda_entry_item));
                    DateString date = new DateString(newEntry.getDate());
                    int pos = data.getIntExtra(getResources().getString(R.string.group_pos), -1);
                    if(listDateChildren.get(date).contains(newEntry)){
                        listDateChildren.get(date).remove(newEntry);
                        listDateChildren.get(date).add(newEntry);
                    }
                    else {
                        listDateChildren.get(date).add(newEntry);
                    }
                    if(pos != -1)
                    {
                        expandableListView.expandGroup(pos);
                    }

                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void getDataForDevice(String remoteDevice){
        String sessionId = getUniqueSessionIdentifier(remoteDevice);
        ref.child(sessionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    String val = dataSnapshot.getValue().toString();
                    addItemsShared(val);
                }
                resetConnectionFor(sessionId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Do nothing
            }
        });
    }

    private String getUniqueSessionIdentifier(String remoteDevice) {
        if(remoteDevice.compareTo(deviceUUID) < 0) {
            return deviceUUID + "-" + remoteDevice;
        }
        else {
            return remoteDevice + "-" + deviceUUID;
        }
    }

    private void setupAndroidToIOS() {
        ref = database.getReference();
        Map<String, Boolean> selfEntry = new HashMap<>();
        selfEntry.put(deviceUUID, false);
        ref.child(TripAgendaConstants.USER_PATH).child(Build.MODEL).setValue(selfEntry);
        ref.child(TripAgendaConstants.USER_PATH).child(Build.MODEL).child(TripAgendaConstants.CONNECTION_KEY).setValue("");
        ref.child(TripAgendaConstants.USER_PATH).child(Build.MODEL).addChildEventListener(childEventListener);
    }

    private void resetConnectionFor(String sessionId) {
        ref.child(sessionId).removeValue((DatabaseError databaseError, DatabaseReference databaseReference) -> {
            if(databaseError != null) {
                System.out.println(databaseError.getMessage());
            }
            else {
                System.out.println(databaseReference.toString());
                System.out.println("Child Removed Correctly");
                ref.child(TripAgendaConstants.USER_PATH).child(deviceUUID)
                        .child(TripAgendaConstants.CONNECTION_KEY).setValue("", (DatabaseError databaseErrorInner, DatabaseReference databaseReferenceInner) -> {

                    if(databaseErrorInner != null) {
                        System.out.println(databaseErrorInner.getMessage());
                    }
                    else {
                        System.out.println(databaseReferenceInner.toString());
                        System.out.println("Value set to blank correctly");
                        ref.child(TripAgendaConstants.USER_PATH).child(deviceUUID).addChildEventListener(childEventListener);
                    }
                });
            }
        });
    }

    private void addItemsShared(String readMessage) {
        String[] objectsAsStrings = readMessage.split("~");
        String date = "";
        for (String objectsAsString : objectsAsStrings) {
            if (!objectsAsString.contains("|")) {
                date = objectsAsString;
            } else if (!date.isEmpty()) {
                String[] parts = objectsAsString.split("\\|");
                if (parts.length == 4) {
                    DateEntry dateEntry =
                            new DateEntry(parts[0], parts[1], parts[2], parts[3], date);
                    DateString dateString = new DateString(date);
                    if (listDateHeader.contains(dateString)) {
                        if (listDateChildren.containsKey(dateString)
                                && listDateChildren.get(dateString).contains(dateEntry)) {
                            listDateChildren.get(dateString).remove(dateEntry);
                        }
                        listDateChildren.get(dateString).add(dateEntry);
                    } else {
                        listDateHeader.add(dateString);
                        ArrayList<DateEntry> dateEntries = new ArrayList<>();
                        dateEntries.add(dateEntry);
                        listDateChildren.put(dateString, dateEntries);
                    }
                }
            }
        }
        listAdapter.notifyDataSetChanged();
        Log.d(TRIP_AGENDA_TAG, "MESSAGE " + readMessage + " RECEIVED");
    }


    private void handleDataTransfer(DataSnapshot snapshot) {
        String val;
        if(snapshot.getValue() != null) {
            val = snapshot.getValue().toString();
        }
        else {
            return;
        }
        System.out.println("DATA TRANSFER HERE with device id: " + val);
        getDataForDevice(val);
    }


    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
            //Do nothing
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            handleDataTransfer(dataSnapshot);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            //Do nothing
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            //Do nothing
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //Do nothing
        }
    };
}
