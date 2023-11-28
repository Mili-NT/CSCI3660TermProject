package com.zybooks.csci3660termproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.zybooks.csci3660termproject.api.WordAPIManager;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // API CHECKS AND LISTENER
        Button saveApiKeyButton = rootView.findViewById(R.id.buttonSaveApiKey);
        EditText editTextApiKey = rootView.findViewById(R.id.editTextApiKey);
        String userAPIKey = WordAPIManager.getApiKey(requireContext()); // requireContext() is required in fragments
        if (userAPIKey != null) {
            editTextApiKey.setHint(userAPIKey); // Updates text hint to display key -- not necessary but looks good
        }
        saveApiKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveApiKey();
            }
        });
        // GRID SIZE & CHIP LISTENERS

        return rootView;
    }
    private void updateGameFragment(int gridSize) {
        // Create a bundle and send it to GameFragment
        Bundle bundle = new Bundle();
        bundle.putInt("gridSize", gridSize);

        GameFragment gameFragment = new GameFragment();
        gameFragment.setArguments(bundle);

        // Replace the current GameFragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, gameFragment)
                .addToBackStack(null)
                .commit();
    }
    private void saveApiKey() {
        // TODO: Add validity checking?
        // Retrieve the API key from the EditText
        EditText editTextApiKey = requireView().findViewById(R.id.editTextApiKey);
        String userAPIKey = editTextApiKey.getText().toString();
        // Save to shared preferences
        WordAPIManager.saveApiKey(requireContext(), userAPIKey);
        // TODO: Better looking toast/pop-up
        Toast.makeText(requireContext(), "API Key saved", Toast.LENGTH_SHORT).show();
        editTextApiKey.setHint(userAPIKey);
    }
}