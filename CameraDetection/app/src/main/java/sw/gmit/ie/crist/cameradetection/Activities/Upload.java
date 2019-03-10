package sw.gmit.ie.crist.cameradetection.Activities;

import android.app.Activity;
import android.widget.Toast;

public class Upload {
    private String name, imgUrl;

    public Upload(){
        // do not delete
    }

    public Upload(String name, String imgUrl) {
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

    @Override
    public String toString() {
        return "\nName: " +name + "\nImg Url: " + imgUrl;
    }
}
