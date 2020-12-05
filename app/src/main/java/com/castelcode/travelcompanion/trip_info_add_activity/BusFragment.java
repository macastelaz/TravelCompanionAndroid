package com.castelcode.travelcompanion.trip_info_add_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.castelcode.travelcompanion.HomePage;
import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.trip_info_add_activity.info_items.BusInfo;
import com.castelcode.travelcompanion.utils.DateStringUtil;
import com.castelcode.travelcompanion.utils.TimeStringUtil;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CruiseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusFragment extends Fragment implements View.OnClickListener{

    private static final int DEFAULT_HOUR = 12;
    private static final int DEFAULT_MINUTE = 0;

    private BusInfo info;

    Button departureDateButton = null;
    Button departureTimeButton = null;
    Button arrivalTimeButton = null;

    //private OnFragmentInteractionListener mListener;

    public BusFragment() {
        // FlightFragment empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CruiseFragment.
     */
    public static BusFragment newInstance() {
        BusFragment fragment = new BusFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            info = getArguments().getParcelable(
                    getResources().getString(R.string.info_item));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle resources){
        View viewToUse = this.getView();
        if(viewToUse != null) {
            departureDateButton = viewToUse.findViewById(R.id.bus_departure_date_value);
            departureTimeButton = viewToUse.findViewById(R.id.bus_departure_time_value);
            arrivalTimeButton = viewToUse.findViewById(R.id.arrival_time_value);

            departureTimeButton.setOnClickListener(this);
            arrivalTimeButton.setOnClickListener(this);
        }
        if(info != null && viewToUse != null){
            EditText nameText = viewToUse.findViewById(R.id.name_value);
            nameText.setText(info.getPrimaryName());

            EditText confNumberText = viewToUse.findViewById(R.id.confrimation_code_value);
            confNumberText.setText(info.getConfirmationNumber());

            EditText phoneNumberText = viewToUse.findViewById(R.id.phone_number_value);
            phoneNumberText.setText(info.getPhoneNumber());

            EditText seatNumberText = viewToUse.findViewById(R.id.seat_assignment_value);
            seatNumberText.setText(info.getSeatNumber());

            EditText originText = viewToUse.findViewById(R.id.origin_value);
            originText.setText(info.getOrigin());

            EditText destinationText = viewToUse.findViewById(R.id.destination_value);
            destinationText.setText(info.getDestination());
            if(info.getStartDate() == null || info.getStartDate().equals("")){
                departureDateButton.setText(getString(R.string.select_date));
            } else {
                departureDateButton.setText(info.getStartDate());
            }
            if(info.getStartTime() == null || info.getStartTime().equals("")){
                departureTimeButton.setText(getString(R.string.select_time));
            } else {
                departureTimeButton.setText(info.getStartTime());
            }
            if(info.getArrivalTime() == null || info.getArrivalTime().equals("")){
                arrivalTimeButton.setText(getString(R.string.select_arrival_time));
            } else {
                arrivalTimeButton.setText(info.getArrivalTime());
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if(getActivity().getCurrentFocus() != null) {
            getActivity().getCurrentFocus().clearFocus();
        }
        if (v == departureDateButton) {
            String defaultDate = "";
            Calendar cruiseStartDate = HomePage.cruise.getCruiseDateTime();
            if(cruiseStartDate != null) {
                defaultDate = DateStringUtil.intToDateString(
                        cruiseStartDate.get(Calendar.MONTH),
                        cruiseStartDate.get(Calendar.DAY_OF_MONTH),
                        cruiseStartDate.get(Calendar.YEAR));
            }
            createDatePickerDialog(departureDateButton,  getString(R.string.select_departure_date),
                    getString(R.string.confirm), getString(R.string.cancel), defaultDate);
        }
        else if(v == departureTimeButton) {
            createTimePickerDialog(departureTimeButton,  getString(R.string.select_departure_time),
                    getString(R.string.confirm), getString(R.string.cancel),
                    TimeStringUtil.createTimeString(DEFAULT_HOUR, DEFAULT_MINUTE));
        }
        else if (v == arrivalTimeButton) {
            createTimePickerDialog(arrivalTimeButton,  getString(R.string.select_arrival_time),
                    getString(R.string.confirm), getString(R.string.cancel),
                    TimeStringUtil.createTimeString(DEFAULT_HOUR, DEFAULT_MINUTE));
        }
    }

    private void createDatePickerDialog(final Button launcher, String title, String positiveMessage,
                                        String negativeMessage, String defaulDate){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        final DatePicker picker = new DatePicker(this.getActivity());
        if(!defaulDate.equals("") && !defaulDate.equals(getString(R.string.select_date))){
            picker.init(DateStringUtil.getYear(defaulDate),
                    DateStringUtil.getMonth(defaulDate),
                    DateStringUtil.getDay(defaulDate), (DatePicker view, int year, int monthOfYear,
                                                        int dayOfMonth) -> {
                    });
        }
        builder.setTitle(title)
                .setView(picker)
                .setPositiveButton(positiveMessage, (DialogInterface dialog, int which) -> {
                        int day = picker.getDayOfMonth();
                        int month = picker.getMonth() + 1;
                        int year = picker.getYear();
                        launcher.setText(DateStringUtil.intToDateString(month, day, year));
                    })
                .setNegativeButton(negativeMessage, null);
        builder.show();
    }

    private void createTimePickerDialog(final Button launcher, String title, String positiveMessage,
                                        String negativeMessage, String defaultTime){
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        final TimePicker picker = new TimePicker(this.getActivity());
        if(!defaultTime.equals("")) {
            int hour = TimeStringUtil.getHour(defaultTime);
            if (TimeStringUtil.getAMorPM(defaultTime) == Calendar.PM) {
                hour += 12;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                picker.setHour(hour);
                picker.setMinute(TimeStringUtil.getMinute(defaultTime));
            }
            else{
                picker.setCurrentHour(hour);
                picker.setCurrentMinute(TimeStringUtil.getMinute(defaultTime));
            }
        }
        builder.setTitle(title)
                .setView(picker)
                .setPositiveButton(positiveMessage, (DialogInterface dialog, int which) -> {
                        int hour, minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = picker.getHour();
                            minute = picker.getMinute();
                        }
                        else {
                            hour = picker.getCurrentHour();
                            minute = picker.getCurrentMinute();
                        }
                        launcher.setText(TimeStringUtil.getSummaryString(
                                TimeStringUtil.createTimeString(hour, minute)));
                    })
                .setNegativeButton(negativeMessage, null);
        builder.show();
    }
}
