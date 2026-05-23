package com.example.nnews.data.remote;

import androidx.annotation.NonNull;

import com.example.nnews.BuildConfig;
import com.example.nnews.utils.Constants;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp Interceptor yang otomatis menambahkan API key
 * ke setiap request tanpa perlu menulis manual di tiap endpoint.
 */
public class ApiKeyInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Tambahkan apikey sebagai query parameter
        HttpUrl urlWithKey = originalRequest.url()
                .newBuilder()
                .addQueryParameter(Constants.PARAM_API_KEY, BuildConfig.NEWS_API_KEY)
                .build();

        Request newRequest = originalRequest.newBuilder()
                .url(urlWithKey)
                .build();

        return chain.proceed(newRequest);
    }
}