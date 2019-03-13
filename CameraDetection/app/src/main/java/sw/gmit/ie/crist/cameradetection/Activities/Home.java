package sw.gmit.ie.crist.cameradetection.Activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pusher.pushnotifications.PushNotificationReceivedListener;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.auth.AuthData;
import com.pusher.pushnotifications.auth.AuthDataGetter;
import com.pusher.pushnotifications.auth.BeamsTokenProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Pattern;

import sw.gmit.ie.crist.cameradetection.R;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isSignedIn;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private FirebaseUser user;

    public boolean getSignedIn() {
        return isSignedIn;
    }

    public void setSignedIn(boolean signedIn) {
        isSignedIn = signedIn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        PushNotifications.start(getApplicationContext(), "2f23a1d1-dc77-48d8-8474-d7dda1d9ee14");
        PushNotifications.subscribe("hello");

//        BeamsTokenProvider tokenProvider = new BeamsTokenProvider(
//                "<YOUR_BEAMS_AUTH_URL_HERE>",
//                new AuthDataGetter() {
//                    @Override
//                    public AuthData getAuthData() {
//                        // Headers and URL query params your auth endpoint needs to
//                        // request a Beams Token for a given user
//                        HashMap<String, String> headers = new HashMap<>();
//                        // for example:
//                        // headers.put("Authorization", sessionToken);
//                        HashMap<String, String> queryParams = new HashMap<>();
//                        return new AuthData(
//                                headers,
//                                queryParams
//                        );
//                    }
//                }
//        );

        initVariables();

        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }
    }

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
            case R.id.nav_profile:
                // redirect to the ProfileFragment java class
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
            case R.id.nav_message:
                // redirect to the MessageFragment java class
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessageFragment()).commit();
                break;
            case R.id.nav_gallery:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GalleryFragment()).commit();
                break;
            case R.id.nav_call_911:
                showMessage("Calling 911");
                break;
            case R.id.nav_add_photo:
                choosePicture();
                break;
            case R.id.nav_take_photo:
                takePicture();
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
                this.setSignedIn(false);  // not signed in anymore
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

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);

    }

    private void downloadVideo() {
        StorageReference storageReference, ref;

        storageReference = FirebaseStorage.getInstance().getReference();
        ref = storageReference.child("images/" + user.getDisplayName() + "/unknown/Monday11March2019022140PM.avi"); // + Pattern.compile(".") + ".mp4");

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFiles(Home.this, "Folders", ".avi", Environment.DIRECTORY_MUSIC + "", url);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void downloadFiles(Context context,
                               String fileName,
                               String fileExtension,
                               String destinationDirectory,
                               String url) {

        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName + fileExtension);
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


}
