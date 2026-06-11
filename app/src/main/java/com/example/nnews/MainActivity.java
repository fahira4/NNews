package com.example.nnews;

import android.content.Intent; // Tambahkan import Intent ini
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.nnews.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.example.nnews.databinding.ActivityMainBinding;
import com.example.nnews.utils.ThemeUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        // ==========================================
        // PENGECEKAN SESI FIREBASE
        // ==========================================
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Jika user belum login, langsung lempar ke LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // Hancurkan MainActivity agar tidak menumpuk di riwayat tombol "Back"
            return;   // 'return' agar kode di bawah ini (load UI) tidak dieksekusi
        }
        // ==========================================

        ThemeUtils.applySavedTheme(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(
                    binding.bottomNavigation,
                    navController
            );
        }

        if (navController != null) {
            navController.addOnDestinationChangedListener(
                    (controller, destination, arguments) -> {
                        if (destination.getId() == R.id.detailFragment) {
                            binding.bottomNavigation.setVisibility(View.GONE);
                        } else {
                            binding.bottomNavigation.setVisibility(View.VISIBLE);
                        }
                    });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (navController != null) {
            return navController.navigateUp()
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navController = null;
        binding = null;
    }
}