package sw.gmit.ie.crist.cameradetection.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Video {
    private String name;
    private String personType;
    private String personTypeUnchanged;

    public Video() {}


    public Video(String name, String personType, String personTypeUnchanged) {
        this.name = name;
        this.personType = personType;
        this.personTypeUnchanged = personTypeUnchanged;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getPersonTypeUnchanged() {
        return personTypeUnchanged;
    }

    public void setPersonTypeUnchanged(String personTypeUnchanged) {
        this.personTypeUnchanged = personTypeUnchanged;
    }
}
