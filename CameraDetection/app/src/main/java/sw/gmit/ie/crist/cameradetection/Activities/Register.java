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

import sw.gmit.ie.crist.cameradetection.Models.UserDatabaseCreation;
import sw.gmit.ie.crist.cameradetection.R;

public class Register extends AppCompatActivity {

    private final UserDatabaseCreation userDatabaseCreation = new UserDatabaseCreation ();
    // Register variables
    private ButtonVisibility bs = new ButtonVisibility();
    private EditText userName, userEmail, userPass, userPassConfig;
    private ProgressBar registerProgress;
    private Button regBtn;

    // Redirecting variables
    private Intent LoginActivity;
    private Redirect redirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);         // it opens the registration activity file
        setTitle("Register");
        initVariables();                                    // initializes registration variables

        registerProgress.setVisibility(View.INVISIBLE);     // progress bar visibility is initially invisible

        userDatabaseCreation.setmAuth(FirebaseAuth.getInstance());                 // firebase authentification instance
        userDatabaseCreation.setUserDatabaseRef(FirebaseDatabase.getInstance ()    // firebase database instance creates
                .getReference ("users"));          //     -> a "users" reference on the database
        register();

    }

    private void initVariables() {
        userName = findViewById(R.id.regName);              // full name
        userEmail = findViewById(R.id.regEmail);            // email
        userPass = findViewById(R.id.regPass);              // password
        userPassConfig = findViewById(R.id.regPassConf);    // password configuration
        registerProgress = findViewById(R.id.regBar);       // progress bar
        regBtn = findViewById(R.id.regBtn);                 // registration button
    }

    private void register() {
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Stores the values from the initialized variables into strings
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String pass = userPass.getText().toString();
                final String pass2 = userPassConfig.getText().toString();
                bs.hideBtn(regBtn, registerProgress);

                // If there is no input then show a message
                if ( name.isEmpty() || email.isEmpty() || pass.isEmpty() || !pass.equals(pass2)){

                    if (!pass.equals(pass2)){
                        // shows an error message if both passwords are different
                        userPassConfig.setError("Both passwords must be the same");
                    } else {
                        // shows a message if fields are empty
                        showMessage("All fields must be filled");
                    }

                    bs.showBtn(regBtn, registerProgress);
                } else {
                    // creates user account
                    CreateAccount(name, email, pass);
                }

            }
        });
    }

    private void CreateAccount(final String name, final String email, final String pass) {
        getAuthResultTask (name, email, pass);
    }

    @NonNull
    private Task<AuthResult> getAuthResultTask(final String name, final String email, String pass) {
        return userDatabaseCreation.getmAuth().createUserWithEmailAndPassword(email, pass).
        addOnCompleteListener(this, new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            // registration successful
            if (task.isSuccessful()) {

                userDatabaseCreation.createUserDatabase (name, email);

                // it gets the current user
                FirebaseUser firebaseUser = FirebaseAuth
                                            .getInstance()
                                            .getCurrentUser();

                // if the current user doesn't have the full name stored in the database
                // then add it to the database and update the profile
                if (firebaseUser.getDisplayName() == null){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
                    firebaseUser.updateProfile(profileUpdates);
                }
                bs.showBtn(regBtn, registerProgress);
                updateLoginUI();
            } else {
                // registration failed
                showMessage(task.getException().getMessage().toString());
            }
            }
        });
    }


    private void createUserDatabase(final String name, String email) {
        userDatabaseCreation.createUserDatabase (name, email);
    }

    // redirects the user to the login page
    private void updateLoginUI() {
        LoginActivity = new Intent(getApplicationContext(),Login.class);    // it gets the login activity class
        startActivity(LoginActivity);                                       // it redirects the user to the login activity
        finish();                                                           // finishes this activity
    }

    // method that shows a toast message
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
