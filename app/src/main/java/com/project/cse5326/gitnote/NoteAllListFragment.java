package com.project.cse5326.gitnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Model.NoteList;

import java.util.List;

/**
 * Created by sifang
 */

public class NoteAllListFragment extends Fragment{

    private static final int previewLength = 20;

    private RecyclerView mNoteRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_note_all_list, container, false);

        mNoteRecyclerView = view.findViewById(R.id.note_all_recycler_view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    private void updateUI(){
        NoteList noteList = NoteList.get();
        mNoteRecyclerView.setAdapter(new NoteAdapter(noteList.getNoteList()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_note:
                Note note = new Note();
                NoteList.get().addNote(note);
                Intent intent = NoteShowActivity.newIntent(getActivity(), note.getNumber());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public class NoteHolder extends RecyclerView.ViewHolder
                    implements View.OnClickListener{

        private Note mNote;
        private TextView mNoteTitle;
        private TextView mNoteDate;
        private TextView mNoteBodyPreview;

        public NoteHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_note, parent, false));

            mNoteTitle = itemView.findViewById(R.id.note_title);
            mNoteDate = itemView.findViewById(R.id.note_date);
            mNoteBodyPreview = itemView.findViewById(R.id.note_body_preview);

            itemView.setOnClickListener(this);
        }

        public void bind(Note note){
            mNote = note;
            mNoteTitle.setText(note.getTitle());
            mNoteDate.setText(note.getUpdate_at());
            mNoteBodyPreview.setText(note.getBody());
        }

        @Override
        public void onClick(View v) {
            Intent intent = NoteShowActivity.newIntent(getActivity(),mNote.getNumber());
            startActivity(intent);
        }
    }

    public class NoteAdapter extends RecyclerView.Adapter<NoteHolder>{

        private List<Note> mNotes;

        public NoteAdapter(List<Note> notes){
            this.mNotes = notes;
        }


        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NoteHolder(LayoutInflater.from(getActivity()), parent);
        }

        @Override
        public void onBindViewHolder(NoteHolder holder, int position) {
            holder.bind(mNotes.get(position));
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }
    }

}
