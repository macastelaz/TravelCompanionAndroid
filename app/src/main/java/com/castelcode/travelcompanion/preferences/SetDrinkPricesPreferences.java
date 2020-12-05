package com.castelcode.travelcompanion.preferences;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.utils.DrinkConstants;
import com.castelcode.travelcompanion.utils.SharedPreferencesManager;

import java.util.HashMap;
import java.util.Map;

public class SetDrinkPricesPreferences extends AppCompatActivity implements View.OnClickListener {

    CurrencyEditText cocktailPrice;
    CurrencyEditText winePrice;
    CurrencyEditText beerPrice;
    CurrencyEditText coffeePrice;
    CurrencyEditText sodaPrice;
    CurrencyEditText waterPrice;
    CurrencyEditText juicePrice;

    Button resetDefaultsButton;

    Map<String, Double> prices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_drink_prices_preferences);
        prices = SharedPreferencesManager.getDrinkPrices(this);
        if (prices == null) {
           setupMap();
        }
        cocktailPrice = findViewById(R.id.cocktail_price);
        winePrice = findViewById(R.id.wine_price);
        beerPrice = findViewById(R.id.beer_price);
        coffeePrice = findViewById(R.id.coffee_price);
        sodaPrice = findViewById(R.id.soda_price);
        waterPrice = findViewById(R.id.water_price);
        juicePrice = findViewById(R.id.juice_price);
        resetDefaultsButton = findViewById(R.id.reset_to_defaults);
        resetDefaultsButton.setOnClickListener(this);
        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        saveNewValues();
        super.onBackPressed();
    }

    private void setupMap() {
        prices = new HashMap<>();
        prices.put(DrinkConstants.COCKTAILS_KEY,
                DrinkConstants.DRINK_TYPES.get(DrinkConstants.COCKTAILS_KEY));
        prices.put(DrinkConstants.WINES_KEY,
                DrinkConstants.DRINK_TYPES.get(DrinkConstants.WINES_KEY));
        prices.put(DrinkConstants.BEERS_KEY,
                DrinkConstants.DRINK_TYPES.get(DrinkConstants.BEERS_KEY));
        prices.put(DrinkConstants.COFFEES_KEY,
                DrinkConstants.DRINK_TYPES.get(DrinkConstants.COFFEES_KEY));
        prices.put(DrinkConstants.SODAS_KEY,
                DrinkConstants.DRINK_TYPES.get(DrinkConstants.SODAS_KEY));
        prices.put(DrinkConstants.WATERS_KEY,
                DrinkConstants.DRINK_TYPES.get(DrinkConstants.WATERS_KEY));
        prices.put(DrinkConstants.JUICES_KEY,
                DrinkConstants.DRINK_TYPES.get(DrinkConstants.JUICES_KEY));
    }

    private void saveNewValues() {
        prices.clear();
        prices.put(DrinkConstants.COCKTAILS_KEY, cocktailPrice.getRawValue()/100.0);
        prices.put(DrinkConstants.WINES_KEY, winePrice.getRawValue()/100.0);
        prices.put(DrinkConstants.BEERS_KEY, beerPrice.getRawValue()/100.0);
        prices.put(DrinkConstants.COFFEES_KEY, coffeePrice.getRawValue()/100.0);
        prices.put(DrinkConstants.SODAS_KEY, sodaPrice.getRawValue()/100.0);
        prices.put(DrinkConstants.WATERS_KEY, waterPrice.getRawValue()/100.0);
        prices.put(DrinkConstants.JUICES_KEY, juicePrice.getRawValue()/100.0);
        SharedPreferencesManager.saveDrinkPrices(this, prices);
    }

    private void updateUI() {
        cocktailPrice.setValue((long)(prices.get(DrinkConstants.COCKTAILS_KEY) * 100.0));
        winePrice.setValue((long)(prices.get(DrinkConstants.WINES_KEY) * 100.0));
        beerPrice.setValue((long)(prices.get(DrinkConstants.BEERS_KEY) * 100.0));
        coffeePrice.setValue((long)(prices.get(DrinkConstants.COFFEES_KEY) * 100.0));
        sodaPrice.setValue((long)(prices.get(DrinkConstants.SODAS_KEY) * 100.0));
        waterPrice.setValue((long)(prices.get(DrinkConstants.WATERS_KEY) * 100.0));
        juicePrice.setValue((long)(prices.get(DrinkConstants.JUICES_KEY) * 100.0));
    }

    @Override
    public void onClick(View view) {
        if (view == resetDefaultsButton) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm resetting to defaults")
                    .setMessage("Do you really want to reset all of the drink prices to their " +
                            "default values?")
                    .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                        setupMap();
                        updateUI();
                        Toast.makeText(SetDrinkPricesPreferences.this,
                                "Drink prices reset", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }
}
