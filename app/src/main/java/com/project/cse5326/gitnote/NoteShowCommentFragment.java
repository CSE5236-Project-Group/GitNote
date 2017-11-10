package com.project.cse5326.gitnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

/**
 * Created by sifang
 */

public class NoteShowCommentFragment extends Fragment {

    private static final String ARG_NOTE = "note";

    private RecyclerView mCommentRecyclerView;

    public static NoteShowCommentFragment newInstance(Note note){
        Bundle args = new Bundle();
        args.putString(ARG_NOTE, ModelUtils.toString(note, new TypeToken<Note>(){}));

        NoteShowCommentFragment fragment = new NoteShowCommentFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View  view = inflater.inflate(R.layout.fragment_note_comment, container, false);

        mCommentRecyclerView = (RecyclerView) view.findViewById(R.id.note_comment_recycler_view);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public class CommentHolder extends RecyclerView.ViewHolder{

        public CommentHolder(View itemView) {
            super(itemView);
        }
    }







}
