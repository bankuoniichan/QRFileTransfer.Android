package com.example.qrfiletransferandroid;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {History.class}, version = 1)
public abstract class myAppDatabase extends RoomDatabase
{
    public abstract MyDao myDao();
}
