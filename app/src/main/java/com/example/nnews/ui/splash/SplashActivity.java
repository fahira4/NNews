package com.example.nnews.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.fahira.nnews.R;
import com.fahira.nnews.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Intent intent =
                    new Intent(SplashActivity.this,
                            MainActivity.class);

            startActivity(intent);

            finish();

        }, SPLASH_DELAY);
    }
}