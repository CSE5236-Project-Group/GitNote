package com.project.cse5326.gitnote;

import android.os.AsyncTask;

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.Note;

import java.util.List;

/**
 * Created by sifang on 11/13/17.
 */

public class FetchLabelNotes extends AsyncTask<String, String, List<Note>> {

    String mLabelName;
    String mRepoName;

    public FetchLabelNotes(String repoName, String labelName){
        mRepoName = repoName;
        mLabelName = labelName;
    }

    @Override
    protected List<Note> doInBackground(String... strings) {
        List<Note> notes = null;
        try {
            notes = Github.getNotes(mRepoName, mLabelName);
        } catch (GithubException e) {
            e.printStackTrace();
        }
        return notes;
    }
}
