package com.rise.live.Model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName="favoritelist")
public class Channel implements Serializable {

    @PrimaryKey
    int id;
    @ColumnInfo(name = "image")
    String ChannelImage;
    @ColumnInfo(name = "prname")
    String channelName;
    String channelCategory;
    String channelType;
    String channelLink;
    String channelDesc;
    String channelLanguage;

    public Channel() {
    }

    public Channel(int id, String channelImage, String channelName, String channelCategory, String channelType, String channelLink, String channelDesc, String channelLanguage) {
        this.id = id;
        ChannelImage = channelImage;
        this.channelName = channelName;
        this.channelCategory = channelCategory;
        this.channelType = channelType;
        this.channelLink = channelLink;
        this.channelDesc = channelDesc;
        this.channelLanguage = channelLanguage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelImage() {
        return ChannelImage;
    }

    public void setChannelImage(String channelImage) {
        ChannelImage = channelImage;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelCategory() {
        return channelCategory;
    }

    public void setChannelCategory(String channelCategory) {
        this.channelCategory = channelCategory;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getChannelLink() {
        return channelLink;
    }

    public void setChannelLink(String channelLink) {
        this.channelLink = channelLink;
    }

    public String getChannelDesc() {
        return channelDesc;
    }

    public void setChannelDesc(String channelDesc) {
        this.channelDesc = channelDesc;
    }

    public String getChannelLanguage() {
        return channelLanguage;
    }

    public void setChannelLanguage(String channelLanguage) {
        this.channelLanguage = channelLanguage;
    }
}
