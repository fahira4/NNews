package com.example.nnews.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.nnews.data.model.Article;
import com.example.nnews.data.model.CachedArticle;
import com.example.nnews.utils.Constants;

/**
 * Room Database — single instance untuk seluruh app.
 */
@Database(
        entities = {Article.class, CachedArticle.class},
        version = 2,
        exportSchema = false
)
@TypeConverters({ArticleTypeConverter.class})
public abstract class NewsDatabase extends RoomDatabase {

    private static volatile NewsDatabase instance;

    public abstract NewsDao newsDao();
    public abstract CacheDao cacheDao();

    public static NewsDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (NewsDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    NewsDatabase.class,
                                    Constants.DATABASE_NAME
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}