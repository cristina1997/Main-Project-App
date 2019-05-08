package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.webkit.MimeTypeMap;

public class ImageExtension extends AppCompatActivity {

    public ImageExtension() {
    }

    public String getFileExtension(Uri uri) {
        // get extension from all files
        ContentResolver cR = getContentResolver ();
        MimeTypeMap mime = MimeTypeMap.getSingleton ();
        return mime.getExtensionFromMimeType (cR.getType (uri));
    }
}