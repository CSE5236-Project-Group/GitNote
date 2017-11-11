package com.project.cse5326.gitnote;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;


import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

/**
 * Created by sifang
 */

public class MileStoneNoteListActivity extends SingleFragmentActivity {

    public static final String EXTRA_MS_NOTES = "com.project.cse5235.gitnote.milestone_notes";

    public static Intent newIntent(Context packageContext, List<Note> notes){
        Intent intent = new Intent(packageContext, MileStoneNoteListActivity.class);
        intent.putExtra(EXTRA_MS_NOTES, ModelUtils.toString(notes, new TypeToken<List<Note>>(){}));

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        List<Note> notes = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_MS_NOTES),new TypeToken<List<Note>>(){});
        return NoteListFragment.newInstance(notes);
    }
}
