package com.zybooks.csci3660termproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ColorFragment extends Fragment {
    private static final String COLOR_PREF = "colorPref";
    private static final String COLOR_KEY = "colorKey";
    // text view variable to set the color for GFG text
    private TextView gfgTextView;

    // two buttons to open color picker dialog and one to
    // set the color for GFG text
    private Button mSetColorButton, mPickColorButton;

    // view box to preview the selected color
    private View mColorPreview;

    // this is the default color of the preview box
    private int mDefaultColor;
    private ColorViewModel colorViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //inflates color fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color,container, false);

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        colorViewModel = new ViewModelProvider(requireActivity()).get(ColorViewModel.class);
        // register the GFG text with appropriate ID
        gfgTextView = view.findViewById(R.id.gfg_heading);

        // register two of the buttons with their
        // appropriate IDs
        mPickColorButton = view.findViewById(R.id.pick_color_button);
        mSetColorButton = view.findViewById(R.id.set_color_button);

        // and also register the view which shows the
        // preview of the color chosen by the user
        mColorPreview = view.findViewById(R.id.preview_selected_color);

        // set the default color to 0 as it is black
        SharedPreferences prefs = requireActivity().getSharedPreferences(COLOR_PREF, Context.MODE_PRIVATE);
        mDefaultColor = prefs.getInt(COLOR_KEY, Color.BLACK);
        mColorPreview.setBackgroundColor(mDefaultColor);
        gfgTextView.setTextColor(mDefaultColor);
        // button open the AmbilWanra color picker dialog.
        mPickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to make code look cleaner the color
                // picker dialog functionality are
                // handled in openColorPickerDialogue()
                // function
                openColorPickerDialogue();
            }
        });

        // button to set the color GFG text
        mSetColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // as the mDefaultColor is the global
                // variable its value will be changed as
                // soon as ok button is clicked from the
                // color picker dialog.
                gfgTextView.setTextColor(mDefaultColor);
            }
        });
    }

    // the dialog functionality is handled separately
    // using openColorPickerDialog this is triggered as
    // soon as the user clicks on the Pick Color button And
    // the AmbilWarnaDialog has 2 methods to be overridden
    // those are onCancel and onOk which handle the "Cancel"
    // and "OK" button of color picker dialog
    public void openColorPickerDialogue() {

        // the AmbilWarnaDialog callback needs 3 parameters
        // one is the context, second is default color,
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(requireContext(), mDefaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // leave this function body as
                        // blank, as the dialog
                        // automatically closes when
                        // clicked on cancel button
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // change the mDefaultColor to
                        // change the GFG text color as
                        // it is returned when the OK
                        // button is clicked from the
                        // color picker dialog
                        mDefaultColor = color;
                        colorViewModel.setSelectedColor(mDefaultColor);
                        saveColorToSharedPreferences(mDefaultColor);
                        // now change the picked color
                        // preview box to mDefaultColor
                        mColorPreview.setBackgroundColor(mDefaultColor);

                    }
                });
        colorPickerDialogue.show();
    }
    private void saveColorToSharedPreferences(int color) {
        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(COLOR_PREF, Context.MODE_PRIVATE).edit();
        editor.putInt(COLOR_KEY, color);
        editor.apply();
    }
}