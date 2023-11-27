package com.zybooks.csci3660termproject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import retrofit2.Retrofit;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.retrofit.WordAPI;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private WordAPI wordAPI;
    private BottomNavigationView navView;
    private NavHostFragment navHostFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create Retrofit client
        Retrofit retrofit = WordAPIManager.getClient();
        // Create API interface
        wordAPI = retrofit.create(WordAPI.class);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                    R.id.game_Fragment, R.id.color_Fragment, R.id.settings_Fragment)
                    .build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
            NavigationUI.setupWithNavController(navView, navController);
        }
    }
}

