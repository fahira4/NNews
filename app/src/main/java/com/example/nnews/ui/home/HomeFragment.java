package com.example.nnews.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.nnews.R;
import com.example.nnews.adapter.NewsAdapter;
import com.example.nnews.data.model.Article;
import com.example.nnews.databinding.FragmentHomeBinding;
import com.example.nnews.utils.Constants;
import com.example.nnews.utils.DebounceUtils;
import com.example.nnews.utils.NetworkUtils;
import com.example.nnews.viewmodel.NewsViewModel;
import com.example.nnews.viewmodel.NewsViewModelFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private NewsViewModel viewModel;
    private NewsAdapter adapter;
    private DebounceUtils debounce;

    // Flag agar chip listener tidak trigger saat pertama dibuat
    private boolean chipsInitialized = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        debounce = new DebounceUtils(600);

        setupViewModel();
        setupRecyclerView();
        setupCategoryChips();
        setupSearchView();
        setupSwipeRefresh();
        observeHeadlines();
        observeSearchResults();
        observeSearchMode();
        observeBookmarks();
    }

    // ===================================================
    // SETUP
    // ===================================================

    private void setupViewModel() {
        NewsViewModelFactory factory =
                new NewsViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(requireActivity(), factory)
                .get(NewsViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new NewsAdapter();
        binding.rvNews.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.rvNews.setAdapter(adapter);
        binding.rvNews.setHasFixedSize(false);

        adapter.setOnArticleClickListener(
                new NewsAdapter.OnArticleClickListener() {
                    @Override
                    public void onArticleClick(Article article) {
                        navigateToDetail(article);
                    }

                    @Override
                    public void onBookmarkClick(Article article,
                                                boolean isBookmarked) {
                        handleBookmark(article, isBookmarked);
                    }
                });
    }

    private void setupCategoryChips() {
        binding.chipGroupCategory.setOnCheckedStateChangeListener(
                (group, checkedIds) -> {
                    if (checkedIds.isEmpty()) return;
                    if (viewModel.isCurrentlySearching()) return;

                    int chipId = checkedIds.get(0);
                    String category = getCategoryFromChipId(chipId);

                    // ViewModel sudah handle skip jika sama
                    viewModel.setCategory(category);
                });
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(
                new androidx.appcompat.widget.SearchView
                        .OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        debounce.cancel();
                        if (query != null
                                && !query.trim().isEmpty()) {
                            viewModel.searchNews(query.trim());
                        }
                        binding.searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText == null
                                || newText.trim().isEmpty()) {
                            debounce.cancel();
                            viewModel.clearSearch();
                        } else {
                            String query = newText.trim();
                            debounce.debounce(() ->
                                    viewModel.searchNews(query)
                            );
                        }
                        return true;
                    }
                });

        binding.searchView.setOnCloseListener(() -> {
            debounce.cancel();
            viewModel.clearSearch();
            return false;
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(
                R.color.colorPrimaryBrand
        );
        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (viewModel.isCurrentlySearching()) {
                String query = binding.searchView
                        .getQuery().toString().trim();
                if (!query.isEmpty()) {
                    viewModel.searchNews(query);
                } else {
                    binding.swipeRefresh.setRefreshing(false);
                }
            } else {
                viewModel.refreshHeadlines();
            }
        });
    }

    // ===================================================
    // OBSERVE
    // ===================================================

    private void observeHeadlines() {
        viewModel.getTopHeadlines().observe(
                getViewLifecycleOwner(), result -> {

                    android.util.Log.d("HomeFragment",
                            "observeHeadlines triggered");

                    if (viewModel.isCurrentlySearching()) return;
                    if (result == null) return;

                    android.util.Log.d("HomeFragment",
                            "result status: " + result.getStatus()
                                    + " | isSearching: "
                                    + viewModel.isCurrentlySearching());

                    binding.swipeRefresh.setRefreshing(false);

                    switch (result.getStatus()) {
                        case LOADING:
                            showShimmer();
                            break;
                        case SUCCESS:
                            hideShimmer();
                            List<Article> articles = result.getData();
                            android.util.Log.d("HomeFragment",
                                    "SUCCESS, articles: "
                                            + (articles != null
                                            ? articles.size()
                                            : "null"));
                            if (articles != null
                                    && !articles.isEmpty()) {
                                showContent(articles);
                            } else {
                                showEmpty();
                            }
                            break;
                        case ERROR:
                            hideShimmer();
                            android.util.Log.e("HomeFragment",
                                    "ERROR: " + result.getMessage());
                            showError(result.getMessage());
                            break;
                    }
                });
    }

    private void observeSearchResults() {
        viewModel.getSearchResults().observe(
                getViewLifecycleOwner(), result -> {
                    if (!viewModel.isCurrentlySearching()) return;
                    if (result == null) return;

                    binding.swipeRefresh.setRefreshing(false);

                    switch (result.getStatus()) {
                        case LOADING:
                            showShimmer();
                            break;
                        case SUCCESS:
                            hideShimmer();
                            List<Article> articles = result.getData();
                            if (articles != null
                                    && !articles.isEmpty()) {
                                showContent(articles);
                            } else {
                                showEmpty();
                            }
                            break;
                        case ERROR:
                            hideShimmer();
                            showError(result.getMessage());
                            break;
                    }
                });
    }

    private void observeSearchMode() {
        viewModel.getIsSearchMode().observe(
                getViewLifecycleOwner(), isSearchMode -> {
                    if (isSearchMode != null && isSearchMode) {
                        binding.scrollCategories
                                .setVisibility(View.GONE);
                    } else {
                        binding.scrollCategories
                                .setVisibility(View.VISIBLE);
                    }
                });
    }

    private void observeBookmarks() {
        viewModel.getBookmarks().observe(
                getViewLifecycleOwner(), bookmarks -> {
                    if (bookmarks != null) {
                        Set<String> urls = new HashSet<>();
                        for (Article article : bookmarks) {
                            if (article.getUrl() != null) {
                                urls.add(article.getUrl());
                            }
                        }
                        adapter.setBookmarkedUrls(urls);
                    }
                });
    }

    // ===================================================
    // UI STATE
    // ===================================================

    private void showShimmer() {
        binding.shimmerLayout.startShimmer();
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.rvNews.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
    }

    private void hideShimmer() {
        binding.shimmerLayout.stopShimmer();
        binding.shimmerLayout.setVisibility(View.GONE);
    }

    private void showContent(List<Article> articles) {
        android.util.Log.d("HomeFragment",
                "showContent called, size: "
                        + (articles != null ? articles.size() : "null"));

        binding.rvNews.setVisibility(View.VISIBLE);
        binding.layoutError.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
        adapter.submitList(articles);

        android.util.Log.d("HomeFragment",
                "rvNews visibility: "
                        + binding.rvNews.getVisibility());
    }

    private void showError(String message) {
        binding.rvNews.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.VISIBLE);

        if (message == null) {
            binding.tvErrorMessage.setText(R.string.state_error);
        } else if (message.equals(Constants.ERROR_NO_INTERNET)) {
            binding.tvErrorMessage
                    .setText(R.string.state_error_network);
        } else if (message.equals(Constants.ERROR_RATE_LIMIT)) {
            binding.tvErrorMessage
                    .setText(R.string.state_error_rate_limit);
        } else if (message.equals(
                Constants.ERROR_INVALID_API_KEY)) {
            binding.tvErrorMessage
                    .setText(R.string.state_error_api_key);
        } else {
            binding.tvErrorMessage.setText(R.string.state_error);
        }

        binding.btnRetry.setOnClickListener(v -> {
            if (viewModel.isCurrentlySearching()) {
                String query = binding.searchView
                        .getQuery().toString().trim();
                if (!query.isEmpty()) {
                    viewModel.searchNews(query);
                }
            } else {
                viewModel.refreshHeadlines();
            }
        });
    }

    private void showEmpty() {
        binding.rvNews.setVisibility(View.GONE);
        binding.layoutError.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    // ===================================================
    // NAVIGATION
    // ===================================================

    private void navigateToDetail(Article article) {
        if (article == null) return;

        viewModel.setSelectedArticle(article);

        HomeFragmentDirections.ActionHomeToDetail action =
                HomeFragmentDirections.actionHomeToDetail(
                        article.getUrl() != null
                                ? article.getUrl() : "",
                        article.getTitle() != null
                                ? article.getTitle() : ""
                );
        Navigation.findNavController(requireView()).navigate(action);
    }

    // ===================================================
    // BOOKMARK
    // ===================================================

    private void handleBookmark(Article article,
                                boolean isBookmarked) {
        if (isBookmarked) {
            viewModel.addBookmark(article);
        } else {
            viewModel.removeBookmark(article);
        }
    }

    // ===================================================
    // HELPER
    // ===================================================

    private String getCategoryFromChipId(int chipId) {
        if (chipId == R.id.chip_world)
            return Constants.CATEGORY_WORLD;
        if (chipId == R.id.chip_technology)
            return Constants.CATEGORY_TECHNOLOGY;
        if (chipId == R.id.chip_business)
            return Constants.CATEGORY_BUSINESS;
        if (chipId == R.id.chip_sports)
            return Constants.CATEGORY_SPORTS;
        if (chipId == R.id.chip_science)
            return Constants.CATEGORY_SCIENCE;
        if (chipId == R.id.chip_health)
            return Constants.CATEGORY_HEALTH;
        if (chipId == R.id.chip_entertainment)
            return Constants.CATEGORY_ENTERTAINMENT;
        return Constants.CATEGORY_GENERAL;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (debounce != null) {
            debounce.cancel();
        }
        binding = null;
    }
}