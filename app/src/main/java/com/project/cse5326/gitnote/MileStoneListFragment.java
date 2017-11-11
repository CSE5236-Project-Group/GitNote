package com.project.cse5326.gitnote;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.MileStone;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by sifang.
 */

public class MileStoneListFragment extends Fragment {

    private static final String ARG_MILESTONES = "milestones";
    private static final String ARG_REPO = "repo";

    private List<MileStone> mMileStones;
    private String mRepoName;
    private RecyclerView mRecyclerView;

    public static MileStoneListFragment newInstance(List<MileStone> mileStones, String repoName) {
        Bundle args = new Bundle();
        args.putString(ARG_MILESTONES, ModelUtils.toString(mileStones, new TypeToken<List<MileStone>>() {
        }));
        args.putString(ARG_REPO, repoName);

        MileStoneListFragment fragment = new MileStoneListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MileStones",getArguments().getString(ARG_MILESTONES));
        mMileStones = ModelUtils.toObject(getArguments().getString(ARG_MILESTONES), new TypeToken<List<MileStone>>() {
        });
        mRepoName = getArguments().getString(ARG_REPO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new MileStoneAdapter(mMileStones));

        return view;
    }

    public class MileStoneHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private MileStone mMileStone;
        private TextView mName;

        public MileStoneHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_with_name, parent, false));

            mName = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
        }

        public void bind(MileStone mileStone) {
            mMileStone = mileStone;
            mName.setText(mMileStone.title);
        }

        @Override
        public void onClick(View v) {
            List<Note> notes = null;
            try {
                notes = new FetchMileStoneNotes(mMileStone.number,mRepoName).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            Intent intent = MileStoneNoteListActivity.newIntent(getActivity(),notes);
            startActivity(intent);
        }
    }

    public class MileStoneAdapter extends RecyclerView.Adapter<MileStoneHolder> {

        private List<MileStone> mMileStones;

        public MileStoneAdapter(List<MileStone> mileStones) {
            mMileStones = mileStones;
        }


        @Override
        public MileStoneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MileStoneHolder(LayoutInflater.from(getActivity()), parent);
        }

        @Override
        public void onBindViewHolder(MileStoneHolder holder, int position) {
            holder.bind(mMileStones.get(position));
        }

        @Override
        public int getItemCount() {
            return mMileStones.size();
        }
    }

    public class FetchMileStoneNotes extends AsyncTask<String, String, List<Note>> {

        private int mMileStoneId;
        private String mRepoName;

        public FetchMileStoneNotes(int mileStoneId, String repoName){
            mMileStoneId = mileStoneId;
            mRepoName = repoName;
        }

        @Override
        protected List<Note> doInBackground(String... strings) {
            List<Note> notes = null;
            try {
                notes = Github.getNotes(mRepoName,mMileStoneId);
            } catch (GithubException e) {
                e.printStackTrace();
            }
            return notes;
        }
    }
}
