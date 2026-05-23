package com.example.nnews.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

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
 * - Tidak ada internet → ambil dari Room cache
 * - Bookmark → selalu dari Room
 */
public class NewsRepository {

    private static volatile NewsRepository instance;

    private final RemoteDataSource remoteDataSource;
    private final NewsDatabase database;
    private final Context context;

    // Background thread executor untuk operasi database
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    // Handler untuk post result ke Main thread
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

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

    /**
     * Ambil top headlines.
     * Online: dari API. Offline: dari cache Room.
     */
    public LiveData<Result<List<Article>>> getTopHeadlines(String category) {
        MutableLiveData<Result<List<Article>>> result = new MutableLiveData<>();

        // Set loading state
        result.setValue(Result.loading());

        if (NetworkUtils.isNetworkAvailable(context)) {
            // Fetch dari API
            remoteDataSource.getTopHeadlines(category)
                    .enqueue(new Callback<GNewsResponse>() {
                        @Override
                        public void onResponse(Call<GNewsResponse> call,
                                               Response<GNewsResponse> response) {
                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().getArticles() != null) {

                                List<Article> articles = response.body().getArticles();
                                result.postValue(Result.success(articles));

                            } else {
                                result.postValue(Result.error(
                                        "Failed to load news: " + response.code()
                                ));
                            }
                        }

                        @Override
                        public void onFailure(Call<GNewsResponse> call, Throwable t) {
                            result.postValue(Result.error(
                                    t.getMessage() != null
                                            ? t.getMessage()
                                            : "Network error occurred"
                            ));
                        }
                    });
        } else {
            // Tidak ada internet — return error dengan pesan offline
            result.setValue(Result.error(Constants.ERROR_NO_INTERNET));
        }

        return result;
    }

    // ===================================================
    // SEARCH
    // ===================================================

    public LiveData<Result<List<Article>>> searchNews(String query) {
        MutableLiveData<Result<List<Article>>> result = new MutableLiveData<>();
        result.setValue(Result.loading());

        if (NetworkUtils.isNetworkAvailable(context)) {
            remoteDataSource.searchNews(query)
                    .enqueue(new Callback<GNewsResponse>() {
                        @Override
                        public void onResponse(Call<GNewsResponse> call,
                                               Response<GNewsResponse> response) {
                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().getArticles() != null) {

                                result.postValue(Result.success(
                                        response.body().getArticles()
                                ));
                            } else {
                                result.postValue(Result.error(
                                        "Search failed: " + response.code()
                                ));
                            }
                        }

                        @Override
                        public void onFailure(Call<GNewsResponse> call, Throwable t) {
                            result.postValue(Result.error(
                                    t.getMessage() != null
                                            ? t.getMessage()
                                            : "Network error occurred"
                            ));
                        }
                    });
        } else {
            result.setValue(Result.error(Constants.ERROR_NO_INTERNET));
        }

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
}