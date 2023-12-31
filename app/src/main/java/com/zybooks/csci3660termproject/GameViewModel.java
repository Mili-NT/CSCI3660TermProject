package com.zybooks.csci3660termproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends ViewModel {
    // Game variables
    private WordAPIInterface wordAPI;
    private int currentGridSize = 10;
    private boolean displayPopup = true;
    // Word grid and bank
    private char[][] wordSearchGrid;
    private MutableLiveData<List<String>> wordsLiveData = new MutableLiveData<>();

    // Getters and Setters
    public WordAPIInterface getWordAPI() {
        return wordAPI;
    }

    public int getCurrentGridSize() {
        return currentGridSize;
    }

    public boolean shouldDisplayPopup() {
        return displayPopup;
    }

    public char[][] getWordSearchGrid() { return wordSearchGrid; }

    public LiveData<List<String>> getWordsLiveData() {
        return wordsLiveData;
    }

    public void setWords(List<String> newWords) {
        this.wordsLiveData.setValue(newWords);
    }

    public void removeWord(String word) {
        List<String> currentWords = this.wordsLiveData.getValue();
        if (currentWords != null) {
            currentWords.remove(word);
            this.wordsLiveData.setValue(currentWords);
        }
    }

    public void addWord(String word) {
        List<String> currentWords = this.wordsLiveData.getValue();
        if (currentWords != null) {
            currentWords.add(word.toLowerCase());
            this.wordsLiveData.setValue(currentWords);
        }
    }
    public void setWordSearchGrid(char[][] newWordSearchGrid) {
        this.wordSearchGrid = newWordSearchGrid;
    }
    public void setDisplayPopup(boolean displayPopup) {
        this.displayPopup = displayPopup;
    }

    public void setWordAPI(WordAPIInterface wordAPIInterface) {
        this.wordAPI = wordAPIInterface;
    }
}

