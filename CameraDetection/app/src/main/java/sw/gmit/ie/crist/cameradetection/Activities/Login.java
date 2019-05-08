package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sw.gmit.ie.crist.cameradetection.Models.LoginInit;
import sw.gmit.ie.crist.cameradetection.R;
import sw.gmit.ie.crist.cameradetection.Readable.Signeable;

public class Login extends AppCompatActivity {
    // Login
    private LoginInit logInit = new LoginInit ();
    private ButtonVisibility btnVisibility = new ButtonVisibility ();

    // Firebase variables
    final private FirebaseAuth mAuth = FirebaseAuth.getInstance (); // authentification
    final private FirebaseUser user = mAuth.getInstance ().getCurrentUser (); // user

    // Redirect variables
    private Intent HomeActivity, RegisterActivity;
    private Redirect redirect;

    // Class Variables
    private Signeable signeable = new Signeable ();

    @Override
    protected void onStart() {
        super.onStart ();
        // If the user is already connect then redirect him/her redirect user to the home page
        if (signeable.getSignedIn () == true && user != null) {    // or if the user does not exist
            updateHomeUI ();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        init ();                                     // general initialization - all initializations are called in this method
        registerActivity ();                         // user can register if they press the registration button
        login ();                                    // if the user isn't already connect - redirect to login page so that they can log in

    }

    /************************************************
     Init
     ***********************************************/
    private void init() {
        setContentView (R.layout.activity_login);    // it opens the login activity file
        setTitle ("Login");                          // set Activity Title
        initVariables ();                            // initialize variables
        initBtn ();                                  // initialize Login Button and Progress Bar (Visible/Invisible)
    }

    /************************************************
     Initialize Variables
     ***********************************************/
    private void initVariables() {
        logInit.setUserEmail ((EditText) findViewById (R.id.logEmail));
        logInit.setUserPass ((EditText) findViewById (R.id.regName));
        logInit.setLoginBtn ((Button) findViewById (R.id.logBtn));
        logInit.setLoginProgress ((ProgressBar) findViewById (R.id.logBar));
        logInit.setBtnRegActivity ((Button) findViewById (R.id.regActivityBtn));
    }

    /************************************************
     Init Button
     ***********************************************/
    private void initBtn() {
        btnVisibility.showBtn (logInit.getLoginBtn (), logInit.getLoginProgress ());
    }

    /************************************************
     Registration
     ***********************************************/
    private void registerActivity() {
        // user is redirected to the registration activity when the button is clicked
        logInit.getBtnRegActivity ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                updateRegisterUI ();
            }
        });
    }

    /************************************************
     Login
     ***********************************************/
    private void login() {
        logInit.getLoginBtn ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                // Stores the values from the initialized variables into strings
                final String email = logInit.getUserEmail ().getText ().toString ();
                final String password = logInit.getUserPass ().getText ().toString ();
                btnVisibility.hideBtn (logInit.getLoginBtn (), logInit.getLoginProgress ());

                // If there is no input then show a message
                if (email.isEmpty () || password.isEmpty ()) {
                    showMessage ("Please verify all fields");

                    btnVisibility.showBtn (logInit.getLoginBtn (), logInit.getLoginProgress ());
                } else {
                    // signs in the user
                    getSignIn (email, password);
                }
            }
        });

    }

    /************************************************
     Verify User Authentification
     ***********************************************/
    private void getSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword (email, password).addOnCompleteListener (new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful ()) {
                    btnVisibility.hideBtn (logInit.getLoginBtn (), logInit.getLoginProgress ());
                    updateHomeUI ();
                } else {
                    showMessage (task.getException ().getMessage ());
                    btnVisibility.showBtn (logInit.getLoginBtn (), logInit.getLoginProgress ());
                }
            }
        });
    }

    /************************************************
     Registration Redirect
     ***********************************************/
    private void updateRegisterUI() {
        RegisterActivity = new Intent (getApplicationContext (), Register.class);  // it gets the registration activity class
        startActivity (RegisterActivity);                                        // it redirects the user to the registration activity
        finish ();                                                               // finishes this activity
    }

    /************************************************
     Home Redirect
     ***********************************************/
    private void updateHomeUI() {
        HomeActivity = new Intent (getApplicationContext (), Home.class);          // it gets the home activity class
        startActivity (HomeActivity);                                            // it redirects the user to the home activity
        signeable.setSignedIn (true);                                            // user is already signed in
        finish ();                                                               // finishes this activity
    }


    /************************************************
     Toast Message Method
     ***********************************************/
    private void showMessage(String message) {
        Toast.makeText (getApplicationContext (), message, Toast.LENGTH_LONG).show ();
    }

}
