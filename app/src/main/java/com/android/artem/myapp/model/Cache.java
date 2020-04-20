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

    @ColumnInfo(name = "group")
    private String group;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    @Ignore
    public Cache() {

    }

    public Cache(long id, String url, String netUrl, String group, String title, String imageUrl) {
        this.id = id;
        this.url = url;
        this.netUrl = netUrl;
        this.group = group;
        this.title = title;
        this.imageUrl = imageUrl;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

