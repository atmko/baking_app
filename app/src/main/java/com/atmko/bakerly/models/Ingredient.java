/*
 * Copyright (C) 2019 Aayat Mimiko
 */

package com.atmko.bakerly.models;

import org.parceler.Parcel;

//ingredient object class
@Parcel
public class Ingredient {
    private String mQuantity;
    private String mMeasure;
    private String mName;

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
