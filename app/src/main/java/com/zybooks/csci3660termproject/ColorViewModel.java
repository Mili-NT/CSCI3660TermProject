package com.zybooks.csci3660termproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// ColorViewModel is exclusively used for storing highlighter color
// The shared ViewModel is used to send the value to GameViewModel

public class ColorViewModel extends ViewModel {
    private final MutableLiveData<Integer> selectedColor = new MutableLiveData<>();

    public void setSelectedColor(int color) {
        selectedColor.setValue(color);
    }

    public LiveData<Integer> getSelectedColor() {
        return selectedColor;
    }
}

