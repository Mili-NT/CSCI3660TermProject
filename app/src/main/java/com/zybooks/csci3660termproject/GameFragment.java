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
import android.widget.GridView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.responses.WordAPIRandomResponse;
import com.zybooks.csci3660termproject.responses.WordAPISearchResponse;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
            //handler for the pop up message
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showPopup();
            }
        }, 1000);

    }
        //this is for the pop window for the game
    private void showPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Welcome to CosmicCross!");
        builder.setMessage("Try and find all of the words to complete the crossword! Happy solving!");
        builder.setPositiveButton("I'm ready to solve!",null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        List<String> words = new ArrayList<>();
        words.add("JAVA");
        words.add("PROGRAM");
        words.add("ALGORITHM");
        words.add("CODE");

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


        char[][] wordSearchGrid = generateWordSearchGrid(12, 12, words);
        GridView gridView = rootView.findViewById(R.id.gridView);
        WordSearchAdapter adapter = new WordSearchAdapter(requireContext(), wordSearchGrid);
        gridView.setAdapter(adapter);


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
    public static char[][] generateWordSearchGrid(int rows, int cols, List<String> words) {
        char[][] grid = new char[rows][cols];
        Random random = new Random();

        // Shuffle the list of words for better randomness
        Collections.shuffle(words);

        // Place words in the grid
        for (String word : words) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 100) { // Increase attempts as needed
                int direction = random.nextInt(8);
                int startRow = random.nextInt(rows);
                int startCol = random.nextInt(cols);

                int stepRow = 0;
                int stepCol = 0;

                switch (direction) {
                    case 0: // Horizontal (left to right)
                        stepCol = 1;
                        break;
                    case 1: // Horizontal (right to left)
                        stepCol = -1;
                        break;
                    case 2: // Vertical (top to bottom)
                        stepRow = 1;
                        break;
                    case 3: // Vertical (bottom to top)
                        stepRow = -1;
                        break;
                    case 4: // Diagonal (top-left to bottom-right)
                        stepRow = 1;
                        stepCol = 1;
                        break;
                    case 5: // Diagonal (bottom-right to top-left)
                        stepRow = -1;
                        stepCol = -1;
                        break;
                    case 6: // Diagonal (top-right to bottom-left)
                        stepRow = 1;
                        stepCol = -1;
                        break;
                    case 7: // Diagonal (bottom-left to top-right)
                        stepRow = -1;
                        stepCol = 1;
                        break;
                }

                int currentRow = startRow;
                int currentCol = startCol;
                boolean fits = true;

                for (char letter : word.toCharArray()) {
                    if (currentRow < 0 || currentRow >= rows || currentCol < 0 || currentCol >= cols || grid[currentRow][currentCol] != 0) {
                        fits = false;
                        break;
                    }

                    currentRow += stepRow;
                    currentCol += stepCol;
                }

                if (fits) {
                    currentRow = startRow;
                    currentCol = startCol;
                    for (char letter : word.toCharArray()) {
                        grid[currentRow][currentCol] = letter;
                        currentRow += stepRow;
                        currentCol += stepCol;
                    }
                    placed = true;
                }

                attempts++;
            }
        }

        // Fill the empty spaces with random letters
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 0) {
                    grid[i][j] = (char) ('A' + random.nextInt(26));
                }
            }
        }

        return grid;
    }

    private static void printWordSearchGrid(char[][] grid) {
        for (char[] row : grid) {
            for (char cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    private void displayWordSearchGrid(View view, char[][] grid) {

        GridView wordSearchTextView = view.findViewById(R.id.gridView);

        // Convert the 2D char array to a string for display
        StringBuilder displayText = new StringBuilder();
        for (char[] row : grid) {
            for (char cell : row) {
                displayText.append(cell).append(" ");
            }
            displayText.append("\n");
        }

    }

}
