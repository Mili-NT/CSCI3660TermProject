package com.zybooks.csci3660termproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameViewModel extends ViewModel {
    // Game variables
    private WordAPIInterface wordAPI;
    private int currentGridSize = 10;
    private boolean displayPopup = true;
    // Word grid and bank
    private char[][] wordSearchGrid;
    private MutableLiveData<List<String>> wordsLiveData = new MutableLiveData<>();
    private ArrayList<String> selectedWords = new ArrayList<>();
    private int remainingWordCount = 0;

    private int totalWordCount = 6;

    // Getters and Setters
    public WordAPIInterface getWordAPI() {
        return wordAPI;
    }

    public int getCurrentGridSize() {
        return currentGridSize;
    }

    public int getRemainingWordCount() {
        return remainingWordCount;
    }
    public void setRemainingWordCount() {
        for (String word : this.wordsLiveData.getValue()) {
            if (!word.contains("PLACEHOLDER")) {
                remainingWordCount++;
            }
        }
    }
    public boolean shouldDisplayPopup() {
        return displayPopup;
    }

    public char[][] getWordSearchGrid() { return wordSearchGrid; }

    public LiveData<List<String>> getWordsLiveData() {
        return wordsLiveData;
    }

    public int getTotalWordCount() {
        return totalWordCount;
    }
    public int getCurrentWordCount() {
        int currentWordCount = 0;
        for (int i = 0; i < this.wordsLiveData.getValue().size(); i++) {
            String atPos = this.wordsLiveData.getValue().get(i);
            if (!atPos.contains("placeholder")) {
                currentWordCount++;
            }
        }
        return currentWordCount;
    }
    public void setWords(List<String> newWords) {
        this.wordsLiveData.setValue(newWords);
        this.setRemainingWordCount();
    }

    public void addToSelectedWords(String word) {
        this.selectedWords.add(word);
        this.remainingWordCount--;
    }
    public boolean isWordFound(String word) {
        return this.selectedWords.contains(word);
    }
    public void addPlaceholders() {
        // Add 9 placeholder elements (these get rendered as transparent in the word adapter)
        // 9 is just an arbitrary number to force the recyclerview to start out taking up the rest of the screen space
        for (int i = 0; i < 9; i++) {
            this.addWord("PLACEHOLDER");
        }
    }
    public void wipePlaceholders() {
        List<String> currentWords = this.wordsLiveData.getValue();
        for (int i = 0; i < currentWords.size(); i++) {
            if (Objects.equals(currentWords.get(i), "placeholder")) {
                currentWords.remove(i);
            }
        }
        this.wordsLiveData.setValue(currentWords);
    }
    public void addWord(String word) {
        List<String> currentWords = this.wordsLiveData.getValue();
        if (currentWords != null) {
            currentWords.add(word.toLowerCase());
            // Sort the list based on a custom comparator
            currentWords.sort((s1, s2) -> {
                // Check if either of the words contains "placeholder"
                boolean containsPlaceholder1 = s1.contains("placeholder");
                boolean containsPlaceholder2 = s2.contains("placeholder");

                // Ensure elements containing "placeholder" come last
                if (containsPlaceholder1 && !containsPlaceholder2) {
                    return 1;
                } else if (!containsPlaceholder1 && containsPlaceholder2) {
                    return -1;
                } else {
                    // For other cases, use default string comparison
                    return s1.compareToIgnoreCase(s2);
                }
            });
            this.wordsLiveData.setValue(currentWords);
            if (!word.contains("PLACEHOLDER")) {
                this.remainingWordCount++;
            }
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

