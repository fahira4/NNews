package com.example.nnews.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Model Article — digunakan untuk:
 * 1. Parsing response dari GNews API
 * 2. Penyimpanan bookmark di Room (@Entity bookmarks)
 */
@Entity(tableName = "bookmarks")
public class Article {

    @PrimaryKey
    @NonNull
    @SerializedName("url")
    private String url = "";

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

    public Article() {}

    @NonNull
    public String getUrl() { return url; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getContent() { return content; }
    public String getImage() { return image; }
    public String getPublishedAt() { return publishedAt; }
    public Source getSource() { return source; }

    public void setUrl(@NonNull String url) { this.url = url; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setContent(String content) { this.content = content; }
    public void setImage(String image) { this.image = image; }
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
    public void setSource(Source source) { this.source = source; }

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