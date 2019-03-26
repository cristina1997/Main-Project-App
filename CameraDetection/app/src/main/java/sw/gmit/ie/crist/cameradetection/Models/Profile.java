package sw.gmit.ie.crist.cameradetection.Models;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class Profile {// Image Variables
    private ImageButton btnChooseImg;
    private ImageButton btnUploadImg;
    private ImageButton btnTakePic;
    private ImageView imgView;
    private EditText imgText;
    private ProgressBar imgProgressBar;

    public Profile() {
    }

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