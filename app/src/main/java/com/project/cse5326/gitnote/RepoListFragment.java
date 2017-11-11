package com.project.cse5326.gitnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Model.Repo;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import java.util.List;

/**
 * Created by sifang
 */

public class RepoListFragment extends Fragment {

    private static final String ARG_REPOS = "repos";

    private List<Repo> mRepos;

    private RecyclerView mRepoRecyclerView;

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
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mRepoRecyclerView = view.findViewById(R.id.recycler_view);
        mRepoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRepoRecyclerView.setAdapter(new RepoAdapter(mRepos));

        getActivity().setTitle("All Repositories");

        return view;
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
