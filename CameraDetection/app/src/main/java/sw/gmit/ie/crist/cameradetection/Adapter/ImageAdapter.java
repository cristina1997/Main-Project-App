package sw.gmit.ie.crist.cameradetection.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sw.gmit.ie.crist.cameradetection.Activities.Upload;
import sw.gmit.ie.crist.cameradetection.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context context;
    private List<Upload> uploads;

    // get values into adapter
    public ImageAdapter(Context context, List<Upload> uploads) {
        this.context = context;
        this.uploads = uploads;
    }
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = uploads.get(position);
        holder.textViewName.setText((uploadCurrent.getName()));
        Picasso.with(context)
                .load(uploadCurrent.getImgUrl())
                .fit()
                .centerCrop()
                .into(holder.imgView);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    public  class  ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imgView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.tv_name);
            imgView = itemView.findViewById(R.id.iv_upload);
        }
    }
}
