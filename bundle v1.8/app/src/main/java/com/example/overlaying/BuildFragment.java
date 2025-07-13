package com.example.overlaying;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

public class BuildFragment extends Fragment {

    SimulationLayout simulation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Activity activity = (Activity) getActivity();
        assert activity != null;

        FrameLayout frame = (FrameLayout) inflater.inflate(R.layout.build_fragment, container, false);
        simulation = new SimulationLayout(getContext(), activity.manager);
        frame.addView(simulation);

        simulation.StartAdventure();

        return frame;
    }

}
