package com.project.cse5326.gitnote;

import android.os.AsyncTask;

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.Repo;

/**
 * Created by sifang on 11/12/17.
 */

public class FetchRepo extends AsyncTask<String, String, Repo> {

    String mRepoName;

    public FetchRepo(String repoName){
        mRepoName = repoName;
    }

    @Override
    protected Repo doInBackground(String... strings) {
        Repo repo = null;
        try {
            repo = Github.getRepo(mRepoName);
        } catch (GithubException e) {
            e.printStackTrace();
        }
        return repo;
    }
}
