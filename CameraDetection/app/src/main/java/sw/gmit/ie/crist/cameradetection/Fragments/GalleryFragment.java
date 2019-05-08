package sw.gmit.ie.crist.cameradetection.Fragments;


import android.os.Bundle;
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import sw.gmit.ie.crist.cameradetection.Models.UploadPictures;
import sw.gmit.ie.crist.cameradetection.Fragments.Holder.ViewHolder;
import sw.gmit.ie.crist.cameradetection.R;

public class GalleryFragment extends Fragment {
    // Instantiate this Fragment as a FragmentActivity
    final FragmentActivity thisActivity = getActivity ();

    // Picture variables
    private RecyclerView recyclerView;
    private ViewHolder viewHolder;

    // Firebase Database Variables
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;

    /// Firebase Variables
    final FirebaseUser user = FirebaseAuth.getInstance ().getCurrentUser ();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.fragment_gallery, container, false);
        init (rootView);                        // general initialization - all initializations are called in this method
        return rootView;

    }


    /************************************************
     Init
     ***********************************************/
    private void init(View rootView) {
        getActivity ().setTitle ("Gallery");    // set Fragment Title
        initVariables (rootView);               // initialize variables
        databaseInit ();                        // initialize images database
    }


    /************************************************
     Initialize Variables
     ***********************************************/
    private void initVariables(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById (R.id.recycler_view);        // gets the recycler view
        recyclerView.setHasFixedSize (true);                                             // sets picture size
        recyclerView.setLayoutManager (new LinearLayoutManager (thisActivity));
    }


    /************************************************
     Initialize Database
     ***********************************************/
    private void databaseInit() {
        databaseReference = FirebaseDatabase
                .getInstance ()                         // firebase database instance creates
                .getReference ("images/" +           //  -> a "users" reference on the database with a child of
                        user.getDisplayName ());        //  -> a user's name reference on the database
    }

    @Override
    public void onStart() {
        super.onStart ();
        FirebaseRecyclerAdapter<UploadPictures, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<UploadPictures, ViewHolder> (
                        UploadPictures.class,
                        R.layout.image_item,
                        ViewHolder.class,
                        databaseReference
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, UploadPictures uploadPictures, int position) {
                        // Download the images from the firebase database to the gallery
                        // by getting the name and url of each photo
                        // using the url to download the photo
                        // and setting the name at the top of each photo
                        viewHolder.setDetails (getActivity ().getApplicationContext (), uploadPictures.getName (), uploadPictures.getImgUrl ());
                    }
                };
        // set adapter to recyclerview
        recyclerView.setAdapter (firebaseRecyclerAdapter);

    }

    private void showMessage(String message) {
        Toast.makeText (getActivity ().getApplicationContext (), message, Toast.LENGTH_SHORT).show ();
    }
}
