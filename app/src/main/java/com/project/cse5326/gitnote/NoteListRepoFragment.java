package com.project.cse5326.gitnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final String DIALOG_DELETE = "DialogDate";
    private static final String CURRENT_NOTES = "current_notes";
    private static final int REQUEST_SHOW = 0;
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_DELETE = 2;
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
        if(savedInstanceState != null){
            mNotes = ModelUtils.toObject(savedInstanceState.getString(CURRENT_NOTES), new TypeToken<List<Note>>(){});
        }
        mRepoName = getArguments().getString(ARG_REPO_NAME);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mNoteRecyclerView = view.findViewById(R.id.recycler_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new NoteAdapter(mNotes, mRepoName);
        mNoteRecyclerView.setAdapter(mAdapter);
        mNoteRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.spacing_small)));


        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ModelUtils.hasNetworkConnection(getActivity())){
                    Intent intentRepo = AddNoteActivity.newIntent(getActivity(), mRepoName, "RepoShowActivity");
                    startActivityForResult(intentRepo, REQUEST_ADD);
                }else{
                    Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(CURRENT_NOTES, ModelUtils.toString(mNotes, new TypeToken<List<Note>>(){}));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SHOW) {
            if (resultCode == RESULT_OK) {
                boolean edit = data.getBooleanExtra("EDIT",false);
                if(edit){
                    Note note = ModelUtils.toObject(data.getStringExtra("EDITED_NOTE"), new TypeToken<Note>(){});
                    mNotes.remove(viewed_pos);
                    mNotes.add(0, note);
                }else {
                    mNotes.remove(viewed_pos);
                }
                mAdapter.notifyDataSetChanged();
            }
        }else if(requestCode == REQUEST_ADD){
            if(resultCode == RESULT_OK){
                Note note = ModelUtils.toObject(data.getStringExtra("UPDATED_NOTES"), new TypeToken<Note>(){});
                mNotes.add(0,note);
                mAdapter.notifyDataSetChanged();
            }
        }else if(requestCode == REQUEST_DELETE){
            if (resultCode == RESULT_OK) {
                List<Integer> deletedIndex = ModelUtils.toObject(data.getStringExtra(DeleteFragment.EXTRA_DELETE_INDEX), new TypeToken<List<Integer>>(){});
                for (int i = 0; i<deletedIndex.size(); i++){
                    mNotes.remove((int)deletedIndex.get(i));
                }
                mAdapter.notifyDataSetChanged();
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
                if(ModelUtils.hasNetworkConnection(getActivity())){
                    FragmentManager fm = getFragmentManager();
                    Log.i("RepoNoteList", "HERE!!");
                    DeleteFragment dialog = DeleteFragment.newInstance(mRepoName,mNotes);
                    dialog.setTargetFragment(this, REQUEST_DELETE);
                    dialog.show(fm,DIALOG_DELETE);
//                    new NoteContentFragment.LockNote(mRepoName, mNote.getNumber()).execute();
                }else{
                    Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
                }
                return true;
            case android.R.id.home:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("DELETE",false);
                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
            if(ModelUtils.hasNetworkConnection(getActivity())){
                viewed_pos = NoteListRepoFragment.mNotes.indexOf(mNote);
                Intent intent = NoteShowActivity.newIntent(getActivity(),mNote, mRepoName);
                startActivityForResult(intent,REQUEST_SHOW);
            }else{
                Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
            }
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
