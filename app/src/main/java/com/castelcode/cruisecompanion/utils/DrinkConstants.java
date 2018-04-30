package com.castelcode.cruisecompanion.utils;

import com.google.common.collect.ImmutableMap;

import java.util.Map;


public class DrinkConstants {

    private static final String COCKTAILS_KEY = "Cocktail";
    private static final String WINES_KEY = "Wine";
    private static final String BEERS_KEY = "Beer";
    private static final String COFFEES_KEY = "Coffee";
    private static final String SODAS_KEY = "Soda";
    private static final String WATERS_KEY = "Water";
    private static final String JUICES_KEY = "Juice";

    private static final double COCKTAILS = 11.00;
    private static final double WINES = 13.00;
    private static final double BEERS = 7.10;
    private static final double COFFEES = 4.25;
    private static final double SODAS = 3.25;
    private static final double WATERS = 2.75;
    private static final double JUICES  = 4.50;

    public static final Map<String, Double> DRINK_TYPES = ImmutableMap.<String, Double>builder()
            .put(COCKTAILS_KEY, COCKTAILS)
            .put(WINES_KEY, WINES)
            .put(BEERS_KEY, BEERS)
            .put(COFFEES_KEY, COFFEES)
            .put(SODAS_KEY, SODAS)
            .put(WATERS_KEY, WATERS)
            .put(JUICES_KEY, JUICES)
            .build();
}
