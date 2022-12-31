package com.rise.live.Model;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.*;

public class Category {
    public String name, desc, thumb;



    public Category(String name, String desc, String thumb) {
        this.name = name;
        this.desc = desc;
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }



    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
    public String getThumb() {
        return thumb;
    }
}
