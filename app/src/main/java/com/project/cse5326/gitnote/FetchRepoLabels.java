package com.project.cse5326.gitnote;

import android.os.AsyncTask;

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.Label;

import java.util.List;

/**
 * Created by sifang on 11/13/17.
 */

public class FetchRepoLabels extends AsyncTask<String, String, List<Label>> {

    String mRepoName;

    public FetchRepoLabels(String repoName){
        mRepoName = repoName;
    }

    @Override
    protected List<Label> doInBackground(String... strings) {
        List<Label> labels = null;
        try {
            labels = Github.getRepoLabels(mRepoName);
        } catch (GithubException e) {
            e.printStackTrace();
        }
        return labels;
    }

}
