package com.zybooks.csci3660termproject.api;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordAPIManager {

    public static final String X_RAPIDAPI_HOST = "https://wordsapiv1.p.rapidapi.com/";
    public static final String X_RAPIDAPI_KEY = "";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(X_RAPIDAPI_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static String getxRapidapiKeyApiKey() {
        return X_RAPIDAPI_KEY;
    }
}
