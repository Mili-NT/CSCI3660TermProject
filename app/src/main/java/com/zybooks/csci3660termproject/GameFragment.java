package com.zybooks.csci3660termproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;



import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.responses.WordAPIRandomResponse;
import com.zybooks.csci3660termproject.responses.WordAPISearchResponse;
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
    private int currentGridSize = 6; // Default grid value, can be changed


    private boolean hasShownPopup = false;

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
        Log.d("KEY-DBG", "API Key: " + userAPIKey);
        if (userAPIKey == null) {
            Log.d("KEY-DBG", "NULL KEY DETECTED");
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.settings_Fragment);
        }
        else {
            // wordAPI is initialized here IF a key exists in the shared preference
            wordAPI = WordAPIClient.getClient();
        }
            //handler for the pop up message
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showPopup();
            }
        }, 1000);

    }
        //this is for the pop window for the game
    private void showPopup() {
        if (!hasShownPopup && isAdded() && getActivity() != null && !getActivity().isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Welcome to CosmicCross!");
            builder.setMessage("Try and find all of the words to complete the crossword! Happy solving!");
            builder.setPositiveButton("I'm ready to solve!", null);

            AlertDialog dialog = builder.create();
            dialog.show();
            hasShownPopup = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayout);

        getRandomWord(
                "^[a-zA-Z]+$",
                6,
                1,
                1
        );
        List<String> words = new ArrayList<>();
        words.add("JAVA");
        words.add("PRO");
        words.add("TEST");
        words.add("CODE");

        char[][] wordSearchGrid = generateWordSearchGrid(words);

        // Check if currentGridSize has changed or if the grid is not initialized
        if (wordSearchGrid == null || wordSearchGrid.length != currentGridSize || wordSearchGrid[0].length != currentGridSize) {
            wordSearchGrid = generateWordSearchGrid(words);
        }

        // Find the existing TextView in your layout with the id "wordBank"
        TextView wordBankTextView = rootView.findViewById(R.id.word_bank);

        // Create a StringBuilder to build the text for the TextView
        StringBuilder wordBankText = new StringBuilder();

        // Append each word to the StringBuilder
        for (String word : words) {
            wordBankText.append(word).append("\n"); // Add a newline for each word
        }

        // Set the text of the TextView to the built text
        wordBankTextView.setText(wordBankText.toString());



        if (wordAPI == null) {
            // wordAPI is initialized here when the user navigates back from SettingsFragment
            wordAPI = WordAPIClient.getClient();
        }

        displayGrid(tableLayout, wordSearchGrid);

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
                    Log.d("API-DBG", "Word received: " + randomWord);
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

    private char[][] generateWordSearchGrid(List<String> words) {
        int numRows = currentGridSize;
        int numCols = currentGridSize;

        char[][] grid = new char[numRows][numCols];

        // Place words in the grid
        for (String word : words) {
            placeWord(grid, word, 100); // Try placing each word up to 100 times
        }

        // Fill the remaining empty spaces with random letters
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (grid[i][j] == '\0') {
                    grid[i][j] = (char) ('A' + (int) (Math.random() * 26));
                }
            }
        }

        return grid;
    }


    private void placeWord(char[][] grid, String word, int maxAttempts) {
        int length = word.length();
        int startRow, startCol;
        boolean placed = false;
        int attempts = 0;

        while (!placed && attempts < maxAttempts) {
            startRow = (int) (Math.random() * currentGridSize);
            startCol = (int) (Math.random() * currentGridSize);

            int direction = (int) (Math.random() * 8); // 0 to 7

            int rowIncrement = 0;
            int colIncrement = 0;

            switch (direction) {
                case 0: colIncrement = 1; break; // Horizontal (left to right)
                case 1: colIncrement = -1; break; // Horizontal (right to left)
                case 2: rowIncrement = 1; break; // Vertical (top to bottom)
                case 3: rowIncrement = -1; break; // Vertical (bottom to top)
                case 4: rowIncrement = 1; colIncrement = 1; break; // Diagonal (top-left to bottom-right)
                case 5: rowIncrement = -1; colIncrement = -1; break; // Diagonal (bottom-right to top-left)
                case 6: rowIncrement = 1; colIncrement = -1; break; // Diagonal (top-right to bottom-left)
                case 7: rowIncrement = -1; colIncrement = 1; break; // Diagonal (bottom-left to top-right)
            }

            if (canPlaceWord(grid, word, startRow, startCol, rowIncrement, colIncrement)) {
                for (int i = 0; i < length; i++) {
                    int row = startRow + i * rowIncrement;
                    int col = startCol + i * colIncrement;

                    grid[row][col] = word.charAt(i);
                }

                placed = true;
            }

            attempts++;
        }
    }

    private boolean canPlaceWord(char[][] grid, String word, int startRow, int startCol, int rowIncrement, int colIncrement) {
        int length = word.length();

        int endRow = startRow + (length - 1) * rowIncrement;
        int endCol = startCol + (length - 1) * colIncrement;

        if (endRow >= 0 && endRow < currentGridSize && endCol >= 0 && endCol < currentGridSize) {
            for (int i = 0; i < length; i++) {
                int row = startRow + i * rowIncrement;
                int col = startCol + i * colIncrement;

                if (grid[row][col] != '\0' && grid[row][col] != word.charAt(i)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }


    // Method to display the word search grid in the TableLayout
    private void displayGrid(TableLayout tableLayout, char[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            TableRow tableRow = new TableRow(requireContext());

            for (int j = 0; j < grid[i].length; j++) {
                TextView cell = new TextView(requireContext());
                cell.setText(String.valueOf(grid[i][j]));
                cell.setPadding(10, 10, 10, 10);
                tableRow.addView(cell);
            }

            tableLayout.addView(tableRow);
        }
    }

}
