package sw.gmit.ie.crist.cameradetection.Models;

import android.app.Activity;
import android.widget.Toast;

public class UploadPictures {
    private String name, imgUrl;

    public UploadPictures(){}

    public UploadPictures(String name, String imgUrl) {
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
