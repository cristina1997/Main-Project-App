package sw.gmit.ie.crist.cameradetection.Activities;


import android.os.Bundle;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sw.gmit.ie.crist.cameradetection.R;

public class GalleryFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);

    }
}
