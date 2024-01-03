package com.csci3660.cosmiccross.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.csci3660.cosmiccross.R;
import com.csci3660.cosmiccross.viewmodels.ColorViewModel;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ColorFragment extends Fragment {
    // text view variable to set the color for GFG text
    // view box to preview the selected color
    private View mColorPreview;

    // this is the default color of the preview box
    private ColorViewModel colorViewModel;

    //inflates color fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_color,container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        colorViewModel = new ViewModelProvider(requireActivity()).get(ColorViewModel.class);
        // register the GFG text with appropriate ID
        TextView gfgTextView = view.findViewById(R.id.gfg_heading);

        // register two of the buttons with their
        // appropriate IDs
        Button mPickColorButton = view.findViewById(R.id.pick_color_button);
        // two buttons to open color picker dialog and one to
        // set the color for GFG text
        Button mSetColorButton = view.findViewById(R.id.set_color_button);

        // and also register the view which shows the
        // preview of the color chosen by the user
        mColorPreview = view.findViewById(R.id.preview_selected_color);

        // set the default color to 0 as it is black
        int mDefaultColor = ColorViewModel.getColorFromPreference(requireActivity());
        colorViewModel.setSelectedColor(mDefaultColor);
        mColorPreview.setBackgroundColor(mDefaultColor);
        if (mDefaultColor == Color.BLACK) {
            // Black text is hard to see against the starry background
            gfgTextView.setTextColor(Color.WHITE);
        }
        else {
            gfgTextView.setTextColor(mDefaultColor);
        }
        // button open the AmbilWanra color picker dialog.
        mPickColorButton.setOnClickListener(v ->
            // to make code look cleaner the color
            // picker dialog functionality are
            // handled in openColorPickerDialogue()
            // function
            openColorPickerDialogue()
        );

        // button to set the color GFG text
        mSetColorButton.setOnClickListener(v -> {
            // as the mDefaultColor is the global
            // variable its value will be changed as
            // soon as ok button is clicked from the
            // color picker dialog.
            int selectedColor = colorViewModel.getSelectedColor();
            colorViewModel.setSelectedColor(selectedColor);
            gfgTextView.setTextColor(selectedColor);
            colorViewModel.saveColorToSharedPreferences(selectedColor, requireContext());
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
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(requireContext(), colorViewModel.getSelectedColor(),
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
                        colorViewModel.setSelectedColor(color);
                        // now change the picked color
                        // preview box to mDefaultColor
                        mColorPreview.setBackgroundColor(color);

                    }
                });
        colorPickerDialogue.show();
    }
}