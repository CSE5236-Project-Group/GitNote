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
import com.project.cse5326.gitnote.Model.Comment;
import com.project.cse5326.gitnote.Model.MileStone;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import okhttp3.Response;

/**
 * Created by sifang
 */

public class AddCommentActivity extends AppCompatActivity {

    public static final String EXTRA_REPO_NAME = "com.project.cse5235.gitnote.add_c.repo_name";
    public static final String EXTRA_NOTE_NUM = "com.project.cse5235.gitnote.add_c.note_num";

    private Comment mComment;
    private String mRepoName;
    private int mNoteNum;

    private Toolbar mToolbarAdd;

    public static Intent newIntent(Context packageContext, String repoName, int noteNum){
        Intent intent = new Intent(packageContext, AddCommentActivity.class);
        intent.putExtra(EXTRA_REPO_NAME, repoName);
        intent.putExtra(EXTRA_NOTE_NUM, noteNum);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mComment = new Comment();
        mRepoName = getIntent().getStringExtra(EXTRA_REPO_NAME);
        mNoteNum = getIntent().getIntExtra(EXTRA_NOTE_NUM, 0);

        mToolbarAdd = findViewById(R.id.toolbar_add);
        setSupportActionBar(mToolbarAdd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new AddCommentFragment();
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
                mComment = AddCommentFragment.getComment();
                if(mComment.body.equals("")){
                    Toast.makeText(AddCommentActivity.this, "Comment body can not be empty", Toast.LENGTH_LONG).show();
                }else{
                    new AddCommentActivity.PostNewComment(mComment, mRepoName, mNoteNum).execute();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PostNewComment extends AsyncTask<String, String, String> {

        private Comment mComment;
        private String mRepoName;
        private int mNoteNum;
        private boolean responseOk;
        private String responseMessage;

        public PostNewComment(Comment comment, String repoName, int noteNum){
            mComment = comment;
            mRepoName = repoName;
            mNoteNum = noteNum;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.addComment(mRepoName,mNoteNum,mComment.body);
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
                Toast.makeText(AddCommentActivity.this, "Successfully Added", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("ADDED_COMMENT", ModelUtils.toString(updatedComments(), new TypeToken<List<Comment>>(){}));
                setResult(RESULT_OK,returnIntent);
                finish();
            }else{
                Toast.makeText(AddCommentActivity.this, responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    public List<Comment>  updatedComments(){
        List<Comment> Comments = null;
        try {
            Comments = new FetchNoteComments(mNoteNum, mRepoName).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Comments;
    }

}
