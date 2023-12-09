package com.zybooks.csci3660termproject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //nav bar
        navView = findViewById(R.id.nav_view);
        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(navView, navController);
        }

        // List to store individual delays for each star
        List<Integer> starDelays = new ArrayList<>();
        FrameLayout container = findViewById(R.id.star_container);

        //Add stars to background in activity main
        for (int i = 0; i < 50; i++) {
            ImageView star = new ImageView(this);
            star.setImageResource(R.drawable.star_shape); // Single star drawable

            //sets size of stars
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );

            // Set random positions for stars
            params.setMargins(getRandomPosition(), getRandomPosition(), 0, 0);
            star.setLayoutParams(params);

            int duration = getRandomDuration();
            int delay = getRandomDelay();
            starDelays.add(delay);

            // Apply twinkling animation to stars
            star.setAnimation(AnimationUtils.loadAnimation(this, R.anim.star_fade));
            container.addView(star);
        }
    }

    //Sporadically spread stars out
    private int getRandomPosition() {
        return (int) (Math.random() * 1000); // Adjust this range as needed
    }

    // Method to get random duration for twinkling animation
    private int getRandomDuration() {
        return (int) (Math.random() * 2000); // Adjust this range as needed
    }
    // Method to get random delay for twinkling animation
    private int getRandomDelay() {
        return new Random().nextInt(1000); // Adjust this range as needed
    }
}

