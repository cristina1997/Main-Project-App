package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;

import sw.gmit.ie.crist.cameradetection.R;

public class Login extends AppCompatActivity {
    private EditText userEmail, userPass;
    private Button btnLogin, btnRegActivity;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity, RegisterActivity;

    private Home home = new Home();

    @Override
    /***************************************************/
    /********* At the start of the application *********/
    /***************************************************/
    protected void onStart() {
        super.onStart();
        // If the user is already connect then redirect him/her redirect user to the home page
        FirebaseUser user = mAuth.getCurrentUser();
        if (home.getSignedIn() == true && user != null){ // or if the user does not exist
            updateHomeUI();
//            showMessage(user.getUid());
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(this);

        initVariables();

        loginProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        registerActivity();                 // user can register if they press the registration button
        loginActivity();                    // if the user isn't already connect - redirect to login page so that they can log in

    }

    private void initVariables() {
        setContentView(R.layout.activity_login);
        userEmail = findViewById(R.id.logEmail);
        userPass = findViewById(R.id.regName);
        btnLogin = findViewById(R.id.logBtn);
        loginProgress = findViewById(R.id.logBar);
        btnRegActivity = findViewById(R.id.regActivityBtn);
    }

    private void registerActivity() {
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
                hideBtn();
                final String email = userEmail.getText().toString();
                final String password = userPass.getText().toString();

                if (email.isEmpty() || password.isEmpty()){
                    showMessage("Please verify all fields");
                    showBtn();
                } else {
                    signIn(email, password);
                }
            }
        });

    }

    private void hideBtn() {
        btnLogin.setVisibility(View.INVISIBLE);
        loginProgress.setVisibility(View.VISIBLE);
    }

    private void showBtn() {
        btnLogin.setVisibility(View.VISIBLE);
        loginProgress.setVisibility(View.INVISIBLE);
    }

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    showBtn();
                    updateHomeUI();
                } else {
                    showMessage(task.getException().getMessage());
                    showBtn();
                }
            }
        });
    }

    private void updateRegisterUI() {
        RegisterActivity = new Intent(getApplicationContext(),Register.class);
        startActivity(RegisterActivity);
        finish();
    }

    private void updateHomeUI() {
        HomeActivity = new Intent(getApplicationContext(),Home.class);
        startActivity(HomeActivity);
        home.setSignedIn(true);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}
