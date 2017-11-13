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
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by sifang
 */

public class NoteShowMainActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE = "com.project.cse5235.gitnote.note";

    private Note mNote;
    private List<Comment> mComments;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public static Intent newIntent(Context packageContext, Note note){
        Intent intent = new Intent(packageContext, NoteShowMainActivity.class);
        intent.putExtra(EXTRA_NOTE, ModelUtils.toString(note, new TypeToken<Note>(){}));

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_page);

        mNote = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_NOTE), new TypeToken<Note>(){});
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
            mComments = new FetchNoteComments(mNote.getNumber(),mNote.getRepository().getName()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
                return NoteContentFragment.newInstance(mNote, mNote.getRepository().getName());
            }else if(position == 1){
                return NoteCommentFragment.newInstance(mComments, mNote.getRepository().getName(), mNote);
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
