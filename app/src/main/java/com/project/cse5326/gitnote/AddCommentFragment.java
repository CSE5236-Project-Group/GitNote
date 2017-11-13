package com.project.cse5326.gitnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project.cse5326.gitnote.Model.Comment;

/**
 * Created by sifang
 */

public class AddCommentFragment extends Fragment {

    private static Comment sComment;
    private EditText mCommentBody;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sComment = new Comment();
        sComment.body = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_add_comment, container, false);
        mCommentBody = view.findViewById(R.id.edit_comment_body);

        getActivity().setTitle("Add New Comment");

        mCommentBody.setText("");
        mCommentBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sComment.body = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    public static Comment getComment(){
        return sComment;
    }
}
