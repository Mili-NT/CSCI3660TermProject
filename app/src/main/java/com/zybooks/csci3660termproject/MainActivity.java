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

        //starry background
        FrameLayout starrySky = findViewById(R.id.starrySky);

        for (int i = 0; i < 100; i++) {
            ImageView star = new ImageView(this);
            star.setImageResource(R.drawable.star_shape);

            Animation animation = AnimationUtils.loadAnimation(this, R.anim.star_fade);
            starrySky.startAnimation(animation);

            starrySky.addView(star);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) star.getLayoutParams();
            params.leftMargin = getRandomPosition();
            params.topMargin = getRandomPosition();
            star.setLayoutParams(params);
        }
    }

    private int getRandomPosition() {
        return (int) (Math.random() * getResources().getDisplayMetrics().widthPixels);
    }

}

