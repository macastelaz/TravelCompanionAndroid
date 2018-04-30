package com.castelcode.cruisecompanion.trip_info_share_activity;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.castelcode.cruisecompanion.bluetooth.BluetoothShareService;
import com.castelcode.cruisecompanion.bluetooth.Constants;
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.adapters.BluetoothDevicesAdapter;
import com.castelcode.cruisecompanion.tile_activities.TripInformation;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.Info;
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

public class ShareTripInfo extends AppCompatActivity implements ListView.OnItemClickListener,
    View.OnClickListener{

    private static final String SHARE_TRIP_INFORMATION_TAG = "ShareTripInformation";

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

    private ArrayList<ChildEventListener> listenters;

    private Button shareViaEmailButton;

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
                    if (entry.getKey().equals("connectionInitiatedWith")) {
                        if(it.hasNext()) {
                            entry = (Map.Entry<String, Boolean>) it.next();
                        }
                    }
                    if (entry.getValue() != null && entry.getValue() &&
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
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ref.child(TripInfoConstants.USER_PATH).addChildEventListener(newListener);
        listenters.add(newListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_trip_info);

        shareViaEmailButton = (Button) findViewById(R.id.share_via_email_button);
        shareViaEmailButton.setOnClickListener(this);

        progress = new ProgressDialog(this);
        progress.setTitle("Creating Document to Share");
        progress.setMessage("Please wait while we create your document");
        progress.setCancelable(false);

        listenters = new ArrayList<>();
        deviceUUID = new DeviceUuidFactory(this).getDeviceUuidAsString();

        database = FirebaseDatabase.getInstance();

        deviceItemList = new ArrayList<>();
        devices = (ListView) findViewById(R.id.bluetooth_devices);

        String itemToShare = getIntent().getStringExtra(TripInformation.ITEM_TO_SHARE_NAME);
        message = getIntent().getStringExtra((TripInformation.ITEM_TO_SHARE_VALUE));
        TextView itemToShareText = (TextView) findViewById(R.id.share_item_title);
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
                    ShareTripInfo.this.runOnUiThread(() ->
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
                    ShareTripInfo.this.runOnUiThread(() ->
                            Toast.makeText(ShareTripInfo.this,
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
        for (ChildEventListener listener : listenters) {
            ref.removeEventListener(listener);
        }
        listenters.clear();
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
        Log.d(SHARE_TRIP_INFORMATION_TAG, "setupChat()");

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
        Log.d(SHARE_TRIP_INFORMATION_TAG, "BT ENABLED - PROCEED");
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
        Log.d(SHARE_TRIP_INFORMATION_TAG, "DISCOVERY STARTED");

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
    private final Handler mHandler = new customHandler();

    @Override
    public void onClick(View v) {
        if(v == shareViaEmailButton) {
            shareViaEmailButton.setEnabled(false);
            progress.show();
            CreateEmailParamContainer params = new CreateEmailParamContainer(this,
                    getApplicationContext(), shareViaEmailButton);
            new ShareTripInfo.createEmailInBackground().execute(params);
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
                    Log.d(SHARE_TRIP_INFORMATION_TAG, "MESSAGE " + writeMessage + " WRITTEN");
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
            ref.child(TripInfoConstants.USER_PATH).child(deviceItemList.get(position).getDeviceName()).child("connectionInitiatedWith").setValue(deviceUUID);
            ref.child(getUniqueSessionIdentifier(deviceItemList.get(position))).setValue(message);
        }
        else {
            connectDevice(deviceItemList.get(position).getAddress());
            new SendBluetoothMessageAsync().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class SendBluetoothMessageAsync extends AsyncTask<Void, Void, Void> {
        SendBluetoothMessageAsync asyncObject;
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
            String subject = "Trip information for my upcoming travel";

            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, getMessageForEmail(positionOfItemToShare));


            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivityForResult(Intent.createChooser(emailIntent,"Send via:"),
                    EMAIL_SEND);

        } catch (Throwable t) {
            Toast.makeText(activity,
                    "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private static String getMessageForEmail(int positionOfItemToShare) {
        StringBuilder builder = new StringBuilder();
        if(positionOfItemToShare != -1) {
            Info itemShared = TripInformation.infoItems.get(positionOfItemToShare);
            if (itemShared instanceof CruiseInfo) {
                builder.append(getCruiseEmailFriendlyString((CruiseInfo)itemShared));
                builder.append("\n");
            }
            else if (itemShared instanceof HotelInfo) {
                builder.append(getHotelEmailFriendlyString((HotelInfo)itemShared));
                builder.append("\n");
            }
            else if (itemShared instanceof FlightInfo) {
                builder.append(getFlightEmailFriendlyString((FlightInfo)itemShared));
                builder.append("\n");
            }
            else if (itemShared instanceof BusInfo) {
                builder.append(getBusEmailFriendlyString((BusInfo)itemShared));
                builder.append("\n");
            }
        }
        else {

            for (Info item : TripInformation.infoItems) {
                if (item instanceof CruiseInfo) {
                    builder.append(getCruiseEmailFriendlyString((CruiseInfo)item));
                    builder.append("\n");
                }
                else if (item instanceof HotelInfo) {
                    builder.append(getHotelEmailFriendlyString((HotelInfo)item));
                    builder.append("\n");
                }
                else if (item instanceof FlightInfo) {
                    builder.append(getFlightEmailFriendlyString((FlightInfo)item));
                    builder.append("\n");
                }
                else if (item instanceof BusInfo) {
                    builder.append(getBusEmailFriendlyString((BusInfo)item));
                    builder.append("\n");
                }
            }
        }
        return builder.toString();
    }

    private static String getCruiseEmailFriendlyString(CruiseInfo item) {
        return "Cruise" + "\n\t" +
                "Cruise line: " + item.getPrimaryName() + "\n\t" +
                "Cruise ship: " + item.getShipName() + "\n\t" +
                "Cruise date: " + item.getStartDate() + "\n\t" +
                "Cruise time: " + item.getStartTime() + "\n\t" +
                "Room number: " + item.getRoomNumber() + "\n\t" +
                "Confirmation Number: " + item.getConfirmationNumber() + "\n\t" +
                "Phone Number: " + item.getPhoneNumber() + "\n";

    }
    private static String getFlightEmailFriendlyString(FlightInfo item) {
        return "Flight" + "\n\t" +
                "Airline: " + item.getPrimaryName() + "\n\t" +
                "Flight number: " + item.getFlightNumber() + "\n\t" +
                "Seat number: " + item.getSeatNumber() + "\n\t" +
                "Departure date: " + item.getStartDate() + "\n\t" +
                "Departure time: " + item.getStartTime() + "\n\t" +
                "Arrival time: " + item.getArrivalTime() + "\n\t" +
                "Origin: " + item.getOrigin() + "\n\t" +
                "Destination: " + item.getDestination() + "\n\t" +
                "Confirmation Number: " + item.getConfirmationNumber() + "\n\t" +
                "Phone Number: " + item.getPhoneNumber() + "\n";
    }
    private static String getHotelEmailFriendlyString(HotelInfo item) {
        return "Hotel" + "\n\t" +
                "Hotel: " + item.getPrimaryName() + "\n\t" +
                "Address: " + item.getAddress() + "\n\t" +
                "City: " + item.getCity() + "\n\t" +
                "State/Province/Country: " + item.getStateProvince() + "\n\t" +
                "Check in date: " + item.getStartDate() + "\n\t" +
                "Check in time: " + item.getStartTime() + "\n\t" +
                "Check out date: " + item.getCheckOutDate() + "\n\t" +
                "Check out time: " + item.getCheckOutTime() + "\n\t" +
                "Confirmation Number: " + item.getConfirmationNumber() + "\n\t" +
                "Phone Number: " + item.getPhoneNumber() + "\n";
    }
    private static String getBusEmailFriendlyString(BusInfo item) {
        return "Bus" + "\n\t" +
                "Bus line: " + item.getPrimaryName() + "\n\t" +
                "Seat number: " + item.getSeatNumber() + "\n\t" +
                "Departure date: " + item.getStartDate() + "\n\t" +
                "Departure time: " + item.getStartTime() + "\n\t" +
                "Arrival time: " + item.getArrivalTime() + "\n\t" +
                "Origin: " + item.getOrigin() + "\n\t" +
                "Destination: " + item.getDestination() + "\n\t" +
                "Confirmation Number: " + item.getConfirmationNumber() + "\n\t" +
                "Phone Number: " + item.getPhoneNumber() + "\n";
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
