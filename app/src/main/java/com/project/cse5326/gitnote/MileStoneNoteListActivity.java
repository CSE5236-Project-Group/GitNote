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
import com.project.cse5326.gitnote.Model.MileStone;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

import okhttp3.Response;

/**
 * Created by sifang
 */

public class MileStoneNoteListActivity extends SingleFragmentActivity {

    public static final String EXTRA_MS_NOTES = "com.project.cse5235.gitnote.milestone_notes";
    public static final String EXTRA_MS_REPO = "com.project.cse5235.gitnote.milestone_repo";
    public static final String EXTRA_MS = "com.project.cse5235.gitnote.milestone";

    private List<Note> mNotes;
    private String mRepoName;
    private MileStone mMilestone;

    public static Intent newIntent(Context packageContext, List<Note> notes, String repoName, MileStone milestone){
        Intent intent = new Intent(packageContext, MileStoneNoteListActivity.class);
        intent.putExtra(EXTRA_MS_NOTES, ModelUtils.toString(notes, new TypeToken<List<Note>>(){}));
        intent.putExtra(EXTRA_MS_REPO, repoName);
        intent.putExtra(EXTRA_MS, ModelUtils.toString(milestone, new TypeToken<MileStone>(){}));
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mNotes = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_MS_NOTES),new TypeToken<List<Note>>(){});
        mRepoName = getIntent().getStringExtra(EXTRA_MS_REPO);
        mMilestone = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_MS), new TypeToken<MileStone>(){});
        return NoteListMSFragment.newInstance(mNotes, mRepoName, mMilestone);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.delete, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.delete:
//                   new DeleteMileStone(mRepoName, mMilestone.number).execute();
//                   return true;
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

    public class DeleteMileStone extends AsyncTask<String, String, String> {

        private int mMilestoneNum;
        private String mRepoName;
        private boolean responseOk;
        private String responseMessage;

        public DeleteMileStone(String repoName, int milestoneNum){
            mMilestoneNum = milestoneNum;
            mRepoName = repoName;
        }

        @Override
        protected void onPreExecute(){
            if(!ModelUtils.hasNetworkConnection(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "No Network Connection!", Toast.LENGTH_LONG).show();
                this.cancel(true);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.deleteMileStone(mRepoName, mMilestoneNum);
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
                Toast.makeText(MileStoneNoteListActivity.this, "Successfully Deleted", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("DELETE",true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }else{
                Toast.makeText(MileStoneNoteListActivity.this, responseMessage, Toast.LENGTH_LONG).show();
            }
        }

    }
}
