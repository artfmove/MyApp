package com.android.artem.myapp.data;

import com.android.artem.myapp.model.Cache;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Cache.class}, version = 1)
public abstract class CacheAppData extends RoomDatabase {

    public abstract CacheDAO getCacheDAO();
}
