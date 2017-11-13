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

public class AddMileStoneActivity extends AppCompatActivity {

    public static final String EXTRA_REPO_NAME = "com.project.cse5235.gitnote.add_ms.repo_name";

    private MileStone mMileStone;
    private String mRepoName;
    private Toolbar mToolbarAdd;

    public static Intent newIntent(Context packageContext, String repoName){
        Intent intent = new Intent(packageContext, AddMileStoneActivity.class);
        intent.putExtra(EXTRA_REPO_NAME, repoName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mMileStone = new MileStone();
        mRepoName = getIntent().getStringExtra(EXTRA_REPO_NAME);

        mToolbarAdd = findViewById(R.id.toolbar_add);
        setSupportActionBar(mToolbarAdd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new AddMileStoneFragment();
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
                mMileStone = AddMileStoneFragment.getMileStone();
                if(mMileStone.title.equals("")){
                    Toast.makeText(AddMileStoneActivity.this, "MileStone title can not be empty", Toast.LENGTH_LONG).show();
                }else{
                    new AddMileStoneActivity.PostNewMileStone(mMileStone, mRepoName).execute();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PostNewMileStone extends AsyncTask<String, String, String> {

        private MileStone mMileStone;
        private String mRepoName;
        private boolean responseOk;
        private String responseMessage;

        public PostNewMileStone(MileStone mileStone, String repoName){
            mMileStone = mileStone;
            mRepoName = repoName;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.addMileStone(mRepoName,mMileStone.title);
                responseOk = response.isSuccessful();
                responseMessage = response.message();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(responseOk){
                Toast.makeText(AddMileStoneActivity.this, "Successfully Added", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("UPDATED_MSS", ModelUtils.toString(updatedMileStones(), new TypeToken<List<MileStone>>(){}));
                setResult(RESULT_OK,returnIntent);
                finish();
            }else{
                Toast.makeText(AddMileStoneActivity.this, responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    public List<MileStone>  updatedMileStones(){
        List<MileStone> milestones = null;
        try {
            milestones = new FetchRepoMileStones(mRepoName).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return milestones;
    }




}
