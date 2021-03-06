package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import sw.gmit.ie.crist.cameradetection.Readable.Signeable;

public class Redirect extends AppCompatActivity {
    private Intent HomeActivity, RegisterActivity, LoginActivity;
    private Signeable signeable = new Signeable ();

    // redirects the user to the registation page
    protected void updateRegisterUI() {
        RegisterActivity = new Intent (getApplicationContext (), Register.class);  // it gets the registration activity class
        startActivity (RegisterActivity);                                        // it redirects the user to the registration activity
        finish ();                                                               // finishes this activity
    }

    // redirects the user to the home page
    protected void updateHomeUI() {
        HomeActivity = new Intent (getApplicationContext (), Home.class);          // it gets the home activity class
        startActivity (HomeActivity);                                            // it redirects the user to the home activity
        signeable.setSignedIn (true);                                                 // user is already signed in
        finish ();                                                               // finishes this activity
    }

    // redirects the user to the login page
    protected void updateLoginUI() {
        LoginActivity = new Intent (getApplicationContext (), Login.class);    // it gets the login activity class
        startActivity (LoginActivity);                                       // it redirects the user to the login activity
        finish ();                                                           // finishes this activity
    }
}
