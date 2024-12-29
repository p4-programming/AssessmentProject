package com.deepak.assesment.dao;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DataTable.class}, version = 1, exportSchema = false)
public abstract class DataBaseHelper extends RoomDatabase {
    private static final String DB_NAME = "UserDataDB";
    private static DataBaseHelper instance;

    public static synchronized DataBaseHelper getDB(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), DataBaseHelper.class, DB_NAME)
                    .fallbackToDestructiveMigration() // For version upgrades
                    .build();
        }
        return instance;
    }

    public abstract DataTableDao dataTableDao();
}
