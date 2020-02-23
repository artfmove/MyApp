package com.android.artem.myapp;

public class Song {
    public String title;
    public String group;
    public String id;
    public String image;

    public Song(){

    }

    public Song(String title, String group, String id, String image) {
        this.title = title;
        this.group = group;
        this.id = id;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
