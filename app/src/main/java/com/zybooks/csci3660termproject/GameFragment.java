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
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.responses.WordAPIRandomResponse;
import com.zybooks.csci3660termproject.responses.WordAPISearchResponse;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {
    private WordAPIInterface wordAPI;
    private int currentGridSize = 6; // Default grid value, can be changed
    public GameFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Code for receiving grid size selection from SettingsFragment
        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            Log.d("GRD-DBG", "STARTING GRID SIZE: " + currentGridSize);
            int gridSize = receivedBundle.getInt("gridSize");
            Log.d("GRD-DBG", "BUNDLE RECEIVED: " + gridSize);
            currentGridSize = gridSize;
            Log.d("GRD-DBG", "UPDATED GRID SIZE: " + currentGridSize);
        }
        // Use the WordAPIManager to check SharedPref for a key
        String userAPIKey = WordAPIManager.getApiKey(requireContext());
        // If there is no key (e.g. when the user first runs the app), redirect to the settings fragment
        if (userAPIKey == null) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.settings_Fragment);
        }
        else {
            // wordAPI is initialized here IF a key exists in the shared preference
            wordAPI = WordAPIClient.getClient();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        if (wordAPI == null) {
            // wordAPI is initialized here when the user navigates back from SettingsFragment
            wordAPI = WordAPIClient.getClient();
        }

        return rootView;
    }
    public void getWordList(String letterPattern, int letters, int limit, int page) {
        Call<WordAPISearchResponse> call = wordAPI.getWords(
                WordAPIManager.getApiKey(requireContext()),
                letterPattern,
                letters,
                limit,
                page
        );
        call.enqueue(new Callback<WordAPISearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<WordAPISearchResponse> call, @NonNull Response<WordAPISearchResponse> response) {
                if (response.isSuccessful()) {
                    WordAPISearchResponse apiResponse = response.body();
                    assert apiResponse != null;
                    List<String> randomWords = apiResponse.getResults().getData();
                    Log.d("API-DBG", "onResponse: " + randomWords.toString());
                } else {
                    // TODO: handle the error response
                }
            }
            @Override
            public void onFailure(@NonNull Call<WordAPISearchResponse> call, @NonNull Throwable t) {
                Log.e("API-DBG", "onFailure: ", t);
            }
        });
    }

    public void getRandomWord(String letterPattern, int letters, int limit, int page) {
        Call<WordAPIRandomResponse> call = wordAPI.getRandomWord(
                WordAPIManager.getApiKey(requireContext()),
                letterPattern,
                letters,
                limit,
                page,
                true
        );
        call.enqueue(new Callback<WordAPIRandomResponse>() {
            @Override
            public void onResponse(@NonNull Call<WordAPIRandomResponse> call, @NonNull Response<WordAPIRandomResponse> response) {
                if (response.isSuccessful()) {
                    WordAPIRandomResponse apiResponse = response.body();
                    assert apiResponse != null;
                    String randomWord = apiResponse.getWord();
                    Log.d("API-DBG", "onResponse: " + randomWord);
                } else {
                    // TODO: handle the error response
                }
            }
            @Override
            public void onFailure(@NonNull Call<WordAPIRandomResponse> call, @NonNull Throwable t) {
                Log.e("API-DBG", "onFailure: ", t);
            }
        });
    }

    //Basic Template for grid for Word Search (change as needed to fix error and work with app)
    /*Define the size of the grid
    int rows = 6;
    int cols = 6;

    // Create a 2D array to represent the grid
    String[][] grid = new String[rows][cols];

    // Fill the grid with the string "A"
        for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            grid[i][j] = "A";
        }
    }

    // Display the grid
        for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            System.out.print(grid[i][j] + " ");
        }
        System.out.println(); // Move to the next line after each row


    }

     */



}
