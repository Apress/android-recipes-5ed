package com.examples.cardviewlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

class CardViewAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private final Bitmap bitmap;
    private Context context;
    private int selectedPosition = -1;

    public CardViewAdapter(Context context) {
        super();
        this.context = context;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.photo);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_item, parent, false);
        return new CardViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        String title = context.getString(R.string.title_text);
        holder.titleView.setText(title);
        holder.descriptionView.setText(R.string.ipsum_lorem);
        if (selectedPosition == position) {
            holder.descriptionView.setVisibility(View.VISIBLE);
        } else {
            holder.descriptionView.setVisibility(View.GONE);
        }
        holder.imageView.setImageResource(R.drawable.photo);
        Palette.from(bitmap)
                .generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch swatch = palette.getDarkVibrantSwatch();
                        if (swatch == null) {
                            swatch = palette.getSwatches().get(0);
                        }
                        int titleTextColor = Color.WHITE;
                        if (swatch != null) {
                            titleTextColor = swatch.getTitleTextColor();
                            titleTextColor = ColorUtils.setAlphaComponent(titleTextColor, 255);
                        }
                        holder.titleView.setTextColor(titleTextColor);
                    }
                });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (selectedPosition == position) {
                    selectedPosition = -1;
                    notifyItemChanged(position);
                } else {
                    int oldSelectedPosition = selectedPosition;
                    selectedPosition = position;
                    notifyItemChanged(oldSelectedPosition);
                    notifyItemChanged(selectedPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 50;
    }
}
