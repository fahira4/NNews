package com.example.nnews.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.nnews.data.model.Article;
import com.example.nnews.data.repository.NewsRepository;
import com.example.nnews.utils.Constants;
import com.example.nnews.utils.Result;

import java.util.List;

/**
 * ViewModel untuk Home, Bookmark, dan Search screen.
 * Mengekspos LiveData ke Fragment — Fragment hanya observe, tidak fetch langsung.
 */
public class NewsViewModel extends ViewModel {

    private final NewsRepository repository;

    // ===== TOP HEADLINES =====
    private final MutableLiveData<String> selectedCategory = new MutableLiveData<>();
    private final LiveData<Result<List<Article>>> topHeadlines;

    // ===== SEARCH =====
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final LiveData<Result<List<Article>>> searchResults;

    // ===== BOOKMARK =====
    private final LiveData<List<Article>> bookmarks;

    // ===== LOADING STATE =====
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public NewsViewModel(NewsRepository repository) {
        this.repository = repository;

        // Top headlines — reactive terhadap perubahan category
        topHeadlines = Transformations.switchMap(
                selectedCategory,
                category -> repository.getTopHeadlines(category)
        );

        // Search results — reactive terhadap perubahan query
        searchResults = Transformations.switchMap(
                searchQuery,
                query -> {
                    if (query == null || query.trim().isEmpty()) {
                        MutableLiveData<Result<List<Article>>> empty =
                                new MutableLiveData<>();
                        empty.setValue(Result.success(null));
                        return empty;
                    }
                    return repository.searchNews(query);
                }
        );

        // Bookmarks — selalu dari Room
        bookmarks = repository.getAllBookmarks();

        // Load default category saat pertama kali
        selectedCategory.setValue(Constants.DEFAULT_CATEGORY);
    }

    // ===================================================
    // TOP HEADLINES
    // ===================================================

    public LiveData<Result<List<Article>>> getTopHeadlines() {
        return topHeadlines;
    }

    public void setCategory(String category) {
        // Hanya trigger reload jika category berbeda
        if (!category.equals(selectedCategory.getValue())) {
            selectedCategory.setValue(category);
        }
    }

    public void refreshHeadlines() {
        // Force refresh dengan category yang sama
        String current = selectedCategory.getValue();
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

    public void searchNews(String query) {
        searchQuery.setValue(query);
    }

    public void clearSearch() {
        searchQuery.setValue("");
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

    // ===================================================
    // LOADING STATE
    // ===================================================

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}