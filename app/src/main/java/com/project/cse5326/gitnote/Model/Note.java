package com.project.cse5326.gitnote.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by sifang.
 */

public class Note {

    @SerializedName("number")
    private int number;
    @SerializedName("update_at")
    private String update_at;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("comment")
    private String comment;
//    private Label label;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

//    public Label getLabel() {
//        return label;
//    }
//
//    public void setLabel(Label label) {
//        this.label = label;
//    }

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
