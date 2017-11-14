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
import com.project.cse5326.gitnote.Model.Label;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Response;

/**
 * Created by sifang on 11/13/17.
 */

public class AddLabelActivity extends AppCompatActivity {

    public static final String EXTRA_REPO_NAME = "com.project.cse5235.gitnote.add_label.repo_name";

    private Label mLabel;
    private String mRepoName;
    private Toolbar mToolbarAdd;

    public static Intent newIntent(Context packageContext, String repoName){
        Intent intent = new Intent(packageContext, AddLabelActivity.class);
        intent.putExtra(EXTRA_REPO_NAME, repoName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mLabel = new Label();
        mRepoName = getIntent().getStringExtra(EXTRA_REPO_NAME);

        mToolbarAdd = findViewById(R.id.toolbar_add);
        setSupportActionBar(mToolbarAdd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new AddLabelFragment();
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
                mLabel = AddLabelFragment.getLabel();
                if(mLabel.name.equals("")){
                    Toast.makeText(AddLabelActivity.this, "Label name can not be empty", Toast.LENGTH_LONG).show();
                }else{
                    new AddLabelActivity.PostNewLabel(mRepoName,mLabel).execute();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PostNewLabel extends AsyncTask<String, String, String> {

        private Label mLabel;
        private String mRepoName;
        private boolean responseOk;
        private String responseMessage;

        public PostNewLabel(String repoName, Label label){
            mRepoName = repoName;
            mLabel = label;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.addLabel(mRepoName,mLabel.name,mLabel.color);
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
                Toast.makeText(AddLabelActivity.this, "Successfully Added", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("UPDATED_LABELS", ModelUtils.toString(updatedMileStones(), new TypeToken<List<Label>>(){}));
                setResult(RESULT_OK,returnIntent);
                finish();
            }else{
                Toast.makeText(AddLabelActivity.this, responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    public List<Label>  updatedMileStones(){
        List<Label> milestones = null;
        try {
            milestones = new FetchRepoLabels(mRepoName).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return milestones;
    }


}
