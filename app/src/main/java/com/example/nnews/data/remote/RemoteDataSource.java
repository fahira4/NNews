package com.example.nnews.data.remote;

import com.example.nnews.data.model.GNewsResponse;
import com.example.nnews.utils.Constants;

import retrofit2.Call;

/**
 * Remote Data Source — wrapper untuk NewsApiService.
 * Memisahkan logika network dari Repository.
 */
public class RemoteDataSource {

    private final NewsApiService apiService;

    public RemoteDataSource(NewsApiService apiService) {
        this.apiService = apiService;
    }

    public Call<GNewsResponse> getTopHeadlines(String category) {
        return apiService.getTopHeadlines(
                category,
                Constants.DEFAULT_LANG,
                Constants.DEFAULT_COUNTRY,
                Constants.DEFAULT_PAGE_SIZE
        );
    }

    public Call<GNewsResponse> searchNews(String query) {
        return apiService.searchNews(
                query,
                Constants.DEFAULT_LANG,
                Constants.DEFAULT_COUNTRY,
                Constants.DEFAULT_PAGE_SIZE
        );
    }
}