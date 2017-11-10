package com.project.cse5326.gitnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.cse5326.gitnote.Model.Repo;

/**
 * Created by sifang
 */

public class RepoAllListFragment extends Fragment {

    private Toolbar mToolbar;
    private RecyclerView mRepoRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_repo_all_list, container, false);
        mToolbar = view.findViewById(R.id.main_toolbar);
        mRepoRecyclerView = view.findViewById(R.id.repo_all_recycler_view);
        mRepoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public class RepoHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private Repo mRepo;
        private TextView mRepoName;

        public RepoHolder(View itemView) {
            super(itemView);
            mRepoName = itemView.findViewById(R.id.repo_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public class RepoAdapter extends RecyclerView.Adapter<RepoHolder>{



        @Override
        public RepoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RepoHolder holder, int position) {

        }


        @Override
        public int getItemCount() {
            return 0;
        }
    }

}
