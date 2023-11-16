package com.zybooks.csci3660termproject;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.zybooks.csci3660termproject.api.WordAPIManager;
import com.zybooks.csci3660termproject.retrofit.WordAPI;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private WordAPI wordAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create Retrofit client
        Retrofit retrofit = WordAPIManager.getClient();
        // Create API interface
        wordAPI = retrofit.create(WordAPI.class);
    }
}