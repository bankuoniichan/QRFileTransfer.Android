package com.example.qrfiletransferandroid;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history")
public class History
{
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String type;
    @ColumnInfo
    private String time;
    @ColumnInfo
    private String dataName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }
}
