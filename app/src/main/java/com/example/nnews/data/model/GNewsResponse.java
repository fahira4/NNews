package com.example.nnews.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Model response dari GNews API.
 */
public class GNewsResponse {

    @SerializedName("totalArticles")
    private int totalArticles;

    @SerializedName("articles")
    private List<Article> articles;

    public int getTotalArticles() {
        return totalArticles;
    }

    public List<Article> getArticles() {
        return articles;
    }
}