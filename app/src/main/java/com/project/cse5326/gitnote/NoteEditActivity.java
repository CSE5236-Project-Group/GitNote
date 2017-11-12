package com.project.cse5326.gitnote;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

/**
 * Created by sifang
 */

public class NoteEditActivity extends SingleFragmentActivity {

    public static final String EXTRA_NOTE = "com.project.cse5235.gitnote.note";

    public static Intent newIntent(Context packageContext, Note note){
        Intent intent = new Intent(packageContext, NoteEditActivity.class);
        intent.putExtra(EXTRA_NOTE, ModelUtils.toString(note, new TypeToken<Note>(){}));
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Note note = ModelUtils.toObject(getIntent().getStringExtra(EXTRA_NOTE), new TypeToken<Note>(){});
        return NoteEditFragment.newInstance(note);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return true;
//    }
}
