package sw.gmit.ie.crist.cameradetection.Models;

import android.app.Activity;
import android.widget.Toast;

public class UploadPictures {
    // Variables
    private String name, // person name
            imgUrl; // image URL

    // Constructors
    public UploadPictures() {
    }

    public UploadPictures(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    // Getters and setters
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

    // output the name of the person and the url to make sure they are correct
    @Override
    public String toString() {
        return "\nName: " + name + "\nImg Url: " + imgUrl;
    }
}
