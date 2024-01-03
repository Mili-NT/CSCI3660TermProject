package com.csci3660.cosmiccross.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csci3660.cosmiccross.WordGrid;
import com.csci3660.cosmiccross.data.api.retrofit.WordAPIInterface;

import java.util.ArrayList;
import java.util.List;

public class GameViewModel extends ViewModel {
    // Game variables
    private WordAPIInterface wordAPI;
    private boolean displayPopup = true;
    // Word grid and bank
    private WordGrid wordSearchGrid;
    private final MutableLiveData<List<String>> wordsLiveData = new MutableLiveData<>();
    private final ArrayList<String> selectedWords = new ArrayList<>();
    private int remainingWordCount = 0;
    private static final String PLACEHOLDER = "placeholder";

    // Getters and Setters
    public WordAPIInterface getWordAPI() {
        return this.wordAPI;
    }

    public int getCurrentGridSize() {
        return 10;
    }

    public int getRemainingWordCount() {
        return this.remainingWordCount;
    }
    public void setRemainingWordCount() {
        for (String word : this.wordsLiveData.getValue()) {
            if (!word.contains(PLACEHOLDER)) {
                this.remainingWordCount++;
            }
        }
    }
    public boolean shouldDisplayPopup() {
        return this.displayPopup;
    }
    public WordGrid getWordSearchGrid() { return this.wordSearchGrid; }

    public LiveData<List<String>> getWordsLiveData() {
        return this.wordsLiveData;
    }

    public int getTotalWordCount() {
        return 6;
    }
    public int getCurrentWordCount() {
        int currentWordCount = 0;
        for (int i = 0; i < this.wordsLiveData.getValue().size(); i++) {
            String word = this.wordsLiveData.getValue().get(i);
            if (!word.contains(PLACEHOLDER)) {
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
        // Add placeholder elements (these get rendered as transparent in the word adapter)
        for (int i = 0; i < this.getTotalWordCount(); i++) {
            this.addWord(PLACEHOLDER);
        }
    }
    public void wipePlaceholders() {
        List<String> currentWords = this.wordsLiveData.getValue();
        if (currentWords != null) {
            currentWords.removeIf(word -> word.toLowerCase().contains(PLACEHOLDER));
        }
        this.wordsLiveData.setValue(currentWords);
    }
    public void addWord(String word) {
        List<String> currentWords = this.wordsLiveData.getValue();
        if (currentWords != null) {
            if (!word.contains(PLACEHOLDER)) {
                for (int i = 0; i < currentWords.size(); i++) {
                    String currentWord = currentWords.get(i);
                    if (currentWord.contains(PLACEHOLDER)) {
                        currentWords.set(i, word.toLowerCase());
                        break;
                    }
                }
                this.remainingWordCount++;
            } else {
                currentWords.add(word);
            }
            this.wordsLiveData.setValue(currentWords);
        }
    }
    public void newWordSearchGrid() {
        this.wordSearchGrid = new WordGrid(this.getCurrentGridSize(), this.wordsLiveData.getValue());
    }
    public void setDisplayPopup(boolean displayPopup) {
        this.displayPopup = displayPopup;
    }

    public void setWordAPI(WordAPIInterface wordAPIInterface) {
        this.wordAPI = wordAPIInterface;
    }
}