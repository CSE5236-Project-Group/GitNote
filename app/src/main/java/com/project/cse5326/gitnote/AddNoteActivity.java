package com.project.cse5326.gitnote;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Model.MileStone;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Response;


/**
 * Created by sifang
 */

public class AddNoteActivity extends AppCompatActivity {

    public static final String EXTRA_MS = "com.project.cse5235.gitnote.add.milestone";
    public static final String EXTRA_REPO_NAME = "com.project.cse5235.gitnote.add.repo_name";

    private Note mNote;
    private String mRepoName;
    private String mCaller;
    private MileStone mMilestone;
    private boolean mIsInMileStone;
    private Toolbar mToolbarAdd;

    public static Intent newIntent(Context packageContext, String repoName, String caller) {
        Intent intent = new Intent(packageContext, AddNoteActivity.class);
        intent.putExtra(EXTRA_REPO_NAME, repoName);
        intent.putExtra("caller", caller);
        intent.putExtra("isInMileStone", false);
        return intent;
    }

    public static Intent newIntent(Context packageContext, String repoName
           ,String caller , MileStone milestone) {
        Intent intent = new Intent(packageContext, AddNoteActivity.class);
        intent.putExtra(EXTRA_REPO_NAME, repoName);
        intent.putExtra(EXTRA_MS, ModelUtils.toString(milestone, new TypeToken<MileStone>(){}));
        intent.putExtra("caller", caller);
        intent.putExtra("isInMileStone", true);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mNote = new Note();
        mRepoName = getIntent().getStringExtra(EXTRA_REPO_NAME);
        mIsInMileStone = getIntent().getBooleanExtra("isInMileStone", false);
        if (mIsInMileStone) {
            mMilestone = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_MS), new TypeToken<MileStone>(){});
        }
        mCaller = getIntent().getStringExtra("caller");

        mToolbarAdd = findViewById(R.id.toolbar_add);
        setSupportActionBar(mToolbarAdd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new AddNoteFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.confirm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.confirm:
                if (mIsInMileStone) {
                    mNote = AddNoteFragment.getNote();
                    if(mNote.getTitle().equals("")){
                        Toast.makeText(AddNoteActivity.this, "Note title can not be empty", Toast.LENGTH_LONG).show();
                    }else{
                        new PostNewNoteInMilestone(mNote, mRepoName, mMilestone.number).execute();
                    }
                } else {
                    mNote = AddNoteFragment.getNote();
                    if(mNote.getTitle().equals("")){
                        Toast.makeText(AddNoteActivity.this, "Note title can not be empty", Toast.LENGTH_LONG).show();
                    }else{
                        new PostNewNoteInRepo(mNote, mRepoName).execute();
                    }
                }
                return true;
            case android.R.id.home:
                if(mCaller.equals("RepoShowActivity")){
                    updateRepoPage();
                }else if(mCaller.equals("MileStoneNoteListActivity")){
                    updateMSPage();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateRepoPage(){
        try {
            Repo repo = new FetchRepo(mRepoName).execute().get();
            Intent intent = RepoShowActivity.newIntent(this, repo);
            startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void updateMSPage(){
        try {
            List<Note> notes = new FetchMileStoneNotes(mMilestone.number, mRepoName).execute().get();
            Intent intent = MileStoneNoteListActivity.newIntent(this, notes, mRepoName, mMilestone);
            startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public class PostNewNoteInMilestone extends AsyncTask<String, String, String> {

        private Note mNote;
        private String mRepoName;
        private int mMilestoneNum;
        private boolean responseOk;
        private String responseMessage;

        public PostNewNoteInMilestone(Note note, String repoName, int milestoneNum){
            mNote = note;
            mRepoName = repoName;
            mMilestoneNum = milestoneNum;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.addNote(mRepoName, mNote.getTitle(),mNote.getBody(), mMilestoneNum);
                responseOk = response.isSuccessful();
                responseMessage = response.message();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(responseOk){
                Toast.makeText(AddNoteActivity.this, "Successfully Added", Toast.LENGTH_LONG).show();
                updateMSPage();
            }else{
                Toast.makeText(AddNoteActivity.this, responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class PostNewNoteInRepo extends AsyncTask<String, String, String> {

        private Note mNote;
        private String mRepoName;
        private boolean responseOk;
        private String responseMessage;

        public PostNewNoteInRepo(Note note, String repoName){
            mNote = note;
            mRepoName = repoName;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.addNote(mRepoName, mNote.getTitle(),mNote.getBody());
                responseOk = response.isSuccessful();
                responseMessage = response.message();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(responseOk){
                Toast.makeText(AddNoteActivity.this, "Successfully Added", Toast.LENGTH_LONG).show();
                updateRepoPage();
            }else{
                Toast.makeText(AddNoteActivity.this, responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
