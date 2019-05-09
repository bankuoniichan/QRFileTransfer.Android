package com.example.qrfiletransferandroid;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

@Dao
public interface MyDao
{
    @Insert
    public void addHistory(History history);

}
