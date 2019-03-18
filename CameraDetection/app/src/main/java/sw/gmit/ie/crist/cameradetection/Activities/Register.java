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

import sw.gmit.ie.crist.cameradetection.R;

public class Register extends AppCompatActivity {

    private final UserDatabase userDatabase = new UserDatabase ();
    // Register variables
    private EditText userName, userEmail, userPass, userPassConfig;
    private ProgressBar registerProgress;
    private Button regBtn;

    // Redirecting variables
    private Intent LoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);         // it opens the registration activity file
        initVariables();                                    // initializes registration variables

        registerProgress.setVisibility(View.INVISIBLE);     // progress bar visibility is initially invisible

        userDatabase.mAuth = FirebaseAuth.getInstance ();                 // firebase authentification instance
        userDatabase.userDatabaseRef = FirebaseDatabase.getInstance ()    // firebase database instance creates
                .getReference ("users");          //     -> a "users" reference on the database
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
                hideBtn();

                // Stores the values from the initialized variables into strings
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String pass = userPass.getText().toString();
                final String pass2 = userPassConfig.getText().toString();


                // If there is no input then show a message
                if ( name.isEmpty() || email.isEmpty() || pass.isEmpty() || !pass.equals(pass2)){

                    if (!pass.equals(pass2)){
                        // shows an error message if both passwords are different
                        userPassConfig.setError("Both passwords must be the same");
                    } else {
                        // shows a message if fields are empty
                        showMessage("All fields must be filled");
                    }

                    showBtn();
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
        return userDatabase.mAuth.createUserWithEmailAndPassword(email, pass).
        addOnCompleteListener(this, new OnCompleteListener<AuthResult> () {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            // registration successful
            if (task.isSuccessful()) {

                userDatabase.createUserDatabase (name, email);

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
                showBtn();
                updateLoginUI();
            } else {
                // registration failed
                showMessage("Registration failed" + task.getException().getMessage().toString());
                hideBtn();

            }
            }
        });
    }

    private void hideBtn() {
        regBtn.setVisibility(View.INVISIBLE);                   // hides registration button
        registerProgress.setVisibility(View.VISIBLE);           // shows progresss bar
    }

    private void showBtn() {
        regBtn.setVisibility(View.VISIBLE);                     // shows registration button
        registerProgress.setVisibility(View.INVISIBLE);         // hides progresss bar
    }

    private void createUserDatabase(final String name, String email) {
        userDatabase.createUserDatabase (name, email);
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
