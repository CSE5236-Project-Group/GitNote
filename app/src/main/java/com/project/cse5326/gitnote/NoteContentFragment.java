package com.project.cse5326.gitnote;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by sifang
 */

public class NoteContentFragment extends Fragment {

    private static final String ARG_NOTE = "note";
    private static final String ARG_REPO_NAME = "repo_name";
    private static final int EDIT_CONDITION = 1;

    public Note mNote;
    private String mRepoName;
    private TextView mNoteTitle;
    private MarkdownView mNoteBody;
    private FloatingActionButton mEditButton;


    public static NoteContentFragment newInstance(Note note, String repoName){
        Bundle args = new Bundle();
        args.putString(ARG_NOTE, ModelUtils.toString(note, new TypeToken<Note>(){}));
        args.putString(ARG_REPO_NAME, repoName);
        NoteContentFragment fragment = new NoteContentFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNote = ModelUtils.toObject(getArguments().getString(ARG_NOTE), new TypeToken<Note>(){});
        mRepoName = getArguments().getString(ARG_REPO_NAME);
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

        updateUI();
        mEditButton = view.findViewById(R.id.edit_button);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditButton.show();
                Intent intent = NoteEditActivity.newIntent(getActivity(), mNote, mRepoName);
                startActivityForResult(intent, EDIT_CONDITION);
            }
        });
    }

    private void updateUI(){
        mNoteTitle.setText(mNote.getTitle());
        mNoteBody.loadMarkdown(mNote.getBody());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CONDITION) {
            if (resultCode == RESULT_OK) {
                mNote = ModelUtils.toObject(data.getStringExtra("EDITED_NOTE"), new TypeToken<Note>(){});
                updateUI();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete:
                return true;
            case android.R.id.home:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("EDITED_NOTE",ModelUtils.toString(mNote, new TypeToken<Note>(){}));
                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
