package se.hellsoft.onlineimages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;

public class OnlineImageActivity extends AppCompatActivity {
    private static final String BASE_IMAGE_URL = "https://unsplash.it/200/300?image=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.image_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ImagesAdapter());
    }

    private class ImagesAdapter extends RecyclerView.Adapter<ImageViewHolder> {
        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(OnlineImageActivity.this)
                    .inflate(R.layout.image_item, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            Glide.with(OnlineImageActivity.this)
                    .load(BASE_IMAGE_URL + position)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .centerCrop()
                    .placeholder(R.drawable.ic_cloud_download_black_24dp)
                    .error(R.drawable.ic_warning_black_24dp)
                    .crossFade()
                    .into(holder.image);
        }

        @Override
        public void onViewRecycled(ImageViewHolder holder) {
            super.onViewRecycled(holder);
            Glide.clear(holder.image);
        }

        @Override
        public int getItemCount() {
            return 1000;
        }
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image;

        public ImageViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
