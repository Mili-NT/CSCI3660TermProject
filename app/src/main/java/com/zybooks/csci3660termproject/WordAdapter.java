package com.zybooks.csci3660termproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<String> words;
    private int selectedColor;

    public WordAdapter(List<String> words, int selectedColor) {
        this.words = words;
        this.selectedColor = selectedColor;
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
        holder.wordTextView.setTextColor(selectedColor);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public void setWords(List<String> newWords) {
        this.words = newWords;
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordTextView;

        WordViewHolder(View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.wordTextView);
        }
    }
}
