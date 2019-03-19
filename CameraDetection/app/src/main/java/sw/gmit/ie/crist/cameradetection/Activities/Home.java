package sw.gmit.ie.crist.cameradetection.Activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.PushNotificationsInstance;
import com.pusher.pushnotifications.auth.TokenProvider;

import java.util.regex.Pattern;

import sw.gmit.ie.crist.cameradetection.R;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NameDialog.NameDialogListener {
    private final ImageExtension imageExtension = new ImageExtension ();
    private Bitmap bitmap;
    private boolean isSignedIn;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private FirebaseUser user;
    private StorageTask uploadTask;
    private Uri imgURI;

    private TokenProvider tokenProvider;
    private PushNotificationsInstance instance;
    private Upload upload = new Upload();

    // Class Variables
    private Chosable chosable = new Chosable();
    private Signeable signeable = new Signeable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GalleryFragment ()).commit();
            navigationView.setCheckedItem(R.id.nav_gallery);
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
                FirebaseAuth.getInstance().signOut();       // it logs out the user
                signeable.setSignedIn(false);               // not signed in anymore
                sendToStart();                              // it redirects the user to the start page
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

    private void choosePicture(String name) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, ImageReq.CHOOSE_IMAGE_REQUEST.getValue ());

    }

    private void takePicture(String name) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, ImageReq.TAKE_IMAGE_REQUEST.getValue ());
    }

    private void userFirebaseStorage(String name){
        String userDisplayName = user.getDisplayName();
        String personName = name;
        StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference("images/" +userDisplayName+ "/" + personName);
        DatabaseReference imageDatabaseRef = FirebaseDatabase.getInstance().getReference("images/" +userDisplayName);
        uploadImage(imageStorageRef, imageDatabaseRef, name);
    }

    private void uploadImage(StorageReference imageStorageRef, DatabaseReference imageDatabaseRef, String name) {
        if (uploadTask != null && uploadTask.isInProgress()) {}
        else {uploadFile(imageStorageRef, imageDatabaseRef, name); }

    }

    private void uploadFile(StorageReference imageStorageRef, final DatabaseReference imageDatabaseRef, final String name) {

        if (imgURI != null) {
            final StorageReference fileReference = imageStorageRef.child(System.currentTimeMillis() + "." + imageExtension.getFileExtension (imgURI));

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

    private void downloadVideo() {
        StorageReference storageReference, ref;
        Pattern p = Pattern.compile(".");
        storageReference = FirebaseStorage.getInstance().getReference();
//        ref = storageReference.child("images/" + user.getDisplayName() + "/unknown/*.mp4 ."); // + Pattern.compile(".") + ".mp4");
//        ref = storageReference.child("images/" + user.getDisplayName() + "/unknown/VID_20190318_154951.mp4"); // + Pattern.compile(".") + ".mp4");
        ref = storageReference.child("images/" + user.getDisplayName() + "/unknown/"); // + Pattern.compile(".") + ".mp4");


//
//        Matcher m = p.matcher("aaaaab");
//
//        boolean b = Pattern.matches("a*b", "aaaaab");
        showMessage (ref.toString ());

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFiles(Home.this, "Folders", ".mp4", Environment.DIRECTORY_DOWNLOADS, url);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void downloadFiles(Context context,
                               String fileName,
                               String downloadExtension,
                               String destinationDirectory,
                               String url) {

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

        if (resultCode == RESULT_OK) {

            if (requestCode == ImageReq.CHOOSE_IMAGE_REQUEST.getValue()) {
                imgURI = data.getData();
                userFirebaseStorage(upload.getName());
            } else if (requestCode == ImageReq.TAKE_IMAGE_REQUEST.getValue()) {
                bitmap = (Bitmap) data.getExtras().get("data");
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
            choosePicture(upload.getName());
        } else {
            takePicture(upload.getName());
        }

    }
}
