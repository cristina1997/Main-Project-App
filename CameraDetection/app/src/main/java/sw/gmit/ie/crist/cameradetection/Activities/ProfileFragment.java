package sw.gmit.ie.crist.cameradetection.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;

import java.net.URI;

import sw.gmit.ie.crist.cameradetection.R;

public class ProfileFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference mStorageRef;
    private Button btnChooseImg;
    private Button btnUploadImg;
    private ImageView imgView;
    private ProgressBar imgProgressBar;

    private Uri imgURI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        btnChooseImg = (Button) rootView.findViewById(R.id.chooseImgBtn);
        btnUploadImg = (Button) rootView.findViewById(R.id.uploadBtn);
        imgView = (ImageView) rootView.findViewById(R.id.imageView);
        imgProgressBar = (ProgressBar) rootView.findViewById(R.id.imgProgress);


        btnChooseImg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);


            }
        });


        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imgURI = data.getData();

//            Picasso.with(this).load(imgURI).into(imgView);
            imgView.setImageURI(imgURI);
        }
    }
}
