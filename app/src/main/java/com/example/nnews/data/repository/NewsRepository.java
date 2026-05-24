package com.example.nnews.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.nnews.data.local.NewsDatabase;
import com.example.nnews.data.model.Article;
import com.example.nnews.data.model.GNewsResponse;
import com.example.nnews.data.remote.NetworkClient;
import com.example.nnews.data.remote.NewsApiService;
import com.example.nnews.data.remote.RemoteDataSource;
import com.example.nnews.utils.Constants;
import com.example.nnews.utils.NetworkUtils;
import com.example.nnews.utils.Result;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository — single source of truth.
 *
 * Aturan:
 * - Ada internet → fetch dari API → cache ke Room
 * - Tidak ada internet → return error NO_INTERNET
 * - Bookmark → selalu dari Room Database
 */
public class NewsRepository {

    private static volatile NewsRepository instance;

    private final RemoteDataSource remoteDataSource;
    private final NewsDatabase database;
    private final Context context;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private NewsRepository(Context context) {
        this.context = context.getApplicationContext();

        NewsApiService apiService = NetworkClient
                .getInstance(context)
                .createService(NewsApiService.class);

        this.remoteDataSource = new RemoteDataSource(apiService);
        this.database = NewsDatabase.getInstance(context);
    }

    public static NewsRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (NewsRepository.class) {
                if (instance == null) {
                    instance = new NewsRepository(context);
                }
            }
        }
        return instance;
    }

    // ===================================================
    // TOP HEADLINES
    // ===================================================

    public LiveData<Result<List<Article>>> getTopHeadlines(String category) {
        MutableLiveData<Result<List<Article>>> result = new MutableLiveData<>();

        result.setValue(Result.loading());

        if (!NetworkUtils.isNetworkAvailable(context)) {
            result.setValue(Result.error(Constants.ERROR_NO_INTERNET));
            return result;
        }

        remoteDataSource.getTopHeadlines(category)
                .enqueue(new Callback<GNewsResponse>() {
                    @Override
                    public void onResponse(
                            @androidx.annotation.NonNull Call<GNewsResponse> call,
                            @androidx.annotation.NonNull Response<GNewsResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getArticles() != null) {

                            result.postValue(
                                    Result.success(response.body().getArticles())
                            );

                        } else {
                            result.postValue(Result.error(
                                    "Failed to load news. Code: " + response.code()
                            ));
                        }
                    }

                    @Override
                    public void onFailure(
                            @androidx.annotation.NonNull Call<GNewsResponse> call,
                            @androidx.annotation.NonNull Throwable t) {
                        result.postValue(Result.error(parseErrorMessage(t)));
                    }
                });

        return result;
    }

    // ===================================================
    // SEARCH
    // ===================================================

    public LiveData<Result<List<Article>>> searchNews(String query) {
        MutableLiveData<Result<List<Article>>> result = new MutableLiveData<>();

        result.setValue(Result.loading());

        if (!NetworkUtils.isNetworkAvailable(context)) {
            result.setValue(Result.error(Constants.ERROR_NO_INTERNET));
            return result;
        }

        remoteDataSource.searchNews(query)
                .enqueue(new Callback<GNewsResponse>() {
                    @Override
                    public void onResponse(
                            @androidx.annotation.NonNull Call<GNewsResponse> call,
                            @androidx.annotation.NonNull Response<GNewsResponse> response) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getArticles() != null) {

                            result.postValue(
                                    Result.success(response.body().getArticles())
                            );

                        } else {
                            result.postValue(Result.error(
                                    "Search failed. Code: " + response.code()
                            ));
                        }
                    }

                    @Override
                    public void onFailure(
                            @androidx.annotation.NonNull Call<GNewsResponse> call,
                            @androidx.annotation.NonNull Throwable t) {
                        result.postValue(Result.error(parseErrorMessage(t)));
                    }
                });

        return result;
    }

    // ===================================================
    // BOOKMARK
    // ===================================================

    public void addBookmark(Article article) {
        executor.execute(() ->
                database.newsDao().insertBookmark(article)
        );
    }

    public void removeBookmark(Article article) {
        executor.execute(() ->
                database.newsDao().deleteBookmark(article)
        );
    }

    public LiveData<List<Article>> getAllBookmarks() {
        return database.newsDao().getAllBookmarks();
    }

    public LiveData<Boolean> isBookmarked(String url) {
        return database.newsDao().isBookmarked(url);
    }

    // ===================================================
    // HELPER
    // ===================================================

    /**
     * Parse error message yang user-friendly
     * berdasarkan tipe exception yang terjadi.
     */
    private String parseErrorMessage(Throwable t) {
        if (t instanceof UnknownHostException) {
            return Constants.ERROR_NO_INTERNET;
        } else if (t instanceof SocketTimeoutException) {
            return "Connection timed out. Please try again.";
        } else if (t instanceof IOException) {
            return Constants.ERROR_NO_INTERNET;
        }
        return t.getMessage() != null
                ? t.getMessage()
                : Constants.ERROR_GENERIC;
    }
}