package com.example.nnews.utils;

/**
 * Konstanta global aplikasi.
 * Hindari hardcoded value di seluruh project.
 */
public final class Constants {

    // Private constructor — class ini tidak boleh di-instantiate
    private Constants() {}

    // API
    public static final String BASE_URL = "https://newsapi.org/v2/";
    public static final String API_KEY = "YOUR_API_KEY_HERE";

    // Database
    public static final String DATABASE_NAME = "nnews_database";
    public static final int DATABASE_VERSION = 1;

    // SharedPreferences
    public static final String PREF_NAME = "nnews_prefs";
    public static final String PREF_DARK_MODE = "dark_mode";

    // Network
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;

    // Pagination
    public static final int PAGE_SIZE = 20;
    public static final String DEFAULT_COUNTRY = "us";
    public static final String DEFAULT_CATEGORY = "general";
}