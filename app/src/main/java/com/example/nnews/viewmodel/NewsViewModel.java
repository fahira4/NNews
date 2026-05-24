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

public class NewsViewModel extends ViewModel {

    private final NewsRepository repository;

    // ===== TOP HEADLINES =====
    private final MutableLiveData<String> selectedCategory =
            new MutableLiveData<>();
    private final LiveData<Result<List<Article>>> topHeadlines;

    // ===== SEARCH =====
    private final MutableLiveData<String> searchQuery =
            new MutableLiveData<>();
    private final LiveData<Result<List<Article>>> searchResults;

    // ===== SEARCH MODE FLAG =====
    private final MutableLiveData<Boolean> isSearchMode =
            new MutableLiveData<>(false);

    // ===== BOOKMARK =====
    private final LiveData<List<Article>> bookmarks;

    // ===== SELECTED ARTICLE =====
    private final MutableLiveData<Article> selectedArticle =
            new MutableLiveData<>();

    public NewsViewModel(NewsRepository repository) {
        this.repository = repository;

        // Top headlines — reactive terhadap perubahan category
        topHeadlines = Transformations.switchMap(
                selectedCategory,
                category -> repository.getTopHeadlines(category)
        );

        // Search — reactive terhadap perubahan query
        searchResults = Transformations.switchMap(
                searchQuery,
                query -> {
                    if (query == null || query.trim().isEmpty()) {
                        MutableLiveData<Result<List<Article>>> empty =
                                new MutableLiveData<>();
                        empty.setValue(Result.success(null));
                        return empty;
                    }
                    return repository.searchNews(query.trim());
                }
        );

        // Bookmarks
        bookmarks = repository.getAllBookmarks();

        // Load default
        selectedCategory.setValue(Constants.DEFAULT_CATEGORY);
    }

    // ===================================================
    // TOP HEADLINES
    // ===================================================

    public LiveData<Result<List<Article>>> getTopHeadlines() {
        return topHeadlines;
    }

    public void setCategory(String category) {
        if (!category.equals(selectedCategory.getValue())) {
            selectedCategory.setValue(category);
        }
    }

    public void refreshHeadlines() {
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
        if (query != null && !query.trim().isEmpty()) {
            isSearchMode.setValue(true);
            searchQuery.setValue(query.trim());
        } else {
            clearSearch();
        }
    }

    public void clearSearch() {
        isSearchMode.setValue(false);
        searchQuery.setValue("");
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
}