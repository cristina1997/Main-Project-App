package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SendToLogin extends AppCompatActivity {

    public void sendToStart(Home home) {
        showMessage ("" +home);
//        Intent startIntent = new Intent(home, Login.class);
//        startActivity(startIntent);
//        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

}
