package com.zybooks.csci3660termproject.api;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordAPIClient {

    public static final String X_RAPIDAPI_HOST = "wordsapiv1.p.rapidapi.com";
    private static final String BASE_URL = "https://wordsapiv1.p.rapidapi.com";
    private static Retrofit retrofit = null;

    public static WordAPIInterface getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(WordAPIInterface.class);
    }
}
