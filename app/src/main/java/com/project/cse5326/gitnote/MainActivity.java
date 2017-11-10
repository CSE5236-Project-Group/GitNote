package com.project.cse5326.gitnote;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.NoteList;
import com.project.cse5326.gitnote.Utils.ImageUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    // bind views
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.drawer) NavigationView navigationView;
    @BindView(R.id.text) TextView textView;

    private ActionBarDrawerToggle drawerToggle; // tie drawer_layout with ActionBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUpDrawer();
        new FetchAllNotes().execute();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    /* Called by the system when the device configuration changes while your activity is running. */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.syncState();
    }

    /* This hook is called whenever an item in your options menu is selected.*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item);
    }

    private void setUpDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);

        View headerView = navigationView.inflateHeaderView(R.layout.drawer_header);

        // User name & avatar
        ((TextView) headerView.findViewById(R.id.nav_header_user_name))
                .setText(Github.getCurrentUser().name);
        ImageView userPicture = headerView.findViewById(R.id.nav_header_user_picture);
        ImageUtils.loadUserPicture(this, userPicture, Github.getCurrentUser().avatar_url);


        // Logout button
        headerView.findViewById(R.id.nav_header_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // logout
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.isChecked()) {
                    drawerLayout.closeDrawers();
                    return true;
                }
                switch (item.getItemId()) {
                    case R.id.drawer_item_all_notes:
                        Toast.makeText(MainActivity.this, "All Notes", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.drawer_item_Note_Repo:
                        Toast.makeText(MainActivity.this, "Note repo", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.drawer_item_gists:
                        Toast.makeText(MainActivity.this, "gists", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.drawer_item_notification:
                        Toast.makeText(MainActivity.this, "notification", Toast.LENGTH_LONG).show();
                        break;
                }
                drawerLayout.closeDrawers();
                return false;
            }
        });
    }

    public void showNoteList() {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);

            if(fragment == null){
                fragment = new NoteAllListFragment();
                fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
            }
    }

    public class FetchAllNotes extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                NoteList.set(Github.getNotes(1));
            } catch (GithubException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showNoteList();
        }


    }
}
