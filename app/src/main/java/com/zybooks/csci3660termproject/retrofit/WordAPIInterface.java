package com.zybooks.csci3660termproject.retrofit;
import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.responses.WordAPIResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface WordAPIInterface {

    @Headers({
            "X-RapidAPI-Host: " + WordAPIClient.X_RAPIDAPI_HOST
    })
    @GET("words")
    Call<WordAPIResponse> getWords(
            @Query("letterPattern") String letterPattern,
            @Query("letters") int letters,
            @Query("limit") int limit,
            @Query("page") int page
    );
}
