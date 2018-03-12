package com.lymenglong.laptop.audiobookapp1verion2.model;


public class Chapter {
    private int id;
    private String title;
    private String content;

    public int getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(int insertTime) {
        this.insertTime = insertTime;
    }

    private String fileUrl;

    public Chapter(int id, String title, String content, String fileUrl, int insertTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
        this.insertTime = insertTime;
    }

    private int insertTime;

    public Chapter(int id, String title, String content, String fileUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
    }

    public Chapter(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getFileUrl() {

        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Chapter(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Chapter() {
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
