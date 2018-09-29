package com.example.memo;

import java.io.Serializable;

public class Memory implements Serializable {
    //成员属性，包括id，标题title，内容content，日期date，图片路径uri
    private int id;
    private String title;
    private String content;
    private String date;
    private String uri;

    public Memory() {
        this.id = 0;
        this.title = "";
        this.content = "";
        this.date = "";
        this.uri = "";
    }
    public Memory(int id, String title, String content, String date, String uri) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.uri = uri;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
