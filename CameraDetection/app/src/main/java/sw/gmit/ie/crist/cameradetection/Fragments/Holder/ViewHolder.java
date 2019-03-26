package sw.gmit.ie.crist.cameradetection.Fragments.Holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.provider.PicassoProvider;

import java.util.List;

import sw.gmit.ie.crist.cameradetection.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    private View view;

    public ViewHolder(View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setDetails(Context context, String name, String image){
        TextView imageName = view.findViewById(R.id.tv_item_name);
        ImageView imageView = view.findViewById(R.id.iv_item_upload);

        imageName.setText(name);
        PicassoProvider.get().load(image).into(imageView);

    }

}
