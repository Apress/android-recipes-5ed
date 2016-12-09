package com.androidrecipes.keys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container_fragment, MyFragment.newInstance("First Fragment"));
        ft.commit();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_fragment, MyFragment.newInstance("Second Fragment"));
        ft.addToBackStack("second");
        ft.commit();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_fragment, MyFragment.newInstance("Third Fragment"));
        ft.addToBackStack("second");
        ft.commit();

        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container_fragment, MyFragment.newInstance("Fourth Fragment"));
        ft.addToBackStack("fourth");
        ft.commit();
    }

    public void onHomeClick(View v) {
        getSupportFragmentManager().popBackStack("second", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static class MyFragment extends Fragment {
        private CharSequence mTitle;

        public static MyFragment newInstance(String title) {
            MyFragment fragment = new MyFragment();
            fragment.setTitle(title);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            TextView text = new TextView(getActivity());
            text.setText(mTitle);

            return text;
        }

        public void setTitle(CharSequence title) {
            mTitle = title;
        }
    }
}
