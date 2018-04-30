package com.castelcode.cruisecompanion.tile_activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.castelcode.cruisecompanion.HomePage;
import com.castelcode.cruisecompanion.R;
import com.castelcode.cruisecompanion.utils.DrinkConstants;
import com.castelcode.cruisecompanion.utils.ButtonUtil;
import com.castelcode.cruisecompanion.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class DrinkCounter extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener {

    private static final int DEFAULT_NUM_BEVERAGES_CONSUMED = 0;

    private static int numDrinksConsumed;

    private TextView numDrinksConsumedTV;
    private TextView valueDrinkConsumedTV;

    private TextView drinkTypeCountLabel;
    private TextView drinkTypeCountValue;
    private TextView drinkTypeCountDisclaimer;

    private Button consumeDrinkButton;
    private Button removeDrinkButton;

    private Spinner drinkSpinner;

    ArrayList<String> drinkTypes;
    ArrayAdapter<String> drinkTypeAdapter;
    HashMap<String, Integer> numDrinksPerType;
    Locale locale = Locale.getDefault();

    double currentValue = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_counter);

        drinkTypeCountLabel = (TextView) findViewById(R.id.drink_type_count_label);
        drinkTypeCountValue = (TextView) findViewById(R.id.drink_type_count_value);
        drinkTypeCountDisclaimer = (TextView) findViewById(R.id.drink_type_count_disclaimer);

        drinkTypes = new ArrayList<>();

        numDrinksPerType = SharedPreferencesManager.getDrinksConsumed(this);

        if(numDrinksPerType != null && numDrinksPerType.size() > DrinkConstants.DRINK_TYPES.size()) {
            Iterator<Map.Entry<String,Integer>> iter = numDrinksPerType.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String,Integer> entry = iter.next();
                if(!DrinkConstants.DRINK_TYPES.containsKey(entry.getKey())){
                    iter.remove();
                }
            }
        }

        for (String key: DrinkConstants.DRINK_TYPES.keySet()) {

            if(numDrinksPerType == null){
                numDrinksPerType = new HashMap<>();
            }
            if (numDrinksPerType.get(key) == null) {
                numDrinksPerType.put(key, 0);
                drinkTypes.add(key + " - 0");
            }
            else {
                currentValue += numDrinksPerType.get(key) * DrinkConstants.DRINK_TYPES.get(key);
                drinkTypes.add(key + " - " + numDrinksPerType.get(key));
            }
        }

        drinkTypeAdapter =  new ArrayAdapter<>(this, R.layout.conversion_spinner,
                drinkTypes);
        // Specify the layout to use when the list of choices appears
        drinkTypeAdapter.setDropDownViewResource(R.layout.conversion_spinner_dropdown);

        drinkSpinner = (Spinner) findViewById(R.id.drink_type);
        drinkSpinner.setAdapter(drinkTypeAdapter);

        drinkSpinner.setOnItemSelectedListener(this);

       updateTypeSpecificTextViews();

        numDrinksConsumedTV = (TextView) findViewById(R.id.num_drinks_consumed);
        valueDrinkConsumedTV = (TextView) findViewById(R.id.value_of_drinks_consumed);
        consumeDrinkButton = (Button) findViewById(R.id.consume_drink_button);

        consumeDrinkButton.setOnClickListener(this);

        removeDrinkButton = (Button) findViewById(R.id.remove_drink_button);
        removeDrinkButton.setOnClickListener(this);

        ButtonUtil.buttonEffect(consumeDrinkButton, this);
        ButtonUtil.buttonEffect(removeDrinkButton, this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //TODO USE BELOW FOR LONG-TERM SAVING
        //HomePage.cruise = (new CruiseIO(this.getFilesDir())).readCruise();
        //numDrinksConsumed = HomePage.cruise.getNumDrinksConsumed();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        numDrinksConsumed = sharedPref.getInt(getString(R.string.beverages_consumed),
                DEFAULT_NUM_BEVERAGES_CONSUMED);

        updateUI();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //TODO USE BELOW FOR LONG-TERM SAVING
        //HomePage.cruise.save(new CruiseIO(this.getFilesDir()));
        SharedPreferencesManager.saveDrinksConsumed(this, numDrinksPerType);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.beverages_consumed), numDrinksConsumed);
        editor.apply();
    }

    private void updateUI(){
        numDrinksConsumedTV.setText(String.valueOf(numDrinksConsumed));
        String dinksConsumedValueText = "$" + String.format(locale, "%.02f",
                currentValue) + " USD";
        valueDrinkConsumedTV.setText(dinksConsumedValueText);
    }

    @Override
    public void onClick(View v) {
        if(v == consumeDrinkButton){

            numDrinksConsumed++;
            String itemKey = drinkSpinner.getSelectedItem().toString();
            itemKey = (itemKey.split("-")[0]).trim();
            int currentValueOfDrinksConsumedOfThisType = numDrinksPerType.get(itemKey);
            numDrinksPerType.remove(itemKey);
            numDrinksPerType.put(itemKey, currentValueOfDrinksConsumedOfThisType + 1);

            currentValue += DrinkConstants.DRINK_TYPES.get(itemKey);

            HomePage.cruise.setNumDrinksConsumed(numDrinksConsumed);
            updateUI();
            updateTypeSpecificTextViews();
        }
        else if(v == removeDrinkButton){
            if(numDrinksConsumed > 0) {
                String itemKey = drinkSpinner.getSelectedItem().toString();
                itemKey = (itemKey.split("-")[0]).trim();
                int currentValueOfDrinksConsumedOfThisType = numDrinksPerType.get(itemKey);
                if(currentValueOfDrinksConsumedOfThisType > 0) {
                    numDrinksConsumed--;
                    numDrinksPerType.remove(itemKey);
                    currentValueOfDrinksConsumedOfThisType -= 1;
                    if (currentValueOfDrinksConsumedOfThisType < 0) {
                        currentValueOfDrinksConsumedOfThisType = 0;
                    }
                    numDrinksPerType.put(itemKey, currentValueOfDrinksConsumedOfThisType);

                    currentValue -= DrinkConstants.DRINK_TYPES.get(itemKey);

                    HomePage.cruise.setNumDrinksConsumed(numDrinksConsumed);
                    updateUI();
                    updateTypeSpecificTextViews();
                }
            }
        }
    }

    private void updateTypeSpecificTextViews(){
        String selectedText = drinkSpinner.getSelectedItem().toString();

        String[] parts = selectedText.split("-");
        String currentType = parts[0];
        currentType = currentType.trim();
        int index = drinkTypes.indexOf(selectedText);
        drinkTypes.remove(drinkSpinner.getSelectedItemPosition());
        drinkTypes.add(index, parts[0].trim() + " - " + numDrinksPerType.get(currentType));
        drinkTypeAdapter.notifyDataSetChanged();

        String label = "Number of " + currentType.toLowerCase() + "s consumed: ";
        String value = numDrinksPerType.get(currentType).toString();
        String disclaimer = "Based on a value of $" +
                String.format(locale, "%.02f",
                        DrinkConstants.DRINK_TYPES.get(currentType)) + " per " +
                currentType.toLowerCase();
        drinkTypeCountLabel.setText(label);
        drinkTypeCountValue.setText(value);
        drinkTypeCountDisclaimer.setText(disclaimer);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == drinkSpinner) {
            updateTypeSpecificTextViews();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
