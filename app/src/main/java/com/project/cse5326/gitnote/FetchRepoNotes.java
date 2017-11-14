package com.project.cse5326.gitnote;

import android.os.AsyncTask;

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.Note;

import java.util.List;

/**
 * Created by sifang on 11/12/17.
 */

public class FetchRepoNotes extends AsyncTask<String, String, List<Note>> {

    String mRepoName;

    public FetchRepoNotes(String repoName){
        mRepoName = repoName;
    }

    @Override
    protected List<Note> doInBackground(String... strings) {
        List<Note> notes = null;
        try {
            notes = Github.getNotes(mRepoName);
        } catch (GithubException e) {
            e.printStackTrace();
        }
        return notes;
    }
}
