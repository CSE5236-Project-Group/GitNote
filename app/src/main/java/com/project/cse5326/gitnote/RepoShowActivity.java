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

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

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
        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mTabLayout = findViewById(R.id.tabs);

        mViewPager = findViewById(R.id.viewpager);


    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private String mTitle[] = {"All Notes", "MileStones"};
        private Repo mRepo;

        public ViewPagerAdapter(FragmentManager fm, Repo repo) {
            super(fm);
            mRepo = repo;
        }

        @Override
        public Fragment getItem(int position) {

            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }

    public class FetchRepoAllNotes extends AsyncTask<String, String, String>{

        List<Note> mNotes;

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }


}
