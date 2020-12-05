package com.castelcode.travelcompanion.tile_activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.conversions.DesiredSingleValueFragment;
import com.castelcode.travelcompanion.conversions.DesiredTimeValueFragment;
import com.castelcode.travelcompanion.conversions.OriginalSingleValueFragment;
import com.castelcode.travelcompanion.conversions.OriginalTimeValueFragment;
import com.castelcode.travelcompanion.converters.Converter;
import com.castelcode.travelcompanion.converters.CurrencyConverter;
import com.castelcode.travelcompanion.converters.DistanceConverter;
import com.castelcode.travelcompanion.converters.SpeedConverter;
import com.castelcode.travelcompanion.converters.TemperatureConverter;
import com.castelcode.travelcompanion.converters.TimeConverter;
import com.castelcode.travelcompanion.converters.TimeWrapper;
import com.castelcode.travelcompanion.utils.JsonObjectRequestFactory;
import com.castelcode.travelcompanion.utils.RequestQueueSingleton;

import java.lang.reflect.Array;
import java.util.Locale;
import java.util.Objects;

public class UnitConverter extends AppCompatActivity implements View.OnClickListener, TextWatcher,
        AdapterView.OnItemSelectedListener{

    private Spinner conversionType;
    private Spinner originalUnit;
    private Spinner desiredUnit;

    private EditText originalValue;
    private EditText originalValueHour;
    private EditText originalValueMinute;
    private EditText desiredValue;
    private EditText desiredValueHour;
    private EditText desiredValueMinute;

    private Button swapButton;

    private TextView disclaimer;

    private Converter converter;

    private int oldOriginalUnitPos;
    private int oldDesiredUnitPos;
    private Context context = this;
    private String primaryApiUrl = "http://www.castelcode.com/travelcompanion";
    private String backupApiUrl = "http://data.fixer.io/api/latest?access_key=61dbeaf8d8b07977fff2aa2e689868cd";

    JsonObjectRequest jsonObjectRequest = JsonObjectRequestFactory
            .createCurrencyConversionJsonObjectRequest(context, primaryApiUrl, backupApiUrl);

    ArrayAdapter<CharSequence> originalUnitAdapter;
    ArrayAdapter<CharSequence> desiredUnitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestQueueSingleton.getInstance(this.getApplicationContext())
                .addToRequestQueue(jsonObjectRequest);
        setContentView(R.layout.activity_unit_converter);

        conversionType = findViewById(R.id.conversion_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> conversionAdapter = ArrayAdapter.createFromResource(this,
                R.array.supported_conversions, R.layout.conversion_spinner);
        // Specify the layout to use when the list of choices appears
        conversionAdapter.setDropDownViewResource(R.layout.conversion_spinner_dropdown);
        // Apply the adapter to the spinner
        conversionType.setAdapter(conversionAdapter);
        conversionType.setOnItemSelectedListener(this);
        updateFragment();
        originalUnit = findViewById(R.id.original_unit);
        originalUnitAdapter = ArrayAdapter.createFromResource(this,
                R.array.supported_currencies, R.layout.conversion_spinner);
        
        desiredUnit = findViewById(R.id.desired_unit);
        desiredUnitAdapter = ArrayAdapter.createFromResource(this,
                R.array.supported_currencies, R.layout.conversion_spinner);
        originalUnit.setOnItemSelectedListener(this);

        updateDropdownResources();

        originalUnit.setAdapter(originalUnitAdapter);
        desiredUnit.setAdapter(desiredUnitAdapter);


        desiredUnit.setOnItemSelectedListener(this);

        swapButton = findViewById(R.id.swap_origin_with_destination);
        swapButton.setOnClickListener(this);

        disclaimer = findViewById(R.id.disclaimer);
    }

    private void updateOldPositions(){
        if(desiredUnit.getSelectedItemPosition() == originalUnit.getSelectedItemPosition()){
            try{
                desiredUnit.setSelection(desiredUnit.getSelectedItemPosition() + 1);
            }
            catch (Exception ex){
                desiredUnit.setSelection(desiredUnit.getSelectedItemPosition() - 1);
            }
        }
        oldOriginalUnitPos = originalUnit.getSelectedItemPosition();
        oldDesiredUnitPos = desiredUnit.getSelectedItemPosition();
    }

    private void changeDisclaimerVisibilityIfNecessary(){
        if(conversionType.getSelectedItem().toString().equals(
                getResources().getString(R.string.currency))){
            disclaimer.setVisibility(View.VISIBLE);
        }
        else{
            disclaimer.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        FrameLayout originalValueFrame = findViewById(R.id.original_value_fragment);
        FrameLayout desiredValueFrame = findViewById(R.id.desired_value_fragment);
        if (converter instanceof TimeConverter) {
            maybeSetEditTextValuesForTime();
        } else {
            originalValue = originalValueFrame.findViewById(R.id.original_single_value);
            originalValue.addTextChangedListener(this);
            desiredValue = desiredValueFrame.findViewById(R.id.desired_single_value);
            desiredValue.addTextChangedListener(this);
        }
        conversionTypeChanged();
    }
    @Override
    public void onClick(View v) {
        if(v == swapButton){
            int originalUnitPos = originalUnit.getSelectedItemPosition();
            originalUnit.setSelection(desiredUnit.getSelectedItemPosition());
            desiredUnit.setSelection(originalUnitPos);
            oldOriginalUnitPos = originalUnit.getSelectedItemPosition();
            oldDesiredUnitPos = desiredUnit.getSelectedItemPosition();
            updateConversion();
        }
    }

    private void updateConversion(){
        Locale defaultLocale = Locale.getDefault();
        if (converter instanceof TimeConverter) {
            if (originalValueHour.getText().toString().isEmpty()
                    && originalValueMinute.getText().toString().isEmpty()
                    && desiredValueHour.getText().toString().isEmpty()
                    && desiredValueMinute.getText().toString().isEmpty()) {
                return;
            }
            handleTimeValueConversion(defaultLocale);
        }
        else {
            if(originalValue.getText().toString().isEmpty() &&
                    desiredValue.getText().toString().isEmpty())
                return;
            handleSingleValueConversion(defaultLocale);
        }
    }

    private void handleTimeValueConversion(Locale defaultLocale) {
        if(getCurrentFocus() != null
                && (getCurrentFocus().equals(originalValueHour)
                        || getCurrentFocus().equals(originalValueMinute))) {
            try {
                ((TimeConverter)converter).setOriginalHour(Integer.parseInt(
                        originalValueHour.getText().toString()));
            } catch (NumberFormatException e) {
                //Original value did not contain valid double.
                ((TimeConverter)converter).setOriginalHour(0);
            }
            try {
                ((TimeConverter)converter).setOriginalMinute(Integer.parseInt(
                        originalValueMinute.getText().toString()));
            } catch (NumberFormatException e) {
                //Original value did not contain valid double.
                ((TimeConverter)converter).setOriginalMinute(0);
            }

            TimeWrapper result = ((TimeConverter)converter).convert(
                    originalUnit.getSelectedItem().toString(),
                    desiredUnit.getSelectedItem().toString());
            desiredValueHour.removeTextChangedListener(this);
            desiredValueMinute.removeTextChangedListener(this);
            desiredValueHour.setText(String.format(defaultLocale, "%02d", result.getHour()));
            desiredValueMinute.setText(String.format(defaultLocale, "%02d",
                    result.getMinute()));
            desiredValueHour.addTextChangedListener(this);
            desiredValueMinute.addTextChangedListener(this);
        }
        else{
            try {
                ((TimeConverter)converter).setOriginalHour(Integer.parseInt(
                        desiredValueHour.getText().toString()));
            } catch (NumberFormatException e) {
                //Original value did not contain valid double.
                ((TimeConverter)converter).setOriginalHour(0);
            }
            try {
                ((TimeConverter)converter).setOriginalMinute(Integer.parseInt(
                        desiredValueMinute.getText().toString()));
            } catch (NumberFormatException e) {
                //Original value did not contain valid double.
                ((TimeConverter)converter).setOriginalMinute(0);
            }

            TimeWrapper result = ((TimeConverter)converter).convert(
                    desiredUnit.getSelectedItem().toString(),
                    originalUnit.getSelectedItem().toString());

            originalValueHour.removeTextChangedListener(this);
            originalValueMinute.removeTextChangedListener(this);
            originalValueHour.setText(String.format(defaultLocale, "%02d", result.getHour()));
            originalValueMinute.setText(String.format(defaultLocale, "%02d",
                    result.getMinute()));
            originalValueHour.addTextChangedListener(this);
            originalValueMinute.addTextChangedListener(this);
        }
    }

    private void handleSingleValueConversion(Locale defaultLocale) {
        if(getCurrentFocus() != null && getCurrentFocus().equals(originalValue)){
            try {
                converter.setOriginalValue(Double.parseDouble(
                        originalValue.getText().toString()));
            } catch (NumberFormatException e) {
                //Original value did not contain valid double.
                converter.setOriginalValue(0);
            }
            double result = converter.convert(originalUnit.getSelectedItem().toString(),
                    desiredUnit.getSelectedItem().toString()).getResult();
            desiredValue.removeTextChangedListener(this);
            desiredValue.setText(String.format(defaultLocale, "%.2f", result));
            desiredValue.addTextChangedListener(this);
        }
        else{
            try {
                converter.setOriginalValue(Double.parseDouble(
                        desiredValue.getText().toString()));
            } catch (NumberFormatException e) {
                converter.setOriginalValue(0);
            }
            double result = converter.convert(desiredUnit.getSelectedItem().toString(),
                    originalUnit.getSelectedItem().toString()).getResult();
            originalValue.removeTextChangedListener(this);
            originalValue.setText(String.format(defaultLocale, "%.2f", result));
            originalValue.addTextChangedListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        EditText currentView = (EditText) getCurrentFocus();
        if (Objects.requireNonNull(currentView).equals(originalValueHour)
                || Objects.requireNonNull(currentView).equals(desiredValueHour)) {
            try {
                if (Integer.parseInt(s.toString()) > 23) {
                    Objects.requireNonNull(currentView)
                            .setText(getResources().getString(R.string.max_hours));
                }
            } catch (NumberFormatException ex) {
                // This is normal, so it's okay!
            }
        } else if(Objects.requireNonNull(currentView).equals(originalValueMinute)
                || Objects.requireNonNull(currentView).equals(originalValueMinute)) {
            try {
                if (Integer.parseInt(s.toString()) > 59) {
                    Objects.requireNonNull(currentView)
                            .setText(getResources().getString(R.string.max_minutes));
                }
            } catch (NumberFormatException ex){
                // This is normal, so it's okay!
            }
        }
        updateConversion();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void conversionTypeChanged(){
        if (conversionType.getSelectedItem().equals(getResources().getString(R.string.currency))) {
            converter = new CurrencyConverter(this);
            originalUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_currencies, R.layout.conversion_spinner);
            desiredUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_currencies, R.layout.conversion_spinner);
        } else if (conversionType.getSelectedItem().equals(getResources().getString(R.string.speed))) {
            converter = new SpeedConverter(this);
            originalUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_speeds, R.layout.conversion_spinner);
            desiredUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_speeds, R.layout.conversion_spinner);
        } else if (conversionType.getSelectedItem().equals(
                getResources().getString(R.string.distance))) {
            converter = new DistanceConverter(this);
            originalUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_distances, R.layout.conversion_spinner);
            desiredUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_distances, R.layout.conversion_spinner);
        }
        else if (conversionType.getSelectedItem().equals(
                getResources().getString(R.string.temperature))){
            converter = new TemperatureConverter(this);
            originalUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_temperatures, R.layout.conversion_spinner);
            desiredUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_temperatures, R.layout.conversion_spinner);

        } else if (conversionType.getSelectedItem().equals(
                getResources().getString(R.string.time))) {
            converter = new TimeConverter(this);
            originalUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_timezones, R.layout.conversion_spinner);
            desiredUnitAdapter = ArrayAdapter.createFromResource(this,
                    R.array.supported_timezones, R.layout.conversion_spinner);
        }
        updateDropdownResources();
        originalUnit.setAdapter(originalUnitAdapter);
        desiredUnit.setAdapter(desiredUnitAdapter);
        updateOldPositions();
    }

    private void updateDropdownResources(){
        originalUnitAdapter.setDropDownViewResource(R.layout.conversion_spinner_dropdown);
        desiredUnitAdapter.setDropDownViewResource(R.layout.conversion_spinner_dropdown);
    }

    private void originalUnitChanged(){
        if(originalUnit.getSelectedItemPosition() == desiredUnit.getSelectedItemPosition()){
            try{
                desiredUnit.setSelection(oldOriginalUnitPos);
                oldDesiredUnitPos = oldOriginalUnitPos;
                oldOriginalUnitPos = originalUnit.getSelectedItemPosition();
            }
            catch (Exception ex){
                //LOG
            }
        }
        updateConversion();
    }

    private void desiredUnitChanged(){
        if(originalUnit.getSelectedItemPosition() == desiredUnit.getSelectedItemPosition()){
            try{
                originalUnit.setSelection(oldDesiredUnitPos);
                oldOriginalUnitPos = oldDesiredUnitPos;
                oldDesiredUnitPos = desiredUnit.getSelectedItemPosition();
            }
            catch (Exception ex){
                //LOG
            }
        }
        updateConversion();
    }

    private void updateFragment() {
        String selectedItem = conversionType.getSelectedItem().toString();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment originalFrag;
        Fragment desiredFrag;
        switch (selectedItem) {
            case "Time":
                originalFrag = OriginalTimeValueFragment.newInstance();
                desiredFrag = DesiredTimeValueFragment.newInstance();
                fragmentTransaction.replace(R.id.original_value_fragment, originalFrag);
                fragmentTransaction.replace(R.id.desired_value_fragment, desiredFrag);
                break;
            default:
                originalFrag = OriginalSingleValueFragment.newInstance();
                desiredFrag = DesiredSingleValueFragment.newInstance();
                fragmentTransaction.replace(R.id.original_value_fragment, originalFrag);
                fragmentTransaction.replace(R.id.desired_value_fragment, desiredFrag);
                break;
        }
        fragmentTransaction.commit();
        fm.executePendingTransactions();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.equals(conversionType)) {
            updateFragment();
            conversionTypeChanged();
            maybeSetEditTextValuesForTime();
            maybeSetEditTextValuesForGeneric();
        }
        else if(parent.equals(originalUnit)){
            originalUnitChanged();
        }
        else if(parent.equals(desiredUnit)){
            desiredUnitChanged();
        }
    }

    private void maybeSetEditTextValuesForGeneric() {
        if (!(converter instanceof TimeConverter)) {
            FrameLayout originalValueFrame = findViewById(R.id.original_value_fragment);
            FrameLayout desiredValueFrame = findViewById(R.id.desired_value_fragment);

            originalValue = originalValueFrame.findViewById(R.id.original_single_value);
            desiredValue = desiredValueFrame.findViewById(R.id.desired_single_value);

            originalValue.addTextChangedListener(this);
            desiredValue.addTextChangedListener(this);

            changeDisclaimerVisibilityIfNecessary();
        }
    }

    private void maybeSetEditTextValuesForTime() {
        if (converter instanceof TimeConverter) {
            FrameLayout originalValueFrame = findViewById(R.id.original_value_fragment);
            FrameLayout desiredValueFrame = findViewById(R.id.desired_value_fragment);

            originalValueHour = originalValueFrame.findViewById(R.id.original_time_value_hour);
            originalValueHour.addTextChangedListener(this);
            originalValueMinute = originalValueFrame.findViewById(R.id.original_time_value_minute);
            originalValueMinute.addTextChangedListener(this);
            desiredValueHour = desiredValueFrame.findViewById(R.id.desired_time_value_hour);
            desiredValueHour.addTextChangedListener(this);
            desiredValueMinute = desiredValueFrame.findViewById(R.id.desired_time_value_minute);
            desiredValueMinute.addTextChangedListener(this);
            changeDisclaimerVisibilityIfNecessary();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
