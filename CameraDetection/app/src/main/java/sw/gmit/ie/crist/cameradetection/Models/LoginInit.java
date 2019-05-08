package sw.gmit.ie.crist.cameradetection.Models;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import sw.gmit.ie.crist.cameradetection.R;

public class LoginInit {

    // Register variables
    private EditText
            userEmail,                          // email
            userPass;                           // password
    private ProgressBar loginProgress;          // progress bar
    private Button loginBtn;                    // login button
    private Button btnRegActivity;              // registration button

    // Getters and setters
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

    public ProgressBar getLoginProgress() {
        return loginProgress;
    }

    public void setLoginProgress(ProgressBar loginProgress) {
        this.loginProgress = loginProgress;
    }

    public Button getLoginBtn() {
        return loginBtn;
    }

    public void setLoginBtn(Button loginBtn) {
        this.loginBtn = loginBtn;
    }

    public Button getBtnRegActivity() {
        return btnRegActivity;
    }

    public void setBtnRegActivity(Button btnRegActivity) {
        this.btnRegActivity = btnRegActivity;
    }
}
