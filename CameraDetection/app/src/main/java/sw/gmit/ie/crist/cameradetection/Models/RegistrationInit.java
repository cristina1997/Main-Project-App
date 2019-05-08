package sw.gmit.ie.crist.cameradetection.Models;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import sw.gmit.ie.crist.cameradetection.Activities.ButtonVisibility;
import sw.gmit.ie.crist.cameradetection.R;

public class RegistrationInit {

    // Register variables
    private EditText
            userName,                           // full name
            userEmail,                          // email
            userPass,                           // password
            userPassConfig;                     // password configuration
    private ProgressBar registerProgress;       // progress bar
    private Button regBtn;                      // registration button

    // Getters and setters
    public EditText getUserName() {
        return userName;
    }

    public void setUserName(EditText userName) {
        this.userName = userName;
    }

    public EditText getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(EditText userEmail) {
        this.userEmail = userEmail;
    }

    public EditText getUserPass() {
        return userPass;
    }

    public void setUserPass(EditText userPass) {
        this.userPass = userPass;
    }

    public EditText getUserPassConfig() {
        return userPassConfig;
    }

    public void setUserPassConfig(EditText userPassConfig) {
        this.userPassConfig = userPassConfig;
    }

    public ProgressBar getRegisterProgress() {
        return registerProgress;
    }

    public void setRegisterProgress(ProgressBar registerProgress) {
        this.registerProgress = registerProgress;
    }

    public Button getRegBtn() {
        return regBtn;
    }

    public void setRegBtn(Button regBtn) {
        this.regBtn = regBtn;
    }
}
