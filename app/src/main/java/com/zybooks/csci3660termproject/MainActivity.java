package com.zybooks.csci3660termproject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import retrofit2.Call;
import retrofit2.Retrofit;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zybooks.csci3660termproject.api.WordAPIClient;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.responses.WordAPIResponse;
import com.zybooks.csci3660termproject.retrofit.WordAPIInterface;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Remove the type declaration to use the class-level variables
        navView = findViewById(R.id.nav_view);
        navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(
                    R.id.game_Fragment, R.id.color_Fragment, R.id.settings_Fragment)
                    .build();

            NavigationUI.setupWithNavController(navView, navController);
        }
    }
}

