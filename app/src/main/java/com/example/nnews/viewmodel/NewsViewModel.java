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
    // Gunakan format "category|timestamp" untuk force refresh
    private final MutableLiveData<String> headlineTrigger =
            new MutableLiveData<>();
    private final LiveData<Result<List<Article>>> topHeadlines;
    private String currentCategory = Constants.DEFAULT_CATEGORY;

    // ===== SEARCH =====
    private final MediatorLiveData<Result<List<Article>>>
            searchResults = new MediatorLiveData<>();
    private LiveData<Result<List<Article>>> lastSearchLiveData = null;

    // ===== SEARCH MODE =====
    private final MutableLiveData<Boolean> isSearchMode =
            new MutableLiveData<>(false);
    private String lastQuery = "";

    // ===== BOOKMARK =====
    private final LiveData<List<Article>> bookmarks;

    // ===== SELECTED ARTICLE =====
    private final MutableLiveData<Article> selectedArticle =
            new MutableLiveData<>();

    public NewsViewModel(NewsRepository repository) {
        this.repository = repository;

        // switchMap berdasarkan trigger string
        // Format: "category|timestamp"
        topHeadlines = Transformations.switchMap(
                headlineTrigger,
                trigger -> {
                    if (trigger == null) {
                        return new MutableLiveData<>();
                    }
                    // Ambil category dari trigger (sebelum |)
                    String category = trigger.contains("|")
                            ? trigger.split("\\|")[0]
                            : trigger;

                    android.util.Log.d("NewsViewModel",
                            "fetchHeadlines: " + category);

                    return repository.getTopHeadlines(category);
                }
        );

        bookmarks = repository.getAllBookmarks();

        // Set trigger awal
        triggerHeadlines(Constants.DEFAULT_CATEGORY);
    }

    // ===================================================
    // TOP HEADLINES
    // ===================================================

    public LiveData<Result<List<Article>>> getTopHeadlines() {
        return topHeadlines;
    }

    /**
     * Ganti category — trigger fetch baru.
     */
    public void setCategory(String category) {
        if (category != null
                && !category.equals(currentCategory)) {
            currentCategory = category;
            triggerHeadlines(category);
        }
    }

    /**
     * Force refresh dengan category yang sama.
     * Gunakan timestamp agar LiveData detect perubahan.
     */
    public void refreshHeadlines() {
        triggerHeadlines(currentCategory);
    }

    /**
     * Internal trigger — selalu emit value baru
     * karena timestamp selalu berbeda.
     */
    private void triggerHeadlines(String category) {
        String trigger = category + "|"
                + System.currentTimeMillis();
        headlineTrigger.setValue(trigger);
    }

    public String getSelectedCategory() {
        return currentCategory;
    }

    // ===================================================
    // SEARCH
    // ===================================================

    public LiveData<Result<List<Article>>> getSearchResults() {
        return searchResults;
    }

    public void searchNews(String query) {
        if (query == null || query.trim().isEmpty()) {
            clearSearch();
            return;
        }

        String trimmedQuery = query.trim();

        if (trimmedQuery.equals(lastQuery)
                && Boolean.TRUE.equals(isSearchMode.getValue())) {
            return;
        }

        lastQuery = trimmedQuery;
        isSearchMode.setValue(true);
        searchResults.setValue(Result.loading());

        if (lastSearchLiveData != null) {
            searchResults.removeSource(lastSearchLiveData);
            lastSearchLiveData = null;
        }

        LiveData<Result<List<Article>>> newSearch =
                repository.searchNews(trimmedQuery);
        lastSearchLiveData = newSearch;

        searchResults.addSource(newSearch, result -> {
            if (trimmedQuery.equals(lastQuery)) {
                searchResults.setValue(result);
            }
        });
    }

    public void clearSearch() {
        lastQuery = "";
        isSearchMode.setValue(false);

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

    public LiveData<Boolean> isBookmarked(String url) {
        return repository.isBookmarked(url);
    }

    public void clearAllBookmarks() {
        repository.deleteAllBookmarks();
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
        if (lastSearchLiveData != null) {
            searchResults.removeSource(lastSearchLiveData);
            lastSearchLiveData = null;
        }
    }
}