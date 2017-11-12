package com.project.cse5326.gitnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Comment;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

/**
 * Created by sifang
 */

public class NoteCommentFragment extends Fragment {

    private static final String ARG_COMMENTS = "comments";

    private String mRepoName;
    private List<Comment> mComments;
    private RecyclerView mCommentRecyclerView;

    public static NoteCommentFragment newInstance(List<Comment> comments){
        Bundle args = new Bundle();
        args.putString(ARG_COMMENTS, ModelUtils.toString(comments, new TypeToken<List<Comment>>(){}));

        NoteCommentFragment fragment = new NoteCommentFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComments = ModelUtils.toObject(getArguments().getString(ARG_COMMENTS), new TypeToken<List<Comment>>(){});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View  view = inflater.inflate(R.layout.fragment_note_comment, container, false);

        mCommentRecyclerView = (RecyclerView) view.findViewById(R.id.note_comment_recycler_view);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommentRecyclerView.setAdapter(new CommentAdapter(mComments));

        return view;
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