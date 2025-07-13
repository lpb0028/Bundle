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
        loadButtons(view);

        return view;
    }

    private void loadButtons(View view) {
        Activity activity = (Activity) getActivity();
        assert activity != null;

        // Start service and end activity
        view.findViewById(R.id.home_button1).setOnClickListener(v -> {
            if (Manager.boundToService) {
                Manager.binder.StartAdventure();
                activity.close();
            }
            else
                activity.manager.bindToService(activity);
        });

        // End service, end activity; Exit Application
        view.findViewById(R.id.home_button2).setOnClickListener(v -> {
            if (Manager.boundToService) {
                Manager.binder.StopAdventure();
                Manager.binder.StopService();
            }
            activity.close();
        });
    }
}
