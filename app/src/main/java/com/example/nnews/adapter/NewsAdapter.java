package com.example.nnews.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.nnews.R;
import com.example.nnews.data.model.Article;
import com.example.nnews.databinding.ItemNewsBinding;

import java.util.HashSet;
import java.util.Set;

/**
 * RecyclerView Adapter untuk daftar berita.
 * Menggunakan ListAdapter + DiffUtil untuk performa optimal.
 */
public class NewsAdapter extends ListAdapter<Article, NewsAdapter.NewsViewHolder> {

    private OnArticleClickListener listener;

    // Set URL artikel yang sudah di-bookmark
    private Set<String> bookmarkedUrls = new HashSet<>();

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
        void onBookmarkClick(Article article, boolean isBookmarked);
    }

    public NewsAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    /**
     * Update set URL yang di-bookmark.
     * Dipanggil dari Fragment saat observe bookmarks berubah.
     */
    public void setBookmarkedUrls(Set<String> urls) {
        this.bookmarkedUrls = urls;
        notifyDataSetChanged();
    }

    private static final DiffUtil.ItemCallback<Article> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Article>() {
                @Override
                public boolean areItemsTheSame(@NonNull Article oldItem,
                                               @NonNull Article newItem) {
                    return oldItem.getUrl().equals(newItem.getUrl());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Article oldItem,
                                                  @NonNull Article newItem) {
                    return oldItem.getUrl().equals(newItem.getUrl())
                            && safeEquals(oldItem.getTitle(), newItem.getTitle())
                            && safeEquals(oldItem.getDescription(),
                            newItem.getDescription());
                }

                private boolean safeEquals(String a, String b) {
                    if (a == null && b == null) return true;
                    if (a == null || b == null) return false;
                    return a.equals(b);
                }
            };

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        ItemNewsBinding binding = ItemNewsBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new NewsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = getItem(position);
        boolean isBookmarked = bookmarkedUrls.contains(article.getUrl());
        holder.bind(article, isBookmarked);
    }

    // ===================================================
    // VIEW HOLDER
    // ===================================================

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        private final ItemNewsBinding binding;
        private boolean bookmarked = false;

        public NewsViewHolder(ItemNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Article article, boolean isBookmarked) {
            this.bookmarked = isBookmarked;

            // Title
            binding.tvTitle.setText(article.getTitle());

            // Description
            if (article.getDescription() != null
                    && !article.getDescription().isEmpty()) {
                binding.tvDescription.setText(article.getDescription());
            } else {
                binding.tvDescription.setText(R.string.state_empty);
            }

            // Source
            if (article.getSource() != null
                    && article.getSource().getName() != null) {
                binding.tvSource.setText(article.getSource().getName());
            } else {
                binding.tvSource.setText(R.string.label_source);
            }

            // Date
            if (article.getPublishedAt() != null
                    && article.getPublishedAt().length() >= 10) {
                binding.tvDate.setText(
                        article.getPublishedAt().substring(0, 10)
                );
            }

            // Thumbnail
            Glide.with(binding.getRoot().getContext())
                    .load(article.getImage())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(binding.ivThumbnail);

            // Bookmark icon state
            updateBookmarkIcon(isBookmarked);

            // Click — buka detail
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArticleClick(article);
                }
            });

            // Bookmark click
            binding.btnBookmark.setOnClickListener(v -> {
                this.bookmarked = !this.bookmarked;
                updateBookmarkIcon(this.bookmarked);
                if (listener != null) {
                    listener.onBookmarkClick(article, this.bookmarked);
                }
            });
        }

        private void updateBookmarkIcon(boolean isBookmarked) {
            binding.btnBookmark.setImageResource(
                    isBookmarked
                            ? R.drawable.ic_bookmark_filled
                            : R.drawable.ic_bookmark
            );
        }
    }
}