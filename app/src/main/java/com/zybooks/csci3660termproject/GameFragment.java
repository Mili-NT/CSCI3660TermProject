package com.zybooks.csci3660termproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.responses.WordAPIResponse;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {
    private WordAPIInterface wordAPI;
    public GameFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the WordAPIManager to check SharedPref for a key
        String userAPIKey = WordAPIManager.getApiKey(requireContext());
        // If there is no key (e.g. when the user first runs the app), redirect to the settings fragment
        if (userAPIKey == null) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.settings_Fragment);
        }
        else {
            wordAPI = WordAPIClient.getClient();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        if (wordAPI == null) {
            wordAPI = WordAPIClient.getClient();
        }

        /*
        FloatingActionButton testButton = rootView.findViewById(R.id.floatingActionButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateWordBank("^[a-zA-Z]+$", 6, 100, 1);
            }
        });
        */

        return rootView;
    }
    public void populateWordBank(String letterPattern, int letters, int limit, int page) {
        String preCallKey = WordAPIManager.getApiKey(requireContext());
        Log.d("API-DBG", "populateWordBank: " + preCallKey);
        Call<WordAPIResponse> call = wordAPI.getWords(
                WordAPIManager.getApiKey(requireContext()),
                letterPattern,
                letters,
                limit,
                page
        );
        call.enqueue(new Callback<WordAPIResponse>() {
            @Override
            public void onResponse(@NonNull Call<WordAPIResponse> call, @NonNull Response<WordAPIResponse> response) {
                if (response.isSuccessful()) {
                    WordAPIResponse apiResponse = response.body();
                    assert apiResponse != null;
                    List<String> randomWords = apiResponse.getResults().getData();
                    Log.d("API-DBG", "onResponse: " + randomWords.toString());
                } else {
                    // TODO: handle the error response
                }
            }
            @Override
            public void onFailure(@NonNull Call<WordAPIResponse> call, @NonNull Throwable t) {
                Log.e("API-DBG", "onFailure: ", t);
            }
        });
    }
}
