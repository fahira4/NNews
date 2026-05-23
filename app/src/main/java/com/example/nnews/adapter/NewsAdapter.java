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

/**
 * RecyclerView Adapter untuk daftar berita.
 * Menggunakan ListAdapter + DiffUtil untuk performa optimal —
 * hanya item yang berubah yang di-redraw.
 */
public class NewsAdapter extends ListAdapter<Article, NewsAdapter.NewsViewHolder> {

    private OnArticleClickListener listener;

    /**
     * Interface callback untuk click event.
     */
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

    // DiffUtil — membandingkan item lama dan baru secara efisien
    private static final DiffUtil.ItemCallback<Article> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Article>() {
                @Override
                public boolean areItemsTheSame(@NonNull Article oldItem,
                                               @NonNull Article newItem) {
                    // Item sama jika URL sama
                    return oldItem.getUrl().equals(newItem.getUrl());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Article oldItem,
                                                  @NonNull Article newItem) {
                    // Konten sama jika title dan description sama
                    return oldItem.getUrl().equals(newItem.getUrl())
                            && safeEquals(oldItem.getTitle(), newItem.getTitle())
                            && safeEquals(oldItem.getDescription(), newItem.getDescription());
                }

                private boolean safeEquals(String a, String b) {
                    if (a == null && b == null) return true;
                    if (a == null || b == null) return false;
                    return a.equals(b);
                }
            };

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNewsBinding binding = ItemNewsBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new NewsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bind(getItem(position));
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

        public void bind(Article article) {
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

            // Published date — ambil 10 karakter pertama (YYYY-MM-DD)
            if (article.getPublishedAt() != null
                    && article.getPublishedAt().length() >= 10) {
                binding.tvDate.setText(article.getPublishedAt().substring(0, 10));
            }

            // Thumbnail image
            Glide.with(binding.getRoot().getContext())
                    .load(article.getImage())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(binding.ivThumbnail);

            // Click listener — buka detail
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArticleClick(article);
                }
            });

            // Bookmark click
            binding.btnBookmark.setOnClickListener(v -> {
                bookmarked = !bookmarked;
                updateBookmarkIcon(bookmarked);
                if (listener != null) {
                    listener.onBookmarkClick(article, bookmarked);
                }
            });
        }

        public void setBookmarked(boolean isBookmarked) {
            this.bookmarked = isBookmarked;
            updateBookmarkIcon(isBookmarked);
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