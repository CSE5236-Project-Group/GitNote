package com.project.cse5326.gitnote;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.NoteList;

import us.feras.mdv.MarkdownView;

/**
 * Created by sifang on 11/5/17.
 */

public class NoteShowFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";

    private Note mNote;
    private TextView mNoteTitle;
    private MarkdownView mNoteBody;


    public static NoteShowFragment newInstance(int noteId){
        Bundle args = new Bundle();
        args.putInt(ARG_NOTE_ID, noteId);

        NoteShowFragment fragment = new NoteShowFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int noteId = (int) getArguments().getInt(ARG_NOTE_ID);
        mNote = NoteList.get().getNote(noteId);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                final Bundle savedInstanceState){
            View view = inflater.inflate(R.layout.fragment_note_show, container, false);
            mNoteTitle = view.findViewById(R.id.show_note_title);
            mNoteBody = view.findViewById(R.id.show_note_body);

            getActivity().setTitle(mNote.getTitle());
            mNoteTitle.setText(mNote.getTitle());
            mNoteBody.loadMarkdown(mNote.getBody());

            return view;
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
                Intent intent = NoteEditActivity.newIntent(getActivity(), mNote.getId());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
