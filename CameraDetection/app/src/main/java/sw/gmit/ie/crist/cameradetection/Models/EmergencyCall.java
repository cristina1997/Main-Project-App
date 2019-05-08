package sw.gmit.ie.crist.cameradetection.Models;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

public class EmergencyCall {
    Intent intent = init (); // initialization of the intent to call 911

    // Getter
    public Intent getIntent() {
        return intent;
    }

    private Intent init() {
        intent = new Intent (Intent.ACTION_CALL); // creates an Action Call intent that calls the police automatically
        intent.setData (Uri.parse ("tel: 911")); // sets the data (phone number) it is to be called
        return intent; // returns the intent and its action
    }
}
