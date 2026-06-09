package com.example.nnews.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.nnews.data.model.Article;
import com.example.nnews.data.repository.NewsRepository;
import com.example.nnews.utils.Constants;
import com.example.nnews.utils.Result;

import java.util.List;

public class NewsViewModel extends ViewModel {

    private final NewsRepository repository;

    // ===== TOP HEADLINES =====
    private final MutableLiveData<String> selectedCategory =
            new MutableLiveData<>();
    private final LiveData<Result<List<Article>>> topHeadlines;

    // ===== SEARCH =====
    // MutableLiveData query — trigger search setiap berubah
    private final MutableLiveData<String> searchQuery =
            new MutableLiveData<>();

    // MediatorLiveData — lebih aman dari observeForever
    private final MediatorLiveData<Result<List<Article>>> searchResults =
            new MediatorLiveData<>();

    // Track LiveData search sebelumnya agar bisa dilepas
    private LiveData<Result<List<Article>>> lastSearchLiveData = null;

    // ===== SEARCH MODE =====
    private final MutableLiveData<Boolean> isSearchMode =
            new MutableLiveData<>(false);

    // ===== LAST QUERY =====
    private String lastQuery = "";

    // ===== BOOKMARK =====
    private final LiveData<List<Article>> bookmarks;

    // ===== SELECTED ARTICLE =====
    private final MutableLiveData<Article> selectedArticle =
            new MutableLiveData<>();

    public NewsViewModel(NewsRepository repository) {
        this.repository = repository;

        // Headlines reactive terhadap category
        topHeadlines = Transformations.switchMap(
                selectedCategory,
                category -> {
                    if (category == null) {
                        return new MutableLiveData<>();
                    }
                    return repository.getTopHeadlines(category);
                }
        );

        bookmarks = repository.getAllBookmarks();

        // Default category
        selectedCategory.setValue(Constants.DEFAULT_CATEGORY);
    }

    // ===================================================
    // TOP HEADLINES
    // ===================================================

    public LiveData<Result<List<Article>>> getTopHeadlines() {
        return topHeadlines;
    }

    public void setCategory(String category) {
        if (category != null) {
            selectedCategory.setValue(category);
        }
    }

    public void refreshHeadlines() {
        String current = selectedCategory.getValue() != null
                ? selectedCategory.getValue()
                : Constants.DEFAULT_CATEGORY;
        selectedCategory.setValue(current);
    }

    public String getSelectedCategory() {
        return selectedCategory.getValue() != null
                ? selectedCategory.getValue()
                : Constants.DEFAULT_CATEGORY;
    }

    // ===================================================
    // SEARCH
    // ===================================================

    public LiveData<Result<List<Article>>> getSearchResults() {
        return searchResults;
    }

    /**
     * Trigger search dengan query baru.
     * Menggunakan MediatorLiveData agar aman dari memory leak.
     */
    public void searchNews(String query) {
        if (query == null || query.trim().isEmpty()) {
            clearSearch();
            return;
        }

        String trimmedQuery = query.trim();

        // Skip jika query sama dengan yang terakhir
        if (trimmedQuery.equals(lastQuery)
                && Boolean.TRUE.equals(isSearchMode.getValue())) {
            return;
        }

        lastQuery = trimmedQuery;
        isSearchMode.setValue(true);

        // Set loading
        searchResults.setValue(Result.loading());

        // Lepas source lama dari MediatorLiveData
        if (lastSearchLiveData != null) {
            searchResults.removeSource(lastSearchLiveData);
            lastSearchLiveData = null;
        }

        // Buat LiveData baru dari repository
        LiveData<Result<List<Article>>> newSearch =
                repository.searchNews(trimmedQuery);

        lastSearchLiveData = newSearch;

        // Tambahkan sebagai source baru
        searchResults.addSource(newSearch, result -> {
            // Hanya forward jika query masih relevan
            if (trimmedQuery.equals(lastQuery)) {
                searchResults.setValue(result);
            }
        });
    }

    public void clearSearch() {
        lastQuery = "";
        isSearchMode.setValue(false);

        // Lepas source dari MediatorLiveData
        if (lastSearchLiveData != null) {
            searchResults.removeSource(lastSearchLiveData);
            lastSearchLiveData = null;
        }

        searchResults.setValue(Result.success(null));
    }

    public LiveData<Boolean> getIsSearchMode() {
        return isSearchMode;
    }

    public boolean isCurrentlySearching() {
        Boolean val = isSearchMode.getValue();
        return val != null && val;
    }

    // ===================================================
    // BOOKMARK
    // ===================================================

    public LiveData<List<Article>> getBookmarks() {
        return bookmarks;
    }

    public void addBookmark(Article article) {
        repository.addBookmark(article);
    }

    public void removeBookmark(Article article) {
        repository.removeBookmark(article);
    }

    public void clearAllBookmarks() {
        repository.clearAllBookmarks();
    }

    public LiveData<Boolean> isBookmarked(String url) {
        return repository.isBookmarked(url);
    }

    // ===================================================
    // SELECTED ARTICLE
    // ===================================================

    public void setSelectedArticle(Article article) {
        selectedArticle.setValue(article);
    }

    public LiveData<Article> getSelectedArticle() {
        return selectedArticle;
    }

    public Article getSelectedArticleValue() {
        return selectedArticle.getValue();
    }

    // ===================================================
    // CLEANUP
    // ===================================================

    @Override
    protected void onCleared() {
        super.onCleared();
        // Bersihkan semua source saat ViewModel destroyed
        if (lastSearchLiveData != null) {
            searchResults.removeSource(lastSearchLiveData);
            lastSearchLiveData = null;
        }
    }
}
