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
import sw.gmit.ie.crist.cameradetection.Models.ProfileInit;
import sw.gmit.ie.crist.cameradetection.Models.UploadPictures;
import sw.gmit.ie.crist.cameradetection.R;

public class ProfileFragment extends Fragment {

    // Image Upload variables
    private UploadPictures uploadPictures = new UploadPictures ();
    private Uri imgURI, photoURI;
    private StorageTask uploadTask;
    private String userDisplayName, personName, pathToFile;

    // Firebase Database Variables
    final private FirebaseUser user = FirebaseAuth.getInstance ().getCurrentUser ();
    private StorageReference imageStorageRef; // Firebase file storage
    private DatabaseReference imageDatabaseRef; // Firebase real time database storage

    // Model
    private ProfileInit profileInit = new ProfileInit ();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.fragment_profile, container, false);
        init (rootView); // initializes all the necessary variables

        // If the sdk version of the android is bigger or equal to 23
        // then the user receives a permission request on whether or not
        // they allow the use of the camera in this application.
        // Permission request is only asked once per mobile app.
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions (new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        pickImage ();
        return rootView;
    }


    /************************************************
     Init
     ***********************************************/
    private void init(View rootView) {
        getActivity ().setTitle ("Home"); // sets the name of the page as "Home"
        initVariables (rootView); // It takes the XML variable values and initializes them to java variables
    }


    /************************************************
     Initialize Variables
     ***********************************************/
    private void initVariables(View rootView) {
        profileInit.setBtnChooseImg ((ImageButton) rootView.findViewById (R.id.chooseImgBtn));
        profileInit.setBtnUploadImg ((ImageButton) rootView.findViewById (R.id.uploadImgBtn));
        profileInit.setBtnTakePic ((ImageButton) rootView.findViewById (R.id.takeImgBtn));
        profileInit.setImgView ((ImageView) rootView.findViewById (R.id.imageView));
        profileInit.setImgText ((EditText) rootView.findViewById (R.id.imgName));
        profileInit.setImgProgressBar ((ProgressBar) rootView.findViewById (R.id.imgProgress));
    }


    /************************************************
     Pick Image
     ***********************************************/
    private void pickImage() {
        // Choose an image (Gallery)
        profileInit.getBtnChooseImg ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // If the text input is not empty then allow the user to choose a photo
                if (isTextEmpty () == false) {
                    uploadPictures.setName (profileInit.getImgText ().getText ().toString ()); // it gets the image text
                    Intent intent = new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // sets the action as pick (Gallery)
                    intent.setType ("image/*"); // it sents you to the image/gallery folder in the phone
                    startActivityForResult (intent, ImageReq.CHOOSE_IMAGE_REQUEST.getValue ()); // starts the activity to choose an image
                }
                userFirebaseStorage (); // sets up the firebase storage folders
                uploadImage (); // uploads the image
            }
        });

        // Take a photo (Camera)
        profileInit.getBtnTakePic ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // If the text input is not empty then allow the user to take a photo
                if (isTextEmpty () == false) {
                    uploadPictures.setName (profileInit.getImgText ().getText ().toString ()); // it gets the image text
                    personName = profileInit.getImgText ().getText ().toString ();
                    Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE); // sets the action as capture (Camera)
                    // if the intent package manager is not null then allow the user to take a photo
                    if (intent.resolveActivity (getActivity ().getPackageManager ()) != null) {
                        File picture = createPhotoFile (); // gets the name of the picture

                        if (picture != null) {
                            pathToFile = picture.getAbsolutePath ();
                            photoURI = FileProvider.getUriForFile (getActivity ().getApplicationContext (),
                                    "sw.gmit.ie.crist.cameradetection.fileprovider",
                                    picture); // gets the uri of the photo using fileprovider
                            intent.putExtra (MediaStore.EXTRA_OUTPUT, photoURI); // puts the uri as an extra output onto the intent
                            startActivityForResult (intent, ImageReq.TAKE_IMAGE_REQUEST.getValue ()); // starts activity to take a photo
                        }
                    }
                    userFirebaseStorage (); // sets up the firebase storage folders
                    uploadImage (); // uploads the image
                }
            }
        });


    }


    /************************************************
     Create Photo File
     ***********************************************/
    private File createPhotoFile() {
        String pictureFile = uploadPictures.getName (); // get the name of the picture
        File storageDir = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_PICTURES); // get the mobile directory with the saved image in it
        File image = null; // initializes the file/image

        try {
            image = File.createTempFile (pictureFile, ".jpg", storageDir); // sets a name to the image (<Image Input text>.jpg)
        } catch (IOException e) {
            Log.d ("mylog", "Exception: " + e.toString ()); // it gives out an exception
        }

        return image; // returns the name of the image
    }


    /************************************************
     User Firebase Storage
     ***********************************************/
    private void userFirebaseStorage() {
        userDisplayName = user.getDisplayName (); // get the user display name
        personName = uploadPictures.getName (); // get the name of the person in the picture
        imageStorageRef = FirebaseStorage.getInstance ().getReference ("images/" + userDisplayName + "/" + personName); // folder arrangement in firebase storage
        imageDatabaseRef = FirebaseDatabase.getInstance ().getReference ("images/" + userDisplayName);  // folder arrangement in firebase database
    }


    /************************************************
     Empty Input Text Boolean
     ***********************************************/
    private boolean isTextEmpty() {
        // If the text input is empty, then show an error
        // otherwise get the text from the input
        // and set it as the Image Text
        // which is the name of the person in the picture (used for sorting out folders in the firebase storage)
        if (TextUtils.isEmpty (profileInit.getImgText ().getText ())) {
            profileInit.getImgText ().setError ("Enter person's name"); // it gives out an error of there is no input
            return true;
        } else {
            profileInit.setImgText (profileInit.getImgText ());
            return false;
        }
    }


    /************************************************
     Upload Image
     ***********************************************/
    private void uploadImage() {
        // Gets the upload image button and
        // do nothing if the upload task is still in progress
        // otherwise upload the image
        profileInit.getBtnUploadImg ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress ()) {
                } else if (imgURI != null) {
                    uploadFile ();
                } else {
                    showMessage ("No file selected");
                }
            }
        });

    }


    /************************************************
     File Extensions
     ***********************************************/
    private String getFileExtension(Uri uri) {
        // get extension from all files
        ContentResolver cR = getActivity ().getContentResolver ();
        MimeTypeMap mime = MimeTypeMap.getSingleton ();
        return mime.getExtensionFromMimeType (cR.getType (uri));
    }


    /************************************************
     Upload File
     ***********************************************/
    private void uploadFile() {
        // create a file reference for the image uri
        final StorageReference fileReference = imageStorageRef.child (System.currentTimeMillis () + "." + getFileExtension (imgURI));

        uploadTask = fileReference.putFile (imgURI)
                .addOnSuccessListener (new OnSuccessListener<UploadTask.TaskSnapshot> () {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //  Successful upload
                        // Code adapted from: https://stackoverflow.com/questions/50570893/after-upload-a-file-in-android-firebase-storage-how-get-the-file-download-url-g/50572357
                        fileReference.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
                            @Override
                            public void onSuccess(Uri uri) {
                                Handler handler = new Handler ();
                                handler.postDelayed (new Runnable () {
                                    @Override
                                    public void run() {
                                        profileInit.getImgProgressBar ().setProgress (0);
                                    }
                                }, 500);

                                // set the name and uri of the picture in the constructor from the upload pictures model
                                UploadPictures uploadPictures = new UploadPictures (profileInit.getImgText ().getText ().toString ().trim (),
                                        uri.toString ());
                                // create an ID for each picture
                                String uploadId = imageDatabaseRef.push ().getKey ();
                                // upload each picture to the database under its own generated ID
                                imageDatabaseRef.child (uploadId).setValue (uploadPictures);
                            }
                        });
                    }

                    ;
                })
                .addOnFailureListener (new OnFailureListener () {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //  Failed upload
                        showMessage ("Failed to upload image");

                    }
                })
                .addOnProgressListener (new OnProgressListener<UploadTask.TaskSnapshot> () {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //  Progressing upload - update progress bar with current progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred () / taskSnapshot.getTotalByteCount ());
                        profileInit.getImgProgressBar ().setProgress ((int) progress);
                    }
                });


    }


    /************************************************
     Activity Result
     ***********************************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == ImageReq.CHOOSE_IMAGE_REQUEST.getValue () && data != null && data.getData () != null) {
                imgURI = data.getData (); // gets the uri
                profileInit.getImgView ().setImageURI (imgURI); // sets image uri
            } else if (requestCode == ImageReq.TAKE_IMAGE_REQUEST.getValue ()) {
                imgURI = photoURI; // gets the uri
                profileInit.getImgView ().setImageURI (imgURI); // sets image uri
            }

        }
    }


    /************************************************
     Toast Message Method
     ***********************************************/
    private void showMessage(String message) {
        Toast.makeText (getActivity ().getApplicationContext (), message, Toast.LENGTH_SHORT).show ();
    }
}
