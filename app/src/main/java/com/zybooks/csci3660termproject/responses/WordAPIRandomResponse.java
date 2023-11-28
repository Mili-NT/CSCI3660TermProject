package com.zybooks.csci3660termproject.responses;

/*
{
  "word": "unshovelled",
  "syllables": {
    "count": 3,
    "list": [
      "un",
      "shov",
      "elled"
    ]
  }
}
*/

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WordAPIRandomResponse {

    @SerializedName("word")
    private String word;

    @SerializedName("syllables")
    private Syllables syllables;

    // Getters and setters

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Syllables getSyllables() {
        return syllables;
    }

    public void setSyllables(Syllables syllables) {
        this.syllables = syllables;
    }

    public static class Syllables {

        @SerializedName("count")
        private int count;

        @SerializedName("list")
        private List<String> list;

        // Getters and setters

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }
}
