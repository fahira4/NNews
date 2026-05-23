package com.example.nnews;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.nnews.databinding.ActivityMainBinding;
import com.example.nnews.utils.ThemeUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Wajib sebelum super.onCreate
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Apply saved theme
        ThemeUtils.applySavedTheme(this);

        // ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Navigation
        setupNavigation();
    }

    private void setupNavigation() {
        // Ambil NavController dari NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Hubungkan BottomNav dengan NavController
            NavigationUI.setupWithNavController(
                    binding.bottomNavigation,
                    navController
            );
        }

        // Sembunyikan bottom nav di detail screen
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.detailFragment) {
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}