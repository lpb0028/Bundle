package com.example.overlaying;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        MainActivity activity = (MainActivity) getActivity();

        view.findViewById(R.id.home_button1).setOnClickListener(v -> {
            if(activity.isBound) {
                activity.binder.StartAdventure(activity);
                activity.finishAndRemoveTask();
            } // Unbind from service and begin adventure, close app
        });
        view.findViewById(R.id.home_button2).setOnClickListener(v -> {
            if(activity.isBound) {
                activity.binder.StopAdventure();
                activity.binder.stopService();
                activity.finishAndRemoveTask();
            } // Unbind from service and close app without starting adventure
        });

        return view;
    }
}
