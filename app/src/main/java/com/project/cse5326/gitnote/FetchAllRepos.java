package com.project.cse5326.gitnote;

import android.os.AsyncTask;

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.Repo;

import java.util.List;

/**
 * Created by sifang
 */

public class FetchAllRepos extends AsyncTask<String, String, List<Repo>> {

    List<Repo> mRepos;

    @Override
    protected List<Repo> doInBackground(String... strings) {
        try {
            mRepos = Github.getRepos(1);
        } catch (GithubException e) {
            e.printStackTrace();
        }
        return mRepos;
    }

}
