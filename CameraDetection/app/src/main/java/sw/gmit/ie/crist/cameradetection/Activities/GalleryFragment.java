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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sw.gmit.ie.crist.cameradetection.R;

public class GalleryFragment extends Fragment {
    final FragmentActivity thisActivity = getActivity();

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ViewHolder viewHolder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Images List");

        initVariables(rootView);

        databaseReference = FirebaseDatabase.getInstance().getReference("images");

        return rootView;

    }

    private void initVariables(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
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
