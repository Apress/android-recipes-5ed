package com.examples.cardviewlist;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

class CardViewHolder extends RecyclerView.ViewHolder {

    final ImageView imageView;
    final TextView titleView;
    final TextView descriptionView;

    CardViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.image);
        titleView = (TextView) itemView.findViewById(R.id.title);
        descriptionView = (TextView) itemView.findViewById(R.id.description);
    }
}
