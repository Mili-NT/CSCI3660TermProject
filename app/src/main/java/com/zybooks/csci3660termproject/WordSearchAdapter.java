package com.zybooks.csci3660termproject;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class WordSearchAdapter extends BaseAdapter {
    private final Context context;
    private final char[][] grid;

    public WordSearchAdapter(Context context, char[][] grid) {
        this.context = context;
        this.grid = grid;
    }

    @Override
    public int getCount() {
        return grid.length * grid[0].length;
    }

    @Override
    public Object getItem(int position) {
        int row = position / grid[0].length;
        int col = position % grid[0].length;
        return grid[row][col];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TextView textView;
        if (convertView == null) {
            textView = new TextView(context);
            textView.setLayoutParams(new GridView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(8, 8, 8, 8);
            textView.setTextSize(18);
            textView.setTextColor(context.getResources().getColor(android.R.color.black));
            textView.setBackgroundColor(Color.WHITE); // Set a background color for clarity
        } else {
            textView = (TextView) convertView;
        }

        final int row = position / grid[0].length;
        final int col = position % grid[0].length;
        textView.setText(String.valueOf(grid[row][col]));

        // Handle click events
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLetterClick(row, col);
            }
        });

        return textView;
    }

    // Method to handle click events on letters
    // Declare a global variable to store the selected word
    private StringBuilder selectedWord = new StringBuilder();

// ...

    // Method to handle click events on letters
    private void handleLetterClick(int row, int col) {
        String clickedLetter = String.valueOf(grid[row][col]);

        // Append the clicked letter to the selectedWord
        selectedWord.append(clickedLetter);

        // Show the updated selectedWord in a Toast
        Toast.makeText(context, "Selected Word: " + selectedWord.toString(), Toast.LENGTH_SHORT).show();
    }

    private void resetSelectedWord(){
        selectedWord.setLength(0);
    }

}
