package sw.gmit.ie.crist.cameradetection.Models;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ProfileInit {
    // Image Variables
    private ImageButton btnChooseImg; // gallery button
    private ImageButton btnUploadImg; // upload button
    private ImageButton btnTakePic; // camera button
    private ImageView imgView; // view image
    private EditText imgText; // text input for image (name of the person)
    private ProgressBar imgProgressBar; // progress bar

    // Constructor
    public ProfileInit() {
    }

    // Getters and setters
    public ImageButton getBtnChooseImg() {
        return btnChooseImg;
    }

    public void setBtnChooseImg(ImageButton btnChooseImg) {
        this.btnChooseImg = btnChooseImg;
    }

    public ImageButton getBtnUploadImg() {
        return btnUploadImg;
    }

    public void setBtnUploadImg(ImageButton btnUploadImg) {
        this.btnUploadImg = btnUploadImg;
    }

    public ImageButton getBtnTakePic() {
        return btnTakePic;
    }

    public void setBtnTakePic(ImageButton btnTakePic) {
        this.btnTakePic = btnTakePic;
    }

    public ImageView getImgView() {
        return imgView;
    }

    public void setImgView(ImageView imgView) {
        this.imgView = imgView;
    }

    public EditText getImgText() {
        return imgText;
    }

    public void setImgText(EditText imgText) {
        this.imgText = imgText;
    }

    public ProgressBar getImgProgressBar() {
        return imgProgressBar;
    }

    public void setImgProgressBar(ProgressBar imgProgressBar) {
        this.imgProgressBar = imgProgressBar;
    }
}