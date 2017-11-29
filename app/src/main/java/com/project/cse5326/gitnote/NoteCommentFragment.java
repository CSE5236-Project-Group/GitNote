package com.project.cse5326.gitnote;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.project.cse5326.gitnote.Model.Comment;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sifang
 */

public class NoteCommentFragment extends Fragment {

    private static final String ARG_COMMENTS = "comments";
    private static final String ARG_REPO_NAME = "repo_name";
    private static final String ARG_NOTE = "note";
    private static final int REQUEST = 0;

    private String mRepoName;
    private Note mNote;
    private List<Comment> mComments;
    private RecyclerView mCommentRecyclerView;
    private FloatingActionButton mAddButton;
    private CommentAdapter mAdapter;

    public static NoteCommentFragment newInstance(List<Comment> comments, String repoName, Note note){
        Bundle args = new Bundle();
        args.putString(ARG_COMMENTS, ModelUtils.toString(comments, new TypeToken<List<Comment>>(){}));
        args.putString(ARG_REPO_NAME, repoName);
        args.putString(ARG_NOTE, ModelUtils.toString(note, new TypeToken<Note>(){}));

        NoteCommentFragment fragment = new NoteCommentFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComments = ModelUtils.toObject(getArguments().getString(ARG_COMMENTS), new TypeToken<List<Comment>>(){});
        mRepoName = getArguments().getString(ARG_REPO_NAME);
        mNote = ModelUtils.toObject(getArguments().getString(ARG_NOTE), new TypeToken<Note>(){});
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View  view = inflater.inflate(R.layout.fragment_note_comment, container, false);

        mCommentRecyclerView = (RecyclerView) view.findViewById(R.id.note_comment_recycler_view);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new CommentAdapter(mComments);
        mCommentRecyclerView.setAdapter(mAdapter);
        mCommentRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.spacing_small)));


        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ModelUtils.hasNetworkConnection(getActivity())){
                    mAddButton.show();
                    Intent intent = AddCommentActivity.newIntent(getActivity(),mRepoName,mNote.getNumber());
                    startActivityForResult(intent, REQUEST);
                }else{
                    Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST) {
            if (resultCode == RESULT_OK) {
                Comment comment = ModelUtils.toObject(data.getStringExtra("ADDED_COMMENT"), new TypeToken<Comment>(){});
                mComments.add(0,comment);
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
                    new NoteCommentFragment.LockNote(mRepoName, mNote.getNumber()).execute();
                }else{
                    Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
                }
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

    public class CommentHolder extends RecyclerView.ViewHolder{

        private Comment mComment;
        private TextView mBody;
        private TextView mUserName;
        private TextView mTime;

        public CommentHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_comment,parent,false));

            mBody = itemView.findViewById(R.id.comment_body);
            mUserName = itemView.findViewById(R.id.comment_user_name);
            mTime = itemView.findViewById(R.id.comment_time);

        }

        public void bind(Comment comment){
            mComment = comment;
            mBody.setText(mComment.body);
            mUserName.setText(mComment.user.login);
            mTime.setText(mComment.updated_at);
        }
    }

    public class CommentAdapter extends RecyclerView.Adapter<CommentHolder> {

        private List<Comment> mComments;

        public CommentAdapter(List<Comment> comments){
            mComments = comments;
        }

        @Override
        public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommentHolder(LayoutInflater.from(getActivity()), parent);
        }

        @Override
        public void onBindViewHolder(CommentHolder holder, int position) {
            holder.bind(mComments.get(position));
        }

        @Override
        public int getItemCount() {
            return mComments.size();
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
            if(responseOk){
                Toast.makeText(getActivity(), "Successfully Deleted", Toast.LENGTH_LONG).show();
                getActivity().setResult(RESULT_OK);
                getActivity().finish();
            }else{
//                Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }


}
