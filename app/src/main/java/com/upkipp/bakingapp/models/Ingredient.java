package com.upkipp.bakingapp.models;

import org.parceler.Parcel;

@Parcel
public class Ingredient {
    String mQuantity;
    String mMeasure;
    String mName;

    Ingredient() {
    }

    public Ingredient(String quantity, String measure, String name) {
        this.mQuantity = quantity;
        this.mMeasure = measure;
        this.mName = name;
    }

    public String getQuantity() {
        return mQuantity;
    }

    public String getMeasure() {
        return mMeasure;
    }

    public String getName() {
        return mName;
    }
}
