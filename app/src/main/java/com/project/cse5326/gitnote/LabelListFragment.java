package com.project.cse5326.gitnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Label;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sifang on 11/13/17.
 */

public class LabelListFragment extends Fragment {

    private static String ARG_REPO_NAME = "repo_name";
    private static String ARG_LABELS = "labels";
    private static int REQUEST_ADD = 0;
    private static int REQUEST_DELETE = 1;

    public static List<Label> mLabels;
    private String mRepoName;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddButton;
    private LabelAdapter mAdapter;
    private int viewPos;

    public static LabelListFragment newInstance(List<Label> labels, String repoName){
        Bundle args = new Bundle();
        args.putString(ARG_REPO_NAME, repoName);
        args.putString(ARG_LABELS, ModelUtils.toString(labels, new TypeToken<List<Label>>(){}));

        LabelListFragment fragment = new LabelListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepoName = getArguments().getString(ARG_REPO_NAME);
        mLabels = ModelUtils.toObject(getArguments().getString(ARG_LABELS), new TypeToken<List<Label>>(){});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new LabelAdapter(mLabels);
        mRecyclerView.setAdapter(mAdapter);

        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddButton.show();
                Intent intent = AddLabelActivity.newIntent(getActivity(), mRepoName);
                startActivityForResult(intent, REQUEST_ADD);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD) {
            if (resultCode == RESULT_OK) {
                List<Label> labels = ModelUtils.toObject(data.getStringExtra("UPDATED_LABELS"), new TypeToken<List<Label>>(){});
                mLabels.clear();
                mLabels.addAll(labels);
                mAdapter.notifyDataSetChanged();
            }
        }else if(requestCode == REQUEST_DELETE){
            if (resultCode == RESULT_OK) {
                boolean delete = data.getBooleanExtra("DELETE",false);
                if(delete == true){
                    mLabels.remove(viewPos);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public class LabelHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private Label mLabel;
        private TextView mName;

        public LabelHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_with_name, parent,false));

            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        public void bind(Label label){
            mLabel = label;
            mName.setText(mLabel.name);
        }

        @Override
        public void onClick(View v) {
            viewPos = mLabels.indexOf(mLabel);
            List<Note> notes = null;
            try {
                notes = new FetchLabelNotes(mRepoName,mLabel.name).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Intent intent = LabelNoteListActivity.newIntent(getActivity(),mRepoName,mLabel.name,notes);
            startActivityForResult(intent, REQUEST_DELETE);
        }
    }

    public class LabelAdapter extends RecyclerView.Adapter<LabelHolder> {

        private List<Label> mLabels;

        public LabelAdapter(List<Label> labels){
            mLabels = labels;
        }

        @Override
        public LabelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new LabelHolder(LayoutInflater.from(getActivity()),parent);
        }

        @Override
        public void onBindViewHolder(LabelHolder holder, int position) {
            holder.bind(mLabels.get(position));
        }

        @Override
        public int getItemCount() {
            return mLabels.size();
        }
    }


}