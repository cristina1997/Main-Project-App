package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import sw.gmit.ie.crist.cameradetection.R;

public class Register extends AppCompatActivity {


    // Register variables
    private EditText fName, surname, userEmail, userPass, userPassConf;
    private ProgressBar registerProgress;
    private Button regBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_register);

        // ini views
        fName = findViewById(R.id.logEmail);
        surname = findViewById(R.id.logPassword);
        userEmail = findViewById(R.id.regEmail);
        userPass = findViewById(R.id.regPass);
        userPassConf = findViewById(R.id.regPassConf);
        registerProgress = findViewById(R.id.regBar);
        regBtn = findViewById(R.id.regBtn);

        mAuth = FirebaseAuth.getInstance();

        registerProgress.setVisibility(View.INVISIBLE);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regBtn.setVisibility(View.INVISIBLE);
                registerProgress.setVisibility(View.VISIBLE);

                final String firstName = fName.getText().toString();
                final String lastName = surname.getText().toString();
                final String email = userEmail.getText().toString();
                final String pass = userPass.getText().toString();
                final String pass2 = userPass.getText().toString();

                if ( firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || pass.isEmpty() || !pass.equals(pass2)){

                    if (!pass.equals(pass2)){
                        // error message: passwords must be the same
                        showMessage("Both passwords must be the same");
                    } else {
                        // error message: all fields must be filled
                        showMessage("All fields must be filled");
                    }

                    regBtn.setVisibility(View.VISIBLE);
                    registerProgress.setVisibility(View.INVISIBLE);
                } else {
                    // no errors: create user account
                    CreateAccount(firstName, lastName, email, pass);
                }

            }
        });
    }

    private void CreateAccount(String firstName, String lastName, String email, String pass) {

        mAuth.createUserWithEmailAndPassword(email, pass).
            addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // registration successful
                        showMessage("Account created");
                        regBtn.setVisibility((View.VISIBLE));
                        registerProgress.setVisibility(View.INVISIBLE);
                    } else {
                        // registration failed
                        showMessage("Registration failed" + task.getException().getMessage().toString());
                        regBtn.setVisibility((View.VISIBLE));
                        registerProgress.setVisibility(View.INVISIBLE);

                    }
                }
            });
    }

    // method that shows a toast message
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
