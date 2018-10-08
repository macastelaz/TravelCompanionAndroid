package com.castelcode.cruisecompanion.tile_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.TripChecklistProtos;
import com.castelcode.cruisecompanion.adapters.ChecklistBaseAdapter;
import com.castelcode.cruisecompanion.share_activity.ShareCruiseItem;
import com.castelcode.cruisecompanion.share_activity.SupportedShareItemTypes;
import com.castelcode.cruisecompanion.trip_checklists.Checklist;
import com.castelcode.cruisecompanion.trip_checklists.ChecklistItem;
import com.castelcode.cruisecompanion.trip_checklists.trip_checklist_edit_activity.TripChecklistEdit;
import com.castelcode.cruisecompanion.utils.DeviceUuidFactory;
import com.castelcode.cruisecompanion.utils.TripInfoConstants;
import com.google.common.collect.ImmutableList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripChecklists extends AppCompatActivity implements View.OnClickListener{

    private final static String CHECKLIST_TAG = "TRIP_CHECKLIST";
    private final static int EDIT_CHECKLIST = 1;
    private final static int SHARE_CHECKLIST = 2;

    public static final String ITEM_TO_SHARE_NAME = "itemToShareName";
    public static final String ITEM_TO_SHARE_VALUE = "itemToShareValue";
    private static final String ALL_ITEMS = "all items";

    FloatingActionButton addChecklistButton;
    Button shareAllButton;

    public static ArrayList<Checklist> checklists = new ArrayList<>();

    private static ChecklistBaseAdapter adapter;

    private FirebaseDatabase database;

    private DatabaseReference ref;
    private String deviceUUID;

    private static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_checklists);

        database = FirebaseDatabase.getInstance();
        deviceUUID = new DeviceUuidFactory(this).getDeviceUuidAsString();

        shareAllButton = findViewById(R.id.share_all_button);
        shareAllButton.setOnClickListener(this);

        addChecklistButton = findViewById(R.id.add_checklist_button);
        addChecklistButton.setOnClickListener(this);

        ListView checklistsItemView = findViewById(R.id.checklist_list_view);

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
    public void onPause() {
        super.onPause();
        ref.child(TripInfoConstants.USER_PATH).child(Build.MODEL).removeValue();
    }

    private void setupAndroidToIOS() {
        ref = database.getReference();
        Map<String, Boolean> selfEntry = new HashMap<>();
        selfEntry.put(deviceUUID, false);
        ref.child(TripInfoConstants.USER_PATH).child(Build.MODEL).setValue(selfEntry);
        ref.child(TripInfoConstants.USER_PATH).child(Build.MODEL)
                .child("tripChecklistConnection")
                .setValue("");
        ref.child(TripInfoConstants.USER_PATH)
                .child(Build.MODEL).addChildEventListener(childEventListener);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setupAndroidToIOS();
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
        } else if (v == shareAllButton) {
            Intent shareTripChecklistIntent = new Intent(this, ShareCruiseItem.class);
            shareTripChecklistIntent.putExtra(ITEM_TO_SHARE_NAME, ALL_ITEMS);
            shareTripChecklistIntent.putExtra(ShareCruiseItem.SHARE_ITEM_TYPE_NAME,
                    SupportedShareItemTypes.TRIP_CHECKLISTS);
            String stringToShare =
                    createStringForSharedChecklistInfo(
                            TripChecklistProtos.Checklists.newBuilder()
                                    .addAllChecklists(createChecklistProtos(checklists))
                                    .build());
            shareTripChecklistIntent.putExtra(ITEM_TO_SHARE_VALUE, stringToShare);
            startActivityForResult(shareTripChecklistIntent, SHARE_CHECKLIST);
        }
    }

    private static ImmutableList<TripChecklistProtos.Checklist> createChecklistProtos(
            ArrayList<Checklist> checklistsIn) {
        ArrayList<TripChecklistProtos.Checklist> checklistArrayList = new ArrayList<>();
        for (Checklist checklist : checklistsIn) {
            checklistArrayList.add(
                    TripChecklistProtos.Checklist.newBuilder()
                            .setName(checklist.getChecklistName())
                            .setRateable(checklist.getIsRateable())
                            .addAllItems(createChecklistItemsProto(checklist.getItems()))
                            .build());
        }
        return ImmutableList.copyOf(checklistArrayList);
    }

    private static ImmutableList<TripChecklistProtos.ChecklistItem> createChecklistItemsProto(
            ArrayList<ChecklistItem> checklistItemsIn) {
        ArrayList<TripChecklistProtos.ChecklistItem> checklistItemsArrayList = new ArrayList<>();
        for (ChecklistItem checklistItem : checklistItemsIn) {
            checklistItemsArrayList.add(
                    TripChecklistProtos.ChecklistItem.newBuilder()
                            .setTitle(checklistItem.getItemTitle())
                            .setChecked(checklistItem.getCheckedState())
                            .setRating(checklistItem.getRating())
                            .setOriginalPosition(checklistItem.getOriginalPosition())
                            .build());
        }
        return ImmutableList.copyOf(checklistItemsArrayList);
    }

    private static String createStringForSharedChecklistInfo(
            TripChecklistProtos.Checklists checklistsIn) {
        String sharedTripInfoString = "";
        try {
            sharedTripInfoString = JsonFormat.printer().print(checklistsIn);
        } catch (InvalidProtocolBufferException ex) {
            System.out.println("Exception printing item to JSON: " + ex.toString());
        }
        return sharedTripInfoString;
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

    private void handleDataTransfer(DataSnapshot snapshot) {
        String val;
        if(snapshot.getValue() != null) {
            val = snapshot.getValue().toString();
        }
        else {
            return;
        }
        System.out.println("DATA TRANSFER HERE with device id: " + val);
        getDataForDevice(this, val);
    }

    private void getDataForDevice(Context context, String remoteDevice){
        String sessionId = getUniqueSessionIdentifier(remoteDevice);
        ref.child(sessionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    String val = dataSnapshot.getValue().toString();
                    addItemsShared(context, val);
                }
                resetConnectionFor(sessionId);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Do nothing
            }
        });
    }

    private static void addItemsShared(Context context, String readMessage) {
        TripChecklistProtos.Checklists.Builder sharedChecklists =
                TripChecklistProtos.Checklists.newBuilder();
        try {
            JsonFormat.parser().merge(readMessage, sharedChecklists);
        } catch (InvalidProtocolBufferException ex) {
            System.out.println("Failed to parse proto with exception: " + ex.toString());
        }
        boolean duplicatedFound = false;
        for (TripChecklistProtos.Checklist checklist : sharedChecklists.getChecklistsList()) {
            Checklist checklistToUse = new Checklist(checklist);
            if (!checklists.contains(checklistToUse)) {
                checklists.add(checklistToUse);
                adapter.notifyDataSetChanged();
            } else {
                duplicatedFound = true;
            }
        }
        if (duplicatedFound) {
            showToast(context, "Item shared was a duplicate", Toast.LENGTH_SHORT);
        }
        Log.d(CHECKLIST_TAG, "MESSAGE " + readMessage + " RECEIVED");
    }

    private static void showToast(Context context, String message, int duration) {
        if (toast == null) {
            toast = Toast.makeText(context, message, duration);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    private String getUniqueSessionIdentifier(String remoteDevice) {
        if(remoteDevice.compareTo(deviceUUID) < 0) {
            return deviceUUID + "-" + remoteDevice;
        }
        else {
            return remoteDevice + "-" + deviceUUID;
        }
    }

    private void resetConnectionFor(String sessionId) {
        ref.child(sessionId).removeValue((DatabaseError databaseError, DatabaseReference databaseReference) -> {
            if(databaseError != null) {
                System.out.println(databaseError.getMessage());
            }
            else {
                System.out.println(databaseReference.toString());
                System.out.println("Child Removed Correctly");
                ref.child(TripInfoConstants.USER_PATH).child(deviceUUID)
                        .child(TripInfoConstants.CONNECTION_KEY).setValue("", (DatabaseError databaseErrorInner, DatabaseReference databaseReferenceInner) -> {

                    if(databaseErrorInner != null) {
                        System.out.println(databaseErrorInner.getMessage());
                    }
                    else {
                        System.out.println(databaseReferenceInner.toString());
                        System.out.println("Value set to blank correctly");
                        ref.child(TripInfoConstants.USER_PATH).child(deviceUUID).addChildEventListener(childEventListener);
                    }
                });
            }
        });
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
