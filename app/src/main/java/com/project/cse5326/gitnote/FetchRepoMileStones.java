package com.project.cse5326.gitnote;

import android.os.AsyncTask;

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.MileStone;

import java.util.List;

/**
 * Created by sifang on 11/12/17.
 */


public class FetchRepoMileStones extends AsyncTask<String, String, List<MileStone>> {

    String mRepoName;

    public FetchRepoMileStones(String repoName) {
        mRepoName = repoName;
    }

    @Override
    protected List<MileStone> doInBackground(String... strings) {
        List<MileStone> mileStones = null;
        try {
            mileStones = Github.getMileStone(mRepoName);
        } catch (GithubException e) {
            e.printStackTrace();
        }
        return mileStones;
    }
}