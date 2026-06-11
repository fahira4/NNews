package com.example.nnews.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.nnews.data.local.ArticleTypeConverter;

/**
 * Entity untuk cache berita per kategori di Room Database.
 * Terpisah dari bookmarks agar tidak saling mengganggu.
 */
@Entity(tableName = "article_cache")
@TypeConverters({ArticleTypeConverter.class})
public class CachedArticle {

    @PrimaryKey
    @NonNull
    private String url = "";

    private String title;
    private String description;
    private String content;
    private String image;
    private String publishedAt;
    private String sourceName;
    private String sourceUrl;

    // Kategori berita — untuk filter per category
    private String category;

    // Timestamp saat disimpan — untuk validasi cache
    private long cachedAt;

    public CachedArticle() {}

    // ===== Converter dari Article =====

    /**
     * Buat CachedArticle dari Article + category.
     */
    public static CachedArticle fromArticle(
            Article article, String category) {

        CachedArticle cached = new CachedArticle();
        cached.url = article.getUrl();
        cached.title = article.getTitle();
        cached.description = article.getDescription();
        cached.content = article.getContent();
        cached.image = article.getImage();
        cached.publishedAt = article.getPublishedAt();
        cached.category = category;
        cached.cachedAt = System.currentTimeMillis();

        if (article.getSource() != null) {
            cached.sourceName = article.getSource().getName();
            cached.sourceUrl = article.getSource().getUrl();
        }

        return cached;
    }

    /**
     * Konversi balik ke Article untuk ditampilkan di UI.
     */
    public Article toArticle() {
        Article article = new Article();
        article.setUrl(this.url);
        article.setTitle(this.title);
        article.setDescription(this.description);
        article.setContent(this.content);
        article.setImage(this.image);
        article.setPublishedAt(this.publishedAt);

        if (this.sourceName != null) {
            Article.Source source = new Article.Source();
            source.setName(this.sourceName);
            source.setUrl(this.sourceUrl);
            article.setSource(source);
        }

        return article;
    }

    // ===== Getters & Setters =====

    @NonNull
    public String getUrl() { return url; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getImage() { return image; }
    public String getPublishedAt() { return publishedAt; }
    public String getSourceName() { return sourceName; }
    public String getSourceUrl() { return sourceUrl; }
    public String getCategory() { return category; }
    public long getCachedAt() { return cachedAt; }

    public void setUrl(@NonNull String url) { this.url = url; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String desc) {
        this.description = desc;
    }
    public void setContent(String content) { this.content = content; }
    public void setImage(String image) { this.image = image; }
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setCachedAt(long cachedAt) {
        this.cachedAt = cachedAt;
    }
}