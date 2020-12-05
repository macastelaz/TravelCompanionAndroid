package com.castelcode.travelcompanion.share_activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.adapters.BluetoothDevicesAdapter;
import com.castelcode.travelcompanion.tile_activities.TripAgenda;
import com.castelcode.travelcompanion.tile_activities.TripChecklists;
import com.castelcode.travelcompanion.tile_activities.TripInformation;
import com.castelcode.travelcompanion.tile_activities.TripLog;
import com.castelcode.travelcompanion.utils.DeviceItem;
import com.castelcode.travelcompanion.utils.DeviceUuidFactory;
import com.castelcode.travelcompanion.utils.TripInfoConstants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShareCruiseItem extends AppCompatActivity implements ListView.OnItemClickListener,
        View.OnClickListener{
    public static final String SHARE_ITEM_TYPE_NAME = "ShareItemType";
    private static final String SHARE_CRUISE_ITEM_TAG = "ShareCruiseItem";

    private static final int EMAIL_SEND = 2;

    private ArrayList<DeviceItem> deviceItemList;
    BluetoothDevicesAdapter devicesAdapter;
    ListView devices;


    private static ProgressDialog progress;

    private static int positionOfItemToShare;

    private static SupportedShareItemTypes type;

    private static String message;

    private FirebaseDatabase database;

    private DatabaseReference ref;

    private String deviceUUID;

    private ArrayList<ChildEventListener> listeners;

    private Button shareViaEmailButton;

    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImpl();
    }

    private Intent getParentActivityIntentImpl() {
        Intent i = null;
        // Here you need to do some logic to determine from which Activity you came.
        // example: you could pass a variable through your Intent extras and check that.
        switch(type) {
            case TRIP_LOG:
                i = new Intent(this, TripLog.class);
                // set any flags or extras that you need.
                // If you are reusing the previous Activity (i.e. bringing it to the top
                // without re-creating a new instance) set these flags:
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            case TRIP_AGENDA:
                i = new Intent(this, TripAgenda.class);
                // set any flags or extras that you need.
                // If you are reusing the previous Activity (i.e. bringing it to the top
                // without re-creating a new instance) set these flags:
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            case TRIP_INFO:
                i = new Intent(this, TripInformation.class);
                // set any flags or extras that you need.
                // If you are reusing the previous Activity (i.e. bringing it to the top
                // without re-creating a new instance) set these flags:
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            case TRIP_CHECKLISTS:
                i = new Intent(this, TripChecklists.class);
                // set any flags or extras that you need.
                // If you are reusing the previous Activity (i.e. bringing it to the top
                // without re-creating a new instance) set these flags:
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
        }
        return i;
    }


    @SuppressWarnings("unchecked")
    private void setupAndroidToIOSTransfer() {
        ref = database.getReference();
        ChildEventListener newListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                HashMap<String, Boolean> value = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if(value != null) {
                    Iterator it = value.entrySet().iterator();
                    Map.Entry<String, Boolean> entry = (Map.Entry<String, Boolean>) it.next();
                    if (entry.getKey().equals(TripInfoConstants.CONNECTION_KEY)
                            && type.equals(SupportedShareItemTypes.TRIP_INFO)) {
                        if(it.hasNext()) {
                            entry = (Map.Entry<String, Boolean>) it.next();
                        }
                    } else if (entry.getKey().equals("tripAgendaConnection")
                            && type.equals(SupportedShareItemTypes.TRIP_AGENDA)) {
                        if(it.hasNext()) {
                            entry = (Map.Entry<String, Boolean>) it.next();
                        }
                    } else if (entry.getKey().equals("tripChecklistConnection")
                            && type.equals(SupportedShareItemTypes.TRIP_CHECKLISTS)) {
                        if(it.hasNext()) {
                            entry = (Map.Entry<String, Boolean>) it.next();
                        }
                    }
                    if (entry.getValue() != null &&
                            !entry.getKey().equals(deviceUUID)) {
                        System.out.println("ADDING ANDROID DEVICE OVER FIREBASE W/ KEY: " + dataSnapshot.getKey());
                        DeviceItem newDevice = new DeviceItem(dataSnapshot.getKey(), entry.getKey(), true);
                        if (!deviceItemList.contains(newDevice)) {
                            deviceItemList.add(newDevice);
                            devicesAdapter.notifyDataSetChanged();
                        }
                    }
                    System.out.println("Added: " + dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                HashMap<String, Boolean> value = (HashMap<String, Boolean>) dataSnapshot.getValue();
                if(value != null) {
                    Map.Entry<String, Boolean> entry = value.entrySet().iterator().next();
                    DeviceItem newDevice = new DeviceItem(dataSnapshot.getKey(), entry.getKey(),  true);
                    if (deviceItemList.contains(newDevice)) {
                        deviceItemList.remove(newDevice);
                        devicesAdapter.notifyDataSetChanged();
                    }
                    System.out.println("Removed: " + dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.child(TripInfoConstants.USER_PATH).addChildEventListener(newListener);
        listeners.add(newListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_info);

        shareViaEmailButton = findViewById(R.id.share_via_email_button);
        shareViaEmailButton.setOnClickListener(this);

        progress = new ProgressDialog(this);
        progress.setTitle("Creating Document to Share");
        progress.setMessage("Please wait while we create your document");
        progress.setCancelable(false);

        listeners = new ArrayList<>();
        deviceUUID = new DeviceUuidFactory(this).getDeviceUuidAsString();

        database = FirebaseDatabase.getInstance();

        deviceItemList = new ArrayList<>();
        devices = findViewById(R.id.bluetooth_devices);

        type = (SupportedShareItemTypes) getIntent().getSerializableExtra(SHARE_ITEM_TYPE_NAME);

        String itemToShare = getIntent().getStringExtra(TripInformation.ITEM_TO_SHARE_NAME);
        message = getIntent().getStringExtra((TripInformation.ITEM_TO_SHARE_VALUE));
        TextView itemToShareText = findViewById(R.id.share_item_title);
        String title = "Share "+ itemToShare + " with:";
        itemToShareText.setText(title);

        positionOfItemToShare = getIntent().getIntExtra(TripInformation.ITEM_TO_SHARE_POSITION,
                -1);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        for (ChildEventListener listener : listeners) {
            ref.removeEventListener(listener);
        }
        listeners.clear();
    }

    /**
     * Set up the UI and background operations for sending information.
     */
    private void setupDataSend() {
        Log.d(SHARE_CRUISE_ITEM_TAG, "setupChat()");

        devicesAdapter = new BluetoothDevicesAdapter(this, deviceItemList);

        devices.setAdapter(devicesAdapter);

        devices.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupDataSend();
        setupAndroidToIOSTransfer();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName == null || deviceName.isEmpty() || deviceHardwareAddress == null ||
                        deviceHardwareAddress.isEmpty()){
                    return;
                }
                DeviceItem newDevice = new DeviceItem(deviceName, deviceHardwareAddress,
                        false);
                if(!deviceItemList.contains(newDevice) && devicesAdapter != null) {
                    deviceItemList.add(newDevice);
                    devicesAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EMAIL_SEND && resultCode == RESULT_CANCELED) {
            shareViaEmailButton.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == shareViaEmailButton) {
            shareViaEmailButton.setEnabled(false);
            progress.show();
            CreateEmailParamContainer params = new CreateEmailParamContainer(this,
                    getApplicationContext(), shareViaEmailButton);
            new ShareCruiseItem.createEmailInBackground().execute(params);
        }
    }

    private String getUniqueSessionIdentifier(DeviceItem deviceToConnectWith) {
        if(deviceToConnectWith.getAddress().compareTo(deviceUUID) < 0) {
            return deviceUUID + "-" + deviceToConnectWith.getAddress();
        }
        else {
            return deviceToConnectWith.getAddress() + "-" + deviceUUID;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        Toast.makeText(this,
                "Attempting to share the data.",
                Toast.LENGTH_SHORT).show();
        String connectionKey = "";
        switch(type) {
            case TRIP_AGENDA:
                connectionKey = "tripAgendaConnection";
                break;
            case TRIP_CHECKLISTS:
                connectionKey = "tripChecklistConnection";
                break;
            case TRIP_INFO:
                connectionKey = TripInfoConstants.CONNECTION_KEY;
                break;
            case TRIP_LOG:
                break;
        }
        ref.child(TripInfoConstants.USER_PATH).child(deviceItemList.get(position).getDeviceName())
                .child(connectionKey).setValue(deviceUUID);
        ref.child(getUniqueSessionIdentifier(deviceItemList.get(position))).setValue(message);
    }

    private static void createEmail(CreateEmailParamContainer params){
        sendMail(params.getActivity());
    }
    private static void sendMail(AppCompatActivity activity) {
        try {
            String subject = "travelcompanion";
            switch(type) {
                case TRIP_LOG:
                    subject = "Trip Log for my recent travel";
                    break;
                case TRIP_INFO:
                    subject = "Trip information for my upcoming travel";
                    break;
                case TRIP_AGENDA:
                    subject = "Trip agenda for my upcoming travel";
                    break;
                case TRIP_CHECKLISTS:
                    subject = "Trip checklists for my recent travel";
                    break;
                case ALL_POSSIBLE:
                    subject = "Here's everything about my travel!";
                    break;
            }

            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("text/html");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,subject);
            emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, getHtmlMessageForEmail(positionOfItemToShare));
            emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(getHtmlMessageForEmail(positionOfItemToShare)));


            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(Intent.createChooser(emailIntent,"Send via:"),
                    EMAIL_SEND);

        } catch (Throwable t) {
            Toast.makeText(activity,
                    "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private static String getHtmlMessageForEmail(int positionOfItemToShare) {
        String emailMessage = "";
        switch(type) {
            case TRIP_LOG:
                break;
            case TRIP_AGENDA:
                    emailMessage = ShareTripAgendaInfoItem.getHtmlMessageForEmail(positionOfItemToShare);
                break;
            case TRIP_INFO:
                emailMessage = ShareTripInfoItem.getHtmlMessageForEmail(positionOfItemToShare);
                break;
            case TRIP_CHECKLISTS:
                emailMessage = ShareTripChecklist.getHtmlMessageForEmail(positionOfItemToShare);
                break;
        }
        return emailMessage;
    }

    private static class createEmailInBackground extends AsyncTask<CreateEmailParamContainer,
            Void, CreateEmailParamContainer> {

        @Override
        protected CreateEmailParamContainer doInBackground(CreateEmailParamContainer... params) {
            createEmail(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(CreateEmailParamContainer result) {
            result.getShareButton().setEnabled(true);
            progress.dismiss();
        }
    }
}
