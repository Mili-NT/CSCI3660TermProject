package com.csci3660.cosmiccross.fragments;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;


import com.csci3660.cosmiccross.viewmodels.ColorViewModel;
import com.csci3660.cosmiccross.viewmodels.GameViewModel;
import com.csci3660.cosmiccross.ui.WordAdapter;
import com.csci3660.cosmiccross.WordGenerationCallback;
import com.csci3660.cosmiccross.data.responses.WordAPIRandomResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zybooks.csci3660termproject.R;
import com.csci3660.cosmiccross.data.api.WordAPIClient;
import com.csci3660.cosmiccross.data.api.WordAPIManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {
    private GameViewModel gameViewModel;
    private ColorViewModel colorViewModel;
    private WordAdapter wordAdapter;
    private RecyclerView wordBankRecyclerView;
    private Toast congratulationsToast;
    private final Random random = new Random();
    public GameFragment() {
        // Required empty public constructor
    }
    /**
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initiate both viewmodels to provide permanence to changes made in Game and Color Fragments
        colorViewModel = new ViewModelProvider(requireActivity()).get(ColorViewModel.class);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        // If user does not have an API key, this forces them to go to the settings fragment
        String userAPIKey = WordAPIManager.getApiKey(requireContext());
        if (userAPIKey == null || userAPIKey.equals("")) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.settings_Fragment);
        } else {
            // If the user does have an API key, game set-up can begin
            initObserversAndListeners();
            initGameElements();
            initRecyclerView();
            displayGrid();
        }
    }
    /**
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return                   The inflated game fragment view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);

        return inflater.inflate(R.layout.fragment_game, container, false);
    }
    //
    // Initialization Methods
    //
    private void initRecyclerView() {
        wordBankRecyclerView = this.requireView().findViewById(R.id.wordBankRecyclerView);
        wordBankRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        boolean createPlaceholderViews = Objects.requireNonNull(gameViewModel.getWordsLiveData().getValue()).isEmpty();
        if (createPlaceholderViews) {
            gameViewModel.addPlaceholders();
        }
        wordAdapter = new WordAdapter(gameViewModel.getWordsLiveData().getValue(),
                colorViewModel.getSelectedColor().getValue(),
                gameViewModel);
        wordBankRecyclerView.setAdapter(wordAdapter);
    }
    private void initObserversAndListeners() {
        FloatingActionButton fab = this.requireView().findViewById(R.id.fab);
        View gameView = this.getView();
        wordBankRecyclerView = this.requireView().findViewById(R.id.wordBankRecyclerView);
        gameViewModel.getWordsLiveData().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<String> words) {
                wordAdapter.setWords(words);
                wordAdapter.notifyDataSetChanged();
            }
        });
        fab.setOnClickListener(view -> newWords());
        ViewTreeObserver viewTreeObserver = this.requireView().getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                assert gameView != null;
                gameView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (areViewsOverlapping(fab, wordBankRecyclerView)) {
                    hideFAB();
                }
                return true;
            }
        });
    }
    private void initGameElements() {
        // API Client
        gameViewModel.setWordAPI(WordAPIClient.getClient());
        // Words Data
        if (gameViewModel.getWordsLiveData().getValue() == null) {
            newWords();
        }
        // Grid Data
        if (gameViewModel.getWordSearchGrid() == null) {
            generateWordSearchGrid();
        }
        // Pop-up Handler
        new Handler(Looper.getMainLooper()).postDelayed(this::showPopup, 1000);
        // Congratulations Toast
        congratulationsToast = Toast.makeText(requireContext(), "Congratulations! Press the refresh button for a new game.", Toast.LENGTH_LONG);
        // Highlighter Color
        int colorFromPreference = ColorViewModel.getColorFromPreference(requireContext());
        colorViewModel.setSelectedColor(colorFromPreference);
    }
    //
    // Word Grid Methods (Generation, Word Placement & Fitting, Grid Cell Code)
    //
    private void generateWordSearchGrid() {
        int numRows = gameViewModel.getCurrentGridSize();
        int numCols = gameViewModel.getCurrentGridSize();
        List<String> words = gameViewModel.getWordsLiveData().getValue();
        char[][] grid = new char[numRows][numCols];
        int[][] selectedGrid = new int[numRows][numCols];
        // Place words in the grid
        assert words != null;
        for (String word : words) {
            placeWord(grid, word); // Try placing each word up to 100 times
        }

        // Fill the remaining empty spaces with random letters
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                selectedGrid[i][j] = 0;
                if (grid[i][j] == '\0') {
                    grid[i][j] = (char) ('A' + random.nextInt(26));
                }
            }
        }
        gameViewModel.setSelectedGrid(selectedGrid);
        gameViewModel.setWordSearchGrid(grid);
    }
    private void displayGrid() {
        char[][] grid = gameViewModel.getWordSearchGrid();
        // Clears the original grid
        // Without this, new grids stack on top of the old one
        TableLayout tableLayout = this.requireView().findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();
        // Iterate through grid length (10x10)
        for (int i = 0; i < grid.length; i++) {
            TableRow tableRow = new TableRow(requireContext());

            for (int j = 0; j < grid[i].length; j++) {
                // Creates the cells and adds their listeners for selection
                final int row = i;
                final int col = j;
                TextView cell = new TextView(requireContext());
                // Sets cell contents (letter)
                cell.setText(String.valueOf(grid[i][j]));
                // Sets cell padding and gravity
                cell.setPadding(40, 20, 30, 40);
                cell.setGravity(Gravity.CENTER); // This prevents the letters clipping
                cell.setTextColor(Color.WHITE);
                if (gameViewModel.isCellSelected(row, col)) {
                    cell.setBackgroundColor(colorViewModel.getSelectedColor().getValue());
                }
                cell.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                cell.setShadowLayer(6f, 0f, 0f, Color.WHITE);
                // Applies row parameters
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        WRAP_CONTENT,
                        WRAP_CONTENT, 1f);
                cell.setLayoutParams(params);
                // Adds the click listener for selection
                cell.setOnClickListener(view -> onCellClicked(row, col));
                tableRow.addView(cell);

            }
            tableLayout.addView(tableRow);
        }
    }
    /**
     * @param grid The character array representing the currently being generated
     * @param word The string from the word list being placed in the grid
     */
    private void placeWord(char[][] grid, String word) {
        int length = word.length();
        int startRow;
        int startCol;
        boolean placed = false;
        // App actually generates multiple grids to ensure each word "fits"
        // maxAttempts is kept at 100 (reasonable value for four 6 letter words on a 10x10)
        int attempts = 0;

        while (!placed && attempts < 100) {
            startRow = random.nextInt(gameViewModel.getCurrentGridSize());
            startCol = random.nextInt(gameViewModel.getCurrentGridSize());

            int[][] directions = {
                    {0, 1}, // Horizontal (Right->Left)
                    {0, -1}, // Horizontal (Left->Right)
                    {1, 0}, // Vertical (Top->Bottom)
                    {-1, 0}, // Vertical (Bottom->Top)
                    {1, 1}, // Diagonal (Top Left->Bottom Right)
                    {-1, -1}, // Diagonal (Bottom Right->Top Left)
                    {1, -1}, // Diagonal (Top Left->Bottom Right)
                    {-1, 1}, // Diagonal (Bottom Left->Top Right)
            };
            // Select random direction for word
            int[] direction = directions[random.nextInt(directions.length)];
            int rowIncrement = direction[0];
            int colIncrement = direction[1];

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
    /**
     * @param grid The character array representing the word search grid.
     * @param word The string from the word list being placed in the grid.
     * @param startRow Index of the row that the word will start on (randomly chosen).
     * @param startCol Index of the column that the word will start on (randomly chosen).
     * @param rowIncrement Integer by which each character in the word will be incremented horizontally.
     * @param colIncrement Integer by which each character in the word will be incremented vertically.
     * @return Returns True if the word fits at the position determined by the startRow and startCol, otherwise False
     */
    private boolean canPlaceWord(char[][] grid, String word, int startRow, int startCol, int rowIncrement, int colIncrement) {
        int length = word.length();

        int endRow = startRow + (length - 1) * rowIncrement;
        int endCol = startCol + (length - 1) * colIncrement;

        if (endRow >= 0 && endRow < gameViewModel.getCurrentGridSize() && endCol >= 0 && endCol < gameViewModel.getCurrentGridSize()) {
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
    /**
     * @param row Index of the row where the word was selected
     * @param col Index of the column where the word was selected
     * @return Returns the string of the selected word if (row, col) is a valid position. Otherwise returns null.
     */
    private String checkForWord(int row, int col) {
        char[][] grid = gameViewModel.getWordSearchGrid();
        List<String> validWords = gameViewModel.getWordsLiveData().getValue();
        assert validWords != null;

        int[][] directions = {
                {0, 1}, // Horizontal (Right->Left)
                {0, -1}, // Horizontal (Left->Right)
                {1, 0}, // Vertical (Top->Bottom)
                {-1, 0}, // Vertical (Bottom->Top)
                {1, 1}, // Diagonal (Top Left->Bottom Right)
                {-1, -1}, // Diagonal (Bottom Right->Top Left)
                {1, -1}, // Diagonal (Top Left->Bottom Right)
                {-1, 1}, // Diagonal (Bottom Left->Top Right)
        };

        for (int[] direction : directions) {
            StringBuilder selectedWord = new StringBuilder();

            int rowIncrement = direction[0];
            int colIncrement = direction[1];

            for (int i = 0; checkValidPosition(row + i * rowIncrement, col + i * colIncrement, grid); i++) {
                selectedWord.append(grid[row + i * rowIncrement][col + i * colIncrement]);

                if (validWords.contains(selectedWord.toString())) {
                    return selectedWord.toString();
                }
            }
        }

        return null;
    }

    /**
     * @param row Row position of starting cell.
     * @param col Column position of starting cell.
     * @param grid The current word search grid.
     * @return True if selected position is valid, otherwise false.
     */
    private boolean checkValidPosition(int row, int col, char[][] grid) {
        return row >= 0 && row < grid.length && col >= 0 && col < grid[row].length && grid[row][col] != '\0';
    }
    /**
     * @param row Index of the row where the word was selected
     * @param col Index of the column where the word was selected
     */
    private void onCellClicked(int row, int col) {
        // Check if word selected is valid
        // If not, checkForWord returns null
        String selectedWord = checkForWord(row, col);
        int selectedColor = colorViewModel.getSelectedColor().getValue();
        TableLayout tableLayout = this.requireView().findViewById(R.id.tableLayout);

        if (selectedWord != null && !gameViewModel.isWordFound(selectedWord)) {
            // If the word is in the bank remove it
            gameViewModel.addToSelectedWords(selectedWord);
            // Call updateWordBank to regenerate the word bank
            updateWordBank();
            // Game end logic
            if (gameViewModel.getRemainingWordCount() == 0) {
                congratulationsToast.show();
            }
        }
        // Apply the selected color to the clicked cell
        TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
        if (tableRow != null) {
            TextView selectedCell = (TextView) tableRow.getChildAt(col);
            if (gameViewModel.isCellSelected(row, col)) {
                // Deselect the previously selected cell by setting its background to transparent
                selectedCell.setBackgroundColor(Color.TRANSPARENT);
            }
            else {
                // Set the background color for the newly selected cell
                selectedCell.setBackgroundColor(selectedColor);
            }
            gameViewModel.toggleCellSelection(row, col);
        }
    }
    //
    // Words Methods (API Interactions)
    //
    private void newWords() {
        ArrayList<String> newWords = new ArrayList<>();
        gameViewModel.setWords(newWords);
        gameViewModel.addPlaceholders();
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
        for (int i = 0; i < gameViewModel.getTotalWordCount(); i++) {
            getRandomWord("^[a-zA-Z]+$", 4, 1, 1, generationCallback);
        }
    }
    /**
     * @param letterPattern A regular expression pattern to apply to the API query.
     * @param letters The number of letters returned words should have.
     * @param limit  Maximum number of words for the query.
     * @param page The page of the query results.
     * @param callback Callback function to run after the query is ran.
     */
    public void getRandomWord(String letterPattern, int letters, int limit, int page, WordGenerationCallback callback) {
        // Uses the retrofit API to initiate an API call
        Call<WordAPIRandomResponse> call = gameViewModel.getWordAPI().getRandomWord(
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
                    gameViewModel.addWord(randomWord);
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
    private void checkIfAllWordsGenerated() {
        if (gameViewModel.getWordsLiveData().getValue() != null) {
            newGame();
        }
    }
    //
    // Game Update Methods
    //
    private void newGame() {
        // Updates both the bank and grid for a new game
        // Never any need to update the grid without the bank
        generateWordSearchGrid();
        if (gameViewModel.getCurrentWordCount() == gameViewModel.getTotalWordCount()) {
            updateWordBank();
            displayGrid();
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void updateWordBank() {
        wordAdapter = (WordAdapter) wordBankRecyclerView.getAdapter();
        assert wordAdapter != null;
        // Deal with placeholder views
        gameViewModel.wipePlaceholders();
        wordAdapter.setWords(gameViewModel.getWordsLiveData().getValue());
        wordAdapter.notifyDataSetChanged();
    }
    //
    // UI/UX Methods
    //
    private void hideFAB() {
        //this.requireView().findViewById(R.id.fab).setVisibility(View.GONE);
    }
    /**
     * @param viewOne A view object on screen.
     * @param viewTwo A second view object that might be overlapping.
     * @return True if the views overlap, otherwise False.
     */
    private boolean areViewsOverlapping(View viewOne, View viewTwo) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        viewOne.getLocationOnScreen(firstPosition);
        viewTwo.getLocationOnScreen(secondPosition);

        Rect rectFirstView = new Rect(firstPosition[0], firstPosition[1],
                firstPosition[0] + viewOne.getMeasuredWidth(), firstPosition[1] + viewOne.getMeasuredHeight());
        Rect rectSecondView = new Rect(secondPosition[0], secondPosition[1],
                secondPosition[0] + viewTwo.getMeasuredWidth(), secondPosition[1] + viewTwo.getMeasuredHeight());

        return rectFirstView.intersect(rectSecondView);
    }
    private void showPopup() {
        if (gameViewModel.shouldDisplayPopup() && isAdded() && getActivity() != null && !getActivity().isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Welcome to CosmicCross!");
            builder.setMessage("Try and find all of the words to complete the crossword! Happy solving!");
            builder.setPositiveButton("I'm ready to solve!", null);

            AlertDialog dialog = builder.create();
            dialog.show();
            // Popup should only ever display once
            gameViewModel.setDisplayPopup(false);
        }
    }
}
