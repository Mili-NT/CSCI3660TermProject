package com.csci3660.cosmiccross.data.api.retrofit;
import com.csci3660.cosmiccross.data.api.WordAPIClient;
import com.csci3660.cosmiccross.data.responses.WordAPIRandomResponse;
import com.csci3660.cosmiccross.data.responses.WordAPISearchResponse;

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