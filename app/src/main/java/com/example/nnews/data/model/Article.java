package com.example.nnews.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Model Article — digunakan untuk:
 * 1. Parsing response dari GNews API (via Gson @SerializedName)
 * 2. Penyimpanan lokal di Room Database (@Entity)
 */
@Entity(tableName = "bookmarks")
public class Article {

    @PrimaryKey
    @SerializedName("url")
    private String url;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("content")
    private String content;

    @SerializedName("image")
    private String image;

    @SerializedName("publishedAt")
    private String publishedAt;

    @SerializedName("source")
    private Source source;

    // ===== Konstruktor =====

    public Article() {}

    // ===== Getter =====

    public String getUrl() { return url; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getImage() { return image; }
    public String getPublishedAt() { return publishedAt; }
    public Source getSource() { return source; }

    // ===== Setter (diperlukan Room) =====

    public void setUrl(String url) { this.url = url; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setContent(String content) { this.content = content; }
    public void setImage(String image) { this.image = image; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
    public void setSource(Source source) { this.source = source; }

    /**
     * Inner class untuk data source berita.
     * GNews mengembalikan source sebagai object.
     */
    public static class Source {

        @SerializedName("name")
        private String name;

        @SerializedName("url")
        private String url;

        public String getName() { return name; }
        public String getUrl() { return url; }

        public void setName(String name) { this.name = name; }
        public void setUrl(String url) { this.url = url; }
    }
}