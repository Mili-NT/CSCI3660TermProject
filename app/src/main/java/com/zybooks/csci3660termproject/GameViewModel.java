package com.zybooks.csci3660termproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends ViewModel {
    // Define your variables here
    private WordAPIInterface wordAPI;
    private int currentGridSize = 6;
    private boolean displayPopup = true;
    private char[][] wordSearchGrid;
    // Add other variables as needed
    private ArrayList<String> words;
    private MutableLiveData<List<String>> wordListLiveData = new MutableLiveData<>();
    public LiveData<List<String>> getWordListLiveData() {
        return wordListLiveData;
    }

    public void setWordList(List<String> wordList) {
        wordListLiveData.setValue(wordList);
    }
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
    public ArrayList<String> getWords() { return words; }
    public void setWords(ArrayList<String> newWords) {
        this.words = newWords;
    }

    public void addWord(String word) {
        this.words.add(word);
    }
    public void setWordSearchGrid(char[][] newWordSearchGrid) {
        this.wordSearchGrid = newWordSearchGrid;
    }
    public void setDisplayPopup(boolean displayPopup) {
        this.displayPopup = displayPopup;
    }

    public void setCurrentGridSize(int gridSize) {
        this.currentGridSize = gridSize;
    }
    public void setWordAPI(WordAPIInterface wordAPIInterface) {
        this.wordAPI = wordAPIInterface;
    }
}

