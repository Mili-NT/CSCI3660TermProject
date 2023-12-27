package com.zybooks.csci3660termproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// ColorViewModel is exclusively used for storing highlighter color
// The shared ViewModel is used to send the value to GameViewModel

public class ColorViewModel extends ViewModel {
    private final MutableLiveData<Integer> selectedColor = new MutableLiveData<>();
    public static final String COLOR_PREF = "colorPref";
    public static final String COLOR_KEY = "colorKey";

    public void setSelectedColor(int color) {
        selectedColor.setValue(color);
    }

    public LiveData<Integer> getSelectedColor() {
        return selectedColor;
    }

    void saveColorToSharedPreferences(int color, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(ColorViewModel.COLOR_PREF, Context.MODE_PRIVATE).edit();
        editor.putInt(ColorViewModel.COLOR_KEY, color);
        editor.apply();
    }
    static Integer getColorFromPreference(Context context) {
        // Gets color from preferences
        SharedPreferences prefs = context.getSharedPreferences(ColorViewModel.COLOR_PREF, Context.MODE_PRIVATE);
        return prefs.getInt(ColorViewModel.COLOR_KEY, Color.BLACK);
    }
}

