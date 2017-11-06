package com.project.cse5326.gitnote;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.NoteList;

/**
 * Created by sifang on 11/5/17.
 */

public class NoteEditFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";

    private Note mNote;
    private EditText mNoteTitle;
    private EditText mNoteBody;

    public static NoteEditFragment newInstance(int noteId){
        Bundle args = new Bundle();
        args.putInt(ARG_NOTE_ID, noteId);

        NoteEditFragment fragment = new NoteEditFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int noteId = getArguments().getInt(ARG_NOTE_ID);
        mNote = NoteList.get().getNote(noteId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_note_edit, container, false);
        mNoteTitle = view.findViewById(R.id.edit_note_title);
        mNoteBody = view.findViewById(R.id.edit_note_body);

        getActivity().setTitle(mNote.getTitle());

        mNoteTitle.setText(mNote.getTitle());
        mNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mNoteBody.setText(mNote.getBody());
        mNoteBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setBody(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }


}
