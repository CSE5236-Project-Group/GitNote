package com.project.cse5326.gitnote;

import android.app.Activity;
import android.content.Intent;
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

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Comment;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

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

        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddButton.show();
                Intent intent = AddCommentActivity.newIntent(getActivity(),mRepoName,mNote.getNumber());
                startActivityForResult(intent, REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST) {
            if (resultCode == RESULT_OK) {
                List<Comment> comments = ModelUtils.toObject(data.getStringExtra("ADDED_COMMENT"), new TypeToken<List<Comment>>(){});
                mComments.clear();
                mComments.addAll(comments);
                mAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
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

}
