package com.zybooks.csci3660termproject;

public interface WordGenerationCallback {
    void onWordGenerated(String word);
    void onWordGenerationFailed(Throwable t);
}

