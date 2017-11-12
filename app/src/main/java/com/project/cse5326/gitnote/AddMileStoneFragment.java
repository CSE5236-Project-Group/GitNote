package com.project.cse5326.gitnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project.cse5326.gitnote.Model.MileStone;

/**
 * Created by sifang
 */

public class AddMileStoneFragment extends Fragment {

    private static MileStone sMileStone;
    private EditText mMilestoneTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sMileStone = new MileStone();
        sMileStone.title = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_add_milestone, container, false);
        mMilestoneTitle = view.findViewById(R.id.edit_ms_title);

        getActivity().setTitle("Add New MileStone");

        mMilestoneTitle.setText("");
        mMilestoneTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sMileStone.title = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    public static MileStone getMileStone(){
        return sMileStone;
    }
}

