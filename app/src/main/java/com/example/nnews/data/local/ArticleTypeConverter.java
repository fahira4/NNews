package com.example.nnews.data.local;

import androidx.room.TypeConverter;

import com.example.nnews.data.model.Article;
import com.google.gson.Gson;

/**
 * TypeConverter untuk Room agar bisa menyimpan
 * object Article.Source sebagai String JSON.
 */
public class ArticleTypeConverter {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromSource(Article.Source source) {
        if (source == null) return null;
        return gson.toJson(source);
    }

    @TypeConverter
    public static Article.Source toSource(String sourceString) {
        if (sourceString == null) return null;
        return gson.fromJson(sourceString, Article.Source.class);
    }
}