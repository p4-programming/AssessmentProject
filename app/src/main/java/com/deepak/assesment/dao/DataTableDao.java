package com.deepak.assesment.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface DataTableDao {
    @Insert
    void insertAll(DataTable dataTables);

    @Query("SELECT * FROM DataTable WHERE page_number = :pageNumber")
    List<DataTable> getDataByPage(int pageNumber);

    @Query("SELECT * FROM datatable")
    LiveData<List<DataTable>> getAllDataTable();

    @Query("SELECT * FROM DataTable WHERE id = :id LIMIT 1")
    DataTable getById(int id);

}
