package com.project.cse5326.gitnote;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;


import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.MileStone;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

/**
 * Created by sifang
 */

public class MileStoneNoteListActivity extends SingleFragmentActivity {

    public static final String EXTRA_MS_NOTES = "com.project.cse5235.gitnote.milestone_notes";
    public static final String EXTRA_MS_REPO = "com.project.cse5235.gitnote.milestone_repo";
    public static final String EXTRA_MS = "com.project.cse5235.gitnote.milestone";

    public static Intent newIntent(Context packageContext, List<Note> notes, String repoName, MileStone milestone){
        Intent intent = new Intent(packageContext, MileStoneNoteListActivity.class);
        intent.putExtra(EXTRA_MS_NOTES, ModelUtils.toString(notes, new TypeToken<List<Note>>(){}));
        intent.putExtra(EXTRA_MS_REPO, repoName);
        intent.putExtra(EXTRA_MS, ModelUtils.toString(milestone, new TypeToken<MileStone>(){}));
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        List<Note> notes = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_MS_NOTES),new TypeToken<List<Note>>(){});
        String repoName = getIntent().getStringExtra(EXTRA_MS_REPO);
        MileStone milestone = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_MS), new TypeToken<MileStone>(){});
        return NoteListMSFragment.newInstance(notes, repoName, milestone);
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
}
