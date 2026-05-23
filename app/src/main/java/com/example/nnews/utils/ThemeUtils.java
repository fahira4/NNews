package com.example.nnews.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Utility class untuk mengelola tema dark/light mode.
 * Menyimpan preferensi user menggunakan SharedPreferences.
 */
public final class ThemeUtils {

    private ThemeUtils() {}

    /**
     * Apply tema yang tersimpan saat app dibuka.
     * Dipanggil di MainActivity sebelum setContentView.
     */
    public static void applySavedTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREF_NAME,
                Context.MODE_PRIVATE
        );
        boolean isDarkMode = prefs.getBoolean(Constants.PREF_DARK_MODE, false);
        applyTheme(isDarkMode);
    }

    /**
     * Toggle dan simpan preferensi dark mode.
     * @param context Context untuk SharedPreferences
     * @param isDarkMode true = dark, false = light
     */
    public static void setDarkMode(Context context, boolean isDarkMode) {
        // Simpan preferensi
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREF_NAME,
                Context.MODE_PRIVATE
        );
        prefs.edit()
                .putBoolean(Constants.PREF_DARK_MODE, isDarkMode)
                .apply();

        // Apply tema
        applyTheme(isDarkMode);
    }

    /**
     * Cek apakah dark mode sedang aktif.
     */
    public static boolean isDarkMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.PREF_NAME,
                Context.MODE_PRIVATE
        );
        return prefs.getBoolean(Constants.PREF_DARK_MODE, false);
    }

    /**
     * Apply tema ke AppCompatDelegate.
     */
    private static void applyTheme(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
            );
        } else {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
            );
        }
    }
}