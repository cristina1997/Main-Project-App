package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import sw.gmit.ie.crist.cameradetection.Models.RegistrationInit;
import sw.gmit.ie.crist.cameradetection.Helpers.UserDatabaseCreation;
import sw.gmit.ie.crist.cameradetection.R;

public class Register extends AppCompatActivity {

    // Registration
    private RegistrationInit regInit = new RegistrationInit ();
    private ButtonVisibility btnVisibility = new ButtonVisibility ();

    // Firebase
    final private FirebaseAuth mAuth = FirebaseAuth.getInstance ();
    final private FirebaseUser user = mAuth.getInstance ().getCurrentUser ();

    // User Database
    private final UserDatabaseCreation userDatabaseCreation = new UserDatabaseCreation ();

    // Redirecting variables
    private Intent LoginActivity;
    private Redirect redirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        init ();                                     // general initialization - all initializations are called in this method
        register ();
    }


    /************************************************
     Init
     ***********************************************/
    private void init() {
        setContentView (R.layout.activity_register);    // it opens the registration activity file
        setTitle ("Register");                          // set Activity Title
        initVariables ();                               // initialize variables
        initBtn ();                                     // initialize Login Button and Progress Bar (Visible/Invisible)
        databaseInit ();                                // initialize users database
    }


    /************************************************
     Initialize Variables
     ***********************************************/
    private void initVariables() {
        regInit.setUserName ((EditText) findViewById (R.id.regName));
        regInit.setUserEmail ((EditText) findViewById (R.id.regEmail));
        regInit.setUserPass ((EditText) findViewById (R.id.regPass));
        regInit.setUserPassConfig ((EditText) findViewById (R.id.regPassConf));
        regInit.setRegisterProgress ((ProgressBar) findViewById (R.id.regBar));
        regInit.setRegBtn ((Button) findViewById (R.id.regBtn));
    }


    /************************************************
     Initialize Button
     ***********************************************/
    private void initBtn() {
        btnVisibility.showBtn (regInit.getRegBtn (), regInit.getRegisterProgress ());
    }


    /************************************************
     Initialize Database
     ***********************************************/
    private void databaseInit() {
        userDatabaseCreation.setmAuth (mAuth);               // firebase authentification instance
        userDatabaseCreation
                .setUserDatabaseRef (FirebaseDatabase
                        .getInstance ()                          // firebase database instance creates
                        .getReference ("users"));           //  -> a "users" reference on the database
    }


    /************************************************
     Registration
     ***********************************************/
    private void register() {
        regInit.getRegBtn ().setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                // Stores the values from the initialized variables into strings
                final String name = regInit.getUserName ().getText ().toString ();
                final String email = regInit.getUserEmail ().getText ().toString ();
                final String pass = regInit.getUserPass ().getText ().toString ();
                final String pass2 = regInit.getUserPassConfig ().getText ().toString ();

                // If there is no input then show a message
                if (name.isEmpty () || email.isEmpty () || pass.isEmpty () || !pass.equals (pass2)) {
                    if (!pass.equals (pass2)) {
                        // shows an error message if both passwords are different
                        regInit.getUserPassConfig ().setError ("Both passwords must be the same");
                    } else {
                        // shows a message if fields are empty
                        showMessage ("All fields must be filled");
                    }
                    btnVisibility.showBtn (regInit.getRegBtn (), regInit.getRegisterProgress ());
                } else {
                    // creates user account
                    CreateAccount (name, email, pass);
                }

            }
        });
    }


    /************************************************
     Account Creation
     ***********************************************/
    private void CreateAccount(final String name, final String email, final String pass) {
        getAuthResultTask (name, email, pass);
    }


    /************************************************
     Create User Authentification
     ***********************************************/
    @NonNull
    private Task<AuthResult> getAuthResultTask(final String name, final String email, String pass) {
        return userDatabaseCreation.getmAuth ().createUserWithEmailAndPassword (email, pass).
                addOnCompleteListener (this, new OnCompleteListener<AuthResult> () {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // registration successful
                        if (task.isSuccessful ()) {

                            userDatabaseCreation.createUserDatabase (name, email);

                            // it gets the current user
                            // if the current user doesn't have the full name stored in the database
                            // then add it to the database and update the profile
                            if (user.getDisplayName () == null) {
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder ()
                                        .setDisplayName (name)
                                        .build ();
                                user.updateProfile (profileUpdates);
                            }
                            btnVisibility.showBtn (regInit.getRegBtn (), regInit.getRegisterProgress ());
                            updateLoginUI ();
                        } else {
                            // registration failed
                            showMessage (task.getException ().getMessage ().toString ());
                        }
                    }
                });
    }


    /************************************************
     Login Redirect
     ***********************************************/
    private void updateLoginUI() {
        LoginActivity = new Intent (getApplicationContext (), Login.class);    // it gets the login activity class
        startActivity (LoginActivity);                                       // it redirects the user to the login activity
        finish ();                                                           // finishes this activity
    }


    /************************************************
     Toast Message Method
     ***********************************************/
    private void showMessage(String message) {
        Toast.makeText (getApplicationContext (), message, Toast.LENGTH_LONG).show ();
    }
}
