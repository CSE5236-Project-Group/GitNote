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

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Model.Repo;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Response;

/**
 * Created by sifang
 */

public class AddRepoActivity extends AppCompatActivity {

    private Repo mRepo;
    private Toolbar mToolbarAdd;

    public static Intent newIntent(Context packageContext){
        Intent intent = new Intent(packageContext, AddRepoActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mRepo = new Repo();

        mToolbarAdd = findViewById(R.id.toolbar_add);
        setSupportActionBar(mToolbarAdd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new AddRepoFragment();
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
                mRepo = AddRepoFragment.getRepo();
                if(mRepo.getName().equals("")){
                    Toast.makeText(AddRepoActivity.this, "Repo name can not be empty", Toast.LENGTH_LONG).show();
                }else{
                    new AddRepoActivity.PostNewRepo(mRepo.getName()).execute();
                }
                return true;
            case android.R.id.home:
                updateRepoList();
                RepoListFragment.adapter.notifyDataSetChanged();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateRepoList(){
        RepoListFragment.mRepos.add(mRepo);
    }

    public class PostNewRepo extends AsyncTask<String, String, String> {

        private String mRepoName;
        private boolean responseOk;
        private String responseMessage;

        public PostNewRepo(String repoName){
            mRepoName = repoName;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.addRepo(mRepoName);
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
                Toast.makeText(AddRepoActivity.this, "Successfully Added", Toast.LENGTH_LONG).show();
                updateRepoList();
            }else{
                Toast.makeText(AddRepoActivity.this, responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }


}
