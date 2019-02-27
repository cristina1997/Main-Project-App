package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
import android.mtp.MtpObjectInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.*;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.jar.Attributes;

import sw.gmit.ie.crist.cameradetection.R;

public class Register extends AppCompatActivity {

    // Register variables
    private EditText userName, userEmail, userPass, userPassConf;
    private ProgressBar registerProgress;
    private Button regBtn;
    private FirebaseAuth mAuth;
    private Intent LoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(this);
        initVariables();

        registerProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        register();

    }

    private void initVariables() {
        setContentView(R.layout.activity_register);
        userName = findViewById(R.id.regName);
        userEmail = findViewById(R.id.regEmail);
        userPass = findViewById(R.id.regPass);
        userPassConf = findViewById(R.id.regPassConf);
        registerProgress = findViewById(R.id.regBar);
        regBtn = findViewById(R.id.regBtn);
    }

    private void register() {
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideBtn();

                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String pass = userPass.getText().toString();
                final String pass2 = userPassConf.getText().toString();


                if ( name.isEmpty() || email.isEmpty() || pass.isEmpty() || !pass.equals(pass2)){

                    if (!pass.equals(pass2)){
                        // error message: passwords must be the same
                        showMessage("Both passwords must be the same");
                    } else {
                        // error message: all fields must be filled
                        showMessage("All fields must be filled");
                    }

                    showBtn();
                } else {
                    // no errors: create user account
                    CreateAccount(name, email, pass);
                }

            }
        });
    }

    private void CreateAccount(final String name, final String email, final String pass) {

        mAuth.createUserWithEmailAndPassword(email, pass).
            addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // registration successful
                        showMessage("Account created");

                        createUserDatabase(name, email);

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
        regBtn.setVisibility(View.INVISIBLE);
        registerProgress.setVisibility(View.VISIBLE);
    }

    private void showBtn() {
        regBtn.setVisibility(View.VISIBLE);
        registerProgress.setVisibility(View.INVISIBLE);
    }

    private void createUserDatabase(final String name, String email) {
        User user = new User(name, email);


        FirebaseDatabase.getInstance().getReference("users")
                .child(mAuth.getUid())
                .setValue(user);
    }

    private void updateLoginUI() {
        LoginActivity = new Intent(getApplicationContext(),Login.class);
        startActivity(LoginActivity);
        finish();
    }

    // method that shows a toast message
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
