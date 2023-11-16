package com.zybooks.csci3660termproject.retrofit;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.responses.WordAPIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface WordAPI {

    @Headers({
            "X-RapidAPI-Host: " + WordAPIManager.X_RAPIDAPI_HOST,
            "X-RapidAPI-Key: " + WordAPIManager.X_RAPIDAPI_KEY
    })
    @GET("words")
    Call<WordAPIResponse> getWords(
            @Query("letterPattern") String letterPattern,
            @Query("letters") int letters,
            @Query("limit") int limit,
            @Query("page") int page
    );
}
