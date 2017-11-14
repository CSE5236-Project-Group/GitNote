package com.project.cse5326.gitnote;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

/**
 * Created by sifang on 11/13/17.
 */

public class NoteListLabelFragment extends Fragment {

    private static final String ARG_NOTES = "notes";
    private static final String ARG_REPO_NAME = "repo_name";
    private static final String ARG_LABEL_NAME = "label_name";
    private static final int REQUEST_SHOW = 0;
    private static final int REQUEST_ADD = 1;

    private static List<Note> mNotes;
    private static String mRepoName;
    private static String mLabelName;

    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddButton;
    private NoteAdapter mAdapter;
    private int viewed_pos;

    public static NoteListLabelFragment newInstance(String repoName, String labelName, List<Note> notes){
        Bundle args =  new Bundle();
        args.putString(ARG_REPO_NAME, repoName);
        args.putString(ARG_LABEL_NAME, labelName);
        args.putString(ARG_NOTES, ModelUtils.toString(notes, new TypeToken<List<Note>>(){}));

        NoteListLabelFragment fragment = new NoteListLabelFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotes = ModelUtils.toObject(getArguments().getString(ARG_NOTES), new TypeToken<List<Note>>(){});
        mRepoName = getArguments().getString(ARG_REPO_NAME);
        mLabelName = getArguments().getString(ARG_LABEL_NAME);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new NoteAdapter(mNotes,mRepoName);
        mRecyclerView.setAdapter(mAdapter);

        getActivity().setTitle(mLabelName);

        mAddButton = view.findViewById(R.id.add_button);
//        mAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                mAddButton.hide();
//                Intent intent = AddNoteActivity.newIntent(getActivity(), mRepoName,"NoteListLabelFragment");
//                startActivityForResult(intent,REQUEST_ADD);
//            }
//        });

        return view;
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
            viewed_pos = NoteListLabelFragment.mNotes.indexOf(mNote);
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


