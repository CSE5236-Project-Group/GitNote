package com.project.cse5326.gitnote.Github;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.NoiseSuppressor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Comment;
import com.project.cse5326.gitnote.Model.Label;
import com.project.cse5326.gitnote.Model.MileStone;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Model.User;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Github {

    public static final int COUNT_PER_LOAD = 12;

    // Request url
    private static final String API_URL = "https://api.github.com";
    private static final String USER_ENDPOINT = API_URL + "/user";
    private static final String USERS_ENDPOINT = API_URL + "/users";

    // Request note url
    private static final String NOTES_ENDPOINT = USER_ENDPOINT + "/issues";
    private static final String SCOPE_ALL = "filter=all&state=open&sort=updated&direction=desc";
    private static final String NOTES_REPO_ENDPOINT = API_URL + "/repos";

    // Request milestone url
    private static final String MILESTONE_ENDPOINT = API_URL + "/repos";

    // Request repo rul
    private static final String REPO_ENDPOINT = USER_ENDPOINT +  "/repos";

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
    private static final TypeToken<MileStone> MILESTONE_TYPE_TOKEN = new TypeToken<MileStone>(){};
    private static final TypeToken<List<MileStone>> MILESTONES_TYPE_TOKEN = new TypeToken<List<MileStone>>(){};
    // TODO more type

    // HTTP client
    private static OkHttpClient client = new OkHttpClient();

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /*----------------------------------------------------------------------------------------------
     * HTTP Request
    ---------------------------------------------------------------------------------------------*/

    // Form builder with bearer
    private static Request.Builder authRequestBuilder(String url) {
        Log.i("access_token", accessToken);
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

    // HTTP PATCH
    private static Response makePatchRequest(@NonNull String url, RequestBody requestBody) throws GithubException {
        Request request = authRequestBuilder(url)
                .patch(requestBody)
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

    // get issues by milestone (by number)
    public static List<Note> getNotes(String repo, int milestone) throws GithubException {
        return parseResponse(makeGetRequest(NOTES_REPO_ENDPOINT + "/" + user.login + "/"
                + repo + "/issues" + "?milestone=" + milestone + '&' + SCOPE_ALL), NOTES_TYPE_TOKEN);
    }

    // get issues by repo
    public static List<Note> getNotes(String repo) throws GithubException {
        return parseResponse(makeGetRequest(NOTES_REPO_ENDPOINT + "/" + user.login + "/"
                + repo + "/issues"+ '?' + SCOPE_ALL), NOTES_TYPE_TOKEN);
    }

    //get issue by label
    public static List<Note> getNotes(String repo, String labelName) throws GithubException {
        return parseResponse(makeGetRequest(NOTES_REPO_ENDPOINT + "/" + user.login + "/"
                + repo + "/issues"+ '?' + "labels=" + labelName), new TypeToken<List<Note>>(){});
    }

    // patch issue
    public static Response patchNote(@NonNull String repo, @NonNull String title, String body, int repoNum)
            throws IOException, JSONException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo + "/issues/" + repoNum;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("body", body);

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .patch(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    public static Response addNote(@NonNull String repo, @NonNull String title, String body, int milestone)
            throws IOException, JSONException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo + "/issues";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("body", body);
        jsonObject.put("milestone", milestone);

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }


        // post issue by repo
    public static Response addNote(@NonNull String repo, @NonNull String title, String body)
            throws JSONException, IOException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo + "/issues";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("body", body);

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    public static Response closeNote(@NonNull String repo, @NonNull int noteNum) throws IOException, JSONException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo + "/issues/" + noteNum;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", "close");

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .patch(requestBody)
                .build();

        return client.newCall(request).execute();
    }


    /*--------------------------------------------------------------------------------------------------
     * Repo
    --------------------------------------------------------------------------------------------------*/
    // request all user repo
    public static List<Repo> getRepos() throws GithubException {
        return parseResponse(makeGetRequest(USERS_ENDPOINT + "/" + user.login + "/repos"), REPOS_TYPE_TOKEN);
    }

    public static Repo getRepo(String repo) throws  GithubException {
        return parseResponse(makeGetRequest(NOTES_REPO_ENDPOINT + "/" + user.login + "/"
                + repo), REPO_TYPE_TOKEN);
    }

    public static Response addRepo(@NonNull String name)
            throws JSONException, IOException {
        String url = REPO_ENDPOINT;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    public static Response deleteRepo(@NonNull String repo) throws GithubException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo;
        Request request = authRequestBuilder(url)
                .delete()
                .build();
        return makeRequest(request);
    }

    /*--------------------------------------------------------------------------------------------------
     * MileStone
    --------------------------------------------------------------------------------------------------*/

    public static List<MileStone> getMileStone(String repo) throws GithubException {
        return parseResponse(makeGetRequest(MILESTONE_ENDPOINT + "/"
                + user.login + "/" + repo + "/milestones"), MILESTONES_TYPE_TOKEN);
    }

    public static Response addMileStone(@NonNull String repo, @NonNull String title)
            throws JSONException, IOException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo + "/milestones";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    public static Response deleteMileStone(@NonNull String repo, @NonNull int milestoneNum) throws GithubException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo + "/milestones/" + milestoneNum;

        Request request = authRequestBuilder(url)
                .delete()
                .build();

        return makeRequest(request);
    }

    /*--------------------------------------------------------------------------------------------------
     * Comment
    --------------------------------------------------------------------------------------------------*/

    // get issue comment
    public static List<Comment> getNoteComment(String repo, int id) throws GithubException {
        return parseResponse(makeGetRequest(NOTES_REPO_ENDPOINT + "/" + user.login + "/"
                + repo + "/issues/" + id + "/comments"), new TypeToken<List<Comment>>(){});
    }

    public static Response addComment(@NonNull String repo, @NonNull int noteNum, @NonNull String body)
            throws JSONException, IOException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo + "/issues/" + noteNum + "/comments";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("body", body);

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    /*--------------------------------------------------------------------------------------------------
     * Label
    --------------------------------------------------------------------------------------------------*/
    public static List<Label> getRepoLabels(@NonNull String repo) throws GithubException {
        return parseResponse(makeGetRequest(NOTES_REPO_ENDPOINT + "/" + user.login + "/"
                + repo + "/labels"), new TypeToken<List<Label>>(){});
    }

    public static Response addLabel(@NonNull String repo, @NonNull String label, @NonNull String color) throws JSONException, IOException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo + "/labels";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", label);
        jsonObject.put("color", color);

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url)
                .post(requestBody)
                .build();

        return client.newCall(request).execute();
    }

    public static Response deleteLabel(@NonNull String repo, @NonNull String label) throws GithubException {
        String url = NOTES_REPO_ENDPOINT + "/" + user.login + "/" + repo + "/labels/" + label;

        Request request = authRequestBuilder(url)
                .delete()
                .build();

        return makeRequest(request);
    }

}