package com.csci3660.cosmiccross;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class WordGrid {
    public static class WordCell {
        private char cellContent;
        private boolean isSelected;
        private boolean isPartOfWord;
        boolean isStartingCharacter;
        private String parentWord;

        public WordCell() {
            this.cellContent = '\0';
            this.isSelected = false;
            this.isPartOfWord = false;
            this.isStartingCharacter = false;
            this.parentWord = "";
        }
        public char getCellContent() {
            return this.cellContent;
        }
        public void setCellContent(char newCellContent) {
            this.cellContent = newCellContent;
        }
        public void toggleSelection() {
            this.isSelected = !this.isSelected;
        }
        public boolean isSelected() {
            return this.isSelected;
        }
        public void setWordCell(String word, boolean isStartingCharacter) {
            this.isPartOfWord = true;
            this.isStartingCharacter = isStartingCharacter;
            this.parentWord = word;
        }
    }
    private final WordCell[][] grid;
    private final HashMap<String, List<WordCell>> wordPositionMap;
    private final int gridSize;
    private final Random random = new Random();
    public WordGrid(int gridSize, List<String> words) {
        this.gridSize = gridSize;
        this.wordPositionMap = new HashMap<>();
        grid = new WordCell[gridSize][gridSize];
        initializeGrid(words);
    }
    private void initializeGrid(List<String> words) {
        // Initialize each cell in the grid
        for (int i = 0; i < this.gridSize; i++) {
            for (int j = 0; j < this.gridSize; j++) {
                this.grid[i][j] = new WordCell();
            }
        }

        for (String word : words) {
            placeWord(word); // Try placing each word up to 100 times
        }

        // Fill the remaining empty spaces with random letters
        for (int i = 0; i < this.gridSize; i++) {
            for (int j = 0; j < this.gridSize; j++) {
                if (!this.getCell(i, j).isPartOfWord) {
                    this.getCell(i, j).setCellContent((char) ('A' + random.nextInt(26)));
                }
            }
        }
    }
    public WordCell getCell(int x, int y) {
        return this.grid[x][y];
    }
    public int getGridSize() {
        return this.gridSize;
    }
    public char getContentByPosition(int x, int y) {
        return this.getCell(x, y).getCellContent();
    }
    public void toggleCellSelection(int x, int y) {
        WordCell cell = this.getCell(x, y);
        // Check if cell is part of a word AND is the starting character
        // A starting character is always going to be part of a word-- remove?
        if (cell.isPartOfWord && cell.isStartingCharacter) {
            // Toggle selection for every cell in the parent word
            for (WordCell wordCell : Objects.requireNonNull(this.wordPositionMap.get(cell.parentWord))) {
                wordCell.toggleSelection();
            }
        }
        else {
            // Only toggle for this particular cell
            cell.isSelected = !cell.isSelected;
        }
    }
    /**
     * @param word The word to placed in the grid
     */
    private void placeWord(String word) {
        int length = word.length();
        int startRow;
        int startCol;
        boolean placed = false;
        // App actually generates multiple grids to ensure each word "fits"
        // maxAttempts is kept at 100 (reasonable value for four 6 letter words on a 10x10)
        int attempts = 0;

        while (!placed && attempts < 100) {
            startRow = random.nextInt(this.gridSize);
            startCol = random.nextInt(this.gridSize);

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

            if (canPlaceWord(word, startRow, startCol, rowIncrement, colIncrement)) {
                ArrayList<WordCell> wordCellList = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    int row = startRow + i * rowIncrement;
                    int col = startCol + i * colIncrement;
                    WordCell cell = this.getCell(row, col);
                    cell.setCellContent(word.charAt(i));
                    cell.setWordCell(word, i == 0);
                    wordCellList.add(cell);
                }
                placed = true;
                this.wordPositionMap.put(word, wordCellList);
            }
            attempts++;
        }
    }
    /**
     * @param word The string from the word list being placed in the grid.
     * @param startRow Index of the row that the word will start on (randomly chosen).
     * @param startCol Index of the column that the word will start on (randomly chosen).
     * @param rowIncrement Integer by which each character in the word will be incremented horizontally.
     * @param colIncrement Integer by which each character in the word will be incremented vertically.
     * @return Returns True if the word fits at the position determined by the startRow and startCol, otherwise False
     */
    private boolean canPlaceWord(String word, int startRow, int startCol, int rowIncrement, int colIncrement) {
        int length = word.length();

        int endRow = startRow + (length - 1) * rowIncrement;
        int endCol = startCol + (length - 1) * colIncrement;

        if (endRow >= 0 && endRow < this.gridSize && endCol >= 0 && endCol < this.gridSize) {
            for (int i = 0; i < length; i++) {
                int row = startRow + i * rowIncrement;
                int col = startCol + i * colIncrement;

                if (this.getContentByPosition(row, col) != '\0' && this.getContentByPosition(row, col) != word.charAt(i)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    /**
     * @param row Row index of starting cell.
     * @param col Col index of starting cell.
     * @return String representing the cell's parent word or null.
     */
    public String checkForWord(int row, int col) {
        WordCell cell = this.getCell(row, col);
        if (cell.isPartOfWord && cell.isStartingCharacter) {
            return cell.parentWord;
        }
        return null;
    }
}