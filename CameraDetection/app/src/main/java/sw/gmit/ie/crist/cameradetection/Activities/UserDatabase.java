package sw.gmit.ie.crist.cameradetection.Activities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class UserDatabase {
    // Firebase Database variables
    protected FirebaseAuth mAuth;
    protected DatabaseReference userDatabaseRef;
    protected User user;

    protected UserDatabase() {
    }

    protected void createUserDatabase(final String name, String email) {
        user = new User (name, email);                         // creates a new User instance

        userDatabaseRef.child (mAuth.getUid ())               // gives each user id the value of the user instance
                .setValue (user);
    }
}