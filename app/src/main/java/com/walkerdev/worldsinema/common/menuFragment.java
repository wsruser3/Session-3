package com.walkerdev.worldsinema.common;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.walkerdev.worldsinema.R;

public class menuFragment extends Fragment {
    Integer load = 0;
    public menuFragment(int type) {
        this.load = type;
    }
    public static menuFragment newInstance(int value) {
        return new menuFragment(value);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int layout = R.layout.frament_nav_home;
        if(load == 1) layout = R.layout.fragment_nav_forme;
        if(load == 2) layout = R.layout.fragment_nav_col;
        if(load == 3) layout = R.layout.fragment_nav_profile;
        return inflater.inflate(layout, container, false);
    }
}