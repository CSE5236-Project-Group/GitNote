package com.project.cse5326.gitnote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

import okhttp3.Response;

/**
 * Created by sifang on 11/13/17.
 */

public class LabelNoteListActivity extends SingleFragmentActivity {

    public static final String EXTRA_LABEL_NAME = "com.project.cse5235.gitnote.label_name";
    public static final String EXTRA_REPO_NAME= "com.project.cse5235.gitnote.repo_name";
    public static final String EXTRA_NOTES = "com.project.cse5235.gitnote.notes";

    private List<Note> mNotes;
    private String mRepoName;
    private String mLabelName;

    public static Intent newIntent(Context packageContext, String repoName, String labelName, List<Note> notes){
        Intent intent = new Intent(packageContext, LabelNoteListActivity.class);
        intent.putExtra(EXTRA_LABEL_NAME, labelName);
        intent.putExtra(EXTRA_REPO_NAME, repoName);
        intent.putExtra(EXTRA_NOTES, ModelUtils.toString(notes, new TypeToken<List<Note>>(){}));
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mRepoName = getIntent().getStringExtra(EXTRA_REPO_NAME);
        mLabelName = getIntent().getStringExtra(EXTRA_LABEL_NAME);
        mNotes = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_NOTES), new TypeToken<List<Note>>(){});
        return NoteListLabelFragment.newInstance(mRepoName, mLabelName, mNotes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                new DeleteLabel(mRepoName, mLabelName).execute();
                return true;
            case android.R.id.home:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("DELETE",false);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class DeleteLabel extends AsyncTask<String, String, String> {

        private String mRepoName;
        private String mLabelName;
        private boolean responseOk;
        private String responseMessage;

        public DeleteLabel(String repoName, String labelName){
            mLabelName = labelName;
            mRepoName = repoName;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.deleteLabel(mRepoName, mLabelName);
                responseOk = response.isSuccessful();
                responseMessage = response.message();
            } catch (GithubException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(responseOk){
                Toast.makeText(LabelNoteListActivity.this, "Successfully Deleted", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("DELETE",true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }else{
                Toast.makeText(LabelNoteListActivity.this, responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
