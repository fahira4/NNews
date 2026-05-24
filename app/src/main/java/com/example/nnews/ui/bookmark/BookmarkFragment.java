package com.example.nnews.ui.bookmark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nnews.adapter.NewsAdapter;
import com.example.nnews.data.model.Article;
import com.example.nnews.databinding.FragmentBookmarkBinding;
import com.example.nnews.utils.SwipeToDeleteCallback;
import com.example.nnews.viewmodel.NewsViewModel;
import com.example.nnews.viewmodel.NewsViewModelFactory;

import java.util.List;

public class BookmarkFragment extends Fragment {

    private FragmentBookmarkBinding binding;
    private NewsViewModel viewModel;
    private NewsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupRecyclerView();
        setupSwipeToDelete();
        observeBookmarks();
    }

    // ===================================================
    // SETUP
    // ===================================================

    private void setupViewModel() {
        NewsViewModelFactory factory =
                new NewsViewModelFactory(requireContext());
        viewModel = new ViewModelProvider(this, factory)
                .get(NewsViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new NewsAdapter();
        binding.rvBookmarks.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        binding.rvBookmarks.setAdapter(adapter);

        adapter.setOnArticleClickListener(
                new NewsAdapter.OnArticleClickListener() {
                    @Override
                    public void onArticleClick(Article article) {
                        navigateToDetail(article);
                    }

                    @Override
                    public void onBookmarkClick(Article article,
                                                boolean isBookmarked) {
                        // Di bookmark screen, bookmark selalu true
                        // Jika di-untoggle, hapus dari bookmark
                        if (!isBookmarked) {
                            viewModel.removeBookmark(article);
                        }
                    }
                });
    }

    private void setupSwipeToDelete() {
        SwipeToDeleteCallback swipeCallback =
                new SwipeToDeleteCallback(requireContext()) {
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Article article = adapter.getCurrentList().get(position);
                        viewModel.removeBookmark(article);
                    }
                };

        new ItemTouchHelper(swipeCallback)
                .attachToRecyclerView(binding.rvBookmarks);
    }

    // ===================================================
    // OBSERVE
    // ===================================================

    private void observeBookmarks() {
        viewModel.getBookmarks().observe(getViewLifecycleOwner(),
                articles -> {
                    if (articles != null && !articles.isEmpty()) {
                        showContent(articles);
                    } else {
                        showEmpty();
                    }
                });
    }

    // ===================================================
    // UI STATE
    // ===================================================

    private void showContent(List<Article> articles) {
        binding.rvBookmarks.setVisibility(View.VISIBLE);
        binding.layoutEmpty.setVisibility(View.GONE);
        adapter.submitList(articles);
    }

    private void showEmpty() {
        binding.rvBookmarks.setVisibility(View.GONE);
        binding.layoutEmpty.setVisibility(View.VISIBLE);
    }

    // ===================================================
    // NAVIGATION
    // ===================================================

    private void navigateToDetail(Article article) {
        viewModel.setSelectedArticle(article);

        BookmarkFragmentDirections.ActionBookmarkToDetail action =
                BookmarkFragmentDirections.actionBookmarkToDetail(
                        article.getUrl() != null ? article.getUrl() : "",
                        article.getTitle() != null ? article.getTitle() : ""
                );
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}