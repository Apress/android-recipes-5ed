package com.examples.transitionanimations;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("NewApi")
public class NativeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setText("Fragment");
        tv.setBackgroundColor(Color.BLUE);
        return tv;
    }

//    @Override
//    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
//        switch (transit) {
//        case FragmentTransaction.TRANSIT_FRAGMENT_FADE:
//            if (enter) {
//                return AnimatorInflater.loadAnimator(getActivity(), android.R.animator.fade_in);
//            } else {
//                return AnimatorInflater.loadAnimator(getActivity(), android.R.animator.fade_out);
//            }
//        case FragmentTransaction.TRANSIT_FRAGMENT_CLOSE:
//            if (enter) {
//                return AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_pop_enter);
//            } else {
//                return AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_pop_exit);
//            }
//        case FragmentTransaction.TRANSIT_FRAGMENT_OPEN:
//        default:
//            if (enter) {
//                return AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_enter);
//            } else {
//                return AnimatorInflater.loadAnimator(getActivity(), R.animator.fragment_exit);
//            }
//        }
//    }
}
