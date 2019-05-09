package com.example.qrfiletransferandroid;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {History.class}, version = 1)
public abstract class myAppDatabase extends RoomDatabase
{
    public abstract MyDao myDao();
}
