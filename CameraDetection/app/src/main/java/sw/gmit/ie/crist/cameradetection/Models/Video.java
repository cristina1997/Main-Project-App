package sw.gmit.ie.crist.cameradetection.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Video {
    // Variables
    private String name; // person name
    private String personType; // person type: either known name with a "-" or the "unknown" value
    private String personTypeUnchanged; // person type unchanged: known name with a space

    // Constructors
    public Video() {
    }

    public Video(String name, String personType, String personTypeUnchanged) {
        this.name = name;
        this.personType = personType;
        this.personTypeUnchanged = personTypeUnchanged;
    }

    // Getters and setters
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
