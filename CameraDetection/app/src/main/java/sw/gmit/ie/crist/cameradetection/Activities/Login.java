package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;

import sw.gmit.ie.crist.cameradetection.R;

public class Login extends AppCompatActivity {
    // Login variables
    private ButtonVisibility bs = new ButtonVisibility();
    private EditText userEmail, userPass;
    private Button btnLogin, btnRegActivity;
    private ProgressBar loginProgress;

    // Firebase variables
    private FirebaseAuth mAuth;

    // Redirect variables
    private Intent HomeActivity, RegisterActivity;
    private Redirect redirect;

    // Class Variables
    private Signeable signeable = new Signeable();
    private Home home = new Home();

    @Override
    protected void onStart() {
        super.onStart();
        // If the user is already connect then redirect him/her redirect user to the home page
        FirebaseUser user = mAuth.getCurrentUser();
        if (signeable.getSignedIn() == true && user != null){    // or if the user does not exist
            updateHomeUI();
//            showMessage(user.getUid());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);            // it opens the login activity file
        setTitle("Login");
        initVariables();                                    // initializes login variables

        loginProgress.setVisibility(View.INVISIBLE);        // progress bar visibility is initially invisible

        mAuth = FirebaseAuth.getInstance();                 // firebase authentification instance

        registerActivity();                                 // user can register if they press the registration button
        loginActivity();                                    // if the user isn't already connect - redirect to login page so that they can log in

    }

    private void initVariables() {
        userEmail = findViewById(R.id.logEmail);            // email
        userPass = findViewById(R.id.regName);              // password
        btnLogin = findViewById(R.id.logBtn);               // login button
        loginProgress = findViewById(R.id.logBar);          // progress bar
        btnRegActivity = findViewById(R.id.regActivityBtn); // registration button
    }

    private void registerActivity() {
        // user is redirected to the registration activity when the button is clicked
        btnRegActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                updateRegisterUI();
            }
        });
    }

    private void loginActivity() {
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Stores the values from the initialized variables into strings
                final String email = userEmail.getText().toString();
                final String password = userPass.getText().toString();

                bs.hideBtn(btnLogin, loginProgress);

                // If there is no input then show a message
                if (email.isEmpty() || password.isEmpty()){
                    showMessage("Please verify all fields");

                    bs.showBtn(btnLogin, loginProgress);
                } else {
                    // signs in the user
                    getSignIn(email, password);
                }
            }
        });

    }

    private void getSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    bs.hideBtn(btnLogin, loginProgress);
                    updateHomeUI();
                } else {
                    showMessage(task.getException().getMessage());
                    bs.showBtn(btnLogin, loginProgress);
                }
            }
        });
    }

    private void updateRegisterUI() {
        RegisterActivity = new Intent(getApplicationContext(),Register.class);  // it gets the registration activity class
        startActivity(RegisterActivity);                                        // it redirects the user to the registration activity
        finish();                                                               // finishes this activity
    }

    private void updateHomeUI() {
        HomeActivity = new Intent(getApplicationContext(),Home.class);          // it gets the home activity class
        startActivity(HomeActivity);                                            // it redirects the user to the home activity
        signeable.setSignedIn(true);                                            // user is already signed in
        finish();                                                               // finishes this activity
    }

    // method that shows a toast message
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}
