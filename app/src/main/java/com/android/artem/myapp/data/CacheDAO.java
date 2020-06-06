package com.android.artem.myapp.data;

import com.android.artem.myapp.model.Cache;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CacheDAO {

    @Insert
    public long addCache(Cache cache);

    @Update
    public void updateCache(Cache cache);

    @Delete
    public void deleteCache(Cache cache);

    @Query("select * from caches")
    public List<Cache> getAllCaches();

    @Query("select * from caches where cache_id ==:cacheId " )
    public Cache getCache(long cacheId);

    @Query("select * from caches where network_url ==:url ")
    public Cache getDownloadCache(String url);

}
