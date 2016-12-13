package com.androidrecipes.emptyliststate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class EmptyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new EmptyStateAdapter());

    }

private class EmptyStateAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private static final int SIMPLE_ITEM = 1;
    private static final int EMPTY_ITEM = 2;
    private int itemCount = 0;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case SIMPLE_ITEM:
                itemView = LayoutInflater.from(EmptyActivity.this)
                        .inflate(R.layout.simple_item, parent, false);
                return new MyViewHolder(itemView);
            case EMPTY_ITEM:
            default:
                itemView = LayoutInflater.from(EmptyActivity.this)
                        .inflate(R.layout.empty_item, parent, false);
                return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case SIMPLE_ITEM:
                holder.textView.setText("Item nr " + (position + 1) + ". Tap to remove.");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int itemPosition = holder.getAdapterPosition();
                        itemCount--;
                        notifyItemRemoved(itemPosition);
                        if(itemCount == 0) {
                            notifyItemInserted(0);
                        }
                    }
                });
                break;
            case EMPTY_ITEM:
                holder.refreshButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyItemRemoved(0);
                        itemCount = 10;
                        notifyItemRangeInserted(0, 10);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return itemCount == 0 ? EMPTY_ITEM : SIMPLE_ITEM;
    }

    @Override
    public int getItemCount() {
        return itemCount == 0 ? 1 : itemCount;
    }
}

    private class MyViewHolder extends RecyclerView.ViewHolder {

        final Button refreshButton;
        final TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_text);
            refreshButton = (Button) itemView.findViewById(R.id.refresh_button);
        }
    }
}
