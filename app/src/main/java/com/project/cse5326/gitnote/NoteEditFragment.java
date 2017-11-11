package com.project.cse5326.gitnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

/**
 * Created by sifang
 */

public class NoteEditFragment extends Fragment {

    private static final String ARG_NOTE = "note";

    private Note mNote;
    private EditText mNoteTitle;
    private EditText mNoteBody;

    public static NoteEditFragment newInstance(Note note){
        Bundle args = new Bundle();
        args.putString(ARG_NOTE, ModelUtils.toString(note, new TypeToken<Note>(){}));

        NoteEditFragment fragment = new NoteEditFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNote = ModelUtils.toObject(getArguments().getString(ARG_NOTE), new TypeToken<Note>(){});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_note_edit, container, false);
        mNoteTitle = view.findViewById(R.id.edit_note_title);
        mNoteBody = view.findViewById(R.id.edit_note_body);

//        getActivity().setTitle(mNote.getTitle());

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
