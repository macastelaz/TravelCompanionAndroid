package com.castelcode.cruisecompanion.tile_activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.castelcode.cruisecompanion.bluetooth.BluetoothShareService;
import com.castelcode.cruisecompanion.bluetooth.Constants;
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.adapters.InfoItemsBaseAdapter;
import com.castelcode.cruisecompanion.adapters.InfoSerializerAdapter;
import com.castelcode.cruisecompanion.trip_info_add_activity.AddTripInfoItem;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.Info;
import com.castelcode.cruisecompanion.trip_info_share_activity.ShareTripInfo;
import com.castelcode.cruisecompanion.utils.DeviceUuidFactory;
import com.castelcode.cruisecompanion.utils.NotificationUtil;
import com.castelcode.cruisecompanion.utils.SettingsConstants;
import com.castelcode.cruisecompanion.utils.SharedPreferencesManager;
import com.castelcode.cruisecompanion.utils.TripInfoConstants;
import com.castelcode.cruisecompanion.utils.WakefulReceiver;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.gsonfire.GsonFireBuilder;

public class TripInformation extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String TRIP_INFORMATION_TAG = "TripInformation";
    public static final String ITEM_TO_SHARE_NAME = "itemToShareName";
    public static final String ITEM_TO_SHARE_VALUE = "itemToShareValue";
    private static final String ALL_ITEMS = "all items";
    public static final String ITEM_TO_SHARE_POSITION = "itemToSharePosition";

    private FirebaseDatabase database;

    private DatabaseReference ref;
    private String deviceUUID;

    private static final int TRIP_INFORMATION_ITEM_ADD = 1;
    private static final int TRIP_INFORMATION_SHARE = 2;

    private FloatingActionButton addTripInfoButton;

    public static ArrayList<Info> infoItems = new ArrayList<>();

    private ListView infoItemsView;
    private static InfoItemsBaseAdapter adapter;

    private Button shareAllButton;

    public static void resetTripInformationItems() {
        infoItems.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_information);

        database = FirebaseDatabase.getInstance();
        deviceUUID = new DeviceUuidFactory(this).getDeviceUuidAsString();

        addTripInfoButton = (FloatingActionButton) findViewById(R.id.add_info_button);
        addTripInfoButton.setOnClickListener(this);

        shareAllButton = (Button) findViewById(R.id.share_all_button);
        shareAllButton.setOnClickListener(this);

        infoItemsView = (ListView) findViewById(R.id.info_list_view);

        adapter = new InfoItemsBaseAdapter(this, infoItems);

        infoItemsView.setAdapter(adapter);

        infoItemsView.setOnItemClickListener((AdapterView<?> parent, View view, int position,
                long id) -> {
            Object o = infoItemsView.getItemAtPosition(position);
            Info info = (Info)o;
            Intent editTripInfoItemIntent = new Intent(parent.getContext(),
                    AddTripInfoItem.class);
            editTripInfoItemIntent.putExtra(getResources().getString(R.string.info_item),
                    (Parcelable) info);
            if(info instanceof CruiseInfo){
                editTripInfoItemIntent.putExtra(getResources().getString(R.string.info_type),
                        getResources().getString(R.string.cruise));
            }
            else if(info instanceof FlightInfo){
                editTripInfoItemIntent.putExtra(getResources().getString(R.string.info_type),
                        getResources().getString(R.string.flight));
            }
            else if(info instanceof HotelInfo){
                editTripInfoItemIntent.putExtra(getResources().getString(R.string.info_type),
                        getResources().getString(R.string.hotel));
            }
            else if(info instanceof BusInfo){
                editTripInfoItemIntent.putExtra(getResources().getString(R.string.info_type),
                        getResources().getString(R.string.bus));
            }
            startActivityForResult(editTripInfoItemIntent, TRIP_INFORMATION_ITEM_ADD);
            // Initialize the BluetoothShareService to perform bluetooth connections
        });
        final Context context = this;
        infoItemsView.setOnItemLongClickListener((AdapterView<?> parent, View view, int position,
                long id) -> {
            Intent shareTripInfoIntent = new Intent(context, ShareTripInfo.class);
            shareTripInfoIntent.putExtra(ITEM_TO_SHARE_NAME,
                    infoItems.get(position).toString());
            shareTripInfoIntent.putExtra(ITEM_TO_SHARE_VALUE,
                    infoItems.get(position).toShareableString());
            shareTripInfoIntent.putExtra(ITEM_TO_SHARE_POSITION, position);
            startActivityForResult(shareTripInfoIntent, TRIP_INFORMATION_SHARE);
            return true;
        });
        if(BluetoothAdapter.getDefaultAdapter() != null ) { // device supports bluetooth
            /*
             Member object for the chat services
            */
            BluetoothShareService mShareService = new BluetoothShareService(mHandler);
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mShareService.getState() == BluetoothShareService.STATE_NONE) {
                // Start the bluetooth chat services
                mShareService.start();
            }
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        GsonFireBuilder builder = new GsonFireBuilder().registerTypeSelector(Info.class,
                (JsonElement readElement) -> {
                    String type = readElement.getAsJsonObject().get("type").getAsString();
                    switch (type){
                        case "Hotel":
                            return HotelInfo.class;
                        case "Cruise":
                            return CruiseInfo.class;
                        case "Flight":
                            return FlightInfo.class;
                        case "Bus":
                            return BusInfo.class;
                        default:
                            return null;
                    }
                });
        Gson gson = builder.createGson();
        String json = sharedPref.getString(getString(R.string.info_items), "");
        Type listType = new TypeToken<ArrayList<Info>>(){}.getType();
        ArrayList<Info> readItems = gson.fromJson(json, listType);
        if(readItems != null && readItems.size() > 0){
            for (Info infoItem:readItems){
                if(!infoItems.contains(infoItem)) {
                    infoItems.add(infoItem);
                }
                else{
                    infoItems.remove(infoItem); //based on name and conf number
                    infoItems.add(infoItem); // other fields might have changed.
                }
            }
            adapter.notifyDataSetChanged();
        }
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
        ref.child(TripInfoConstants.USER_PATH).child(Build.MODEL).child(TripInfoConstants.CONNECTION_KEY).setValue("");
        ref.child(TripInfoConstants.USER_PATH).child(Build.MODEL).addChildEventListener(childEventListener);
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
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    String val = dataSnapshot.getValue().toString();
                    addItemsShared(context, val);
                }
                resetConnectionFor(sessionId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Do nothing
            }
        });
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

    private String getUniqueSessionIdentifier(String remoteDevice) {
        if(remoteDevice.compareTo(deviceUUID) < 0) {
            return deviceUUID + "-" + remoteDevice;
        }
        else {
            return remoteDevice + "-" + deviceUUID;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        setupAndroidToIOS();
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if(v == addTripInfoButton){
            Intent createTripInfoItemIntent = new Intent(this, AddTripInfoItem.class);
            startActivityForResult(createTripInfoItemIntent, TRIP_INFORMATION_ITEM_ADD);
        }
        else if (v == shareAllButton) {
            Intent shareTripInfoIntent = new Intent(this, ShareTripInfo.class);
            shareTripInfoIntent.putExtra(ITEM_TO_SHARE_NAME, ALL_ITEMS);
            StringBuilder builder = new StringBuilder();
            String stringToShare;
            for(int i = 0; i < infoItems.size(); i ++){
                builder.append(infoItems.get(i).toShareableString()).append("\n");
            }
            stringToShare = builder.toString();
            if(stringToShare.length() > 0) {
                stringToShare = stringToShare.substring(0, stringToShare.length() - 1);
            }
            shareTripInfoIntent.putExtra(ITEM_TO_SHARE_VALUE, stringToShare);
            startActivityForResult(shareTripInfoIntent, TRIP_INFORMATION_SHARE);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Info.class,
                new InfoSerializerAdapter()).create();
        String json = gson.toJson(infoItems);
        editor.putString(getString(R.string.info_items), json);
        editor.apply();
        //HomePage.cruise.setTripInfo(infoItems);
        //HomePage.cruise.save(new CruiseIO(this.getFilesDir()));
    }

    private static void addItemsShared(Context context, String readMessage) {
        String[] objectsAsStrings = readMessage.split("\\r?\\n");
        for (String objectsAsString : objectsAsStrings) {
            String[] parts = objectsAsString.split("\\|");
            Info info;
            switch (parts[0]) {
                case CruiseInfo.typeOfItem:
                    info = new CruiseInfo(objectsAsString);
                    break;
                case FlightInfo.typeOfItem:
                    info = new FlightInfo(objectsAsString);
                    break;
                case HotelInfo.typeOfItem:
                    info = new HotelInfo(objectsAsString);
                    break;
                case BusInfo.typeOfItem:
                    info = new BusInfo(objectsAsString);
                    break;
                default:
                    info = null;
                    break;
            }
            if (info != null) {
                if (!infoItems.contains(info)) {
                    infoItems.add(info);
                    addNotificationForItemIfNecessary(context, info);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context,
                            "Item shared was a duplicate",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        Log.d(TRIP_INFORMATION_TAG, "MESSAGE " + readMessage + " RECEIVED");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == TRIP_INFORMATION_ITEM_ADD) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Info item = data.getParcelableExtra(getResources().getString(R.string.info_item));
                if(item == null ){
                    return;
                }
                if(data.getBooleanExtra(getResources().getString(R.string.delete), false)){
                    if(infoItems.contains(item)){
                        infoItems.remove(item);
                        WakefulReceiver.cancelAlarm(this.getApplicationContext(),
                                NotificationUtil.getNotificationIdForInfoItem(item));
                    }
                }
                else{
                    if(!infoItems.contains(item)){
                        infoItems.add(item);
                        addNotificationForItemIfNecessary(this.getApplicationContext(), item);
                    }
                    else{
                        infoItems.remove(item); //based on name and conf number
                        WakefulReceiver.cancelAlarm(this.getApplicationContext(),
                                NotificationUtil.getNotificationIdForInfoItem(item));
                        infoItems.add(item); // other fields might have changed.
                        addNotificationForItemIfNecessary(this.getApplicationContext(), item);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    private static void addNotificationForItemIfNecessary(Context context, Info item) {
        boolean addCruiseNotification = item instanceof CruiseInfo &&
                SharedPreferencesManager.getBooleanFromSP(
                        SettingsConstants.CRUISE_NOTIFICATION_PREFERENCE_TAG,
                        context);
        boolean addFlightNotification = item instanceof FlightInfo &&
                SharedPreferencesManager.getBooleanFromSP(
                        SettingsConstants.FLIGHT_NOTIFICATION_PREFERENCE_TAG,
                        context);
        boolean addHotelNotification = item instanceof HotelInfo &&
                SharedPreferencesManager.getBooleanFromSP(
                        SettingsConstants.HOTEL_NOTIFICATION_PREFERENCE_TAG,
                        context);
        boolean addBusNotification = item instanceof BusInfo &&
                SharedPreferencesManager.getBooleanFromSP(
                        SettingsConstants.BUS_NOTIFICATION_PREFERENCE_TAG,
                        context);
        if(addCruiseNotification ||
                addFlightNotification ||
                addHotelNotification ||
                addBusNotification) {
            Calendar cal = NotificationUtil.getNotificationTimeForInfoItem(item);
            if(cal == null) {
                return;
            }
            WakefulReceiver.setAlarm(
                    context,
                    cal,
                    NotificationUtil.getNotificationIdForInfoItem(item),
                    NotificationUtil.getNotificationMessageForInfoItem(item));
        }
    }

    private final Handler mHandler = new customHandler(this);

    private static class customHandler extends Handler {
        private Context mContext;

        customHandler(Context context) {
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    addItemsShared(mContext, readMessage);
                    break;
            }
        }
    }

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //Do nothing
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            handleDataTransfer(dataSnapshot);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            //Do nothing
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            //Do nothing
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Do nothing
        }
    };
}
