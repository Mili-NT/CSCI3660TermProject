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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // API CHECKS AND LISTENER
        // Gets button & text views
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
        // GRID CHECKS AND LISTENERS
        RadioGroup colorRadioGroup = rootView.findViewById(R.id.color_radio_group);
        // Iterate through RadioGroup children (in case we add more grid sizes)
        for (int i = 0; i < colorRadioGroup.getChildCount(); i++) {
            View radioButtonView = colorRadioGroup.getChildAt(i);
            if (radioButtonView instanceof RadioButton) {
                final RadioButton radioButton = (RadioButton) radioButtonView;
                // Creates a listener for each child of the RadioGroup
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // New bundle to transport the gridSize to GameFragment
                        Bundle bundle = new Bundle();
                        int gridSize = 6;
                        // Handle the RadioButton click event here
                        if (radioButton.getId() == R.id.radio_size6) {
                            // Nothing needs to be done, gridSize already 6
                        } else if (radioButton.getId() == R.id.radio_size10) {
                            gridSize = 10;
                        } else if (radioButton.getId() == R.id.radio_size12) {
                            gridSize = 12;
                        }
                        bundle.putInt("gridSize", gridSize);
                        // Create new GameFragment and pass in the new bundle as an argument
                        GameFragment gameFragment = new GameFragment();
                        gameFragment.setArguments(bundle);
                        // Replace the current GameFragment
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.nav_host_fragment, gameFragment) // Must be nav_host_fragment, not game_Fragment
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
        }

        return rootView;
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