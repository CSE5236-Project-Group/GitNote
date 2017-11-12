package com.project.cse5326.gitnote;

import android.os.AsyncTask;

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.Comment;

import java.util.List;

/**
 * Created by sifang on 11/12/17.
 */


public class FetchComments extends AsyncTask<String, String, List<Comment>> {

    private String mRepoName;
    private int mNoteId;

    public FetchComments(int noteId, String repoName){
        mNoteId = noteId;
        mRepoName = repoName;
    }

    @Override
    protected List<Comment> doInBackground(String... strings) {
        List<Comment> comments = null;
        try {
            comments = Github.getNoteComment(mRepoName,mNoteId);
        } catch (GithubException e) {
            e.printStackTrace();
        }
        return comments;
    }
}
