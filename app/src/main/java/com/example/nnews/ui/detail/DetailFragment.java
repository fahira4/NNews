package com.example.nnews.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nnews.R;
import com.example.nnews.data.model.Article;
import com.example.nnews.databinding.FragmentDetailBinding;
import com.example.nnews.viewmodel.NewsViewModel;
import com.example.nnews.viewmodel.NewsViewModelFactory;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;
    private NewsViewModel viewModel;
    private Article currentArticle;
    private boolean isBookmarked = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupToolbar();
        loadArticleFromArgs();
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

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );
    }

    private void loadArticleFromArgs() {
        DetailFragmentArgs args = DetailFragmentArgs.fromBundle(getArguments());
        String articleUrl = args.getArticleUrl();
        String articleTitle = args.getArticleTitle();

        // Coba ambil artikel lengkap dari shared ViewModel
        viewModel.getSelectedArticle().observe(getViewLifecycleOwner(), article -> {
            if (article != null && article.getUrl() != null
                    && article.getUrl().equals(articleUrl)) {
                displayArticle(article);
            } else {
                // Fallback — tampilkan minimal data dari args
                binding.tvTitle.setText(articleTitle);
                currentArticle = new Article();
                currentArticle.setUrl(articleUrl);
                currentArticle.setTitle(articleTitle);
                setupActionButtons(articleUrl, articleTitle);
                observeBookmarkState(articleUrl);
            }
        });
    }

    private void loadArticleData(String url, String title) {
        // Data sudah tersedia dari navigation args
        // Tampilkan apa yang kita punya
        binding.tvTitle.setText(title);

        // Setup action buttons
        setupActionButtons(url, title);
    }

    // ===================================================
    // DISPLAY
    // ===================================================

    /**
     * Tampilkan data artikel lengkap.
     * Dipanggil saat data tersedia dari ViewModel atau args.
     */
    public void displayArticle(Article article) {
        if (article == null) return;
        this.currentArticle = article;

        // Title
        binding.tvTitle.setText(article.getTitle());

        // Hero image
        if (article.getImage() != null && !article.getImage().isEmpty()) {
            Glide.with(this)
                    .load(article.getImage())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(binding.ivHero);
        }

        // Source
        if (article.getSource() != null
                && article.getSource().getName() != null) {
            binding.tvSource.setText(article.getSource().getName());
            binding.chipCategory.setText(article.getSource().getName());
        } else {
            binding.chipCategory.setVisibility(View.GONE);
        }

        // Date
        if (article.getPublishedAt() != null
                && article.getPublishedAt().length() >= 10) {
            binding.tvDate.setText(
                    article.getPublishedAt().substring(0, 10)
            );
        }

        // Description
        if (article.getDescription() != null
                && !article.getDescription().isEmpty()) {
            binding.tvDescription.setText(article.getDescription());
        } else {
            binding.tvDescription.setVisibility(View.GONE);
        }

        // Content — GNews memotong konten, tampilkan apa adanya
        if (article.getContent() != null
                && !article.getContent().isEmpty()) {
            // Hapus tag "[X chars]" di akhir konten GNews
            String content = article.getContent()
                    .replaceAll("\\[\\+?\\d+ chars\\]$", "")
                    .trim();
            binding.tvContent.setText(content);
        } else {
            binding.tvContent.setVisibility(View.GONE);
        }

        setupActionButtons(article.getUrl(), article.getTitle());
        observeBookmarkState(article.getUrl());
    }

    // ===================================================
    // ACTION BUTTONS
    // ===================================================

    private void setupActionButtons(String url, String title) {
        // Share
        binding.btnShare.setOnClickListener(v -> shareArticle(url, title));

        // Open in browser
        binding.btnOpenBrowser.setOnClickListener(v -> openInBrowser(url));

        // Bookmark
        binding.btnBookmark.setOnClickListener(v -> toggleBookmark());
    }

    private void shareArticle(String url, String title) {
        if (url == null || url.isEmpty()) return;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(
                shareIntent,
                getString(R.string.label_share)
        ));
    }

    private void openInBrowser(String url) {
        if (url == null || url.isEmpty()) return;

        try {
            Intent browserIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
            );
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(
                    requireContext(),
                    R.string.msg_error_generic,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void toggleBookmark() {
        if (currentArticle == null) return;

        isBookmarked = !isBookmarked;
        updateBookmarkIcon(isBookmarked);

        if (isBookmarked) {
            viewModel.addBookmark(currentArticle);
            Toast.makeText(
                    requireContext(),
                    R.string.msg_bookmarked,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            viewModel.removeBookmark(currentArticle);
            Toast.makeText(
                    requireContext(),
                    R.string.msg_bookmark_removed,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // ===================================================
    // BOOKMARK STATE
    // ===================================================

    private void observeBookmarkState(String url) {
        if (url == null || url.isEmpty()) return;

        viewModel.isBookmarked(url).observe(getViewLifecycleOwner(), bookmarked -> {
            if (bookmarked != null) {
                isBookmarked = bookmarked;
                updateBookmarkIcon(bookmarked);
            }
        });
    }

    private void updateBookmarkIcon(boolean bookmarked) {
        binding.btnBookmark.setImageResource(
                bookmarked
                        ? R.drawable.ic_bookmark_filled
                        : R.drawable.ic_bookmark
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}