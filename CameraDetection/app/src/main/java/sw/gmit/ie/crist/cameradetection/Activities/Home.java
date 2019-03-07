package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.pusher.pushnotifications.PushNotifications;
import sw.gmit.ie.crist.cameradetection.R;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isSignedIn;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

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
        PushNotifications.start(getApplicationContext(), "2f23a1d1-dc77-48d8-8474-d7dda1d9ee14");
        PushNotifications.subscribe("hello");

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
                takePicture();
                break;
            case R.id.nav_take_photo:
                showMessage("Taking a picture");
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
                showMessage("Log out button clicked");
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

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private void takePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
