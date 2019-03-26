package sw.gmit.ie.crist.cameradetection.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import sw.gmit.ie.crist.cameradetection.Models.User;

public class UserDatabaseCreation {
    public FirebaseAuth mAuth;
    public DatabaseReference userDatabaseRef;
    public User user;

    public UserDatabaseCreation() {
    }

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public void setmAuth(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public DatabaseReference getUserDatabaseRef() {
        return userDatabaseRef;
    }

    public void setUserDatabaseRef(DatabaseReference userDatabaseRef) {
        this.userDatabaseRef = userDatabaseRef;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void createUserDatabase(final String name, String email) {
        this.setUser(new User (name, email));                           // creates a new User instance
        this.getUserDatabaseRef().child (this.getmAuth().getUid ())                 // gives each user id the value of the user instance
                .setValue (user);
    }
}