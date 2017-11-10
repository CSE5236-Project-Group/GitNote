package com.project.cse5326.gitnote;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by sifang
 */

public class NoteEditActivity extends SingleFragmentActivity {

    public static final String EXTRA_NOTE_ID = "com.project.cse5235.gitnote.note_id";

    public static Intent newIntent(Context packageContext, int noteId){
        Intent intent = new Intent(packageContext, NoteEditActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        int noteId = (int) getIntent().getIntExtra(EXTRA_NOTE_ID, 0);
        return NoteEditFragment.newInstance(noteId);
    }
}
