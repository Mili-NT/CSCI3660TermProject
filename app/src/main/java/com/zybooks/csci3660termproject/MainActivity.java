package com.zybooks.csci3660termproject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
TODO: Make highlighter automatic
TODO: Implement variable length and count of words (will involve making word bank scrollable)
TODO: Change the font and spacing for the word bank
TODO: Change the text color of the grid letters to complement the selected highlight color
TODO?: Variable grid sizes
*/
public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    private NavHostFragment navHostFragment;
    private ColorViewModel colorViewModel;
    private int starCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        colorViewModel = new ViewModelProvider(this).get(ColorViewModel.class);
        //nav bar
        navView = findViewById(R.id.nav_view);
        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(navView, navController);
        }
        addStarsWithDelay();
    }

    //Sporadically spread stars out
    private int getRandomPosition() {
        return (int) (Math.random() * 1000);
    }

    //Get random duration for twinkling animation
    private int getRandomDuration() {
        return (int) ((Math.random() * 2000)+ 1000);
    }

    //Random delay so stars appear staggered
    private int getRandomDelay() {
        return new Random().nextInt(1000);
    }

    //Adds star with delay between stars
    private void addStarsWithDelay() {
        final Handler handler = new Handler();
        final int delayMillis = 200;
        final FrameLayout container = findViewById(R.id.star_container); // Your FrameLayout

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addStar(container);
                starCount++;

                if (starCount < 100) {
                    handler.postDelayed(this, delayMillis);
                }
            }
        }, delayMillis);
    }

    //adds star to the background and makes it twinkle
    private void addStar(FrameLayout container) {
        ImageView star = new ImageView(this);
        star.setImageResource(R.drawable.star_shape); // Single star drawable

        int duration = getRandomDuration();
        int delay = getRandomDelay();

        //sets size of stars
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(getRandomPosition(), getRandomPosition(), 0, 0);
        star.setLayoutParams(params);

        //Makes stars twinkle
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.star_fade);
        animation.setDuration(duration);
        animation.setStartOffset(delay);
        star.startAnimation(animation);

        container.addView(star);
    }
}

