package com.lymenglong.laptop.audiobookapp1verion2.model;


public class BookType {
    private int id;
    private String title;
    private String content;

    public BookType(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public BookType() {
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
