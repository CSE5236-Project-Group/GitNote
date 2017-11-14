package com.project.cse5326.gitnote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.project.cse5326.gitnote.Model.Label;

import java.util.Random;

/**
 * Created by sifang on 11/13/17.
 */

public class AddLabelFragment extends Fragment {

    private static Label sLabel;
    private EditText mLabelName;
    private String mColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sLabel = new Label();
        sLabel.name = "";
        sLabel.color = generateColor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_add_label, container, false);
        mLabelName = view.findViewById(R.id.edit_label_name);

        getActivity().setTitle("Add New Label");

        mLabelName.setText("");
        mLabelName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sLabel.name = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    public static Label getLabel(){
        return sLabel;
    }

    private static String generateColor() {
        int newColor = new Random().nextInt(0x1000000);
        return String.format("%06X", newColor);
    }


}
