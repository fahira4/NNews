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
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.nnews.BuildConfig;
import com.example.nnews.R;
import com.example.nnews.databinding.FragmentSettingsBinding;
import com.example.nnews.utils.ThemeUtils;
import com.example.nnews.viewmodel.NewsViewModel;
import com.example.nnews.viewmodel.NewsViewModelFactory;

import java.io.File;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private NewsViewModel viewModel;

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

        setupViewModel();
        setupDarkModeSwitch();
        setupAppVersion();
        setupClearCache();
        setupClearBookmarks();
        setupAbout();
    }

    // ===================================================
    // SETUP
    // ===================================================

    private void setupViewModel() {
        NewsViewModelFactory factory =
                new NewsViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(requireActivity(), factory)
                .get(NewsViewModel.class);
    }

    // ===================================================
    // DARK MODE
    // ===================================================

    private void setupDarkModeSwitch() {
        boolean isDark = ThemeUtils.isDarkMode(requireContext());
        binding.switchDarkMode.setChecked(isDark);

        binding.switchDarkMode.setOnCheckedChangeListener(
                (buttonView, isChecked) ->
                        ThemeUtils.setDarkMode(requireContext(), isChecked)
        );
    }

    // ===================================================
    // VERSION
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
                .setMessage(R.string.dialog_clear_cache_msg)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> clearCache())
                .setNegativeButton(R.string.label_cancel, null)
                .show();
    }

    private void clearCache() {
        try {
            // 1. Hapus HTTP Cache OkHttp
            File httpCache = new File(
                    requireContext().getCacheDir(), "http_cache"
            );
            deleteDirectory(httpCache);

            // 2. Hapus Glide disk cache (background thread)
            new Thread(() -> {
                try {
                    Glide.get(requireContext()).clearDiskCache();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // 3. Hapus Glide memory cache (main thread)
            Glide.get(requireContext()).clearMemory();

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
    // CLEAR ALL BOOKMARKS
    // ===================================================

    private void setupClearBookmarks() {
        binding.cardClearBookmarks.setOnClickListener(v ->
                showClearBookmarksDialog()
        );
    }

    private void showClearBookmarksDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.label_clear_bookmarks)
                .setMessage(R.string.dialog_clear_bookmarks_msg)
                .setPositiveButton(R.string.label_delete,
                        (dialog, which) -> clearAllBookmarks())
                .setNegativeButton(R.string.label_cancel, null)
                .show();
    }

    private void clearAllBookmarks() {
        viewModel.clearAllBookmarks();
        Toast.makeText(
                requireContext(),
                R.string.msg_bookmarks_cleared,
                Toast.LENGTH_SHORT
        ).show();
    }

    // ===================================================
    // ABOUT
    // ===================================================

    private void setupAbout() {
        binding.cardAbout.setOnClickListener(v ->
                showAboutDialog()
        );
    }

    private void showAboutDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_about, null);

        TextView tvVersion = dialogView
                .findViewById(R.id.tv_dialog_version);
        tvVersion.setText(
                getString(R.string.label_version)
                        + " " + BuildConfig.VERSION_NAME
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