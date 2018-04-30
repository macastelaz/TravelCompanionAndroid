package com.castelcode.cruisecompanion.expenses;

import java.io.Serializable;

public class Expense  implements Serializable{
    private String mDescription;
    private double mCost;

    public Expense(String description, double cost){
        mDescription = description;
        mCost = cost;
    }

    public String getDescription(){
        return mDescription;
    }

    public double getCost(){
        return mCost;
    }
}
