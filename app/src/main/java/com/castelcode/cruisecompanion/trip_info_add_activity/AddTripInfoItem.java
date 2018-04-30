package com.castelcode.cruisecompanion.trip_info_add_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.FlightInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.Info;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class AddTripInfoItem extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener{

    private static final String TAG = AddTripInfoItem.class.getSimpleName();

    private final String HOTEL_INFORMATION = "Hotel";
    private final String FLIGHT_INFORMATION = "Flight";
    private final String CRUISE_INFORMATION = "Cruise";
    private final String BUS_INFORMATION = "Bus";


    private Button deleteButton;
    private Button addButton;

    private Spinner informationType;

    private Info infoItemIn = null;

    private String typeIn = null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip_info_item);

        informationType = (Spinner) findViewById(R.id.trip_info_type);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> infoTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.supported_trip_information_types, R.layout.conversion_spinner);
        // Specify the layout to use when the list of choices appears
        infoTypeAdapter.setDropDownViewResource(R.layout.conversion_spinner_dropdown);
        // Apply the adapter to the spinner
        informationType.setAdapter(infoTypeAdapter);

        deleteButton = (Button) findViewById(R.id.delete_button);
        addButton = (Button) findViewById(R.id.confirm_button);

        addButton.setOnClickListener(this);

        Intent intent = getIntent();
        Info info = intent.getParcelableExtra(getResources().getString(R.string.info_item));
        String type = intent.getStringExtra(getResources().getString(R.string.info_type));
        informationType.setOnItemSelectedListener(this);

        if(info != null && type != null && !type.equals("")){
            informationType.setBackgroundDrawable(ContextCompat.getDrawable(this,
                    R.drawable.spinner_borderless));
            switch(type){
                case CRUISE_INFORMATION:
                    informationType.setSelection(infoTypeAdapter.getPosition(CRUISE_INFORMATION),
                            false);
                    break;
                case HOTEL_INFORMATION:
                    informationType.setSelection(infoTypeAdapter.getPosition(HOTEL_INFORMATION),
                            false);
                    break;
                case FLIGHT_INFORMATION:
                    informationType.setSelection(infoTypeAdapter.getPosition(FLIGHT_INFORMATION),
                            false);
                    break;
                case BUS_INFORMATION:
                    informationType.setSelection(infoTypeAdapter.getPosition(BUS_INFORMATION),
                            false);
                    break;
            }
            informationType.setEnabled(false);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(this);
        }
        else{
            deleteButton.setVisibility(View.INVISIBLE);
            deleteButton.setOnClickListener(null);
        }
        infoItemIn = info;
        typeIn = type;
    }

    private void updateFragment(){
        String selectedItem = informationType.getSelectedItem().toString();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment frag;
        Bundle data = new Bundle();
        switch (selectedItem){
            case HOTEL_INFORMATION:
                frag = HotelFragment.newInstance();
                data.putParcelable(getResources().getString(R.string.info_item),
                        infoItemIn);
                frag.setArguments(data);
                fragmentTransaction.replace(R.id.frag_frame, frag);
                break;
            case FLIGHT_INFORMATION:
                frag = FlightFragment.newInstance();
                data.putParcelable(getResources().getString(R.string.info_item),
                        infoItemIn);
                frag.setArguments(data);
                fragmentTransaction.replace(R.id.frag_frame, frag);
                break;
            case CRUISE_INFORMATION:
                frag = CruiseFragment.newInstance();
                data.putParcelable(getResources().getString(R.string.info_item),
                        infoItemIn);
                frag.setArguments(data);
                fragmentTransaction.replace(R.id.frag_frame, frag);
                break;
            case BUS_INFORMATION:
                frag = BusFragment.newInstance();
                data.putParcelable(getResources().getString(R.string.info_item),
                        infoItemIn);
                frag.setArguments(data);
                fragmentTransaction.replace(R.id.frag_frame, frag);
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        updateFragment();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean validatePhoneNumber(String rawNumberText) {
        if(rawNumberText.equals("")){
            return true;
        }
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try{
            Phonenumber.PhoneNumber phoneNumberProto = phoneUtil.parse(rawNumberText,
                    "US");
            return phoneUtil.isValidNumber(phoneNumberProto);
        } catch(NumberParseException e) {
            Log.d(TAG, "Number parse exception during phone number check: " + e.toString());
            return false;
        }
    }

    private HotelInfo handleHotelInfoClick(){
        FrameLayout frame = (FrameLayout) findViewById(R.id.hotel_frag);

        EditText nameText = (EditText) frame.findViewById(R.id.name_value);
        String name = nameText.getText().toString();

        EditText confNumberText =
                (EditText) frame.findViewById(R.id.confrimation_code_value);
        String confNumber = confNumberText.getText().toString();

        EditText phoneNumberText =
                (EditText) frame.findViewById(R.id.phone_number_value);
        String phoneNumber = phoneNumberText.getText().toString();

        if(!validatePhoneNumber(phoneNumber)) {
            phoneNumberText.setError(getString(R.string.err_tel));
            return null;
        }

        EditText addressText =
                (EditText) frame.findViewById(R.id.address_value);
        String address = addressText.getText().toString();

        EditText cityText =
                (EditText) frame.findViewById(R.id.city_value);
        String city = cityText.getText().toString();

        EditText stateProvinceText =
                (EditText) frame.findViewById(R.id.state_province_value);
        String stateProvince = stateProvinceText.getText().toString();

        Button checkInDateButton =
                (Button) frame.findViewById(R.id.check_in_date_value);
        String checkInDate = checkInDateButton.getText().toString();
        if(checkInDate.equals(getString(R.string.select_date))) {
            checkInDate = "";
        }

        Button checkInTimeButton =
                (Button) frame.findViewById(R.id.check_in_time_value);
        String checkInTime = checkInTimeButton.getText().toString();
        if(checkInTime.equals(getString(R.string.select_time))) {
            checkInTime = "";
        }

        Button checkOutDateButton =
                (Button) frame.findViewById(R.id.check_out_date_value);
        String checkOutDate = checkOutDateButton.getText().toString();
        if(checkOutDate.equals(getString(R.string.select_date))) {
            checkOutDate = "";
        }

        Button checkOutTimeButton =
                (Button) frame.findViewById(R.id.check_out_time_value);
        String checkOutTime = checkOutTimeButton.getText().toString();
        if(checkOutTime.equals(getString(R.string.select_time))) {
            checkOutTime = "";
        }
        return new HotelInfo(name, confNumber, phoneNumber, address, city,
                stateProvince, checkInDate, checkInTime, checkOutDate, checkOutTime);
    }

    private FlightInfo handleFlightInfoClick(){
        FrameLayout frame = (FrameLayout) findViewById(R.id.flight_frag);

        EditText nameText = (EditText) frame.findViewById(R.id.name_value);
        String name = nameText.getText().toString();

        EditText confNumberText =
                (EditText) frame.findViewById(R.id.confrimation_code_value);
        String confNumber = confNumberText.getText().toString();

        EditText phoneNumberText =
                (EditText) frame.findViewById(R.id.phone_number_value);
        String phoneNumber = phoneNumberText.getText().toString();

        if(!validatePhoneNumber(phoneNumber)) {
            phoneNumberText.setError(getString(R.string.err_tel));
            return null;
        }

        EditText seatNumberText =
                (EditText) frame.findViewById(R.id.seat_assignment_value);
        String seatNumber = seatNumberText.getText().toString();

        EditText flightNumberText =
                (EditText) frame.findViewById(R.id.flight_number_value);
        String flightNumber = flightNumberText.getText().toString();

        EditText originText =
                (EditText) frame.findViewById(R.id.origin_value);
        String origin = originText.getText().toString();

        EditText destinationText =
                (EditText) frame.findViewById(R.id.destination_value);
        String destination = destinationText.getText().toString();

        Button departureDateButton =
                (Button) frame.findViewById(R.id.flight_departure_date_value);
        String departureDate = departureDateButton.getText().toString();
        if(departureDate.equals(getString(R.string.select_date))) {
            departureDate = "";
        }

        Button departureTimeButton =
                (Button) frame.findViewById(R.id.flight_departure_time_value);
        String departureTime = departureTimeButton.getText().toString();
        if(departureTime.equals(getString(R.string.select_time))) {
            departureTime = "";
        }

        Button arrivalTimeButton =
                (Button) frame.findViewById(R.id.arrival_time_value);
        String arrivalTime = arrivalTimeButton.getText().toString();
        if(arrivalTime.equals(getString(R.string.select_time))) {
            arrivalTime = "";
        }

        return new FlightInfo(name, confNumber, phoneNumber, flightNumber, seatNumber,
                origin, destination, departureTime, arrivalTime, departureDate);
    }

    private CruiseInfo handleCruiseInfoClick(){
        FrameLayout frame = (FrameLayout) findViewById(R.id.cruise_frag);

        EditText nameText = (EditText) frame.findViewById(R.id.name_value);
        String name = nameText.getText().toString();

        EditText confNumberText =
                (EditText) frame.findViewById(R.id.confrimation_code_value);
        String confNumber = confNumberText.getText().toString();

        EditText phoneNumberText =
                (EditText) frame.findViewById(R.id.phone_number_value);
        String phoneNumber = phoneNumberText.getText().toString();

        if(!validatePhoneNumber(phoneNumber)) {
            phoneNumberText.setError(getString(R.string.err_tel));
            return null;
        }

        EditText roomNumberText =
                (EditText) frame.findViewById(R.id.room_number_value);
        String roomNumber = roomNumberText.getText().toString();

        EditText shipNameText =
                (EditText) frame.findViewById(R.id.ship_name_value);
        String shipName = shipNameText.getText().toString();

        Button departureDateButton =
                (Button) frame.findViewById(R.id.cruise_departure_date_value);
        String departureDate = departureDateButton.getText().toString();
        if(departureDate.equals(getString(R.string.select_date))) {
            departureDate = "";
        }

        Button departureTimeButton =
                (Button) frame.findViewById(R.id.cruise_departure_time_value);
        String departureTime = departureTimeButton.getText().toString();
        if(departureTime.equals(getString(R.string.select_time))) {
            departureTime = "";
        }

        return new CruiseInfo(name, confNumber, phoneNumber, roomNumber, shipName, departureDate,
                departureTime);
    }

    private BusInfo handleBusInfoClick(){
        FrameLayout frame = (FrameLayout) findViewById(R.id.bus_frag);

        EditText nameText = (EditText) frame.findViewById(R.id.name_value);
        String name = nameText.getText().toString();

        EditText confNumberText =
                (EditText) frame.findViewById(R.id.confrimation_code_value);
        String confNumber = confNumberText.getText().toString();

        EditText phoneNumberText =
                (EditText) frame.findViewById(R.id.phone_number_value);
        String phoneNumber = phoneNumberText.getText().toString();

        if(!validatePhoneNumber(phoneNumber)) {
            phoneNumberText.setError(getString(R.string.err_tel));
            return null;
        }

        EditText seatNumberText =
                (EditText) frame.findViewById(R.id.seat_assignment_value);
        String seatNumber = seatNumberText.getText().toString();

        EditText originText =
                (EditText) frame.findViewById(R.id.origin_value);
        String origin = originText.getText().toString();

        EditText destinationText =
                (EditText) frame.findViewById(R.id.destination_value);
        String destination = destinationText.getText().toString();

        Button departureDateButton =
                (Button) frame.findViewById(R.id.bus_departure_date_value);
        String departureDate = departureDateButton.getText().toString();
        if(departureDate.equals(getString(R.string.select_date))) {
            departureDate = "";
        }

        Button departureTimeButton =
                (Button) frame.findViewById(R.id.bus_departure_time_value);
        String departureTime = departureTimeButton.getText().toString();
        if(departureTime.equals(getString(R.string.select_time))) {
            departureTime = "";
        }

        Button arrivalTimeButton =
                (Button) frame.findViewById(R.id.arrival_time_value);
        String arrivalTime = arrivalTimeButton.getText().toString();
        if(arrivalTime.equals(getString(R.string.select_time))) {
            arrivalTime = "";
        }

        return new BusInfo(name, confNumber, phoneNumber, seatNumber,
                origin, destination, departureTime, arrivalTime, departureDate);
    }

    @Override
    public void onClick(View v) {
        String selectedItem = informationType.getSelectedItem().toString();
        Info infoItem;
        Intent resultIntent = new Intent();
        if(v == addButton){
            switch(selectedItem){
                case HOTEL_INFORMATION:
                    infoItem = handleHotelInfoClick();
                    if(infoItem == null){
                        return;
                    }
                    resultIntent.putExtra(getResources().getString(R.string.info_item),
                            (Parcelable)(infoItem));
                    break;
                case FLIGHT_INFORMATION:
                    infoItem = handleFlightInfoClick();
                    if(infoItem == null){
                        return;
                    }
                    resultIntent.putExtra(getResources().getString(R.string.info_item),
                            (Parcelable)(infoItem));
                    break;
                case CRUISE_INFORMATION:
                    infoItem = handleCruiseInfoClick();
                    if(infoItem == null){
                        return;
                    }
                    resultIntent.putExtra(getResources().getString(R.string.info_item),
                            (Parcelable)(infoItem));
                    break;
                case BUS_INFORMATION:
                    infoItem = handleBusInfoClick();
                    if(infoItem == null){
                        return;
                    }
                    resultIntent.putExtra(getResources().getString(R.string.info_item),
                            (Parcelable)(infoItem));
                    break;
            }

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
        else if(v == deleteButton){
            resultIntent.putExtra(getResources().getString(R.string.delete), true);
            switch(typeIn){
                case CRUISE_INFORMATION:
                    resultIntent.putExtra(getResources().getString(R.string.info_item),
                            (Parcelable)(infoItemIn));
                    break;
                case HOTEL_INFORMATION:
                    resultIntent.putExtra(getResources().getString(R.string.info_item),
                            (Parcelable)(infoItemIn));
                    break;
                case FLIGHT_INFORMATION:
                    resultIntent.putExtra(getResources().getString(R.string.info_item),
                            (Parcelable)(infoItemIn));
                    break;
                case BUS_INFORMATION:
                    resultIntent.putExtra(getResources().getString(R.string.info_item),
                            (Parcelable)(infoItemIn));
                    break;
            }

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}
