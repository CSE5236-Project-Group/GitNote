package com.project.cse5326.gitnote;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Github.GithubException;
import com.project.cse5326.gitnote.Model.MileStone;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sifang.
 */

public class MileStoneListFragment extends Fragment {

    private static final String ARG_MILESTONES = "milestones";
    private static final String CURRENT_MILESTONES = "current_milestones";
    private static final String ARG_REPO = "repo";
    private static int REQUEST_ADD = 0;
    private static int REQUEST_DELETE = 1;
    private static int viewedPos;

    private List<MileStone> mMileStones;
    private String mRepoName;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mAddButton;
    public static MileStoneAdapter adapter;

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
        mMileStones = ModelUtils.toObject(getArguments().getString(ARG_MILESTONES), new TypeToken<List<MileStone>>() {
        });
        if(savedInstanceState != null){
            mMileStones = ModelUtils.toObject(savedInstanceState.getString(CURRENT_MILESTONES), new TypeToken<List<MileStone>>(){});
        }
        mRepoName = getArguments().getString(ARG_REPO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MileStoneAdapter(mMileStones);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.spacing_small)));

        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ModelUtils.hasNetworkConnection(getActivity())){
                    mAddButton.show();
                    Intent intent = AddMileStoneActivity.newIntent(getActivity(), mRepoName);
                    startActivityForResult(intent, REQUEST_ADD);
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
        savedInstanceState.putString(CURRENT_MILESTONES, ModelUtils.toString(mMileStones, new TypeToken<List<MileStone>>(){}));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD) {
            if (resultCode == RESULT_OK) {
                MileStone mileStone = ModelUtils.toObject(data.getStringExtra("UPDATED_MSS"), new TypeToken<MileStone>(){});
                mMileStones.add(0,mileStone);
                adapter.notifyDataSetChanged();
            }
        }else if(requestCode == REQUEST_DELETE){
            if (resultCode == RESULT_OK) {
                boolean delete = data.getBooleanExtra("DELETE",false);
                if(delete == true){
                    mMileStones.remove(viewedPos);
                    adapter.notifyDataSetChanged();
                }
            }
        }
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
            if(ModelUtils.hasNetworkConnection(getActivity())){
                viewedPos = mMileStones.indexOf(mMileStone);
                List<Note> notes = null;
                try {
                    notes = new FetchMileStoneNotes(mMileStone.number,mRepoName).execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Intent intent = MileStoneNoteListActivity.newIntent(getActivity(),notes,mRepoName, mMileStone);
                startActivityForResult(intent, REQUEST_DELETE);
            }else{
                Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
            }
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
        protected void onPreExecute(){
            if(!ModelUtils.hasNetworkConnection(getActivity())){
                Toast.makeText(getActivity(), "No Network Connection!", Toast.LENGTH_LONG).show();
                this.cancel(true);
            }
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
