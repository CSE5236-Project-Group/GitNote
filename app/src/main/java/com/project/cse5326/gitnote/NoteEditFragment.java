package com.project.cse5326.gitnote;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.reflect.TypeToken;
import com.project.cse5326.gitnote.Github.Github;
import com.project.cse5326.gitnote.Model.Note;
import com.project.cse5326.gitnote.Utils.ModelUtils;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;

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
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final String JPEG_PREFIX = "IMG_";
    private static final String JPEG_SUFFIX = ".jpg";
    private static final String albumName = "GitNote";
    private static final String CAMERA_DIR = "/dcim/";
    private static final String fireBaseDir = "gitnote_image/";

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();

    private FirebaseAuth mAuth;

    private String downloadUri;
    private int targetW;
    private int targetH;
    private int permission = 0;
//
//    private FirebaseStorage storage = FirebaseStorage.
//            getInstance("gs://gitnote-4706.appspot.com");
//    private StorageReference storageRef = storage.getReference();
//    private StorageReference imageRef;

//    private String downloadUri;

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
        mRepoName = getArguments().getString(ARG_REPO_NAME);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setHasOptionsMenu(true);

    }

    @Override
    public void onStart() {
        super.onStart();

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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 5);
                } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    invokeCamera();
                }

//                if (permission == 999) {
//                    invokeCamera();
//                }
            }
        });

        return view;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            String file = saveToInternalStorage(photo);

            Uri fileUri = Uri.fromFile(new File(file));
//            Uri file = Uri.fromFile(photoFile);
            Log.i("photo", file.toString());
            StorageReference imageRef = storageRef.child(fireBaseDir + fileUri.getLastPathSegment());
            UploadTask uploadTask = imageRef.putFile(fileUri);

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
                    Log.i("download", downloadUri);
                    String text = mNote.getBody() + "![pic](" + downloadUri + ")";
                    mNoteBody.setText(text);
                }
            });
        }
    }

    private String saveToInternalStorage(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir(albumName, Context.MODE_PRIVATE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = JPEG_PREFIX + timeStamp + JPEG_SUFFIX;
        File mypath = new File(directory, fileName);
        Log.i("path", mypath.getAbsolutePath());
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mypath.getAbsolutePath();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photoFile);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void setPic() {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                permission = 999;
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }

    private void invokeCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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
