package com.project.cse5326.gitnote;

import android.os.AsyncTask;

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.Note;

import java.util.List;

/**
 * Created by sifang on 11/12/17.
 */

public class FetchMileStoneNotes extends AsyncTask<String, String, List<Note>> {

    private int mMileStoneId;
    private String mRepoName;

    public FetchMileStoneNotes(int mileStoneId, String repoName){
        mMileStoneId = mileStoneId;
        mRepoName = repoName;
    }

    @Override
    protected List<Note> doInBackground(String... strings) {
        List<Note> notes = null;
        try {
            notes = Github.getNotes(mRepoName,mMileStoneId);
        } catch (GithubException e) {
            e.printStackTrace();
        }
        return notes;
    }
}
