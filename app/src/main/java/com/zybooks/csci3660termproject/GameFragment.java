package com.zybooks.csci3660termproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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

    private GameViewModel viewModel;

    public GameFragment() {
        // Required empty public constructor
    }
    // 325a606facmsh97b63eef2a7c146p114f97jsn41237e067aeb
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameViewModel viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
    }
        //this is for the pop window for the game
    private void showPopup() {
        if (viewModel.shouldDisplayPopup() && isAdded() && getActivity() != null && !getActivity().isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Welcome to CosmicCross!");
            builder.setMessage("Try and find all of the words to complete the crossword! Happy solving!");
            builder.setPositiveButton("I'm ready to solve!", null);

            AlertDialog dialog = builder.create();
            dialog.show();
            viewModel.setDisplayPopup(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GameViewModel viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        String userAPIKey = WordAPIManager.getApiKey(requireContext());
        if (userAPIKey == null) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.settings_Fragment);
        } else {
            viewModel.setWordAPI(WordAPIClient.getClient());
        }

        // handler for the pop-up message
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showPopup();
            }
        }, 1000);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayout);
        WordAPIInterface wordAPI = viewModel.getWordAPI();
        int currentGridSize = viewModel.getCurrentGridSize();
        char[][] wordSearchGrid = viewModel.getWordSearchGrid();

        if (viewModel.getWordAPI() == null) {
            viewModel.setWordAPI(WordAPIClient.getClient());
        }
        if (viewModel.getWords() == null) {
            ArrayList<String> newWords = new ArrayList<>();
            viewModel.setWords(newWords);
            for (int i = 0; i < 4; i++) {
                getRandomWord(
                        "^[a-zA-Z]+$",
                        6,
                        1,
                        1
                );
            }
        }

        // Check if currentGridSize has changed or if the grid is not initialized
        if (wordSearchGrid == null) {
            wordSearchGrid = generateWordSearchGrid(viewModel.getWords());
            viewModel.setWordSearchGrid(wordSearchGrid);
        }

        // Find the existing TextView in your layout with the id "wordBank"
        TextView wordBankTextView = rootView.findViewById(R.id.word_bank);

        // Create a StringBuilder to build the text for the TextView
        StringBuilder wordBankText = new StringBuilder();

        // Append each word to the StringBuilder
        assert viewModel.getWords() != null;
        for (String word : viewModel.getWords()) {
            wordBankText.append(word).append("\n"); // Add a newline for each word
        }

        // Set the text of the TextView to the built text
        wordBankTextView.setText(wordBankText.toString());
        displayGrid(tableLayout, wordSearchGrid);

        return rootView;
    }
    public void getWordList(String letterPattern, int letters, int limit, int page) {
        Call<WordAPISearchResponse> call = viewModel.getWordAPI().getWords(
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
        Call<WordAPIRandomResponse> call = viewModel.getWordAPI().getRandomWord(
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
                    viewModel.addWord(randomWord);
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
        int numRows = viewModel.getCurrentGridSize();
        int numCols = viewModel.getCurrentGridSize();;

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
            startRow = (int) (Math.random() * viewModel.getCurrentGridSize());
            startCol = (int) (Math.random() * viewModel.getCurrentGridSize());

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

        if (endRow >= 0 && endRow < viewModel.getCurrentGridSize() && endCol >= 0 && endCol < viewModel.getCurrentGridSize()) {
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
    private void updateWordBankTextView() {
        TextView wordBankTextView = requireView().findViewById(R.id.word_bank);
        StringBuilder wordBankText = new StringBuilder();
        for (String word : viewModel.getWords()) {
            wordBankText.append(word).append("\n");
        }
        wordBankTextView.setText(wordBankText.toString());
    }
}
