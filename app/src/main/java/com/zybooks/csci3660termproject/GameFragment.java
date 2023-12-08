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


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.responses.WordAPIRandomResponse;
import com.zybooks.csci3660termproject.responses.WordAPISearchResponse;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {

    private GameViewModel viewModel;
    private View rootView;

    public GameFragment() {
        // Required empty public constructor
    }
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
        rootView = view;
        String userAPIKey = WordAPIManager.getApiKey(requireContext());
        if (userAPIKey == null) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.settings_Fragment);
        } else {
            viewModel.setWordAPI(WordAPIClient.getClient());
        }
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);

        if (viewModel.getWordAPI() == null) {
            viewModel.setWordAPI(WordAPIClient.getClient());
        }
        if (viewModel.getWords() == null) {
            newWords();
        }

        TextView wordBankTextView = view.findViewById(R.id.word_bank);

        // Create a StringBuilder to build the text for the TextView
        StringBuilder wordBankText = new StringBuilder();

        // Append each word to the StringBuilder
        assert viewModel.getWords() != null;
        for (String word : viewModel.getWords()) {
            wordBankText.append(word).append("\n"); // Add a newline for each word
        }

        // Set the text of the TextView to the built text
        wordBankTextView.setText(wordBankText.toString());
        // Check if currentGridSize has changed or if the grid is not initialized
        if (viewModel.getWordSearchGrid() == null) {
            ArrayList<String> currentWords = viewModel.getWords();
            viewModel.setWordSearchGrid(generateWordSearchGrid(currentWords));
        }

        displayGrid(tableLayout, viewModel.getWordSearchGrid());
        Log.d("GRID", "First char array: " + Arrays.deepToString(viewModel.getWordSearchGrid()));
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle FAB click event (e.g., generate new words)
                newWords();
                updateUIWithGeneratedWords();
            }
        });
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
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    public void getRandomWord(String letterPattern, int letters, int limit, int page, WordGenerationCallback callback) {
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
                    callback.onWordGenerated(randomWord);
                } else {
                    callback.onWordGenerationFailed(new Exception("Failed to generate word"));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WordAPIRandomResponse> call, @NonNull Throwable t) {
                callback.onWordGenerationFailed(t);
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
        tableLayout.removeAllViews();
        for (int i = 0; i < grid.length; i++) {
            TableRow tableRow = new TableRow(requireContext());

            for (int j = 0; j < grid[i].length; j++) {
                final int row = i;
                final int col = j;
                TextView cell = new TextView(requireContext());
                cell.setText(String.valueOf(grid[i][j]));
                cell.setPadding(10, 10, 10, 10);
                cell.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        onCellClicked(row,col);
                    }
                });
                tableRow.addView(cell);
            }
            tableLayout.addView(tableRow);
        }
    }

    private void onCellClicked(int row, int col) {
        char selectedChar = viewModel.getWordSearchGrid()[row][col];
        Log.d("CELL-CLICKED", "Selected char: " + selectedChar);

        // Implement logic to check if the selected cell is part of any word
        String selectedWord = checkForWord(row, col);

        if (selectedWord != null) {
            // Handle word selection (e.g., highlight the word, show a message)
            Log.d("WORD-SELECTED", "Selected word: " + selectedWord);
        }
    }

    // Method to check if the selected cell is part of any word
    private String checkForWord(int row, int col) {
        char[][] grid = viewModel.getWordSearchGrid();
        StringBuilder selectedWord = new StringBuilder();

        for (int i = col; i < grid[row].length && grid[row][i] != '\0'; i++) {
            selectedWord.append(grid[row][i]);
            Log.d("CELL-CLICKED", "checkForWord: " + selectedWord);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }

        selectedWord.setLength(0);

        for (int i = row; i < grid.length && grid[i][col] != '\0'; i++) {
            selectedWord.append(grid[i][col]);
            Log.d("CELL-CLICKED", "checkForWord: " + selectedWord);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }
        // Reset StringBuilder for checking diagonally (top-left to bottom-right)
        selectedWord.setLength(0);

        for (int i = 0; row + i < grid.length && col + i < grid[row].length && grid[row + i][col + i] != '\0'; i++) {
            selectedWord.append(grid[row + i][col + i]);
            Log.d("CELL-CLICKED", "checkForWord (TL->BR): " + selectedWord);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }

        // Reset StringBuilder for checking diagonally (top-right to bottom-left)
        selectedWord.setLength(0);

        for (int i = 0; row - i >= 0 && col - i >= 0 && grid[row - i][col - i] != '\0'; i++) {
            selectedWord.append(grid[row - i][col - i]);
            Log.d("CELL-CLICKED", "checkForWord (BR->TL): " + selectedWord);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }
        // Reset StringBuilder for checking diagonally (bottom-left to top-right)
        selectedWord.setLength(0);

        for (int i = 0; row + i < grid.length && col - i >= 0 && grid[row + i][col - i] != '\0'; i++) {
            selectedWord.append(grid[row + i][col - i]);
            Log.d("CELL-CLICKED", "checkForWord (BL->TR): " + selectedWord);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }

        // Reset StringBuilder for checking diagonally (bottom-left to top-right)
        selectedWord.setLength(0);

        for (int i = 0; row - i >= 0 && col + i < grid[row].length && grid[row - i][col + i] != '\0'; i++) {
            selectedWord.append(grid[row - i][col + i]);
            Log.d("CELL-CLICKED", "checkForWord (BL->TR): " + selectedWord);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }
        // Reset StringBuilder for checking horizontally (right to left)
        selectedWord.setLength(0);

        for (int i = 0; col - i >= 0 && grid[row][col - i] != '\0'; i++) {
            selectedWord.append(grid[row][col - i]);
            Log.d("CELL-CLICKED", "checkForWord (R->L): " + selectedWord);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }
        // Reset StringBuilder for checking vertically (bottom to top)
        selectedWord.setLength(0);

        for (int i = 0; row - i >= 0 && grid[row - i][col] != '\0'; i++) {
            selectedWord.append(grid[row - i][col]);
            Log.d("CELL-CLICKED", "checkForWord (B->T): " + selectedWord);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }

        return null;
    }
    private void updateUIWithGeneratedWords() {
        // Add your UI update logic here
        // For example, update the TextView with the generated words
        TextView wordBankTextView = rootView.findViewById(R.id.word_bank);
        StringBuilder wordBankText = new StringBuilder();
        for (String word : viewModel.getWords()) {
            wordBankText.append(word).append("\n");
        }
        wordBankTextView.setText(wordBankText.toString());
        Log.d("UI-DBG", "updateUIWithGeneratedWords: " + viewModel.getWords());
        // Generate and display the word search grid
        viewModel.setWordSearchGrid(generateWordSearchGrid(viewModel.getWords()));
        displayGrid(rootView.findViewById(R.id.tableLayout), viewModel.getWordSearchGrid());
    }
    private void checkIfAllWordsGenerated() {
        if (viewModel.getWords() != null && viewModel.getWords().size() == 4) {
            updateUIWithGeneratedWords();
        }
    }
    private void newWords() {
        ArrayList<String> newWords = new ArrayList<>();
        viewModel.setWords(newWords);
        WordGenerationCallback generationCallback = new WordGenerationCallback() {
            @Override
            public void onWordGenerated(String word) {
                // Word generated successfully
                // Add your logic here
                checkIfAllWordsGenerated();
            }

            @Override
            public void onWordGenerationFailed(Throwable t) {
                // Word generation failed
                // Handle the error
                checkIfAllWordsGenerated();
            }
        };

        for (int i = 0; i < 4; i++) {
            getRandomWord("^[a-zA-Z]+$", 4, 1, 1, generationCallback);
        }
    }
}
