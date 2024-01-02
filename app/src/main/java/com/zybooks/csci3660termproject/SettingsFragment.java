package com.zybooks.csci3660termproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.zybooks.csci3660termproject.api.WordAPIManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
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
        // API key isn't saved until button is pressed
        saveApiKeyButton.setOnClickListener(view -> saveApiKey());
        return rootView;
    }
    private void saveApiKey() {
        // Retrieve the API key from the EditText
        EditText editTextApiKey = requireView().findViewById(R.id.editTextApiKey);
        String userAPIKey = editTextApiKey.getText().toString();
        // Save to shared preferences
        WordAPIManager.saveApiKey(requireContext(), userAPIKey);
        Toast.makeText(requireContext(), "API Key saved", Toast.LENGTH_SHORT).show();
        editTextApiKey.setHint(userAPIKey);
    }
}