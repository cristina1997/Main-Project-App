package sw.gmit.ie.crist.cameradetection.Activities;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class ButtonVisibility {
    protected Button button;
    protected ProgressBar progressBar;

    void hideBtn(Button button, ProgressBar progressBar){
        button.setVisibility(View.INVISIBLE);       // hide the button
        progressBar.setVisibility(View.VISIBLE);    // show progess bar
    };

    void showBtn(Button button, ProgressBar progressBar){
        button.setVisibility(View.VISIBLE);         // show the button
        progressBar.setVisibility(View.INVISIBLE);  // hide progess bar
    };
}
