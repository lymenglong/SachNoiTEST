package com.lymenglong.laptop.audiobookapp1verion2.model;


public class Chapter {
    private int id;
    private String title;
    private String content;
    private int pauseTime;

    public int getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(int insertTime) {
        this.insertTime = insertTime;
    }

    private int insertTime;
    private String fileUrl;
    private int categoryId;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(int pauseTime) {
        this.pauseTime = pauseTime;
    }


    public Chapter(int id, String title, String content, String fileUrl, int pauseTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
        this.pauseTime = pauseTime;
    }

    public Chapter(int id, String title, String content, int pauseTime, int insertTime, String fileUrl, int categoryId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.pauseTime = pauseTime;
        this.insertTime = insertTime;
        this.fileUrl = fileUrl;
        this.categoryId = categoryId;
    }

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
