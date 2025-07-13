package com.example.overlaying;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

public class BlockGrid {
    public static void loadGrid(View parent, int gridLayoutId, int[][] blockResources) {
        GridLayout gridLayout = parent.findViewById(gridLayoutId);
        gridLayout.setColumnCount(blockResources[0].length);
        gridLayout.setRowCount(blockResources.length);
        LayoutInflater inflater = LayoutInflater.from(gridLayout.getContext());

        for(int[] row : blockResources) {
            for(int block : row) {
                ImageView image = (ImageView) inflater.inflate(R.layout.block_grid_item, null);
                image.setMaxWidth(50);
                image.setMaxHeight(50);
                image.setImageResource(block);
                gridLayout.addView(image);
            }
        }
    }
}
