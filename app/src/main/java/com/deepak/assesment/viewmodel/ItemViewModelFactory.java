package com.deepak.assesment.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ItemViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;

    public ItemViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            return (T) new ItemViewModel(application);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
