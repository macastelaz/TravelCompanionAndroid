package com.castelcode.travelcompanion.tile_activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.castelcode.protobuf.TripInfoProtos;
import com.castelcode.travelcompanion.bluetooth.Constants;
import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.adapters.InfoItemsBaseAdapter;
import com.castelcode.travelcompanion.adapters.InfoSerializerAdapter;
import com.castelcode.travelcompanion.share_activity.ShareCruiseItem;
import com.castelcode.travelcompanion.share_activity.SupportedShareItemTypes;
import com.castelcode.travelcompanion.trip_info_add_activity.AddTripInfoItem;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.Info;
import com.castelcode.travelcompanion.utils.DeviceUuidFactory;
import com.castelcode.travelcompanion.utils.NotificationUtil;
import com.castelcode.travelcompanion.utils.SettingsConstants;
import com.castelcode.travelcompanion.utils.SharedPreferencesManager;
import com.castelcode.travelcompanion.utils.TripInfoConstants;
import com.castelcode.travelcompanion.utils.WakefulReceiver;
import com.google.common.collect.ImmutableList;
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
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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

        addTripInfoButton = findViewById(R.id.add_info_button);
        addTripInfoButton.setOnClickListener(this);

        shareAllButton = findViewById(R.id.share_all_button);
        shareAllButton.setOnClickListener(this);

        infoItemsView = findViewById(R.id.info_list_view);

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
            Intent shareTripInfoIntent = new Intent(context, ShareCruiseItem.class);
            shareTripInfoIntent.putExtra(ITEM_TO_SHARE_NAME,
                    infoItems.get(position).toString());
            shareTripInfoIntent.putExtra(ITEM_TO_SHARE_VALUE,
                    createStringForSharedTripInfo(
                        TripInfoProtos.SharedTripInfo.newBuilder()
                                .addAllSharedTripInformation(
                                        createAllSharedTripInformationForItems(
                                                ImmutableList.of(infoItems.get(position))))
                                .build()));
            shareTripInfoIntent.putExtra(ShareCruiseItem.SHARE_ITEM_TYPE_NAME,
                    SupportedShareItemTypes.TRIP_INFO);
            shareTripInfoIntent.putExtra(ITEM_TO_SHARE_POSITION, position);
            startActivityForResult(shareTripInfoIntent, TRIP_INFORMATION_SHARE);
            return true;
        });
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

    private String createStringForSharedTripInfo(TripInfoProtos.SharedTripInfo sharedTripInfo) {
        String sharedTripInfoString = "";
        try {
            sharedTripInfoString = JsonFormat.printer().print(sharedTripInfo);
        } catch (InvalidProtocolBufferException ex) {
            System.out.println("Exception printing item to JSON: " + ex.toString());
        }
        return sharedTripInfoString;
    }

    private ArrayList<TripInfoProtos.TripInfo> createAllSharedTripInformationForItems(
            List<Info> items) {
        ArrayList<TripInfoProtos.TripInfo> tripInfoItems = new ArrayList<>();
        for (Info item : items) {
            TripInfoProtos.TripInfo.Builder tripInfo = TripInfoProtos.TripInfo.newBuilder();
            if (item instanceof CruiseInfo) {
                tripInfo.setCruiseInfo(createCruiseInformation((CruiseInfo) item));
            } else if (item instanceof FlightInfo) {
                tripInfo.setFlightInfo(createFlightInformation((FlightInfo) item));
            } else if (item instanceof HotelInfo) {
                tripInfo.setHotelInfo(createHotelInformation((HotelInfo) item));
            } else if (item instanceof BusInfo) {
                tripInfo.setBusInfo(createBusInformation((BusInfo) item));
            }
            tripInfoItems.add(tripInfo.build());
        }
        return tripInfoItems;
    }

    private TripInfoProtos.CruiseInformation createCruiseInformation(CruiseInfo cruiseInfo) {
        return TripInfoProtos.CruiseInformation.newBuilder()
                .setCruiseLine(cruiseInfo.getPrimaryName())
                .setShipName(cruiseInfo.getShipName())
                .setRoomNumber(cruiseInfo.getRoomNumber())
                .setDepartureDate(cruiseInfo.getStartDate())
                .setDepartureTime(cruiseInfo.getStartTime())
                .setConfirmationNumber(cruiseInfo.getConfirmationNumber())
                .setPhoneNumber(cruiseInfo.getPhoneNumber())
                .build();
    }

    private TripInfoProtos.FlightInformation createFlightInformation(FlightInfo flightInfo) {
        return TripInfoProtos.FlightInformation.newBuilder()
                .setAirline(flightInfo.getPrimaryName())
                .setFlightNumber(flightInfo.getFlightNumber())
                .setSeatNumber(flightInfo.getSeatNumber())
                .setOrigin(flightInfo.getOrigin())
                .setDestination(flightInfo.getDestination())
                .setDepartureDate(flightInfo.getStartDate())
                .setDepartureTime(flightInfo.getStartTime())
                .setArrivalTime(flightInfo.getArrivalTime())
                .setConfirmationNumber(flightInfo.getConfirmationNumber())
                .setPhoneNumber(flightInfo.getPhoneNumber())
                .build();
    }

    private TripInfoProtos.HotelInformation createHotelInformation(HotelInfo hotelInfo) {
        return TripInfoProtos.HotelInformation.newBuilder()
                .setName(hotelInfo.getPrimaryName())
                .setAddress(hotelInfo.getAddress())
                .setCity(hotelInfo.getCity())
                .setState(hotelInfo.getStateProvince())
                .setConfrimationNumber(hotelInfo.getConfirmationNumber())
                .setPhoneNumber(hotelInfo.getPhoneNumber())
                .setCheckInDate(hotelInfo.getStartDate())
                .setCheckInTime(hotelInfo.getStartTime())
                .setCheckOutDate(hotelInfo.getCheckOutDate())
                .setCheckOutTime(hotelInfo.getCheckOutTime())
                .build();
    }

    private TripInfoProtos.BusInformation createBusInformation(BusInfo busInfo) {
        return TripInfoProtos.BusInformation.newBuilder()
                .setBusLine(busInfo.getPrimaryName())
                .setSeatNumber(busInfo.getSeatNumber())
                .setOrigin(busInfo.getOrigin())
                .setDestination(busInfo.getDestination())
                .setDepartureDate(busInfo.getStartDate())
                .setDepartureTime(busInfo.getStartTime())
                .setArrivalTime(busInfo.getArrivalTime())
                .setConfirmationNumber(busInfo.getConfirmationNumber())
                .setPhoneNumber(busInfo.getPhoneNumber())
                .build();
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

    private void resetConnectionFor(String sessionId) {
        ref.child(sessionId).removeValue((DatabaseError databaseError, DatabaseReference databaseReference) -> {
            if(databaseError != null) {
                System.out.println(databaseError.getMessage());
            }
            else {
                System.out.println(databaseReference.toString());
                System.out.println("Child Removed Correctly");
                ref.child(TripInfoConstants.USER_PATH).child(Build.MODEL)
                        .child(TripInfoConstants.CONNECTION_KEY).setValue("", (DatabaseError databaseErrorInner, DatabaseReference databaseReferenceInner) -> {

                        if(databaseErrorInner != null) {
                            System.out.println(databaseErrorInner.getMessage());
                        }
                        else {
                            System.out.println(databaseReferenceInner.toString());
                            System.out.println("Value set to blank correctly");
                            ref.child(TripInfoConstants.USER_PATH).child(Build.MODEL).addChildEventListener(childEventListener);
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
            Intent shareTripInfoIntent = new Intent(this, ShareCruiseItem.class);
            shareTripInfoIntent.putExtra(ITEM_TO_SHARE_NAME, ALL_ITEMS);
            shareTripInfoIntent.putExtra(ShareCruiseItem.SHARE_ITEM_TYPE_NAME,
                    SupportedShareItemTypes.TRIP_INFO);

            String stringToShare = createStringForSharedTripInfo(
                    TripInfoProtos.SharedTripInfo.newBuilder()
                            .addAllSharedTripInformation(
                                    createAllSharedTripInformationForItems(infoItems))
                            .build());

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
        TripInfoProtos.SharedTripInfo.Builder sharedTripInfos =
                TripInfoProtos.SharedTripInfo.newBuilder();
        try {
            JsonFormat.parser().merge(readMessage, sharedTripInfos);
        } catch (InvalidProtocolBufferException ex) {
            System.out.println("Failed to parse proto with exception: " + ex.toString());
        }
        for (TripInfoProtos.TripInfo info : sharedTripInfos.getSharedTripInformationList()) {
            Info infoToUse = null;
            if (info.hasCruiseInfo()) {
                infoToUse = new CruiseInfo(info.getCruiseInfo());
            } else if(info.hasFlightInfo()) {
                infoToUse = new FlightInfo(info.getFlightInfo());
            } else if (info.hasBusInfo()) {
                infoToUse = new BusInfo(info.getBusInfo());
            } else if(info.hasHotelInfo()) {
                infoToUse = new HotelInfo(info.getHotelInfo());
            }
            if (infoToUse != null) {
                if (!infoItems.contains(infoToUse)) {
                    infoItems.add(infoToUse);
                    addNotificationForItemIfNecessary(context, infoToUse);
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
