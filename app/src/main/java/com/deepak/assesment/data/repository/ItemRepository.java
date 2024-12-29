package com.deepak.assesment.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.deepak.assesment.dao.DataBaseHelper;
import com.deepak.assesment.dao.DataTable;
import com.deepak.assesment.dao.DataTableDao;
import com.deepak.assesment.data.api.ApiClient;
import com.deepak.assesment.data.api.ApiService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemRepository {
    private final DataTableDao dataTableDao;
    private final ApiService apiService;

    public ItemRepository(Application application) {
        DataBaseHelper database = DataBaseHelper.getDB(application);
        dataTableDao = database.dataTableDao();
        apiService = ApiClient.getInstance().create(ApiService.class);
    }

    public LiveData<List<DataTable>> getDataFromDatabase() {
        return dataTableDao.getAllDataTable();  // This will return live data of all data
    }

    public void fetchUsersByPageNumber(int pageNumber) {
        isDataAlreadyInDatabase(pageNumber).observeForever(isDataPresent -> {
            if (!isDataPresent) {
                apiService.getUsers(pageNumber).enqueue(new Callback<List<DataTable>>() {
                    @Override
                    public void onResponse(Call<List<DataTable>> call, Response<List<DataTable>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<DataTable> dataTables = response.body();
                            new Thread(() -> {
                                // Check for existing entries before inserting
                                for (DataTable dataTable : dataTables) {
                                    if (dataTableDao.getById(dataTable.getId()) == null) {
                                        // Insert data if it doesn't exist
                                        dataTableDao.insertAll(dataTable);
                                    }
                                }
                            }).start();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DataTable>> call, Throwable t) {
                        Log.e("ItemRepository", "API call failed", t);
                    }
                });
            }
        });
    }


    // Check if data already exists for a particular page
    public LiveData<Boolean> isDataAlreadyInDatabase(int pageNumber) {
        MutableLiveData<Boolean> isDataPresent = new MutableLiveData<>();

        // Run the database query on a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<DataTable> dataTables = dataTableDao.getDataByPage(pageNumber);
            isDataPresent.postValue(dataTables != null && !dataTables.isEmpty());
        });

        return isDataPresent; // Return LiveData
    }
}

