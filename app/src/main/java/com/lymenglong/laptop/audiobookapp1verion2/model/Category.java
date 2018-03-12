package com.lymenglong.laptop.audiobookapp1verion2.model;


public class Category {
    private int id;
    private String title;
    private String content;

    public Category(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }
    public Category() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
