package com.project.cse5326.gitnote;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

/**
 * Created by sifang.
 */

public class RepoListShowActivity extends SingleFragmentActivity {

    public static final String EXTRA_REPOS = "com.project.cse5235.gitnote.repos";

    public static Intent newIntent(Context packageContext, List<Repo> repos){
        Intent intent = new Intent(packageContext, RepoListShowActivity.class);
        intent.putExtra(EXTRA_REPOS, ModelUtils.toString(repos, new TypeToken<List<Repo>>(){}));
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        List<Repo> repos = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_REPOS), new TypeToken<List<Repo>>(){});
        return RepoListFragment.newInstance(repos);
    }
}
