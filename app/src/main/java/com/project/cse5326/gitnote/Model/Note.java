package com.project.cse5326.gitnote.Model;

import java.util.Date;

/**
 * Created by sifang.
 */

public class Note {

    private int number;
    private String update_at;
    private String title;
    private String body;
    private String comment;

    public Note(){}

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
