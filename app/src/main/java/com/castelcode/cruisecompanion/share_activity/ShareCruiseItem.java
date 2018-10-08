package com.castelcode.cruisecompanion.share_activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.adapters.BluetoothDevicesAdapter;
import com.castelcode.cruisecompanion.bluetooth.BluetoothShareService;
import com.castelcode.cruisecompanion.bluetooth.Constants;
import com.castelcode.cruisecompanion.tile_activities.TripAgenda;
import com.castelcode.cruisecompanion.tile_activities.TripChecklists;
import com.castelcode.cruisecompanion.tile_activities.TripInformation;
import com.castelcode.cruisecompanion.tile_activities.TripLog;
import com.castelcode.cruisecompanion.utils.DeviceItem;
import com.castelcode.cruisecompanion.utils.DeviceUuidFactory;
import com.castelcode.cruisecompanion.utils.TripInfoConstants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ShareCruiseItem extends AppCompatActivity implements ListView.OnItemClickListener,
        View.OnClickListener{
    public static final String SHARE_ITEM_TYPE_NAME = "ShareItemType";
    private static final String SHARE_CRUISE_ITEM_TAG = "ShareCruiseItem";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int EMAIL_SEND = 2;

    private ArrayList<DeviceItem> deviceItemList;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevicesAdapter devicesAdapter;
    ListView devices;

    private static Thread SuccessThread;
    private static Thread FailureThread;

    private static ProgressDialog progress;

    private static int positionOfItemToShare;

    private static SupportedShareItemTypes type;

    /**
     * Member object for the chat services
     */
    private static BluetoothShareService mShareService = null;

    /**
     * String buffer for outgoing messages
     */
    private static StringBuffer mOutStringBuffer;

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

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Context context = this;
        SuccessThread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(Toast.LENGTH_SHORT); // *2 in best case of bt share time.
                    ShareCruiseItem.this.runOnUiThread(() ->
                            Toast.makeText(context,
                                    "Data shared successfully", Toast.LENGTH_SHORT).show());
                    Thread.sleep(Toast.LENGTH_SHORT);
                    mShareService.stop();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        FailureThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Toast.LENGTH_SHORT); // *2 in best case of bt share time.
                    ShareCruiseItem.this.runOnUiThread(() ->
                            Toast.makeText(context,
                                    "Data could not be sent", Toast.LENGTH_SHORT).show());
                    Thread.sleep(Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        for (ChildEventListener listener : listeners) {
            ref.removeEventListener(listener);
        }
        listeners.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (bluetoothAdapter != null && mShareService == null) {
            setupDataSend();
        }


        if(bluetoothAdapter == null){
            Toast.makeText(this, "Unfortunately your device does not support bluetooth",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBTInent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTInent, REQUEST_ENABLE_BT);
        }
        else {
            proceedWithShareAfterBluetoothEnabled();
        }
    }

    /**
     * Set up the UI and background operations for sending information.
     */
    private void setupDataSend() {
        Log.d(SHARE_CRUISE_ITEM_TAG, "setupChat()");

        devicesAdapter = new BluetoothDevicesAdapter(this, deviceItemList);

        devices.setAdapter(devicesAdapter);

        devices.setOnItemClickListener(this);

        // Initialize the BluetoothShareService to perform bluetooth connections
        mShareService = new BluetoothShareService(mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public void onResume() {
        super.onResume();
        setupDataSend();
        if(bluetoothAdapter != null) {
            // Performing this check in onResume() covers the case in which BT was
            // not enabled during onStart(), so we were paused to enable it...
            // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
            if (mShareService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mShareService.getState() == BluetoothShareService.STATE_NONE) {
                    // Start the bluetooth chat services
                    mShareService.start();
                }
            }
        }
        setupAndroidToIOSTransfer();
    }

    private void proceedWithShareAfterBluetoothEnabled(){
        Log.d(SHARE_CRUISE_ITEM_TAG, "BT ENABLED - PROCEED");
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        boolean newDeviceAdded = false;
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(),  false);
                if(!deviceItemList.contains(newDevice) && devicesAdapter != null) {
                    deviceItemList.add(newDevice);
                    newDeviceAdded = true;
                }
            }
        }
        if(newDeviceAdded && devicesAdapter != null) {
            devicesAdapter.notifyDataSetChanged();
        }
        bluetoothAdapter.startDiscovery();
        Log.d(SHARE_CRUISE_ITEM_TAG, "DISCOVERY STARTED");

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
        if(bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        unregisterReceiver(mReceiver);
        if (mShareService != null) {
            mShareService.stop();
            mShareService = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK) {
                proceedWithShareAfterBluetoothEnabled();
            }
        }
        else if(requestCode == EMAIL_SEND && resultCode == RESULT_CANCELED) {
            shareViaEmailButton.setEnabled(true);
        }
    }

    /**
     * Establish connection with other device
     *
     * @param deviceAddress   A device's address
     */
    private void connectDevice(String deviceAddress) {
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        // Attempt to connect to the device
        mShareService.connect(device);
    }

    /**
     * The Handler that gets information back from the BluetoothShareService
     */
    private final Handler mHandler = new ShareCruiseItem.customHandler();

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

    private static class customHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d(SHARE_CRUISE_ITEM_TAG, "MESSAGE " + writeMessage + " WRITTEN");
                    break;
            }
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
                "Attempting to share the data. Will return to previous screen if successful.",
                Toast.LENGTH_SHORT).show();
        if(deviceItemList.get(position).getType()) //True means iOS so send over firebase
        {
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
        else {
            connectDevice(deviceItemList.get(position).getAddress());
            new ShareCruiseItem.SendBluetoothMessageAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class SendBluetoothMessageAsync extends AsyncTask<Void, Void, Void> {
        ShareCruiseItem.SendBluetoothMessageAsync asyncObject;
        boolean completedSuccessfully = false;
        @Override
        protected void onPreExecute(){
            asyncObject = this;
            new CountDownTimer(10000,1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    if(asyncObject.getStatus() == Status.RUNNING) {
                        asyncObject.cancel(true);
                    }
                }
            }.start();
        }

        @Override
        protected Void doInBackground(Void... strings) {
            if(mShareService == null){
                return null;
            }
            while (mShareService != null &&
                    mShareService.getState() != BluetoothShareService.STATE_CONNECTED &&
                    !asyncObject.isCancelled()) {
                SystemClock.sleep(10);
            }
            if(mShareService == null) {
                return null;
            }
            if(asyncObject.isCancelled()) {
                asyncObject = null;
                return null;
            }
            // Check that there's actually something to send
            if (message.length() > 0) {
                // Get the message bytes and tell the BluetoothShareService to write
                byte[] send = message.getBytes();
                mShareService.write(send);

                // Reset out string buffer to zero and clear the edit text field
                mOutStringBuffer.setLength(0);
                completedSuccessfully = true;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onCancelled() {
            try {
                if (!FailureThread.isAlive()) {
                    FailureThread.start();
                }
            }
            catch(IllegalStateException ex) {
                System.out.println(ex.toString());
            }

        }

        @Override
        protected void onPostExecute(Void result) {
            if(completedSuccessfully) {
                try {
                    if (!SuccessThread.isAlive()) {
                        SuccessThread.start();
                    }
                }
                catch (IllegalStateException ex) {
                    System.out.println(ex.toString());
                }
            }
            else {
                try {
                    if (!FailureThread.isAlive()) {
                        FailureThread.start();
                    }
                }
                catch (IllegalStateException ex){
                    System.out.println(ex.toString());
                }
            }
        }
    }

    private static void createEmail(CreateEmailParamContainer params){
        sendMail(params.getActivity());
    }
    private static void sendMail(AppCompatActivity activity) {
        try {
            String subject = "CruiseCompanion";
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
