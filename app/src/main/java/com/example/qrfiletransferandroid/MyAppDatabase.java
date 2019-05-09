package com.example.qrfiletransferandroid;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {History.class}, version = 1)
public abstract class MyAppDatabase extends RoomDatabase
{
    private static MyAppDatabase INSTANCE;

    public abstract MyDao myDao();

    public static MyAppDatabase getAppDatabase(Context context){
        if (INSTANCE == null) INSTANCE = Room.databaseBuilder(context.getApplicationContext(),MyAppDatabase.class,
                "db").allowMainThreadQueries().build();
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
