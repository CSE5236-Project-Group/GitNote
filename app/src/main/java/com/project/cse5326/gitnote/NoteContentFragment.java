package com.project.cse5326.gitnote;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import us.feras.mdv.MarkdownView;

/**
 * Created by sifang
 */

public class NoteContentFragment extends Fragment {

    private static final String ARG_NOTE = "note";

    private Note mNote;
    private TextView mNoteTitle;
    private MarkdownView mNoteBody;


    public static NoteContentFragment newInstance(Note note){
        Bundle args = new Bundle();
        args.putString(ARG_NOTE, ModelUtils.toString(note, new TypeToken<Note>(){}));

        NoteContentFragment fragment = new NoteContentFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNote = ModelUtils.toObject(getArguments().getString(ARG_NOTE), new TypeToken<Note>(){});

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                final Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.fragment_note_content, container, false);
            return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoteTitle = view.findViewById(R.id.show_note_title);
        mNoteBody = view.findViewById(R.id.show_note_body);

        getActivity().setTitle(mNote.getTitle());
        mNoteTitle.setText(mNote.getTitle());
        mNoteBody.loadMarkdown(mNote.getBody());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note_show, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.edit_note:
                Intent intent = NoteEditActivity.newIntent(getActivity(), mNote);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
