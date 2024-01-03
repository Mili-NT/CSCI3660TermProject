package com.csci3660.cosmiccross.viewmodels;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// ColorViewModel is exclusively used for storing highlighter color
// The shared ViewModel is used to send the value to GameViewModel

public class ColorViewModel extends ViewModel {
    private final MutableLiveData<Integer> selectedColor = new MutableLiveData<>();
    public static final String COLOR_PREF = "colorPref";
    public static final String COLOR_KEY = "colorKey";

    public void setSelectedColor(int color) {
        this.selectedColor.setValue(color);
    }

    public int getSelectedColor() {
        // Prevent NPEs by ensuring default returns if null
        return this.selectedColor.getValue() == null ? Color.BLACK : this.selectedColor.getValue();
    }

    public void saveColorToSharedPreferences(int color, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(ColorViewModel.COLOR_PREF, Context.MODE_PRIVATE).edit();
        editor.putInt(ColorViewModel.COLOR_KEY, color);
        editor.apply();
    }
    public static Integer getColorFromPreference(Context context) {
        // Gets color from preferences
        SharedPreferences prefs = context.getSharedPreferences(ColorViewModel.COLOR_PREF, Context.MODE_PRIVATE);
        return prefs.getInt(ColorViewModel.COLOR_KEY, Color.BLACK);
    }
}

