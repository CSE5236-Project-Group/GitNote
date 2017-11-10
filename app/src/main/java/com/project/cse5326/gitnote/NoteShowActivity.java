package com.project.cse5326.gitnote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by sifang
 */

public class NoteShowActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE_ID = "com.project.cse5235.gitnote.note_id";

    private int mNoteId;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;



    public static Intent newIntent(Context packageContext, int noteId){
        Intent intent = new Intent(packageContext, NoteShowActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_page);

        mNoteId = (int) getIntent().getIntExtra(EXTRA_NOTE_ID, 0);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.note_viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), mNoteId);
        mViewPager.setAdapter(adapter);

        mTabLayout = (TabLayout) findViewById(R.id.note_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private String mTitle[] = {"Note", "Comment"};
        private int mNoteId;

        public ViewPagerAdapter(FragmentManager manager, int noteId) {
            super(manager);
            this.mNoteId = noteId;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return NoteShowContentFragment.newInstance(mNoteId);
            }else if(position == 1){
                return NoteShowCommentFragment.newInstance(mNoteId);
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
