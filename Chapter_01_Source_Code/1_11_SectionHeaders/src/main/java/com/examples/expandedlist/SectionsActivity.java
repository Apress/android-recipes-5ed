package com.examples.expandedlist;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SectionsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SectionsAdapter adapter = new SectionsAdapter();
        recyclerView.setAdapter(adapter);


        adapter.addSection("Fruits", new String[]{"Apples", "Oranges", "Bananas", "Mangos"});
        adapter.addSection("Vegetables", new String[]{"Carrots", "Peas", "Broccoli"});
        adapter.addSection("Meats", new String[]{"Pork", "Chicken", "Beef", "Lamb"});

        setContentView(recyclerView);
    }

private class SectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM = 1;
    public static final int HEADER = 2;
    private List<Pair<String, String[]>> sections = new ArrayList<>();

    public void addSection(String header, String[] items) {
        sections.add(Pair.create(header, items));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(SectionsActivity.this);
        switch (viewType) {
            case ITEM:
                viewHolder = new ItemHolder(inflater.inflate(R.layout.list_item, parent, false));
                break;
            case HEADER:
                viewHolder = new HeaderHolder(inflater.inflate(R.layout.list_header, parent, false));
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int steps = 0;
        for (int i = 0; i < sections.size(); i++) {
            Pair<String, String[]> section = sections.get(i);
            if(steps == position) {
                ((HeaderHolder) holder).textView.setText(section.first);
                return;
            }
            steps++;

            for (int j = 0; j < section.second.length; j++) {
                if(steps == position) {
                    ((ItemHolder) holder).textView.setText(section.second[j]);
                    return;
                }
                steps++;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int steps = 0;
        for (int i = 0; i < sections.size(); i++) {
            Pair<String, String[]> section = sections.get(i);
            if(steps == position) {
                return HEADER;
            }
            steps++;

            for (int j = 0; j < section.second.length; j++) {
                if(steps == position) {
                    return ITEM;
                }
                steps++;
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (Pair<String, String[]> section : sections) {
            count += section.second.length + 1;
        }
        return count;
    }
}

private class ItemHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    public ItemHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(android.R.id.text1);
    }
}

private class HeaderHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    public HeaderHolder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(android.R.id.text1);
    }
}
}