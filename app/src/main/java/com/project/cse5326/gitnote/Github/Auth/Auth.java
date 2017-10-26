package com.project.cse5326.gitnote.Github.Auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/*--------------------------------------------------------------------------------------------------
 * Build Method for Github API
--------------------------------------------------------------------------------------------------*/

public class Auth {
    public static final int REQ_CODE = 100;

    // Constant parameter name from Github API
    private static final String KEY_CODE = "code";
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String KEY_SCOPE = "scope";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REDIRECT_URI = "redirect_uri";

    // Hard code values for this app
    private static final String CLIENT_ID = "4a5fb479d9100dbf7bdc";
    private static final String CLIENT_SECRET = "f372723f959a6fec832ffcd0ea93c99d19edebdd";

    // scope: user & gist & repo
    private static final String SCOPE = "user&repo&gist";

    // url
    private static final String URI_AUTHORIZE = "https://github.com/login/oauth/authorize"; //GET
    private static final String URI_TOKEN = "https://github.com/login/oauth/token"; // post

    // redirect url
    public static final String REDIRECT_URI = "https://www.google.com/";

    private static OkHttpClient client;

    private static String buildAuthorizeUrl() {
        String url = Uri.parse(URI_AUTHORIZE)
                .buildUpon()
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .build()
                .toString();
        url += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;
        url += "&" + KEY_SCOPE + "=" + SCOPE;

        return url;
    }

    public static void openAuthActivity(@NonNull Activity activity) {
        Intent intent = new Intent(activity, AuthActivity.class);
        intent.putExtra(AuthActivity.KEY_URL, buildAuthorizeUrl());
        activity.startActivityForResult(intent, REQ_CODE);
    }

    private static class FetchAccessToken extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            RequestBody postBody = new FormBody.Builder()
                    .add(KEY_CLIENT_ID, CLIENT_ID)
                    .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                    .add(KEY_CODE, strings[0])
                    .add(KEY_REDIRECT_URI, REDIRECT_URI)
                    .build();

            Request request = new Request.Builder()
                    .url(URI_TOKEN)
                    .post(postBody)
                    .build();

            // Start request
            String respond = null;
            try {
                respond = client.newCall(request).execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // handle returned json
            try {
                JSONObject obj = new JSONObject(respond);
                return obj.getString(KEY_ACCESS_TOKEN);
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    // Execute thread
    public static String fetchAccessToken(String authCode)
            throws ExecutionException, InterruptedException {
        client = new OkHttpClient();
        return new FetchAccessToken().execute(authCode).get();
    }
}
