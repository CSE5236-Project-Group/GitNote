package com.project.cse5326.gitnote.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sifang.
 */

public class Note {

    @SerializedName("number")
    private int number;
    @SerializedName("updated_at")
    private String updated_at;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("repository")
    private Repo repository;
    @SerializedName("milestone")
    private MileStone mileStone;

    public MileStone getMileStone() {
        return mileStone;
    }

    public void setMileStone(MileStone mileStone) {
        this.mileStone = mileStone;
    }

    public Repo getRepository() {
        return repository;
    }

    public void setRepository(Repo repository) {
        this.repository = repository;
    }

    public Note(){}

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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
