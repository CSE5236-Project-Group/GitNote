package com.project.cse5326.gitnote;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Response;
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
                if(ModelUtils.hasNetworkConnection(getActivity())){
                    mEditButton.show();
                    Intent intent = NoteEditActivity.newIntent(getActivity(), mNote, mRepoName);
                    startActivityForResult(intent, EDIT_CONDITION);
                }else{
                    Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
                }
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

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.delete, menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
//            case R.id.delete:
//                if(ModelUtils.hasNetworkConnection(getActivity())){
//                    FragmentManager fm = getFragmentManager();
//                    DeleteFragment dialog = new DeleteFragment();
//                    dialog.show(fm,DIALOG_DELETE);
//                    new NoteContentFragment.LockNote(mRepoName, mNote.getNumber()).execute();
//                }else{
//                    Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
//                }
//                return true;
            case android.R.id.home:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("EDITED_NOTE",ModelUtils.toString(mNote, new TypeToken<Note>(){}));
                returnIntent.putExtra("EDIT",true);
                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    public class LockNote extends AsyncTask<String, String, String> {
//        private int mNoteNum;
//        private String mRepoName;
//        private boolean responseOk;
//        private String responseMessage;
//
//        public LockNote(String repoName, int noteNum){
//            mNoteNum = noteNum;
//            mRepoName = repoName;
//        }
//
//        @Override
//        protected void onPreExecute(){
//            if(!ModelUtils.hasNetworkConnection(getActivity())){
//                Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
//                this.cancel(true);
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            try {
//                Response response = Github.closeNote(mRepoName, mNoteNum);
//                responseOk = response.isSuccessful();
//                responseMessage = response.message();
//                Log.i("Response", response.toString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String s){
//            super.onPostExecute(s);
//            if(responseOk){
//                Toast.makeText(getActivity(), "Successfully Deleted", Toast.LENGTH_LONG).show();
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("EDIT",false);
//                getActivity().setResult(Activity.RESULT_OK,returnIntent);
//                getActivity().finish();
//            }else{
//                Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_LONG).show();
//            }
//        }
//    }


}
