package com.zybooks.csci3660termproject;

import static com.google.android.material.color.MaterialColors.getColor;
import org.apache.commons.lang3.StringUtils;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.responses.WordAPIRandomResponse;

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
    private ColorViewModel colorViewModel;

    private View rootView;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private Toast congratulationsToast;

    public GameFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameViewModel viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
    }
    // This is for the pop window for the game
    private void showPopup() {
        if (viewModel.shouldDisplayPopup() && isAdded() && getActivity() != null && !getActivity().isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Welcome to CosmicCross!");
            builder.setMessage("Try and find all of the words to complete the crossword! Happy solving!");
            builder.setPositiveButton("I'm ready to solve!", null);

            AlertDialog dialog = builder.create();
            dialog.show();
            // Popup should only ever display once
            viewModel.setDisplayPopup(false);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initiate both viewmodels to provide permanence to changes made in Game and Color Fragments
        colorViewModel = new ViewModelProvider(requireActivity()).get(ColorViewModel.class);
        GameViewModel viewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        rootView = view;
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);
        TextView wordBankTextView = view.findViewById(R.id.word_bank);
        // If user does not have an API key, this forces them to go to the settings fragment
        String userAPIKey = WordAPIManager.getApiKey(requireContext());
        if (userAPIKey == null || userAPIKey.equals("")) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.settings_Fragment);
        } else {
            viewModel.setWordAPI(WordAPIClient.getClient());
        }
        // Initializes the api client and saves to viewmodel
        if (viewModel.getWordAPI() == null) {
            viewModel.setWordAPI(WordAPIClient.getClient());
        }
        // Checks if the word list is null and initializes it
        if (viewModel.getWords() == null) {
            newWords();
        }

        // Create a StringBuilder to build the text for the TextView
        StringBuilder wordBankText = new StringBuilder();

        // Append each word to the StringBuilder
        assert viewModel.getWords() != null;
        for (String word : viewModel.getWords()) {
            wordBankText.append(word).append("\n"); // Add a newline for each word
        }

        // Set the text of the TextView to the built text
        wordBankTextView.setText(wordBankText.toString());
        if (viewModel.getWordSearchGrid() == null) {
            ArrayList<String> currentWords = viewModel.getWords();
            viewModel.setWordSearchGrid(generateWordSearchGrid(currentWords));
        }
        // Congrats toast for game end
        congratulationsToast = Toast.makeText(requireContext(), "Congratulations! Press the refresh button for a new game.", Toast.LENGTH_LONG);
        // Creates the grid from data in viewmodel
        displayGrid(tableLayout, viewModel.getWordSearchGrid());
        // New game / Refresh FAB
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newWords();
                newGame();
            }
        });
        // Uses the shared viewmodel to check for color changes
        int defaultColor = ColorViewModel.getColorFromPreference(requireContext());
        wordBankTextView.setTextColor(defaultColor);
        colorViewModel.setSelectedColor(defaultColor);
        colorViewModel.getSelectedColor().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer color) {
                wordBankTextView.setTextColor(color);
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
        // Uses the retrofit API to initiate an API call
        Call<WordAPIRandomResponse> call = viewModel.getWordAPI().getRandomWord(
                WordAPIManager.getApiKey(requireContext()),
                letterPattern,
                letters,
                limit,
                page,
                true
        );
        // Uses enqueue to make the request asynchronous
        // Requires the custom callback to make the app wait for the request to finish
        call.enqueue(new Callback<WordAPIRandomResponse>() {
            @Override
            public void onResponse(@NonNull Call<WordAPIRandomResponse> call, @NonNull Response<WordAPIRandomResponse> response) {
                if (response.isSuccessful()) {
                    WordAPIRandomResponse apiResponse = response.body();
                    assert apiResponse != null;
                    String randomWord = apiResponse.getWord();
                    // Uses the viewmodel to store the word
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

    private String gridDebugStringBuilder(char[][] grid) {
        char[][] newGrid = Arrays.copyOf(grid, grid.length);
        for (int i = 0; i < grid.length; i++) {
            newGrid[i] = Arrays.copyOf(grid[i], grid[i].length);
        }
        StringBuilder gridStringBuilder = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                boolean isEmpty = (int) newGrid[i][j] == 0;
                if (isEmpty) {
                    newGrid[i][j] = 'X';
                }
            }
        }
        for (char[] row : newGrid) {
            String rowString = StringUtils.join(row, ' ');
            gridStringBuilder.append(rowString).append(System.lineSeparator());
        }
        return gridStringBuilder.toString();
    }


    private char[][] generateWordSearchGrid(List<String> words) {
        int numRows = viewModel.getCurrentGridSize();
        int numCols = viewModel.getCurrentGridSize();
        char[][] grid = new char[numRows][numCols];
        Log.d("GRD", "generateWordSearchGrid, initial grid: \n" + gridDebugStringBuilder(grid) + "\n");
        // Place words in the grid
        for (String word : words) {
            placeWord(grid, word); // Try placing each word up to 100 times
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


    private void placeWord(char[][] grid, String word) {
        int length = word.length();
        int startRow, startCol;
        boolean placed = false;
        // App actually generates multiple grids to ensure each word "fits"
        // maxAttempts is kept at 100 (reasonable value for four 6 letter words on a 10x10)
        int attempts = 0;

        while (!placed && attempts < 100) {
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
                Log.d("GRD", "New Grid: \n" + gridDebugStringBuilder(grid) + "\n");
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
        // Clears the original grid
        // Without this, new grids stack on top of the old one
        tableLayout.removeAllViews();
        // Iterate through grid length (10x10)
        for (int i = 0; i < grid.length; i++) {
            TableRow tableRow = new TableRow(requireContext());

            for (int j = 0; j < grid[i].length; j++) {
                // Creates the cells and adds their listeners for selection
                final int row = i;
                final int col = j;
                TextView cell = new TextView(requireContext());
                cell.setText(String.valueOf(grid[i][j]));
                cell.setPadding(40, 20, 30, 40);
                // Weight to prevent column clipping
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        0,
                        TableRow.LayoutParams.WRAP_CONTENT, 1f);
                cell.setLayoutParams(params);
                // Adds the click listener for selection
                cell.setOnClickListener(view -> onCellClicked(row,col));
                tableRow.addView(cell);
            }
            tableLayout.addView(tableRow);
        }
    }

    private void onCellClicked(int row, int col) {
        // Check if word selected is valid
        // If not, checkForWord returns null
        String selectedWord = checkForWord(row, col);

        Integer selectedColor = colorViewModel.getSelectedColor().getValue();
        TableLayout tableLayout = rootView.findViewById(R.id.tableLayout);

        if (selectedWord != null) {
            // If the word is in the bank remove it
            viewModel.removeWord(selectedWord);
            // Call updateWordBank to regenerate the word bank + text view
            updateWordBank();
            checkIfAllWordsGenerated();
            // Game end logic
            List<String> remainingWords = viewModel.getWords();
            if (remainingWords != null && remainingWords.isEmpty()) {
                congratulationsToast.show();
            }
        }
        // Apply the selected color to the clicked cell
        if (selectedColor != null) {
            TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
            if (tableRow != null) {
                TextView selectedCell = (TextView) tableRow.getChildAt(col);
                if (row == selectedRow && col == selectedCol) {
                    // Deselect the previously selected cell by setting its background to transparent
                    selectedCell.setBackgroundColor(Color.TRANSPARENT);
                    selectedRow = -1; // Reset the selected row and column
                    selectedCol = -1;
                }
                else {
                    // Set the background color for the newly selected cell
                    selectedCell.setBackgroundColor(selectedColor);
                    // Update the selected row and column
                    selectedRow = row;
                    selectedCol = col;
                }
            }
        }
    }

    // Method to check if the selected cell is part of any word
    private String checkForWord(int row, int col) {
        char[][] grid = viewModel.getWordSearchGrid();
        StringBuilder selectedWord = new StringBuilder();
        // First StringBuilder for checking horizontally (Left to Right)
        for (int i = col; i < grid[row].length && grid[row][i] != '\0'; i++) {
            selectedWord.append(grid[row][i]);
            if (viewModel.getWords().contains(selectedWord.toString())) {

                return selectedWord.toString();
            }
        }

        selectedWord.setLength(0);

        for (int i = row; i < grid.length && grid[i][col] != '\0'; i++) {
            selectedWord.append(grid[i][col]);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }
        // Reset StringBuilder for checking diagonally (top-left to bottom-right)
        selectedWord.setLength(0);

        for (int i = 0; row + i < grid.length && col + i < grid[row].length && grid[row + i][col + i] != '\0'; i++) {
            selectedWord.append(grid[row + i][col + i]);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }

        // Reset StringBuilder for checking diagonally (top-right to bottom-left)
        selectedWord.setLength(0);

        for (int i = 0; row - i >= 0 && col - i >= 0 && grid[row - i][col - i] != '\0'; i++) {
            selectedWord.append(grid[row - i][col - i]);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }
        // Reset StringBuilder for checking diagonally (bottom-left to top-right)
        selectedWord.setLength(0);

        for (int i = 0; row + i < grid.length && col - i >= 0 && grid[row + i][col - i] != '\0'; i++) {
            selectedWord.append(grid[row + i][col - i]);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }

        // Reset StringBuilder for checking diagonally (bottom-left to top-right)
        selectedWord.setLength(0);

        for (int i = 0; row - i >= 0 && col + i < grid[row].length && grid[row - i][col + i] != '\0'; i++) {
            selectedWord.append(grid[row - i][col + i]);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }
        // Reset StringBuilder for checking horizontally (right to left)
        selectedWord.setLength(0);

        for (int i = 0; col - i >= 0 && grid[row][col - i] != '\0'; i++) {
            selectedWord.append(grid[row][col - i]);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }
        // Reset StringBuilder for checking vertically (bottom to top)
        selectedWord.setLength(0);

        for (int i = 0; row - i >= 0 && grid[row - i][col] != '\0'; i++) {
            selectedWord.append(grid[row - i][col]);
            if (viewModel.getWords().contains(selectedWord.toString())) {
                return selectedWord.toString();
            }
        }

        return null;
    }
    private void newGame() {
        // Updates both the bank and grid for a new game
        // Never any need to update the grid without the bank
        updateWordBank();
        viewModel.setWordSearchGrid(generateWordSearchGrid(viewModel.getWords()));
        displayGrid(rootView.findViewById(R.id.tableLayout), viewModel.getWordSearchGrid());
    }
    private void updateWordBank() {
        // This function updates the word bank by building a new string builder
        TextView wordBankTextView = rootView.findViewById(R.id.word_bank);
        StringBuilder wordBankText = new StringBuilder();
        for (String word : viewModel.getWords()) {
            wordBankText.append(word).append("\n");
        }
        wordBankTextView.setText(wordBankText.toString());
    }

    private void checkIfAllWordsGenerated() {
        if (viewModel.getWords() != null && viewModel.getWords().size() == 4) {
            newGame();

        }
    }
    private void newWords() {
        ArrayList<String> newWords = new ArrayList<>();
        viewModel.setWords(newWords);
        // Callback function to ensure that we have the words prior to generating the bank
        WordGenerationCallback generationCallback = new WordGenerationCallback() {
            @Override
            public void onWordGenerated(String word) {
                checkIfAllWordsGenerated();
            }

            @Override
            public void onWordGenerationFailed(Throwable t) {
                checkIfAllWordsGenerated();
            }
        };
        // Actually get the words by calling getRandomWord and passing the callback function
        for (int i = 0; i < 4; i++) {
            getRandomWord("^[a-zA-Z]+$", 4, 1, 1, generationCallback);
        }
    }



}
