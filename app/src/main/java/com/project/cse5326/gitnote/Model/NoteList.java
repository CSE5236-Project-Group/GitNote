package com.project.cse5326.gitnote.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sifang on 11/4/17.
 */

public class NoteList {

    private static NoteList sNoteList;
    private List<Note> mNotes;

    private NoteList(){
        this.mNotes = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            Note note = new Note();
            note.setId(i);
            note.setTitle("Note #" + i);
            note.setDate(new Date());
            note.setBody("An h1 header\n" +
                    "============\n" +
                    "\n" +
                    "Paragraphs are separated by a blank line.\n" +
                    "\n" +
                    "2nd paragraph. *Italic*, **bold**, and `monospace`. Itemized lists\n" +
                    "look like:\n" +
                    "\n" +
                    "  * this one\n" +
                    "  * that one\n" +
                    "  * the other one\n" +
                    "\n" +
                    "Note that --- not considering the asterisk --- the actual text\n" +
                    "content starts at 4-columns in.\n" +
                    "\n" +
                    "> Block quotes are\n" +
                    "> written like so.\n" +
                    ">\n" +
                    "> They can span multiple paragraphs,\n" +
                    "> if you like.\n" +
                    "\n" +
                    "Use 3 dashes for an em-dash. Use 2 dashes for ranges (ex., \"it's all\n" +
                    "in chapters 12--14\"). Three dots ... will be converted to an ellipsis.\n" +
                    "Unicode is supported. ☺");
            mNotes.add(note);
        }
    }

    public static NoteList get(){
        if(sNoteList == null){
            sNoteList = new NoteList();
        }
        return sNoteList;
    }

    public List<Note> getNoteList(){
        return this.mNotes;
    }

    public Note getNote(int noteId){
        for(Note note : mNotes){
            if(note.getId() == noteId){
                return note;
            }
        }

        return null;
    }

    public void addNote(Note note){
        mNotes.add(note);
    }


}
