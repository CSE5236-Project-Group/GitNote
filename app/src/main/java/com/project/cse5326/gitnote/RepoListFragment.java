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
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sifang
 */

public class RepoListFragment extends Fragment {

    private static final String ARG_REPOS = "repos";
    private static final int REQUEST = 0;

    public static RepoAdapter adapter;
    public static List<Repo> mRepos;

    private RecyclerView mRepoRecyclerView;
    private FloatingActionButton mAddButton;

    public static RepoListFragment newInstance(List<Repo> repos){
        Bundle args = new Bundle();
        args.putString(ARG_REPOS, ModelUtils.toString(repos, new TypeToken<List<Repo>>(){}));

        RepoListFragment fragment = new RepoListFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepos = ModelUtils.toObject(getArguments().getString(ARG_REPOS), new TypeToken<List<Repo>>(){});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mRepoRecyclerView = view.findViewById(R.id.recycler_view);
        mRepoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RepoAdapter(mRepos);
        mRepoRecyclerView.setAdapter(adapter);

        getActivity().setTitle("All Repositories");

        mAddButton = view.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddButton.show();
                Intent intent = AddRepoActivity.newIntent(getActivity());
                startActivityForResult(intent, REQUEST);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST) {
            if (resultCode == RESULT_OK) {
                List<Repo> repos = ModelUtils.toObject(data.getStringExtra("UPDATED_REPOS"), new TypeToken<List<Repo>>(){});
                Log.i("Updated_repos", ModelUtils.toString(repos, new TypeToken<List<Repo>>(){}));
                mRepos.clear();
                mRepos.addAll(repos);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public class RepoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private Repo mRepo;
        private TextView mRepoName;

        public RepoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_with_name, parent, false));
            mRepoName = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(this);
        }

        public void bind(Repo repo){
            mRepo = repo;
            mRepoName.setText(mRepo.getName());
        }

        @Override
        public void onClick(View v) {
            Intent intent = RepoShowActivity.newIntent(getActivity(),mRepo);
            startActivity(intent);

        }
    }

    public class RepoAdapter extends RecyclerView.Adapter<RepoHolder>{

        private List<Repo> mRepos;

        public RepoAdapter(List<Repo> repos){
            this.mRepos = repos;
        }

        @Override
        public RepoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RepoHolder(LayoutInflater.from(getActivity()),parent);
        }

        @Override
        public void onBindViewHolder(RepoHolder holder, int position) {
            holder.bind(mRepos.get(position));
        }

        @Override
        public int getItemCount() {
            return mRepos.size();
        }
    }

}
