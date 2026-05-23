package com.example.nnews.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.nnews.data.model.Article;

import java.util.List;

/**
 * Data Access Object untuk operasi Room Database.
 * Semua query database didefinisikan di sini.
 */
@Dao
public interface NewsDao {

    // ===== BOOKMARK OPERATIONS =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookmark(Article article);

    @Delete
    void deleteBookmark(Article article);

    @Query("SELECT * FROM bookmarks ORDER BY publishedAt DESC")
    LiveData<List<Article>> getAllBookmarks();

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE url = :url)")
    LiveData<Boolean> isBookmarked(String url);

    @Query("DELETE FROM bookmarks")
    void deleteAllBookmarks();

    // ===== CACHE OPERATIONS =====

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticles(List<Article> articles);

    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    Article getArticleByUrl(String url);
}