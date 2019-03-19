package sw.gmit.ie.crist.cameradetection.Activities;


import android.os.Bundle;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import sw.gmit.ie.crist.cameradetection.R;

public class GalleryFragment extends Fragment {
    final FragmentActivity thisActivity = getActivity();

    // Picture variables
    private RecyclerView recyclerView;
    private ViewHolder viewHolder;

    // Firebase Database Variables
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;

    /// Firebase Variables
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        getActivity().setTitle("Gallery");
        initVariables(rootView);

        FirebaseUser user = mAuth.getInstance().getCurrentUser();                 // it gets the current user
        databaseReference = FirebaseDatabase                        // it gets
                            .getInstance()
                            .getReference("images/" +
                                    user.getDisplayName());

        return rootView;

    }

    private void initVariables(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);        // gets the recycler view
        recyclerView.setHasFixedSize(true);                                             // sets picture size
        recyclerView.setLayoutManager(new LinearLayoutManager(thisActivity));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Upload, ViewHolder> firebaseRecyclerAdapter =
            new FirebaseRecyclerAdapter<Upload, ViewHolder>(
                    Upload.class,
                    R.layout.image_item,
                    ViewHolder.class,
                    databaseReference
            ) {
                @Override
                protected void populateViewHolder(ViewHolder viewHolder, Upload upload, int position) {
                    viewHolder.setDetails(getActivity().getApplicationContext(), upload.getName(), upload.getImgUrl());
                }
            };
        // set adapter to recyclerview
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    private void showMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
