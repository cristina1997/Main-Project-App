package sw.gmit.ie.crist.cameradetection.Activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import sw.gmit.ie.crist.cameradetection.Models.EmergencyCall;
import sw.gmit.ie.crist.cameradetection.Models.NavMenu;
import sw.gmit.ie.crist.cameradetection.Models.Video;
import sw.gmit.ie.crist.cameradetection.R;
import sw.gmit.ie.crist.cameradetection.Readable.DownloadableAcq;
import sw.gmit.ie.crist.cameradetection.Readable.Signeable;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NameDialog.NameDialogListener {
    // Emergency Call
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private EmergencyCall emergencyCall = new EmergencyCall ();

    // Firebase
    final private FirebaseUser user = FirebaseAuth.getInstance ().getCurrentUser ();

    // Drawer Menu
    private NavMenu navMenu = new NavMenu ();

    // Notifications
    private TokenProvider tokenProvider;
    private PushNotificationsInstance instance;

    // Video Download
    private Video video = new Video ();
    private List videos = new ArrayList<> ();
    private String videoPerson;

    // Boolean Instantiations
    private Signeable signeable = new Signeable ();
    private DownloadableAcq downloadableAcq = new DownloadableAcq ();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        init (savedInstanceState); // initializes all the necessary variables
        notifications (); // it allows notification receival from the server

        // If the sdk version of the android is bigger or equal to 23
        // then the user receives a permission request on whether or not
        // they allow the use of the camera in this application.
        // It only occurs once
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions (new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }


    /************************************************
     Notifications
     ***********************************************/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notifications() {
        String userName = splitAndJoin (user.getDisplayName ()); // gets the name of the current user of the application
        PushNotifications.start (getApplicationContext (),
                "2f23a1d1-dc77-48d8-8474-d7dda1d9ee14"); // it gets the ID from the server to connect it to this application
        PushNotifications.subscribe (userName); // if both the name of this user and the name of the server's user match
        // then the user gets a notification
//        PushNotifications.subscribe ("interest");
    }


    /************************************************
     Init
     ***********************************************/
    private void init(Bundle savedInstanceState) {
        setContentView (R.layout.activity_home);  // shows the home page at the start of the application
        setTitle ("Home"); // sets the title "Home"
        initVariables (); // It takes the XML variable values and initializes them to java variables
        setNavHeader (); // it sets the name of the navigation header as the user's name
        initNav (); // it sets up the variables for the navigation header
        initialiseStartFragment (savedInstanceState);
    }


    /************************************************
     Initialize Variables
     ***********************************************/
    private void initVariables() {
        navMenu.setToolbar ((Toolbar) findViewById (R.id.toolbar));
        navMenu.setDrawer ((DrawerLayout) findViewById (R.id.drawer_layout));
        navMenu.setNavigationView ((NavigationView) findViewById (R.id.nav_view));
        navMenu.getNavigationView ().setNavigationItemSelectedListener (Home.this);
        navMenu.setHeaderName ((TextView) navMenu.getNavigationView ().getHeaderView (0).findViewById (R.id.navUserName));
    }


    /************************************************
     Set Navigation Header
     ***********************************************/
    private void setNavHeader() {
        TextView txtProfileName = navMenu.getHeaderName ();
        txtProfileName.setText (user.getDisplayName ());
    }


    /************************************************
     Initialize Navigation Functionality
     ***********************************************/
    protected void initNav() {
        setSupportActionBar (navMenu.getToolbar ());
        navMenu.setToggle (new ActionBarDrawerToggle (this, navMenu.getDrawer (), navMenu.getToolbar (),
                R.string.navigation_drawer_open, R.string.navigation_drawer_close));
        navMenu.getDrawer ().addDrawerListener (navMenu.getToggle ());
        navMenu.getToggle ().syncState ();
    }


    /************************************************
     Initialize Start Fragment
     ***********************************************/
    private void initialiseStartFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager ().beginTransaction ().replace (R.id.fragment_container, new ProfileFragment ()).commit ();
            navMenu.getNavigationView ().setCheckedItem (R.id.nav_profile);
        }
    }


    /************************************************
     Request permission to use call
     ***********************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callPhone ();
                }
            }
        }
    }


    /************************************************
     Make a 911 Phone Call
     ***********************************************/
    private void callPhone() {
        // The user receives a permission request on whether or not
        // they allow the use of the calling feature in this application
        // and if they do, the application will call 911 automatically .
        // Permission request is only asked once per mobile app.
        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity (emergencyCall.getIntent ());
        }
    }


    /************************************************
     Navigation Bar Menu
     ***********************************************/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // get the item id from the navigation bar
        switch (item.getItemId ()) {
            case R.id.nav_profile:
                // navigate to the profile
                getSupportFragmentManager ()
                        .beginTransaction ()
                        .replace (R.id.fragment_container, new ProfileFragment ())
                        .commit ();
                break;
            case R.id.nav_gallery:
                // navigate to the gallery
                getSupportFragmentManager ()
                        .beginTransaction ()
                        .replace (R.id.fragment_container, new GalleryFragment ())
                        .commit ();
                break;
            case R.id.nav_call_911:
                // check if the user allowed the use of automatic call with the app
                int permissionCheck = ContextCompat.checkSelfPermission (this, Manifest.permission.CALL_PHONE);

                // if the permission was not granted,
                // then the permission must be requested again for the future
                // otherwise call 911 via the phone
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    callPhone ();
                }
                break;
            case R.id.nav_download_unknown:
                // download the videos of unknown people
                downloadableAcq.setDownloadable (false); // this is not an acquaintance to be downloaded (so it's set to false)
                video.setPersonType ("unknown"); // set the name of the folder from firebase
                // from where the videos are downloaded as "unknown"
                downloadVideo (); // download the video
                break;
            case R.id.nav_download_acquaintances:
                // download the videos of the known person requested
                downloadableAcq.setDownloadable (true); // this is an acquaintance to be downloaded (so it's set to true)
                openDialog (); // open the input dialog
                // to allow the user to input the name of the person
                break;
            default:
                break;
        }

        navMenu.getDrawer ().closeDrawer (GravityCompat.START); // close the navigation drawer of it is clicked on and it is already open
        return true;
    }


    /************************************************
     Close Navigation Bar Menu
     ***********************************************/
    @Override
    public void onBackPressed() {
        // If the navigation bar is clicked
        // and the drawer is already open, then close it
        // otherwise open the drawer
        if (navMenu.getDrawer ().isDrawerOpen (GravityCompat.START)) {
            navMenu.getDrawer ().closeDrawer (GravityCompat.START);
        } else {
            super.onBackPressed ();
        }
    }


    /************************************************
     Create Options Menu
     ***********************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu (menu);
        // it creates the options menu for the log out that comes from the XML id secondary_menu
        getMenuInflater ().inflate (R.menu.secondary_menu, menu);
        return true;
    }


    /************************************************
     Options Menu
     ***********************************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected (item);
        // if the logout button is pressed, then log out the user
        if (item.getItemId () == R.id.logoutBtn) {

            signeable.getSignedIn (); // set the value for signed in
            FirebaseAuth.getInstance ().signOut ();                               // sign out
            startActivity (new Intent (this, Login.class));         // send user back to the login page
            finish ();
        }
        return true;
    }


    /************************************************
     Video Download
     ***********************************************/
    private void downloadVideo() {
        videoPerson = video.getPersonType (); // get the type of the person (unknown or the name of the person if known)

        final DatabaseReference database = FirebaseDatabase.getInstance ().getReference (); // create the database for the vieos
        DatabaseReference dataRef = database.child ("videos").child (videoPerson);  // it gets the firebase database for the "video" child
        // and the "videoPerson" represents the name of the person inputted in the dialog box
        // which represents the folder in which the videos of that person's name are
        StorageReference storage = FirebaseStorage.getInstance ().getReference (); // a reference for the firebase storage
        StorageReference ref;

        dataRef.addListenerForSingleValueEvent (new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue () == null) {
                    if (video.getPersonTypeUnchanged () == null) {
                        showMessage ("No videos of present");
                    } else {
                        showMessage ("No videos of \"" + video.getPersonTypeUnchanged () + "\" present");
                    }
                } else {
                    // get the children (videos) from the folder in which the videos are and loop through them
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren ()) {
                        video = singleSnapshot.getValue (Video.class); // get the value of each video
                        videos.add (video.getName ()); // add the value of each video to a list
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
                showMessage ("Database Error: " + de.toException ());
            }
        });


        if (videos.size () != 0) {
            // if there are videos present loop through them and download them
            for (int i = 0; i < videos.size (); i++) {
                ref = storage.child ("images/" + user.getDisplayName () + "/" + videoPerson + "/" + videos.get (i)); // get the folder in which the videos are stored
                // loop through those videos and add them all to a reference
                ref.getDownloadUrl ().addOnSuccessListener (new OnSuccessListener<Uri> () {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString (); // get the url of the videos referenced
                        // Set the name of those files
                        // by getting the name of the person chosen to be downloaded
                        // followed by .mp4
                        // setting the directory folder (downloads) to be downloaded in
                        // and getting their url where to download them from
                        downloadFiles (Home.this, videoPerson, ".mp4", Environment.DIRECTORY_DOWNLOADS, url);
                    }

                }).addOnFailureListener (new OnFailureListener () {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (video.getPersonTypeUnchanged () == null) {
                            showMessage ("No videos of present");
                        } else {
                            showMessage ("No videos of \"" + video.getPersonTypeUnchanged () + "\" present");
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
        // get the current activity and use it to download the videos
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService (Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse (url); // get the uri of the video
        DownloadManager.Request request = new DownloadManager.Request (uri); // request a download of the videos from that uri

        // download the videos based on the context, destination directory and the filename and extension
        request.setDestinationInExternalFilesDir (context, destinationDirectory, fileName + downloadExtension);
        // set a notification for the downloads
        request.setNotificationVisibility (DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // put them on queue
        downloadManager.enqueue (request);
    }


    /************************************************
     Open Input Dialog
     ***********************************************/
    private void openDialog() {
        NameDialog nameDialog = new NameDialog (); // get the name of the dialog
        nameDialog.show (getSupportFragmentManager (), "dialog"); // show the dialog
    }


    /************************************************
     Input Dialog Text
     ***********************************************/
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void applyTexts(String personName) {
        video.setPersonTypeUnchanged (personName); // set the unchanged type (name of the person) to the original

        // if it is an acquintance that needs to be downloaded
        // then first split and join the name
        // set that name to the person's type
        // and then download
        if (downloadableAcq.getDownloadable () == true) {
            video.setPersonType (splitAndJoin (personName));
            downloadVideo ();
        }
    }


    /************************************************
     Split and Join Names
     ***********************************************/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String splitAndJoin(String personName) {

        // If the person's full name contains a space
        // then put a "-" instead of that space
        // otherwise, just return the original person's name
        if (personName.contains (" ")) {
            personName = personName.toLowerCase ();
            String[] splited = personName.trim ().split ("\\s+"); // split the full name name based on space
            String joined = String.join ("-", splited); // join the names with a "-"
            personName = joined; // let the original name be equal to the joined name with a "-"
        } else {
            personName = personName;
        }

        return personName;
    }


    /************************************************
     Message Toast
     ***********************************************/
    private void showMessage(String message) {
        Toast.makeText (getApplicationContext (), message, Toast.LENGTH_LONG).show ();
    }
}