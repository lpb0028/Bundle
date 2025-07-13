package com.example.overlaying;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    //region private static final int[][] BANNER_GRIDS = {}
    private static final int[][] LEFT_BANNER_GRID = {
            {R.drawable.oak_planks, R.drawable.oak_planks, R.drawable.oak_plank_stairs_3, R.drawable.air},
            {R.drawable.oak_planks, R.drawable.oak_planks, R.drawable.oak_planks, R.drawable.oak_plank_stairs_3},
            {R.drawable.cobblestone, R.drawable.cobblestone, R.drawable.oak_log, R.drawable.air},
            {R.drawable.glass, R.drawable.oak_log_2, R.drawable.oak_log, R.drawable.air},
            {R.drawable.cobblestone, R.drawable.cobblestone, R.drawable.oak_log, R.drawable.air},
            {R.drawable.cobblestone, R.drawable.cobblestone, R.drawable.oak_log, R.drawable.oak_plank_stairs_3}
    };
    private static final int[][] RIGHT_BANNER_GRID = {
            {R.drawable.air, R.drawable.composter_side, R.drawable.wheat_stage5, R.drawable.wheat_stage7, R.drawable.wheat_stage0},
            {R.drawable.oak_log, R.drawable.oak_log, R.drawable.oak_log, R.drawable.oak_log, R.drawable.oak_log}
    };
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        loadViews(view);

        return view;
    }
    private void loadViews(View view) {
        Activity activity = (Activity) getActivity();
        assert activity != null;

        // Setup BlockGrids
        BlockGrid.loadGrid(view, R.id.leftBannerGrid, LEFT_BANNER_GRID);
        BlockGrid.loadGrid(view, R.id.rightBannerGrid, RIGHT_BANNER_GRID);

        // Setup CloudView
        ((FrameLayout) view.findViewById(R.id.cloudContainer)).addView(new CloudView(getContext(), null));

        // Start Button
        view.findViewById(R.id.homeButton1).setOnClickListener(v -> onStartButtonClick(activity));

        // End service, end activity; Exit Application
        view.findViewById(R.id.homeButton2).setOnClickListener(v -> onExitButtonClick(activity));
    }
    private void onStartButtonClick(Activity activity) {
        if (Manager.boundToService) {
            Manager.binder.StartAdventure();
            activity.Close(false);
        }
        else
            activity.manager.bindToService(activity);
    }
    private void onExitButtonClick(Activity activity) {
        if (Manager.boundToService) {
            Manager.binder.StopAdventure();
            Manager.binder.StopService();
        }
        activity.Close(true);
    }
}
