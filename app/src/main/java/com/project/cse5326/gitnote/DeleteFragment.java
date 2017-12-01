package com.project.cse5326.gitnote;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * Created by sifang
 */

public class DeleteFragment extends DialogFragment {

    private static final String ARG_NOTES = "notes";
    private static final String ARG_REPO_NAME = "repo_name";
    public static final String EXTRA_DELETE_INDEX = "com.project.cse5235.gitnote.delete.delete_index";

    private List<Note> mNotes;
    private List<Note> mNotesDelete;
    private String mRepoName;
    private List<Integer> checked_pos;
    public NoteAdapter mAdapter;
    private RecyclerView mNoteRecyclerView;

    public static DeleteFragment newInstance(List<Note> notes){
        Bundle args = new Bundle();
        args.putString(ARG_NOTES, ModelUtils.toString(notes, new TypeToken<List<Note>>(){}));
        DeleteFragment fragment = new DeleteFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static DeleteFragment newInstance(String repoName, List<Note> notes){
        Bundle args = new Bundle();
        args.putString(ARG_NOTES, ModelUtils.toString(notes, new TypeToken<List<Note>>(){}));
        args.putString(ARG_REPO_NAME, repoName);
        DeleteFragment fragment = new DeleteFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){

        mNotes = ModelUtils.toObject(getArguments().getString(ARG_NOTES), new TypeToken<List<Note>>(){});
        mRepoName = getArguments().getString(ARG_REPO_NAME);
        checked_pos = new ArrayList<>();
        mNotesDelete = new ArrayList<>();

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_delete, null);

        mNoteRecyclerView = v.findViewById(R.id.recycler_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new NoteAdapter(mNotes);
        mNoteRecyclerView.setAdapter(mAdapter);
        mNoteRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.spacing_small)));

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.delete_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i=0; i<mNotesDelete.size(); i++){
                            Note note = mNotesDelete.get(i);
                            if(mRepoName == null){
                                new LockNote(note.getRepository().getName(), note.getNumber()).execute();
                            }else{
                                new LockNote(mRepoName, note.getNumber()).execute();
                            }
                        }
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode){
        if(getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DELETE_INDEX, ModelUtils.toString(checked_pos, new TypeToken<List<Integer>>(){}));

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    public class NoteHolder extends RecyclerView.ViewHolder {

        private Note mNote;
        private TextView mNoteTitle;
        private TextView mNoteDate;
        private CheckBox mCheckBox;

        public NoteHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_delete, parent, false));

            mNoteTitle = itemView.findViewById(R.id.note_title);
            mNoteDate = itemView.findViewById(R.id.note_date);
            mCheckBox = itemView.findViewById(R.id.check_box);
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        checked_pos.add(mNotes.indexOf(mNote));
                        mNotesDelete.add(mNote);
                    }else{
                        checked_pos.remove((Integer) mNotes.indexOf(mNote));
                        mNotesDelete.remove(mNote);
                    }
                }
            });

        }

        public void bind(Note note){
            mNote = note;
            mNoteTitle.setText(note.getTitle());
            mNoteDate.setText(note.getUpdated_at());
        }

    }

    public class NoteAdapter extends RecyclerView.Adapter<NoteHolder>{

        private List<Note> mNotes;

        public NoteAdapter(List<Note> notes){
            this.mNotes = notes;
        }

        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NoteHolder(LayoutInflater.from(getActivity()), parent);
        }

        @Override
        public void onBindViewHolder(NoteHolder holder, int position) {
            holder.bind(mNotes.get(position));
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }
    }

    public class LockNote extends AsyncTask<String, String, String> {
        private int mNoteNum;
        private String mRepoName;
        private boolean responseOk;
        private String responseMessage;

        public LockNote(String repoName, int noteNum){
            mNoteNum = noteNum;
            mRepoName = repoName;
        }

        @Override
        protected void onPreExecute(){
            if(!ModelUtils.hasNetworkConnection(getActivity())){
                Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
                this.cancel(true);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.closeNote(mRepoName, mNoteNum);
                responseOk = response.isSuccessful();
                responseMessage = response.message();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(!responseOk){
//                Toast.makeText(getActivity(), "Successfully Deleted", Toast.LENGTH_LONG).show();
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("EDIT",false);
//                getActivity().setResult(Activity.RESULT_OK,returnIntent);
//                getActivity().finish();
//            }else{
                Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }




}
