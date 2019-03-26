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
import sw.gmit.ie.crist.cameradetection.Models.Profile;
import sw.gmit.ie.crist.cameradetection.Models.UploadPictures;
import sw.gmit.ie.crist.cameradetection.R;

public class ProfileFragment extends Fragment {
    // Firebase Variables
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Image Upload variables
    private UploadPictures uploadPictures = new UploadPictures();
    private Uri imgURI, photoURI;
    private StorageTask uploadTask;
    private String userDisplayName, personName, pathToFile;

    // Firebase Database Variables
    private StorageReference imageStorageRef;
    private DatabaseReference imageDatabaseRef;

    // Model
    private Profile profile = new Profile();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        init(rootView);

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        pickImage();
        return rootView;
    }

    /************************************************
                        Init
     ***********************************************/
    private void init(View rootView) {
        getActivity().setTitle("Home");
        initVariables(rootView);
    }

    /************************************************
                Initialize Variables
     ***********************************************/
    private void initVariables(View rootView) {
        profile.setBtnChooseImg((ImageButton) rootView.findViewById (R.id.chooseImgBtn));
        profile.setBtnUploadImg((ImageButton) rootView.findViewById (R.id.uploadImgBtn));
        profile.setBtnTakePic((ImageButton) rootView.findViewById (R.id.takeImgBtn));
        profile.setImgView((ImageView) rootView.findViewById (R.id.imageView));
        profile.setImgText((EditText) rootView.findViewById (R.id.imgName));
        profile.setImgProgressBar((ProgressBar) rootView.findViewById (R.id.imgProgress));
    }

    /************************************************
                        Pick Image
     ***********************************************/
    private void pickImage() {
        profile.getBtnChooseImg().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTextEmpty() == false){
                    uploadPictures.setName(profile.getImgText().getText().toString());
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, ImageReq.CHOOSE_IMAGE_REQUEST.getValue());
                }
                userFirebaseStorage();
                uploadImage();
            }
        });

        profile.getBtnTakePic().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (isTextEmpty() == false){
                    uploadPictures.setName(profile.getImgText().getText().toString());
                    personName = profile.getImgText().getText().toString();
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

    /************************************************
                Create Photo File
     ***********************************************/
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
        if( TextUtils.isEmpty(profile.getImgText().getText())){
            profile.getImgText().setError( "Enter person's name" );
            return true;
        } else {
            profile.setImgText(profile.getImgText());
            return false;
        }
    }

    private void uploadImage() {
        profile.getBtnUploadImg().setOnClickListener(new View.OnClickListener() {
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
                                        profile.getImgProgressBar().setProgress(0);

                                    }
                                }, 500);

                                UploadPictures uploadPictures = new UploadPictures(profile.getImgText().getText().toString().trim(),
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
                        profile.getImgProgressBar().setProgress((int) progress);
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
                profile.getImgView().setImageURI(imgURI);
            } else if (requestCode == ImageReq.TAKE_IMAGE_REQUEST.getValue()){
                imgURI = photoURI;
                profile.getImgView().setImageURI(imgURI);
            }

        }
    }

    private void showMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
