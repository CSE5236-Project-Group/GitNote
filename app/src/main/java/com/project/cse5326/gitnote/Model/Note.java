package com.project.cse5326.gitnote.Model;

import java.util.Date;

/**
 * Created by sifang on 11/4/17.
 */

public class Note {

    private int mId;
    private Date mDate;
    private String mTitle;
    private String mBody;

    public Note(){}

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }
}
