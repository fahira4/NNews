package com.example.nnews.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.nnews.BuildConfig;
import com.example.nnews.R;
import com.example.nnews.databinding.FragmentSettingsBinding;
import com.example.nnews.utils.ThemeUtils;

import java.io.File;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDarkModeSwitch();
        setupAppVersion();
        setupClearCache();
        setupAbout();
    }

    // ===================================================
    // DARK MODE
    // ===================================================

    private void setupDarkModeSwitch() {
        // Set initial state dari SharedPreferences
        boolean isDark = ThemeUtils.isDarkMode(requireContext());
        binding.switchDarkMode.setChecked(isDark);

        binding.switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    ThemeUtils.setDarkMode(requireContext(), isChecked);
                });
    }

    // ===================================================
    // APP VERSION
    // ===================================================

    private void setupAppVersion() {
        binding.tvVersion.setText(BuildConfig.VERSION_NAME);
    }

    // ===================================================
    // CLEAR CACHE
    // ===================================================

    private void setupClearCache() {
        binding.cardClearCache.setOnClickListener(v ->
                showClearCacheDialog()
        );
    }

    private void showClearCacheDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.label_clear_cache)
                .setMessage("Are you sure you want to clear all cached data?")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    clearCache();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void clearCache() {
        try {
            // Hapus HTTP cache dari OkHttp
            File cacheDir = new File(
                    requireContext().getCacheDir(), "http_cache"
            );
            deleteDirectory(cacheDir);

            Toast.makeText(
                    requireContext(),
                    R.string.msg_cache_cleared,
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {
            Toast.makeText(
                    requireContext(),
                    R.string.msg_error_generic,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void deleteDirectory(File directory) {
        if (directory != null && directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }

    // ===================================================
    // ABOUT
    // ===================================================

    private void setupAbout() {
        binding.cardAbout.setOnClickListener(v -> showAboutDialog());
    }

    private void showAboutDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_about, null);

        // Set versi di dialog
        TextView tvVersion = dialogView.findViewById(R.id.tv_dialog_version);
        tvVersion.setText(
                getString(R.string.label_version) + " " + BuildConfig.VERSION_NAME
        );

        new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}