package com.castelcode.cruisecompanion.tile_activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.converters.Converter;
import com.castelcode.cruisecompanion.converters.CurrencyConverter;
import com.castelcode.cruisecompanion.converters.DistanceConverter;
import com.castelcode.cruisecompanion.converters.SpeedConverter;
import com.castelcode.cruisecompanion.converters.TemperatureConverter;
import com.castelcode.cruisecompanion.utils.ConversionConstants;
import com.castelcode.cruisecompanion.utils.JsonObjectRequestFactory;
import com.castelcode.cruisecompanion.utils.RequestQueueSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UnitConverter extends AppCompatActivity implements View.OnClickListener, TextWatcher,
        AdapterView.OnItemSelectedListener{

    private Spinner conversionType;
    private Spinner originalUnit;
    private Spinner desiredUnit;

    private EditText originalValue;
    private EditText desiredValue;

    private Button swapButton;

    private TextView disclaimer;

    private Converter converter;

    private int oldOriginalUnitPos;
    private int oldDesiredUnitPos;
    private Context context = this;
    private String primaryApiUrl = "http://www.castelcode.com/CruiseCompanion";
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

        conversionType = (Spinner) findViewById(R.id.conversion_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> conversionAdapter = ArrayAdapter.createFromResource(this,
                R.array.supported_conversions, R.layout.conversion_spinner);
        // Specify the layout to use when the list of choices appears
        conversionAdapter.setDropDownViewResource(R.layout.conversion_spinner_dropdown);
        // Apply the adapter to the spinner
        conversionType.setAdapter(conversionAdapter);
        conversionType.setOnItemSelectedListener(this);

        originalUnit = (Spinner) findViewById(R.id.original_unit);
        originalUnitAdapter = ArrayAdapter.createFromResource(this,
                R.array.supported_currencies, R.layout.conversion_spinner);
        
        desiredUnit = (Spinner) findViewById(R.id.desired_unit);
        desiredUnitAdapter = ArrayAdapter.createFromResource(this,
                R.array.supported_currencies, R.layout.conversion_spinner);
        originalUnit.setOnItemSelectedListener(this);

        updateDropdownResources();

        originalUnit.setAdapter(originalUnitAdapter);
        desiredUnit.setAdapter(desiredUnitAdapter);


        desiredUnit.setOnItemSelectedListener(this);

        originalValue = (EditText) findViewById(R.id.original_value);
        originalValue.addTextChangedListener(this);
        desiredValue = (EditText) findViewById(R.id.desired_value);
        desiredValue.addTextChangedListener(this);

        swapButton = (Button) findViewById(R.id.swap_origin_with_destination);
        swapButton.setOnClickListener(this);

        disclaimer = (TextView) findViewById(R.id.disclaimer);
        conversionTypeChanged();
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
        if(originalValue.getText().toString().equals("") &&
                desiredValue.getText().toString().equals(""))
            return;
        Locale defaultLocale = Locale.getDefault();
        if(getCurrentFocus() != null && getCurrentFocus().equals(originalValue)){
            try {
                converter.setOriginalValue(Double.parseDouble(
                        originalValue.getText().toString()));
            } catch (NumberFormatException e) {
                //Original value did not contain valid double.
                converter.setOriginalValue(0);
            }
            double result = converter.convert(originalUnit.getSelectedItem().toString(),
                    desiredUnit.getSelectedItem().toString());
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
                    originalUnit.getSelectedItem().toString());
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

        }
        updateDropdownResources();
        originalUnit.setAdapter(originalUnitAdapter);
        desiredUnit.setAdapter(desiredUnitAdapter);
        updateOldPositions();
        resetUIElements();
    }

    private void updateDropdownResources(){
        originalUnitAdapter.setDropDownViewResource(R.layout.conversion_spinner_dropdown);
        desiredUnitAdapter.setDropDownViewResource(R.layout.conversion_spinner_dropdown);
    }

    private void resetUIElements(){
        originalValue.removeTextChangedListener(this);
        originalValue.setText("");
        originalValue.addTextChangedListener(this);

        desiredValue.removeTextChangedListener(this);
        desiredValue.setText("");
        desiredValue.addTextChangedListener(this);

        changeDisclaimerVisibilityIfNecessary();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.equals(conversionType)) {
            conversionTypeChanged();
        }
        else if(parent.equals(originalUnit)){
            originalUnitChanged();
        }
        else if(parent.equals(desiredUnit)){
            desiredUnitChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
