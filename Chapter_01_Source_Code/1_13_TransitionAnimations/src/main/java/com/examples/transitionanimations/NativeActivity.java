package com.examples.transitionanimations;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

@SuppressLint("NewApi")
public class NativeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onAddClick(View v) {
        NativeFragment fragment = new NativeFragment();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        //Must be called first!
//        ft.setCustomAnimations(R.animator.fragment_enter, R.animator.fragment_exit, R.animator.fragment_pop_enter, R.animator.fragment_pop_exit);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.container_fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
