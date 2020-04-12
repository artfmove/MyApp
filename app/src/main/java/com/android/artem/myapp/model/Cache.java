package com.android.artem.myapp.model;

import java.net.URI;
import java.net.URL;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "caches")
public class Cache {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cache_id")
    private long id;

    @ColumnInfo(name = "cache_url")
    private String url;

    @ColumnInfo(name = "network_url")
    private String netUrl;

    @Ignore
    public Cache(){

    }

    public Cache(long id, String url, String netUrl) {
        this.id = id;
        this.url = url;
        this.netUrl = netUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNetUrl() {
        return netUrl;
    }

    public void setNetUrl(String netUrl) {
        this.netUrl = netUrl;
    }
}

