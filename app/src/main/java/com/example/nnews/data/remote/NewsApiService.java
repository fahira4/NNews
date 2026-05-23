package com.example.nnews.data.remote;

import com.example.nnews.data.model.GnewsResponse;
import com.example.nnews.utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface untuk GNews API.
 * API key sudah dihandle oleh ApiKeyInterceptor,
 * jadi tidak perlu ditulis manual di sini.
 */
public interface NewsApiService {

    /**
     * Ambil top headlines berdasarkan kategori.
     *
     * @param category Kategori berita (general, technology, sports, dll)
     * @param lang     Bahasa (en, id, dll)
     * @param country  Negara (us, id, dll)
     * @param max      Jumlah artikel per request (max 10 untuk free tier)
     */
    @GET(Constants.ENDPOINT_TOP_HEADLINES)
    Call<GnewsResponse> getTopHeadlines(
            @Query(Constants.PARAM_CATEGORY) String category,
            @Query(Constants.PARAM_LANG) String lang,
            @Query(Constants.PARAM_COUNTRY) String country,
            @Query(Constants.PARAM_MAX) int max
    );

    /**
     * Cari berita berdasarkan keyword.
     *
     * @param query   Kata kunci pencarian
     * @param lang    Bahasa
     * @param country Negara
     * @param max     Jumlah artikel
     */
    @GET(Constants.ENDPOINT_SEARCH)
    Call<GnewsResponse> searchNews(
            @Query(Constants.PARAM_QUERY) String query,
            @Query(Constants.PARAM_LANG) String lang,
            @Query(Constants.PARAM_COUNTRY) String country,
            @Query(Constants.PARAM_MAX) int max
    );
}
