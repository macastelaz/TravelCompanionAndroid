package com.castelcode.cruisecompanion.trip_info_add_activity;

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

import com.castelcode.cruisecompanion.HomePage;
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.CruiseInfo;
import com.castelcode.cruisecompanion.utils.DateStringUtil;
import com.castelcode.cruisecompanion.utils.TimeStringUtil;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CruiseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CruiseFragment extends Fragment implements View.OnClickListener{

    private static final int DEFAULT_HOUR = 12;
    private static final int DEFAULT_MINUTE = 0;

    private CruiseInfo info;

    Button departureDateButton = null;
    Button departureTimeButton = null;

    //private OnFragmentInteractionListener mListener;

    public CruiseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CruiseFragment.
     */
    public static CruiseFragment newInstance() {
        CruiseFragment fragment = new CruiseFragment();
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
        return inflater.inflate(R.layout.fragment_cruise, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle resources){
        View viewToUse = this.getView();
        if(viewToUse != null) {
            departureDateButton = (Button) viewToUse.findViewById(R.id.cruise_departure_date_value);
            departureTimeButton = (Button) viewToUse.findViewById(R.id.cruise_departure_time_value);

            departureDateButton.setOnClickListener(this);
            departureTimeButton.setOnClickListener(this);
        }
        if(info != null && viewToUse != null){
            EditText nameText = (EditText) viewToUse.findViewById(R.id.name_value);
            nameText.setText(info.getPrimaryName());

            EditText confNumberText =
                    (EditText) viewToUse.findViewById(R.id.confrimation_code_value);
            confNumberText.setText(info.getConfirmationNumber());

            EditText phoneNumberText =
                    (EditText) viewToUse.findViewById(R.id.phone_number_value);
            phoneNumberText.setText(info.getPhoneNumber());
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
            EditText roomNumberText =
                    (EditText) viewToUse.findViewById(R.id.room_number_value);
            roomNumberText.setText(info.getRoomNumber());

            EditText shipNameText =
                    (EditText) viewToUse.findViewById(R.id.ship_name_value);
            shipNameText.setText(info.getShipName());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(getActivity().getCurrentFocus() != null) {
            getActivity().getCurrentFocus().clearFocus();
        }
        if(v == departureDateButton) {
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
