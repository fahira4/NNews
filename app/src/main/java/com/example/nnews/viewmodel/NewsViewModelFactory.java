package com.example.nnews.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.nnews.data.repository.NewsRepository;

/**
 * Factory untuk membuat NewsViewModel dengan dependency injection manual.
 * Diperlukan karena ViewModel kita membutuhkan parameter (repository).
 */
public class NewsViewModelFactory implements ViewModelProvider.Factory {

    private final NewsRepository repository;

    public NewsViewModelFactory(Context context) {
        this.repository = NewsRepository.getInstance(context);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NewsViewModel.class)) {
            return (T) new NewsViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: "
                + modelClass.getName());
    }
}