package com.example.nnews.utils;

/**
 * Konstanta global aplikasi.
 * Semua hardcoded value dilarang — gunakan class ini.
 */
public final class Constants {

    private Constants() {}

    // ===== API =====
    public static final String BASE_URL = "https://gnews.io/api/v4/";

    // ===== ENDPOINTS =====
    public static final String ENDPOINT_TOP_HEADLINES = "top-headlines";
    public static final String ENDPOINT_SEARCH = "search";

    // ===== QUERY PARAMS =====
    public static final String PARAM_API_KEY = "apikey";
    public static final String PARAM_LANG = "lang";
    public static final String PARAM_COUNTRY = "country";
    public static final String PARAM_CATEGORY = "category";
    public static final String PARAM_QUERY = "q";
    public static final String PARAM_MAX = "max";
    public static final String PARAM_PAGE = "page";

    // ===== DEFAULT VALUES =====
    public static final String DEFAULT_LANG = "en";
    public static final String DEFAULT_COUNTRY = "us";
    public static final String DEFAULT_CATEGORY = "general";
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 10;

    // ===== CATEGORIES =====
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_WORLD = "world";
    public static final String CATEGORY_NATION = "nation";
    public static final String CATEGORY_BUSINESS = "business";
    public static final String CATEGORY_TECHNOLOGY = "technology";
    public static final String CATEGORY_ENTERTAINMENT = "entertainment";
    public static final String CATEGORY_SPORTS = "sports";
    public static final String CATEGORY_SCIENCE = "science";
    public static final String CATEGORY_HEALTH = "health";

    // ===== DATABASE =====
    public static final String DATABASE_NAME = "nnews_database";
    public static final int DATABASE_VERSION = 1;

    // ===== SHARED PREFERENCES =====
    public static final String PREF_NAME = "nnews_prefs";
    public static final String PREF_DARK_MODE = "dark_mode";

    // ===== NETWORK TIMEOUT (seconds) =====
    public static final int CONNECT_TIMEOUT = 30;
    public static final int READ_TIMEOUT = 30;
    public static final int WRITE_TIMEOUT = 30;

    // ===== CACHE =====
    public static final int CACHE_SIZE_MB = 10;
    public static final int CACHE_MAX_AGE = 5;
    public static final int CACHE_MAX_STALE = 60 * 60 * 24 * 7;
}