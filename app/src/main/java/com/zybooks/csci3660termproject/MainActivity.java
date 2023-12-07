package com.zybooks.csci3660termproject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    private NavHostFragment navHostFragment;
    private GameViewModel gameViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);

        navView = findViewById(R.id.nav_view);
        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(navView, navController);
        }
    }
}

