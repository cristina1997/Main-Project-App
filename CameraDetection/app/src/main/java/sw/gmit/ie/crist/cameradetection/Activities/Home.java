package sw.gmit.ie.crist.cameradetection.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.PushNotificationsInstance;
import com.pusher.pushnotifications.auth.TokenProvider;

import java.util.ArrayList;
import java.util.List;

import sw.gmit.ie.crist.cameradetection.Fragments.GalleryFragment;
import sw.gmit.ie.crist.cameradetection.Fragments.ProfileFragment;
import sw.gmit.ie.crist.cameradetection.Models.Video;
import sw.gmit.ie.crist.cameradetection.R;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NameDialog.NameDialogListener {
    // Firebase
    final private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Drawer Menu
    private NavMenu navMenu = new NavMenu();

    // Notifications
    private TokenProvider tokenProvider;
    private PushNotificationsInstance instance;

    // Video Download
    private Video video = new Video();
    private List videos = new ArrayList<>();
    private String videoPerson;

    // Boolean Instantiations
    private Signeable signeable = new Signeable();
    private DownloadableAcquaintances downloadableAcquaintances = new DownloadableAcquaintances ();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
        notifications();

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notifications() {
        String userName = user.getDisplayName();
        userName = splitAndJoin(userName);


        PushNotifications.start(getApplicationContext(), "2f23a1d1-dc77-48d8-8474-d7dda1d9ee14");
        PushNotifications.subscribe(userName);
    }

    /************************************************
                    Initialize
     ***********************************************/
    private void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);  // shows the home page at the start of the application
        setTitle("Home");
        initVariables();
        setNavHeader ();
        initNav();
        initialiseStartFragment(savedInstanceState);
    }

    /************************************************
                        Variables
     ***********************************************/
    private void initVariables(){
        navMenu.setToolbar((Toolbar) findViewById(R.id.toolbar));
        navMenu.setDrawer((DrawerLayout) findViewById(R.id.drawer_layout));
        navMenu.setNavigationView((NavigationView) findViewById(R.id.nav_view));
        navMenu.getNavigationView().setNavigationItemSelectedListener(Home.this);
        navMenu.setHeaderName((TextView) navMenu.getNavigationView().getHeaderView(0).findViewById(R.id.navUserName));
    }

    /************************************************
                Set Navigation Header
     ***********************************************/
    private void setNavHeader() {
        TextView txtProfileName = navMenu.getHeaderName();
        txtProfileName.setText(user.getDisplayName());
    }

    /************************************************
            Initialize Navigation Functionality
     ***********************************************/
    protected void initNav() {
        setSupportActionBar(navMenu.getToolbar());
        navMenu.setToggle(new ActionBarDrawerToggle(this, navMenu.getDrawer(),  navMenu.getToolbar(),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close));
        navMenu.getDrawer().addDrawerListener( navMenu.getToggle());
        navMenu.getToggle().syncState();
    }

    /************************************************
                Initialize Start Fragment
     ***********************************************/
    private void initialiseStartFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment ()).commit();
            navMenu.getNavigationView().setCheckedItem(R.id.nav_profile);
        }
    }

    /************************************************
                    Navigation Bar Menu
     ***********************************************/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment())
                        .commit();
                break;
            case R.id.nav_gallery:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new GalleryFragment ())
                        .commit();
                break;
            case R.id.nav_call_911:
                break;
            case R.id.nav_download_unknown:
                downloadableAcquaintances.setDownloadable(false);
                video.setPersonType("unknown");
                downloadVideo();
                break;
            case R.id.nav_download_acquaintances:
                downloadableAcquaintances.setDownloadable(true);
                openDialog();
                break;
            default:
                break;
        }

        navMenu.getDrawer().closeDrawer(GravityCompat.START);
        return true;
    }

    /************************************************
                Close Navigation Bar Menu
     ***********************************************/
    @Override
    public void onBackPressed() {
        if (navMenu.getDrawer().isDrawerOpen(GravityCompat.START)){
            navMenu.getDrawer().closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /************************************************
                Create Options Menu
     ***********************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.secondary_menu, menu);
        return true;
    }

    /************************************************
                    Options Menu
     ***********************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.logoutBtn) {
            signeable.getSignedIn();
            FirebaseAuth.getInstance().signOut();                               // sign out
            startActivity(new Intent(this, Login.class));         // send user back to the login page
            finish();
        }
        return true;
    }

     /************************************************
                    Video Download
    ***********************************************/
    private void downloadVideo() {
        videoPerson =  video.getPersonType();

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dataRef = database.child("videos").child(videoPerson);
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference ref;

        dataRef.addListenerForSingleValueEvent(new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    if (video.getPersonTypeUnchanged() == null){
                        showMessage("No videos of present");
                    } else {
                        showMessage("No videos of \"" + video.getPersonTypeUnchanged() + "\" present");
                    }
                } else {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren ()) {
                        video = singleSnapshot.getValue (Video.class);
                        videos.add(video.getName ());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
                showMessage ("Database Error: " + de.toException());
            }
        });



        if (videos.size() != 0) {
            for (int i = 0; i < videos.size(); i++){
                ref = storage.child("images/" + user.getDisplayName() + "/" + videoPerson +"/" + videos.get(i));
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        downloadFiles(Home.this, videoPerson, ".mp4", Environment.DIRECTORY_DOWNLOADS, url);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (video.getPersonTypeUnchanged() == null){
                            showMessage("No videos of present");
                        } else {
                            showMessage("No videos of \"" + video.getPersonTypeUnchanged() + "\" present");
                        }
                    }
                });
            }
        }
    }

    /************************************************
                     Download Files
     ***********************************************/
    private void downloadFiles(Context context, String fileName, String downloadExtension, String destinationDirectory, String url) {

        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + downloadExtension);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        downloadManager.enqueue(request);
    }

    private void openDialog() {
        NameDialog nameDialog = new NameDialog();
        nameDialog.show(getSupportFragmentManager(), "dialog");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void applyTexts(String personName){
        video.setPersonTypeUnchanged(personName);

        if (downloadableAcquaintances.getDownloadable() == true){
            video.setPersonType(splitAndJoin(personName));
            downloadVideo ();
        }
    }

    /************************************************
                    Split and Join Names
     ***********************************************/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String splitAndJoin(String personName) {

        if (personName.contains(" ")){
            personName = personName.toLowerCase();
            String[] splited = personName.trim().split("\\s+");
            String joined = String.join("-", splited);
            personName = joined;
        } else {
            personName = personName;
        }

        return personName;
    }

    /************************************************
                        Message Toast
     ***********************************************/
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}