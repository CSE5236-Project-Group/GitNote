package com.project.cse5326.gitnote.Github;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.NoteList;
import com.project.cse5326.gitnote.Model.User;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Github {

    public static final int COUNT_PER_LOAD = 12;

    // Request url
    private static final String API_URL = "https://api.github.com";
    private static final String USER_ENDPOINT = API_URL + "/user";
    private static final String USERS_ENDPOINT = API_URL + "users";
    private static final String ALL_ISSUE_ENDPOINT = API_URL + "/issues?filter=all&state=all";
    // TODO more endpoint

    // SharePreference key, to store access token
    private static final String SP_AUTH = "auth";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";
    private static final String KEY_ALL_NOTES = "note_list";

    // Memory variable
    private static String accessToken;
    private static User user;

    // TypeToken
    private static final TypeToken<User> USER_TYPE_TOKEN = new TypeToken<User>() {};
    private static final TypeToken<Collection<Note>> ALL_NOTES_TYPE_TOKEN = new TypeToken<Collection<Note>>(){};
    // TODO more type

    // HTTP client
    private static OkHttpClient client = new OkHttpClient();

    /*----------------------------------------------------------------------------------------------
     * HTTP Request
    ---------------------------------------------------------------------------------------------*/

    // Form builder with bearer
    private static Request.Builder authRequestBuilder(String url) {
        return new Request
                .Builder()
                .addHeader("Authorization", "token " + accessToken)
                .url(url);
    }

    private static Response makeRequest(Request request) throws GithubException {
        try {
            Response response = client.newCall(request).execute();
            Log.i("request_respond", response.toString());
            return response;
        } catch (IOException e) {
            throw new GithubException(e.getMessage());
        }
    }

    // HTTP GET
    private static Response makeGetRequest(@NonNull String url) throws GithubException {
        Request request = authRequestBuilder(url).build();

        Log.i("get_url", request.toString());
        Log.i("get_url_header", request.headers().toString());
        return makeRequest(request);
    }

    private static void checkStatusCode(Response response, int statusCode) throws GithubException {
        if (response.code() != statusCode) {
            throw new GithubException(response.message());
        }
    }

    // Parse to target type
    private static <T> T parseResponse(Response response, TypeToken<T> TypeToken)
            throws GithubException {
        String responseString = null;
        try {
            responseString = response.body().string();
        } catch (IOException e) {
            throw new GithubException(e.getMessage());
        }

        return ModelUtils.toObject(responseString, TypeToken);
    }

    /*----------------------------------------------------------------------------------------------
     * Login & logout
     ---------------------------------------------------------------------------------------------*/

    public static void init(@NonNull Context context) {
        accessToken = loadAccessToken(context);
        if (accessToken != null) {
            user = loadUser(context);
        }
    }

    public static boolean isLogin() {
        return accessToken != null;
    }

    private static String loadAccessToken(Context context) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    public static void login(@NonNull Context context, @NonNull String accessToken)
            throws GithubException {

        Github.accessToken = accessToken;
        storeAccessToken(context, accessToken);
        Github.user = fetchUserInfo();
        storeUser(context, user);
        Log.i("User", user.name);
    }



    private static void storeAccessToken(Context context, String accessToken) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply();
    }

    // User Info
    private static User fetchUserInfo() throws GithubException {
        return parseResponse(makeGetRequest(USER_ENDPOINT), USER_TYPE_TOKEN);
    }

    private static void storeUser(Context context, User user) {
        ModelUtils.save(context, KEY_USER, user);
    }

    private static User loadUser(Context context) {
        return ModelUtils.read(context, KEY_USER, USER_TYPE_TOKEN);
    }

    public static User getCurrentUser() {
        return user;
    }

    // Notes Info
    private static List<Note> fetchAllNotes() throws GithubException{
        Collection<Note> notes =  parseResponse(makeGetRequest(ALL_ISSUE_ENDPOINT),ALL_NOTES_TYPE_TOKEN);
        return new ArrayList<>(notes);
    }

    public static void getAllNotes() throws GithubException {
        NoteList.set(fetchAllNotes());
    }

}
