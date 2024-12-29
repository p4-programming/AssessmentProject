package com.deepak.assesment.viewmodel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.deepak.assesment.dao.DataTable;
import com.deepak.assesment.data.repository.ItemRepository;
import java.util.List;


public class ItemViewModel extends AndroidViewModel {
    private final ItemRepository repository;
    private final LiveData<List<DataTable>> data;

    public ItemViewModel(Application application) {
        super(application);
        repository = new ItemRepository(application);
        data = repository.getDataFromDatabase();
    }

    public LiveData<List<DataTable>> getData() {
        return data;
    }

    public void fetchUsersByPostId(int postId) {
        repository.fetchUsersByPageNumber(postId); // Fetch data for the given page ID
    }

    // Expose the isDataAlreadyInDatabase method
    public LiveData<Boolean> isDataAlreadyInDatabase(int pageNumber) {
        return repository.isDataAlreadyInDatabase(pageNumber);
    }
}

