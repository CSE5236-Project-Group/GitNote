package com.project.cse5326.gitnote;

import android.app.Fragment;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    // bind views
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer) NavigationView navigationView;
    @BindView(R.id.text) TextView textView;

    private ActionBarDrawerToggle drawerToggle; // tie drawer_layout with ActionBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("cycle", "onCreate called");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUpDrawer();
    }

    private void changeText(TextView textView) {
        textView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        textView.setText("after");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("cycle", "onStart called");
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("cycle", "onResume called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("cycle", "onStop called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("cycle", "onDestory called");
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

        ((TextView) headerView.findViewById(R.id.nav_header_user_name)).setText("Zhenyu Liu");
        ((ImageView) headerView.findViewById(R.id.nav_header_user_picture))
                .setImageResource(R.drawable.user_picture_placeholder);

        // set user name and pic

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
}
