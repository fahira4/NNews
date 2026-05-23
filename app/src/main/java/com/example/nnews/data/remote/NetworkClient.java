package com.example.nnews.data.remote;

import android.content.Context;

import com.example.nnews.utils.Constants;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton NetworkClient.
 * Menyediakan instance Retrofit dan OkHttpClient yang dikonfigurasi
 * dengan logging, caching, timeout, dan API key interceptor.
 */
public class NetworkClient {

    private static volatile NetworkClient instance;
    private final Retrofit retrofit;

    private NetworkClient(Context context) {
        // Logging interceptor (hanya aktif saat debug)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(
                com.example.nnews.BuildConfig.DEBUG
                        ? HttpLoggingInterceptor.Level.BODY
                        : HttpLoggingInterceptor.Level.NONE
        );

        // Cache untuk offline support
        File cacheDir = new File(context.getCacheDir(), "http_cache");
        Cache cache = new Cache(cacheDir,
                (long) Constants.CACHE_SIZE_MB * 1024 * 1024);

        // OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Constants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(Constants.READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(Constants.WRITE_TIMEOUT, TimeUnit.SECONDS)
                .cache(cache)
                .addInterceptor(new ApiKeyInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();

        // Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Thread-safe singleton instance.
     */
    public static NetworkClient getInstance(Context context) {
        if (instance == null) {
            synchronized (NetworkClient.class) {
                if (instance == null) {
                    instance = new NetworkClient(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * Buat service interface dari Retrofit.
     */
    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}