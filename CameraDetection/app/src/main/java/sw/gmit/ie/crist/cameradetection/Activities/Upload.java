package sw.gmit.ie.crist.cameradetection.Activities;

import android.app.Activity;
import android.widget.Toast;

public class Upload extends Activity {
    private String name, imgUrl;

    public Upload(){
        // do not delete
    }

    public Upload(String name, String imgUrl) {
        if (name.trim().equals("")){
            showMessage("Please enter a name");
        }

        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

}
