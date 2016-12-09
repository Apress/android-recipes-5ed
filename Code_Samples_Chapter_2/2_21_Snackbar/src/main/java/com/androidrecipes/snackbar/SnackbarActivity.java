package com.androidrecipes.snackbar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SnackbarActivity extends AppCompatActivity {
    private static String[] NAMES = {"Andreas", "Beatrice", "Christoffer",
            "David", "Erik", "Fredrik", "Hannah", "Isabelle", "John",
            "Kenneth", "Linda", "Madelene", "Nils", "Olof"};

    private NameAdapter mAdapter;
    @Nullable
    private ArrayList<String> mClearedNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView listOfNames = (RecyclerView) findViewById(R.id.list_of_names);
        listOfNames.setHasFixedSize(true);
        mAdapter = new NameAdapter();
        listOfNames.setAdapter(mAdapter);
        if (savedInstanceState == null) {
            mAdapter.addNames(Arrays.asList(NAMES));
        } else {
            ArrayList<String> names = savedInstanceState.getStringArrayList("names");
            mAdapter.addNames(names);
            if (savedInstanceState.containsKey("clearedNames")) {
                ArrayList<String> clearedNames = savedInstanceState
                        .getStringArrayList("clearedNames");
                showClearSnackbar(clearedNames);
            }
        }

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.clearList();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("names", mAdapter.mNames);
        if (mClearedNames != null) {
            outState.putStringArrayList("clearedNames", mClearedNames);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_items:
                mAdapter.addNames(Arrays.asList(NAMES));
                return true;
            default:
                return false;
        }
    }

    public void doClearList(View view) {
    }

    private void showClearSnackbar(final ArrayList<String> names) {
        Snackbar.make(findViewById(R.id.activity_main),
                getString(R.string.names_deleted, names.size()),
                Snackbar.LENGTH_INDEFINITE)
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        mClearedNames = null;
                    }

                    @Override
                    public void onShown(Snackbar snackbar) {
                        super.onShown(snackbar);
                        mClearedNames = names;
                    }
                })
                .setAction(R.string.undo_label, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Undo click - revert delete action
                        mAdapter.addNames(names);
                    }
                })
                .show();
    }

    private static class NameViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;

        NameViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

    private class NameAdapter extends RecyclerView.Adapter<NameViewHolder> {
        private final ArrayList<String> mNames;

        NameAdapter() {
            mNames = new ArrayList<>();
        }

        @Override
        public NameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(SnackbarActivity.this)
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new NameViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(NameViewHolder holder, int position) {
            holder.nameView.setText(mNames.get(position));
        }

        @Override
        public int getItemCount() {
            return mNames.size();
        }

        void clearList() {
            int oldSize = mNames.size();
            ArrayList<String> oldNames = new ArrayList<>(mNames);
            mNames.clear();
            if (oldSize > 0) {
                notifyItemRangeRemoved(0, oldSize);
                showClearSnackbar(oldNames);
            }
        }

        void addNames(List<String> names) {
            int oldSize = mNames.size();
            mNames.addAll(names);
            notifyItemRangeInserted(oldSize, names.size());
        }
    }
}
