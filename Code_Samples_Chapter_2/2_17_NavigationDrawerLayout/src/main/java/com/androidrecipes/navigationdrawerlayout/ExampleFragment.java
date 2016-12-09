package com.androidrecipes.navigationdrawerlayout;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExampleFragment extends Fragment {
    private static final String EXTRA_TEXT = "text";

    public static ExampleFragment newInstance(String text) {
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        ExampleFragment exampleFragment = new ExampleFragment();
        exampleFragment.setArguments(args);
        return exampleFragment;
    }

    public ExampleFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_example, container, false);
        String text = getArguments().getString(EXTRA_TEXT);
        ((TextView) view.findViewById(R.id.text)).setText(text);
        return view;
    }

}
