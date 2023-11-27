package com.zybooks.csci3660termproject.api;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WordAPIClient {

    public static final String X_RAPIDAPI_HOST = "https://wordsapiv1.p.rapidapi.com/";
    private static Retrofit retrofit = null;

    public static WordAPIInterface getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(X_RAPIDAPI_HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(WordAPIInterface.class);
    }
    public static String getXRapidAPIHost(){
        return X_RAPIDAPI_HOST;
    }
}
