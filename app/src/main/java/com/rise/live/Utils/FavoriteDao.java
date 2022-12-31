package com.rise.live.Utils;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.rise.live.Model.Channel;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Insert
    public void addData(Channel favoriteList);

    @Query("select * from favoritelist")
    public List<Channel> getFavoriteData();

    @Query("SELECT EXISTS (SELECT 1 FROM favoritelist WHERE id=:id)")
    public int isFavorite(int id);

    @Delete
    public void delete(Channel favoriteList);


}
