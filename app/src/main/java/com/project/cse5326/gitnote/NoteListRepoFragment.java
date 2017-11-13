package com.project.cse5326.gitnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sifang
 */

public class NoteListRepoFragment extends Fragment{

    private static final String ARG_NOTES = "notes";
    private static final String ARG_REPO_NAME = "repo_name";
    private static final int REQUEST_SHOW = 0;
    private static final int REQUEST_ADD = 1;
    private int viewed_pos;

    private static List<Note> mNotes;
    public NoteAdapter mAdapter;
    private String mRepoName;

    private RecyclerView mNoteRecyclerView;
    private FloatingActionButton mAddButton;

    public static NoteListRepoFragment newInstance(List<Note> notes, String repoName){
        Bundle args = new Bundle();
        args.putString(ARG_NOTES, ModelUtils.toString(notes, new TypeToken<List<Note>>(){}));
        args.putString(ARG_REPO_NAME, repoName);

        NoteListRepoFragment fragment = new NoteListRepoFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotes = ModelUtils.toObject(getArguments().getString(ARG_NOTES), new TypeToken<List<Note>>(){});
        mRepoName = getArguments().getString(ARG_REPO_NAME);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mNoteRecyclerView = view.findViewById(R.id.recycler_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new NoteAdapter(mNotes, mRepoName);
        mNoteRecyclerView.setAdapter(mAdapter);

        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRepo = AddNoteActivity.newIntent(getActivity(), mRepoName, "RepoShowActivity");
                startActivityForResult(intentRepo, REQUEST_ADD);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SHOW) {
            if (resultCode == RESULT_OK) {
                Note note = ModelUtils.toObject(data.getStringExtra("EDITED_NOTE"), new TypeToken<Note>(){});
                mNotes.remove(viewed_pos);
                mNotes.add(0, note);
                mAdapter.notifyDataSetChanged();
            }
        }else if(requestCode == REQUEST_ADD){
            if(resultCode == RESULT_OK){
                List<Note> notes = ModelUtils.toObject(data.getStringExtra("UPDATED_NOTES"), new TypeToken<List<Note>>(){});
                mNotes.clear();
                mNotes.addAll(notes);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public class NoteHolder extends RecyclerView.ViewHolder
                    implements View.OnClickListener{

        private Note mNote;
        private String mRepoName;
        private TextView mNoteTitle;
        private TextView mNoteDate;

        public NoteHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_note, parent, false));

            mNoteTitle = itemView.findViewById(R.id.note_title);
            mNoteDate = itemView.findViewById(R.id.note_date);

            itemView.setOnClickListener(this);
        }

        public void bind(Note note, String repoName){
            mNote = note;
            mRepoName = repoName;
            mNoteTitle.setText(note.getTitle());
            mNoteDate.setText(note.getUpdated_at());
        }

        @Override
        public void onClick(View v) {
            viewed_pos = NoteListRepoFragment.mNotes.indexOf(mNote);
            Intent intent = NoteShowActivity.newIntent(getActivity(),mNote, mRepoName);
            startActivityForResult(intent,REQUEST_SHOW);
        }
    }

    public class NoteAdapter extends RecyclerView.Adapter<NoteHolder>{

        private List<Note> mNotes;
        private String mRepoName;

        public NoteAdapter(List<Note> notes, String repoName){
            this.mNotes = notes;
            mRepoName = repoName;
        }


        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NoteHolder(LayoutInflater.from(getActivity()), parent);
        }

        @Override
        public void onBindViewHolder(NoteHolder holder, int position) {
            holder.bind(mNotes.get(position), mRepoName);
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }
    }

}
