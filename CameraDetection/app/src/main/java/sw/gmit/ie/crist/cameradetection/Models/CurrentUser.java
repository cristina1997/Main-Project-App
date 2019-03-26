package sw.gmit.ie.crist.cameradetection.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CurrentUser {
    // Firebase
    final private FirebaseUser user = FirebaseAuth.getInstance ().getCurrentUser ();
    final private String userName = user.getDisplayName();
    final private String userUid = user.getUid();

    public FirebaseUser getUser() {
        return user;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUid() {
        return userUid;
    }
}
