package com.example.nnews.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.nnews.data.model.CachedArticle;

import java.util.List;

/**
 * DAO untuk operasi cache berita.
 */
@Dao
public interface CacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CachedArticle> articles);

    @Query("SELECT * FROM article_cache " +
            "WHERE category = :category " +
            "ORDER BY publishedAt DESC")
    List<CachedArticle> getCachedArticles(String category);

    @Query("DELETE FROM article_cache WHERE category = :category")
    void deleteCacheByCategory(String category);

    @Query("DELETE FROM article_cache")
    void deleteAllCache();

    @Query("SELECT COUNT(*) FROM article_cache " +
            "WHERE category = :category")
    int getCacheCount(String category);

    @Query("SELECT cachedAt FROM article_cache " +
            "WHERE category = :category " +
            "ORDER BY cachedAt DESC LIMIT 1")
    long getLastCacheTime(String category);
}