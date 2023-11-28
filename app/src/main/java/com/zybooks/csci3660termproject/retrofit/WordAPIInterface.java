package com.zybooks.csci3660termproject.retrofit;
import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.responses.WordAPIRandomResponse;
import com.zybooks.csci3660termproject.responses.WordAPISearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface WordAPIInterface {

    @Headers({
            "X-RapidAPI-Host: " + WordAPIClient.X_RAPIDAPI_HOST
    })
    @GET("words/")
    Call<WordAPISearchResponse> getWords(
            @Header("X-RapidAPI-Key") String userAPIKey,
            @Query("letterPattern") String letterPattern,
            @Query("letters") int letters,
            @Query("limit") int limit,
            @Query("page") int page
    );

    // Overloaded method without the "random" parameter (defaults to false)
    @Headers({
            "X-RapidAPI-Host: " + WordAPIClient.X_RAPIDAPI_HOST
    })
    @GET("words/")
    Call<WordAPIRandomResponse> getRandomWord(
            @Header("X-RapidAPI-Key") String userAPIKey,
            @Query("letterPattern") String letterPattern,
            @Query("letters") int letters,
            @Query("limit") int limit,
            @Query("page") int page,
            @Query("random") boolean random
    );
}