package com.project.cse5326.gitnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project.cse5326.gitnote.Model.Repo;

/**
 * Created by sifang
 */

public class AddRepoFragment extends Fragment {

    private static Repo sRepo;
    private EditText mRepoName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sRepo = new Repo();
        sRepo.setName("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_add_repo, container, false);
        mRepoName = view.findViewById(R.id.edit_repo_name);

        getActivity().setTitle("Add New Repository");

        mRepoName.setText("");
        mRepoName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sRepo.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    public static Repo getRepo(){
        return sRepo;
    }

}
