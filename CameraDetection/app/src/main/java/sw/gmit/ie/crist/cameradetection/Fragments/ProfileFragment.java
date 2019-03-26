package sw.gmit.ie.crist.cameradetection.Fragments;

import android.Manifest;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.webkit.MimeTypeMap;
import android.widget.*;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

import sw.gmit.ie.crist.cameradetection.Enums.ImageReq;
import sw.gmit.ie.crist.cameradetection.Models.UploadPictures;
import sw.gmit.ie.crist.cameradetection.R;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    // Image Variables
    private ImageButton btnChooseImg, btnUploadImg, btnTakePic;
    protected ImageView imgView;
    private EditText imgText;
    private ProgressBar imgProgressBar;

    // Image Upload variables
    private StorageTask uploadTask;
    private String userDisplayName, personName;
    private Uri imgURI, photoURI;
    private String pathToFile;
    private UploadPictures uploadPictures = new UploadPictures();
    public EditText getImgText() {
        return imgText;
    }

    // Firebase Database Variables
    private StorageReference imageStorageRef;
    private DatabaseReference imageDatabaseRef;

    // Firebase Variables
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public void setImgText(EditText imgText) {
        this.imgText = imgText;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        getActivity().setTitle("Home");
        initVariables(rootView);

        user = mAuth.getInstance().getCurrentUser();

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        pickImage();
        return rootView;
    }

    private void initVariables(View rootView) {
        btnChooseImg = (ImageButton) rootView.findViewById(R.id.chooseImgBtn);
        btnUploadImg = (ImageButton) rootView.findViewById(R.id.uploadImgBtn);
        btnTakePic = (ImageButton) rootView.findViewById(R.id.takeImgBtn);
        imgView = (ImageView) rootView.findViewById(R.id.imageView);
        imgText = (EditText) rootView.findViewById(R.id.imgName);
        imgProgressBar = (ProgressBar) rootView.findViewById(R.id.imgProgress);
    }

    private void pickImage() {
        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTextEmpty() == false){
                    uploadPictures.setName(getImgText().getText().toString());
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, ImageReq.CHOOSE_IMAGE_REQUEST.getValue());
                }
                userFirebaseStorage();
                uploadImage();
            }
        });

        btnTakePic.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (isTextEmpty() == false){
                    uploadPictures.setName(getImgText().getText().toString());
                    personName = getImgText().getText().toString();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null){
                        File picture = createPhotoFile();


                        if (picture != null){
                            pathToFile = picture.getAbsolutePath();
                            photoURI = FileProvider.getUriForFile(getActivity().getApplicationContext(), "sw.gmit.ie.crist.cameradetection.fileprovider", picture);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(intent, ImageReq.TAKE_IMAGE_REQUEST.getValue());
                        }
                    }
                    userFirebaseStorage();
                    uploadImage();
                }
            }
        });


    }

    private File createPhotoFile() {

        String pictureFile = uploadPictures.getName();
        File storageDir = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_PICTURES); // getExternalStoragePublicDirectory
        File image = null;


        try {
            image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("mylog", "Exception: " +e.toString());
        }

        return image;
    }

    private void userFirebaseStorage(){
        userDisplayName = user.getDisplayName();
        personName = uploadPictures.getName();
        imageStorageRef = FirebaseStorage.getInstance().getReference("images/" +userDisplayName+ "/" + personName);
        imageDatabaseRef = FirebaseDatabase.getInstance().getReference("images/" +userDisplayName);
    }

     private boolean isTextEmpty() {
        if( TextUtils.isEmpty(imgText.getText())){
            imgText.setError( "Enter person's name" );
            return true;
        } else {
            setImgText(imgText);
            return false;
        }
    }

    private void uploadImage() {
        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()){
                } else {
                    uploadFile();
                }

            }
        });

    }

    private String getFileExtension(Uri uri) {
        // get extension from all files
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void uploadFile() {
        String userDisplayName = user.getDisplayName();
        final String name = uploadPictures.getName();
        final StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference("images/" +userDisplayName+ "/" + name);
        final DatabaseReference imageDatabaseRef = FirebaseDatabase.getInstance().getReference("images/" +userDisplayName);

        if (uploadTask != null && uploadTask.isInProgress()) {}
        else if (imgURI != null) {
            final StorageReference fileReference = imageStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imgURI));

            uploadTask = fileReference.putFile(imgURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //  Successful upload
                        // Code adapted from: https://stackoverflow.com/questions/50570893/after-upload-a-file-in-android-firebase-storage-how-get-the-file-download-url-g/50572357
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        imgProgressBar.setProgress(0);

                                    }
                                }, 500);

                                UploadPictures uploadPictures = new UploadPictures(imgText.getText().toString().trim(),
                                       uri.toString());
                                String uploadId = imageDatabaseRef.push().getKey();

                                imageDatabaseRef.child(uploadId).setValue(uploadPictures);
                            }
                        });
                    };
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //  Failed upload
                        showMessage("Failed to upload image");

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //  Progressing upload - update progress bar with current progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        imgProgressBar.setProgress((int) progress);
                    }
                });

        } else {
            showMessage("No file selected");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ImageReq.CHOOSE_IMAGE_REQUEST.getValue() && data != null && data.getData() != null){
                imgURI = data.getData();
                imgView.setImageURI(imgURI);
            } else if (requestCode == ImageReq.TAKE_IMAGE_REQUEST.getValue()){
                imgURI = photoURI;
                imgView.setImageURI(imgURI);
            }

        }
    }

    private void showMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
