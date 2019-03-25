package sw.gmit.ie.crist.cameradetection.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.*;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.PushNotificationsInstance;
import com.pusher.pushnotifications.auth.TokenProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import sw.gmit.ie.crist.cameradetection.R;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NameDialog.NameDialogListener {
    private ImageExtension imageExtension = new ImageExtension ();
    private String pathToFile;
    private Video video = new Video();
    private List videos = new ArrayList<> ();
    private Bitmap bitmap;
    private boolean isSignedIn;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private FirebaseUser user;
    private StorageTask uploadTask;
    private Uri imgURI, photoURI;

    private TokenProvider tokenProvider;
    private PushNotificationsInstance instance;
    private Upload upload = new Upload();

    // Class Variables
    private Chosable chosable = new Chosable();
    private Signeable signeable = new Signeable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setTitle("Home");
        user = FirebaseAuth.getInstance().getCurrentUser();
        PushNotifications.start(getApplicationContext(), "2f23a1d1-dc77-48d8-8474-d7dda1d9ee14");
        PushNotifications.subscribe("hello");


        initVariables();

        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }
    }

//    public void setUserId(String userId, TokenProvider tokenProvider, BeamsCallback<Void, PusherCallbackError> callback) {
//        if (instance == null) {
//            throw new IllegalStateException("PushNotifications.start must have been called before");
//        }
//
//        instance.setUserId(userId, tokenProvider, callback);
//    }

    private void initVariables() {
        setContentView(R.layout.activity_home);  // shows the home page at the start of the application

        toolbar = findViewById(R.id.toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(Home.this);
        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navUserName);
        txtProfileName.setText(user.getDisplayName());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GalleryFragment()).commit();
                break;
            case R.id.nav_message:
                // redirect to the MessageFragment java class
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessageFragment()).commit();
                break;
            case R.id.nav_call_911:

                break;
            case R.id.nav_add_photo:
                chosable.setChosen(true);
                openDialog();
                break;
            case R.id.nav_take_photo:
                chosable.setChosen(false);
                openDialog();
                break;
            case R.id.nav_download_video:
                downloadVideo();
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.secondary_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.logoutBtn:
                FirebaseAuth.getInstance().signOut();
                signeable.setSignedIn(false);  // not signed in anymore
                sendToStart();
                break;
            case R.id.delAcc:
                showMessage("Account deletion button clicked");
                break;
            case R.id.settings:
                showMessage("Settings button clicked");
                break;
        }

        return true;
    }

    private void sendToStart(){
        Intent startIntent = new Intent(this, Login.class);
        startActivity(startIntent);
        finish();

    }

    private void openDialog() {
        NameDialog nameDialog = new NameDialog();
        nameDialog.show(getSupportFragmentManager(), "dialog");
    }

    private void choosePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, ImageReq.CHOOSE_IMAGE_REQUEST.getValue());

    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, ImageReq.TAKE_IMAGE_REQUEST.getValue());
        if (intent.resolveActivity(getPackageManager()) != null){
            File picture = createPhotoFile();
            if (picture != null){
                pathToFile = picture.getAbsolutePath();
                photoURI = FileProvider.getUriForFile(Home.this, "sw.gmit.ie.crist.cameradetection.fileprovider", picture);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, ImageReq.TAKE_IMAGE_REQUEST.getValue());
            }
        }
    }

    private File createPhotoFile() {

//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pictureFile = upload.getName(); // + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_PICTURES); // getExternalStoragePublicDirectory
        File image = null;

        try {
            image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("mylog", "Exception: " +e.toString());
        }

        return image;
    }

    public String getFileExtension(Uri uri) {
        // get extension from all files
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton ();
        return mime.getExtensionFromMimeType (cR.getType(uri));
    }

    private void choiceUpload() {
        String userDisplayName = user.getDisplayName();
        final String name = upload.getName();
        final StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference("images/" +userDisplayName+ "/" + name);
        final DatabaseReference imageDatabaseRef = FirebaseDatabase.getInstance().getReference("images/" +userDisplayName);
        if (uploadTask != null && uploadTask.isInProgress()) {}
        else {
            if (imgURI != null) {
                final StorageReference fileReference = imageStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imgURI));

                fileReference.putFile(imgURI)
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
                                            }
                                        }, 50000);

                                        Upload upload = new Upload(name.trim(), uri.toString());
//                                    showMessage("URI: " +uri.toString());
                                        String uploadId = imageDatabaseRef.push().getKey();

                                        imageDatabaseRef.child(uploadId).setValue(upload);
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
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot> () {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                //  Progressing upload - update progress bar with current progress
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            }
                        });

            } else {
                if (chosable.getChosen() == true){
                    showMessage("No file selected");
                } else {
                    showMessage("No picture taken");
                }
            }
        }

    }

    private void downloadVideo() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = database.child("videos").child("unknown");

        dataRef.addListenerForSingleValueEvent(new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    video = singleSnapshot.getValue(Video.class);
                    videos.add(video.getName());
                    video.setAllNames(videos);

//                   showMessage ("separated[0]: " + separated[0] + "\n separated[1]: " +separated[1] + "\n separated[2]" + separated[2]);
//


                }
            }
            @Override
            public void onCancelled(DatabaseError de) {
                showMessage ("Database Error: " + de.toException());
            }
        });

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference ref;


        if (videos.size() != 0) {
            for (int i = 0; i < videos.size(); i++){
                ref = storage.child("images/" + user.getDisplayName() + "/unknown/" + videos.get(i));
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        downloadFiles(Home.this, "Folders", ".mp4", Environment.DIRECTORY_DOWNLOADS, url);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("No videos present");
                    }
                });
            }
        }
    }


    private void downloadFiles(Context context, String fileName, String downloadExtension, String destinationDirectory, String url) {

        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + downloadExtension);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        downloadManager.enqueue(request);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Bitmap bitmap  = (Bitmap)data.getExtras().get("data");
//        ProfileFragment pf = new ProfileFragment();
//        pf.imgView.setImageBitmap(bitmap);
//    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == ImageReq.CHOOSE_IMAGE_REQUEST.getValue()) {
                imgURI = data.getData();
                choiceUpload();
            } else if (requestCode == ImageReq.TAKE_IMAGE_REQUEST.getValue()) {
                imgURI = photoURI;
                choiceUpload();
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void applyTexts(String personName){
        upload.setName(personName);

        if (chosable.getChosen() == true){
            choosePicture();
        } else {
            takePicture();
        }

    }
}
