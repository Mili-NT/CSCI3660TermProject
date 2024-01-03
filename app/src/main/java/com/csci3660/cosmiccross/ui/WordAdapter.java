package com.csci3660.cosmiccross.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csci3660.cosmiccross.R;
import com.csci3660.cosmiccross.viewmodels.GameViewModel;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<String> words;
    private int selectedColor;
    private final GameViewModel gameViewModel;

    public WordAdapter(List<String> words, int selectedColor, GameViewModel gameViewModel) {
        this.words = words;
        this.selectedColor = selectedColor;
        this.gameViewModel = gameViewModel;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        String word = words.get(position);
        holder.wordTextView.setText(word);
        if (word.contains("placeholder")) {
            holder.wordTextView.setTextColor(Color.TRANSPARENT);
        }
        else {
            holder.wordTextView.setTextSize(16);
            holder.wordTextView.setTextColor(selectedColor);
        }
        // If word is found, apply a strikethrough
        if (gameViewModel.isWordFound(word)) {
            holder.wordTextView.setPaintFlags(holder.wordTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.wordTextView.setPaintFlags(holder.wordTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public void setWords(List<String> newWords) {
        this.words = newWords;
    }
    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }
    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView;

        WordViewHolder(View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.wordTextView);
        }
    }
}
