package com.project.cse5326.gitnote.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzhenyu on 10/6/17.
 */

public class User {
    @SerializedName("login")
    public String login;
    @SerializedName("name")
    public String name;
    @SerializedName("avatar_url")
    public String avatar_url;

}
