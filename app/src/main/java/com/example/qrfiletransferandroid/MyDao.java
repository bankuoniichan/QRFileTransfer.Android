package com.example.qrfiletransferandroid;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.util.Log;

import java.util.List;

@Dao
public interface MyDao
{
    @Insert
    public void addHistory(History history);

    @Query("select * from history")
    public List<History> getHistory();

    @Query("DELETE FROM history")
    public void deleteTable();


}
