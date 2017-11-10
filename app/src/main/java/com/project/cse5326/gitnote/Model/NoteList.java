package com.project.cse5326.gitnote.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sifang
 */

public class NoteList {

    private static NoteList sNoteList;
    private List<Note> mNotes;

    private NoteList(){
        this.mNotes = new ArrayList<>();
//            Note note = new Note();
//            note.setNumber(1);
//            note.setTitle("Note " + 1);
//            note.setUpdate_at(new Date());
//            note.setBody("This is a issue for test\n" +
//                    "An h1 header\n" +
//                    "============\n" +
//                    "\n" +
//                    "Paragraphs are separated by a blank line.\n" +
//                    "\n" +
//                    "2nd paragraph. *Italic*, **bold**, and `monospace`. Itemized lists\n" +
//                    "look like:\n" +
//                    "\n" +
//                    "  * this one\n" +
//                    "  * that one\n" +
//                    "  * the other one\n" +
//                    "\n" +
//                    "Note that --- not considering the asterisk --- the actual text\n" +
//                    "content starts at 4-columns in.\n" +
//                    "\n" +
//                    "> Block quotes are\n" +
//                    "> written like so.\n" +
//                    ">\n" +
//                    "> They can span multiple paragraphs,\n" +
//                    "> if you like.\n" +
//                    "\n" +
//                    "Use 3 dashes for an em-dash. Use 2 dashes for ranges (ex., \"it's all\n" +
//                    "in chapters 12--14\"). Three dots ... will be converted to an ellipsis.\n" +
//                    "Unicode is supported. ☺");
//            mNotes.add(note);
//            Note note1 = new Note();
//            note1.setNumber(2);
//            note1.setTitle("Note " + 2);
//            note1.setUpdate_at(new Date());
//            note1.setBody("This is another issue for test\n" +
//                        "### An h3 header ###\n" +
//                    "\n" +
//                    "Now a nested list:\n" +
//                    "\n" +
//                    " 1. First, get these ingredients:\n" +
//                    "\n" +
//                    "      * carrots\n" +
//                    "      * celery\n" +
//                    "      * lentils\n" +
//                    "\n" +
//                    " 2. Boil some water.\n" +
//                    "\n" +
//                    " 3. Dump everything in the pot and follow\n" +
//                    "    this algorithm:\n" +
//                    "\n" +
//                    "        find wooden spoon\n" +
//                    "        uncover pot\n" +
//                    "        stir\n" +
//                    "        cover pot\n" +
//                    "        balance wooden spoon precariously on pot handle\n" +
//                    "        wait 10 minutes\n" +
//                    "        goto first step (or shut off burner when done)\n" +
//                    "\n" +
//                    "    Do not bump wooden spoon or it will fall.\n" +
//                    "\n" +
//                    "Notice again how text always lines up on 4-space indents (including\n" +
//                    "that last line which continues item 3 above).\n" +
//                    "\n" +
//                    "Here's a link to [a website](http://foo.bar), to a [local\n" +
//                    "doc](local-doc.html), and to a [section heading in the current\n" +
//                    "doc](#an-h2-header).");
//            mNotes.add(note1);
//        }
    }

    public static NoteList get(){
        if(sNoteList == null){
            sNoteList = new NoteList();
        }
        return sNoteList;
    }

    public static void set(List<Note> noteList){
        if(sNoteList == null){
            sNoteList = new NoteList();
        }
        sNoteList.mNotes = noteList;

    }


    public List<Note> getNoteList(){
        return this.mNotes;
    }

    public Note getNote(int noteId){
        for(Note note : mNotes){
            if(note.getNumber() == noteId){
                return note;
            }
        }

        return null;
    }

    public void addNote(Note note){
        mNotes.add(note);
    }


}
