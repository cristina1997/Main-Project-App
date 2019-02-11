package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;

import com.google.android.gms.tasks.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;

import sw.gmit.ie.crist.cameradetection.R;

public class Login extends AppCompatActivity {
    private EditText userEmail, userPass;
    private Button btnLogin, btnRegActivity;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity, RegisterActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);
        userEmail = findViewById(R.id.logEmail);
        userPass = findViewById(R.id.logPassword);
        btnLogin = findViewById(R.id.logBtn);
        loginProgress = findViewById(R.id.logBar);
        btnRegActivity = findViewById(R.id.regActivityBtn);

        loginProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        registerActivity();
        loginActivity();

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
                }
                else {
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
        showMessage("Logging in");

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    showMessage("Is successful");
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
        HomeActivity = new Intent(this,sw.gmit.ie.crist.cameradetection.Activities.Home.class);
        startActivity(HomeActivity);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        // user is already connect - redirect user to home page
        updateHomeUI();
    }


}
