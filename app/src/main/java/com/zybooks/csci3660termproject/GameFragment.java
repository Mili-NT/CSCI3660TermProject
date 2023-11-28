package com.zybooks.csci3660termproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {
    private WordAPIInterface wordAPI;
    public GameFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use the WordAPIManager to check SharedPref for a key
        String userAPIKey = WordAPIManager.getApiKey(requireContext());
        // If there is no key (e.g. when the user first runs the app), redirect to the settings fragment
        Log.d("API-DBG", "GameFragment Check: " + userAPIKey);
        if (userAPIKey == null) {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.settings_Fragment);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }
}