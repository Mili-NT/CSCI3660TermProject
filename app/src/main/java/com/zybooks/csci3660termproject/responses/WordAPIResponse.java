package com.zybooks.csci3660termproject.responses;
import com.google.gson.annotations.SerializedName;

public class WordAPIResponse {

    @SerializedName("word")
    private String word;

    public String getWord() {
        return word;
    }
}
