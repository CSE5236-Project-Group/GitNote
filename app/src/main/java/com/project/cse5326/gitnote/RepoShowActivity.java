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
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.MileStone;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by sifang
 */

public class RepoShowActivity extends AppCompatActivity {

    public static final String EXTRA_REPO = "com.project.cse5235.gitnote.repo";

    private Repo mRepo;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;


    public static Intent newIntent(Context packageContext, Repo repo){
        Intent intent = new Intent(packageContext, RepoShowActivity.class);
        intent.putExtra(EXTRA_REPO, ModelUtils.toString(repo, new TypeToken<Repo>(){}));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_page);

        mRepo = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_REPO), new TypeToken<Repo>(){});

        setTitle(mRepo.getName());

        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mViewPager = findViewById(R.id.viewpager);
        List<Note> notes = null;
        List<MileStone> mileStones = null;
        try {
           notes = new FetchRepoAllNotes(mRepo.getName()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            mileStones = new FetchRepoMileStones(mRepo.getName()).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        RepoShowActivity.ViewPagerAdapter adapter = new RepoShowActivity.ViewPagerAdapter(getSupportFragmentManager()
                , notes, mileStones);
        mViewPager.setAdapter(adapter);

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
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

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private String mTitle[] = {"All Notes", "MileStones"};
        private List<Note> mNotes;
        private List<MileStone> mMileStones;

        public ViewPagerAdapter(FragmentManager fm,
                                List<Note> notes, List<MileStone> mileStones) {
            super(fm);
            mNotes = notes;
            mMileStones = mileStones;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return NoteListRepoFragment.newInstance(mNotes, mRepo.getName());
                case 1:
                    return MileStoneListFragment.newInstance(mMileStones, mRepo.getName());
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
