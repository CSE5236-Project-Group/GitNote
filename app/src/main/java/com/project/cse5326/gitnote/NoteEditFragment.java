package com.project.cse5326.gitnote;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by sifang
 */

public class NoteEditFragment extends Fragment {

    private static final String ARG_NOTE = "note";
    private static final String ARG_REPO_NAME = "repo_name";

    private Note mNote;
    private String mRepoName;
    private EditText mNoteTitle;
    private EditText mNoteBody;
    private FloatingActionButton mButtonPhoto;


    private File photoFile;
    private static final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final String JPEG_PREFIX = "IMG_";
    private static final String JPEG_SUFFIX = ".jpg";
    private static final String albumName = "CameraSample";
    private static final String CAMERA_DIR = "/dcim/";

    private FirebaseStorage storage = FirebaseStorage.
            getInstance("gs://gitnote-4706.appspot.com");
    private StorageReference storageRef = storage.getReference();
    private StorageReference imageRef;

    private String downloadUri;

    public static NoteEditFragment newInstance(Note note, String repoName){
        Bundle args = new Bundle();
        args.putString(ARG_NOTE, ModelUtils.toString(note, new TypeToken<Note>(){}));
        args.putString(ARG_REPO_NAME, repoName);

        NoteEditFragment fragment = new NoteEditFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNote = ModelUtils.toObject(getArguments().getString(ARG_NOTE), new TypeToken<Note>(){});
        mRepoName = ModelUtils.toObject(getArguments().getString(ARG_REPO_NAME), new TypeToken<String>(){});
        try {
            photoFile = createFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_note_edit, container, false);
        mNoteTitle = view.findViewById(R.id.edit_note_title);
        mNoteBody = view.findViewById(R.id.edit_note_body);

        getActivity().setTitle(mNote.getTitle());

        mNoteTitle.setText(mNote.getTitle());
        mNoteTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mNoteBody.setText(mNote.getBody());
        mNoteBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mNote.setBody(s.toString());
                mNote.setBody(s.toString() + downloadUri);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mButtonPhoto = view.findViewById(R.id.camera_button);
        mButtonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonPhoto.show();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //galleryAddPic();
            Uri file = Uri.fromFile(photoFile);
            UploadTask uploadTask = imageRef.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "upload fail", Toast.LENGTH_LONG);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri download = taskSnapshot.getDownloadUrl();
                    downloadUri = download.toString();
                }
            });
        }

    }

    private File getPhotoDir() {
        File storDirPrivate = null;
        File storDirPublic = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storDirPrivate = new File(
                    Environment.getExternalStorageDirectory()
                            + CAMERA_DIR
                            + albumName);
        }

        storDirPublic = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),
                albumName);

        if (storDirPublic != null) {
            if (!storDirPublic.mkdir()) {
                if (!storDirPublic.exists()) {
                    Log.i("camera", "FAIL to create a folder");
                    return null;
                }
            }
        } else {
            Log.i(getString(R.string.app_name),
                    "Extrrnal storage is not mounted Read / wirte");
        }

        return storDirPublic;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private File createFile() throws IOException {
        photoFile = null;

        String fileName;

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName = JPEG_PREFIX + timeStamp + "_";

        imageRef = storageRef.child(fileName);
        photoFile = File.createTempFile(fileName, JPEG_SUFFIX, getPhotoDir());

        return photoFile;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.confirm, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.confirm:
                if(mNote.getTitle().equals("")){
                    Toast.makeText(getActivity(), "Note title can not be empty", Toast.LENGTH_LONG).show();
                }else{
                    new NoteEditFragment.PatchEditedNote(mNote, mRepoName).execute();
                }
                return true;
            case android.R.id.home:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("EDITED_NOTE",ModelUtils.toString(mNote, new TypeToken<Note>(){}));
                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PatchEditedNote extends AsyncTask<String, String, String> {

        private Note mNote;
        private String mRepoName;
        private boolean responseOk;
        private String responseMessage;

        public PatchEditedNote(Note note, String repoName){
            mNote = note;
            mRepoName = repoName;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Response response = Github.patchNote(mRepoName, mNote.getTitle(), mNote.getBody(), mNote.getNumber());
                responseOk = response.isSuccessful();
                responseMessage = response.message();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            if(responseOk){
                Toast.makeText(getActivity(), "Successfully Modified", Toast.LENGTH_LONG).show();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("EDITED_NOTE",ModelUtils.toString(mNote, new TypeToken<Note>(){}));
                getActivity().setResult(Activity.RESULT_OK,returnIntent);
                getActivity().finish();
            }else{
                Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_LONG).show();
            }
        }
    }


}
