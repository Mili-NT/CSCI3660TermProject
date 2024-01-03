package com.csci3660.cosmiccross;

public interface WordGenerationCallback {
    void onWordGenerated(String word);
    void onWordGenerationFailed(Throwable t);
}

