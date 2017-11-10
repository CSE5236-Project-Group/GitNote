package com.project.cse5326.gitnote.Github;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Model.User;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Github {

    public static final int COUNT_PER_LOAD = 12;

    // Request url
    private static final String API_URL = "https://api.github.com";
    private static final String USER_ENDPOINT = API_URL + "/user";
    private static final String USERS_ENDPOINT = API_URL + "users";

    // Request note url
    private static final String NOTES_ENDPOINT = USER_ENDPOINT + "/issues";
    private static final String SCOPE_ALL = "filter=all&state=all&sort=updated&direction=desc";

    // Request repo rul
    private static final String REPO_ENDPOINT = USER_ENDPOINT + "/repos";

    // SharePreference key, to store access token
    private static final String SP_AUTH = "auth";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER = "user";

    // Memory variable
    private static String accessToken;
    private static User user;

    // TypeToken
    private static final TypeToken<User> USER_TYPE_TOKEN = new TypeToken<User>(){};
    private static final TypeToken<Note> NOTE_TYPE_TOKEN = new TypeToken<Note>(){};
    private static final TypeToken<List<Note>> NOTES_TYPE_TOKEN = new TypeToken<List<Note>>(){};
    private static final TypeToken<Repo> REPO_TYPE_TOKEN = new TypeToken<Repo>(){};
    private static final TypeToken<List<Repo>> REPOS_TYPE_TOKEN = new TypeToken<List<Repo>>(){};
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
                .addHeader("Authorization", "Bearer " + accessToken)
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
        return makeRequest(request);
    }

    // Check status
    private static void checkStatusCode(Response response, int statusCode) throws GithubException {
        if (response.code() != statusCode) {
            throw new GithubException(response.message());
        }
    }

    // HTTP POST
    private static Response makePostRequest(@NonNull String url, RequestBody requestBody)
            throws GithubException {
        Request request = authRequestBuilder(url)
                .post(requestBody)
                .build();
        return makeRequest(request);
    }

    // HTTP DELETE
    private static Response makeDeleteRequest(@NonNull String url)
            throws GithubException{
        Request request = authRequestBuilder(url)
                .delete()
                .build();
        return makeRequest(request);
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

    /*--------------------------------------------------------------------------------------------------
     * Issues
    --------------------------------------------------------------------------------------------------*/
    // request all issues
    public static List<Note> getNotes(int page) throws GithubException {
        return parseResponse(makeGetRequest(NOTES_ENDPOINT + "?page=" + page
                + '&' + SCOPE_ALL), NOTES_TYPE_TOKEN);
    }

    /*--------------------------------------------------------------------------------------------------
     * Repo
    --------------------------------------------------------------------------------------------------*/
    // request all user repo
    public static List<Repo> getRepos(int page) throws GithubException {
        return parseResponse(makeGetRequest(REPO_ENDPOINT + "?page=" + page), REPOS_TYPE_TOKEN);
    }
}
