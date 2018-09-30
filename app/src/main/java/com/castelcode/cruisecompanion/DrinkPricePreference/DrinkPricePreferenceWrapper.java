package com.castelcode.cruisecompanion.DrinkPricePreference;

import java.io.Serializable;

public class DrinkPricePreferenceWrapper implements Serializable {
    private String drinkName;
    private double drinkPrice;

    public DrinkPricePreferenceWrapper(String name, Double price) {
        this.drinkName = name;
        this.drinkPrice = price;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public Double getDrinkPrice() {
        return drinkPrice;
    }
}
