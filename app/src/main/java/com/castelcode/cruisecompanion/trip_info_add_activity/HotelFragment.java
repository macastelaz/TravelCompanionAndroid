package com.castelcode.cruisecompanion.trip_info_add_activity;

import android.app.AlertDialog;
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
import com.castelcode.cruisecompanion.trip_info_add_activity.info_items.HotelInfo;
import com.castelcode.cruisecompanion.utils.DateStringUtil;
import com.castelcode.cruisecompanion.utils.TimeStringUtil;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CruiseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotelFragment extends Fragment implements View.OnClickListener{

    private static final int STANDARD_CHECK_IN_TIME_HOUR = 14;
    private static final int STANDARD_CHECK_IN_TIME_MINUTE = 0;
    private static final int STANDARD_CHECK_OUT_TIME_HOUR = 12;
    private static final int STANDARD_CHECK_OUT_TIME_MINUTE = 0;

    private HotelInfo info;

    Button checkInDateButton = null;
    Button checkInTimeButton = null;
    Button checkOutDateButton = null;
    Button checkOutTimeButton = null;

    //private OnFragmentInteractionListener mListener;

    public HotelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CruiseFragment.
     */
    public static HotelFragment newInstance() {
        HotelFragment fragment = new HotelFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                info = getArguments().getParcelable(
                        getResources().getString(R.string.info_item));
            }
            catch (ClassCastException ex) {
               System.out.println(ex.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hotel, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle resources){
        View viewToUse = this.getView();

        if(viewToUse != null) {
            checkInDateButton =
                    (Button) viewToUse.findViewById(R.id.check_in_date_value);
            checkInTimeButton =
                    (Button) viewToUse.findViewById(R.id.check_in_time_value);
            checkOutDateButton =
                    (Button) viewToUse.findViewById(R.id.check_out_date_value);
            checkOutTimeButton =
                    (Button) viewToUse.findViewById(R.id.check_out_time_value);

            checkInDateButton.setOnClickListener(this);
            checkInTimeButton.setOnClickListener(this);
            checkOutDateButton.setOnClickListener(this);
            checkOutTimeButton.setOnClickListener(this);

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

            EditText addressText =
                    (EditText) viewToUse.findViewById(R.id.address_value);
            addressText.setText(info.getAddress());

            EditText cityText =
                    (EditText) viewToUse.findViewById(R.id.city_value);
            cityText.setText(info.getCity());

            EditText stateProvinceText =
                    (EditText) viewToUse.findViewById(R.id.state_province_value);
            stateProvinceText.setText(info.getStateProvince());
            if(info.getStartDate() == null || info.getStartDate().equals("")){
                checkInDateButton.setText(getString(R.string.select_date));
            } else {
                checkInDateButton.setText(info.getStartDate());
            }
            if(info.getStartTime() == null || info.getStartTime().equals("")){
                checkInTimeButton.setText(getString(R.string.select_time));
            } else {
                checkInTimeButton.setText(info.getStartTime());
            }
            if(info.getCheckOutDate() == null || info.getCheckOutDate().equals("")){
                checkOutDateButton.setText(getString(R.string.select_date));
            } else {
                checkOutDateButton.setText(info.getCheckOutDate());
            }
            if(info.getCheckOutTime() == null || info.getCheckOutTime().equals("")){
                checkOutTimeButton.setText(getString(R.string.select_time));
            } else {
                checkOutTimeButton.setText(info.getCheckOutTime());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(getActivity().getCurrentFocus() != null)
            getActivity().getCurrentFocus().clearFocus();
        if(v == checkInDateButton) {
            String defaultDate = "";
            Calendar cruiseStartDate = HomePage.cruise.getCruiseDateTime();
            if(cruiseStartDate != null) {
                defaultDate = DateStringUtil.intToDateString(
                        cruiseStartDate.get(Calendar.MONTH),
                        cruiseStartDate.get(Calendar.DAY_OF_MONTH),
                        cruiseStartDate.get(Calendar.YEAR));
            }
            createDatePickerDialog(checkInDateButton,  getString(R.string.select_check_in_date),
                    getString(R.string.confirm), getString(R.string.cancel), defaultDate);
        }
        else if(v == checkInTimeButton) {
            createTimePickerDialog(checkInTimeButton,  getString(R.string.select_check_in_time),
                    getString(R.string.confirm), getString(R.string.cancel),
                    TimeStringUtil.createTimeString(STANDARD_CHECK_IN_TIME_HOUR,
                            STANDARD_CHECK_IN_TIME_MINUTE));
        }
        else if(v == checkOutDateButton) {
            String defaultDate = checkInDateButton.getText().toString();
            if(defaultDate.equals(getString(R.string.select_date))) {
                Calendar cruiseStartDate = HomePage.cruise.getCruiseDateTime();
                if(cruiseStartDate != null) {
                    defaultDate = DateStringUtil.intToDateString(
                            cruiseStartDate.get(Calendar.MONTH),
                            cruiseStartDate.get(Calendar.DAY_OF_MONTH),
                            cruiseStartDate.get(Calendar.YEAR));
                }
            }
            createDatePickerDialog(checkOutDateButton, getString(R.string.select_check_out_date),
                    getString(R.string.confirm), getString(R.string.cancel), defaultDate);
        }
        else if(v == checkOutTimeButton) {
            createTimePickerDialog(checkOutTimeButton,  getString(R.string.select_check_out_time),
                    getString(R.string.confirm), getString(R.string.cancel),
                    TimeStringUtil.createTimeString(STANDARD_CHECK_OUT_TIME_HOUR,
                            STANDARD_CHECK_OUT_TIME_MINUTE));
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
