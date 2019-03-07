package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import static android.app.Activity.RESULT_OK;

import sw.gmit.ie.crist.cameradetection.R;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnChooseImg, btnUploadImg;
    private ImageView imgView;
    private EditText imgText;
    private ProgressBar imgProgressBar;
    private FirebaseUser user;
    private String userDisplayName, personName;
    private StorageTask uploadTask;
    private FirebaseAuth mAuth;
    public EditText getImgText() {
        return imgText;
    }

    public void setImgText(EditText imgText) {
        this.imgText = imgText;
    }

    private Uri imgURI;

    private StorageReference imageStorageRef;
    private DatabaseReference imageDatabaseRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initVariables(rootView);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        // verify if the User's name was added to the database
//        if (user.getDisplayName() == null){
//            showMessage("User Name is null");
//        } else {
//            showMessage(user.getDisplayName());
//        }


//        imageStorageRef = FirebaseStorage.getInstance().getReference("images/");
//        imageDatabaseRef = FirebaseDatabase.getInstance().getReference("images/");

        pickImage();
        uploadImage();
        return rootView;
    }

    private void initVariables(View rootView) {
        btnChooseImg = (Button) rootView.findViewById(R.id.chooseImgBtn);
        btnUploadImg = (Button) rootView.findViewById(R.id.uploadBtn);
        imgView = (ImageView) rootView.findViewById(R.id.imageView);
        imgText = (EditText) rootView.findViewById(R.id.imgName);
        imgProgressBar = (ProgressBar) rootView.findViewById(R.id.imgProgress);
    }

    private void pickImage() {
        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTextEmpty();

                if (isTextEmpty() == false){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }
                userFirebaseStorage();
            }
        });


    }

    private void userFirebaseStorage(){
        userDisplayName = user.getDisplayName();
        String personName = getImgText().getText().toString();
        imageStorageRef = FirebaseStorage.getInstance().getReference("images/" +userDisplayName+ "/" + personName);
        imageDatabaseRef = FirebaseDatabase.getInstance().getReference("images/" +userDisplayName+ "/" + personName);
//        imageDatabaseRef = FirebaseDatabase.getInstance().getReference("images");
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
                    showMessage("Upload in progress");
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
        if (imgURI != null) {
            StorageReference fileReference = imageStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imgURI));

            uploadTask = fileReference.putFile(imgURI)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //  Successful upload
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imgProgressBar.setProgress(0);

                            }
                        }, 500);

                        showMessage("Upload successful");
                        Upload upload = new Upload(imgText.getText().toString().trim(),
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                        String uploadId = imageDatabaseRef.push().getKey();

                        imageDatabaseRef.child(uploadId).setValue(upload);
                    }
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imgURI = data.getData();

//            Picasso.with(this).load(imgURI).into(imgView);
            imgView.setImageURI(imgURI);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
