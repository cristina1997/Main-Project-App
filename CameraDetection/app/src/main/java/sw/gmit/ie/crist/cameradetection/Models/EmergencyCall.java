package sw.gmit.ie.crist.cameradetection.Models;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

public class EmergencyCall {
    Intent intent = init();

    public Intent getIntent() {
        return intent;
    }

    private Intent init(){
        intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:(+353)866623216"));
        return intent;
    }
}
