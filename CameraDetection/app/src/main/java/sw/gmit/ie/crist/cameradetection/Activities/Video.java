package sw.gmit.ie.crist.cameradetection.Activities;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Video {
    private String name;
    private List allNames;

    public Video() {}


    public Video(String name, List allNames) {
        this.name = name;
        this.allNames = allNames;
    }

    public String getName() {
        return name;
    }

    public void setAllNames(List allNames) {
        this.allNames = allNames;
    }

    public List getAllNames() {
        return allNames;
    }
}
