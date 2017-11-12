package com.project.cse5326.gitnote;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.Comment;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by sifang
 */

public class NoteShowActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE = "com.project.cse5235.gitnote.note";
    public static final String EXTRA_REPO_NAME = "com.project.cse5235.gitnote.note_repo_name";

    private Note mNote;
    private String mNoteRepoName;
    private List<Comment> mComments;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;



    public static Intent newIntent(Context packageContext, Note note, String noteRepoName){
        Intent intent = new Intent(packageContext, NoteShowActivity.class);
        intent.putExtra(EXTRA_NOTE, ModelUtils.toString(note, new TypeToken<Note>(){}));
        intent.putExtra(EXTRA_REPO_NAME, noteRepoName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_page);

        mNote = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_NOTE), new TypeToken<Note>(){});
        mNoteRepoName = getIntent().getStringExtra(EXTRA_REPO_NAME);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), mNote);
        mViewPager.setAdapter(adapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        try {
            mComments = new FetchComments(mNote.getNumber(),mNoteRepoName).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private String mTitle[] = {"Note", "Comment"};
        private Note mNote;

        public ViewPagerAdapter(FragmentManager manager, Note note) {
            super(manager);
            this.mNote = note;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return NoteContentFragment.newInstance(mNote);
            }else if(position == 1){
                return NoteCommentFragment.newInstance(mComments);
            }
            return null;
        }

        @Override
        public int getCount() {
            return mTitle.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitle[position];
        }
    }

}
