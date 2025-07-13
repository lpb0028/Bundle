package com.example.overlaying;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BlockGrid {
    private final int[][] blocks;
    private final GridLayout gridLayout;

    public BlockGrid(ViewGroup parent, int[][] blockResources) {
        System.out.println(blockResources.length + " " + blockResources[0].length);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        GridLayout gridLayout = (GridLayout) inflater.inflate(R.layout.block_grid_layout, null);
        parent.addView(gridLayout);
        gridLayout.setMinimumWidth(parent.getMinimumWidth());
        gridLayout.setMinimumHeight(parent.getMinimumHeight());

        loadFromResources(gridLayout, blockResources);

        for(int i = 0; i < blockResources.length; i++)
            for(int j = 0; j < blockResources[0].length; j++)
                if(blockResources[i][j] == 0) blockResources[i][j] = R.drawable.air;
        blocks = blockResources;
        this.gridLayout = gridLayout;
    }
    public static void loadFromResources(GridLayout existingGrid, int[][] blockResources) {
        System.out.println("Loading Grid of size " + blockResources.length + ", " + blockResources[0].length);
        existingGrid.setColumnCount(blockResources[0].length);
        existingGrid.setRowCount(blockResources.length);
        LayoutInflater inflater = LayoutInflater.from(existingGrid.getContext());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ImageView[] loadList = new ImageView[blockResources.length * blockResources[0].length];
            int loadCount = 0;
            for(int i = 0; i < blockResources.length; i++) {
                for(int j = 0; j < blockResources[i].length; j++) {
                    ImageView image = (ImageView) inflater.inflate(R.layout.block_grid_item, null);
                    image.setMaxWidth(Settings.CELL_SIZE);
                    image.setMaxHeight(Settings.CELL_SIZE);

                    if(blockResources[i][j] == 0) {
                        if (Settings.SHOW_DEBUG_BLOCKS) {
                            blockResources[i][j] = Settings.debugRenderBlocks[(blockResources[i].length * i + j) % Settings.debugRenderBlocks.length];
                        }
                        else
                            blockResources[i][j] = R.drawable.air;
                    }

                    image.setImageResource(blockResources[i][j]);
                    loadList[loadCount] = image;
                    loadCount++;
                }
            }
            handler.post(() -> {
                for(ImageView i : loadList)
                    existingGrid.addView(i);
            });
            System.out.println("Loaded Successfully");
        });
        executor.shutdown();
    }
    public void setBlock(Vector2Int pos, int blockResource) {
        setBlock(pos, blockResource, false);
    }
    private void setBlock(Vector2Int pos, int blockResource, boolean skipLog) {
        if(pos.x < 0 || pos.y < 0 || pos.y >= blocks.length || pos.x >= blocks[pos.x].length) {
            if(!skipLog) System.out.println("Invalid setBlock position: (" + pos.x + ", " + pos.y + ")");
            return;
        }
        if(blockResource == 0) return;
        blocks[pos.y][pos.x] = blockResource;
        ImageView image = ((ImageView) gridLayout.getChildAt(blocks[0].length * pos.y + pos.x));
        if(image != null) image.setImageResource(blockResource);
        if(!skipLog) System.out.println("Set block at (" + pos.x + ", " + pos.y + ")");
    }

    public void removeBlock(Vector2Int pos) {
        setBlock(pos, R.drawable.air);
    }
    public int getBlock(Vector2Int pos) {
        if(pos.x < 0 || pos.y < 0 || pos.y >= blocks.length || pos.x >= blocks[pos.x].length) return -1;
        return blocks[pos.y][pos.x];
    }
    public Vector2Int rayCastToBlock(Vector2 position, int direction) {
        Vector2Int blockPos = new Vector2Int((int)position.x / Settings.CELL_SIZE, (int)position.y / Settings.CELL_SIZE);
        switch(direction) {
            case 4: // Left
                for (int i = blockPos.x; i >= 0; i--) {
                    //System.out.println("Searched " + MyDictionary.searchBlock(blocks[blockPos.y][i]) + " at position (" + i + ", " + blockPos.y + ")");
                    if (blocks[blockPos.y][i] != R.drawable.air)
                        return new Vector2Int((i + 1) * Settings.CELL_SIZE, (int)position.y);
                }
                return new Vector2Int(Integer.MIN_VALUE, (int)position.y);
            case 3: // Up
                for (int i = blockPos.y; i >= 0; i--) {
                    //System.out.println("Searched " + MyDictionary.searchBlock(blocks[i][blockPos.x]) + " at position (" + blockPos.x + ", " + i + ")");
                    if (blocks[i][blockPos.x] != R.drawable.air)
                        return new Vector2Int((int)position.x, (i + 1) * Settings.CELL_SIZE);
                }
                return new Vector2Int((int)position.x, Integer.MIN_VALUE);
            case 2: // Right
                for (int i = blockPos.x; i < blocks[0].length; i++) {
                    //System.out.println("Searched " + MyDictionary.searchBlock(blocks[blockPos.y][i]) + " at position (" + i + ", " + blockPos.y + ")");
                    if (blocks[blockPos.y][i] != R.drawable.air)
                        return new Vector2Int(i * Settings.CELL_SIZE, (int)position.y);
                }
                return new Vector2Int(Integer.MAX_VALUE, (int)position.y);
            default: // Down
                for (int i = blockPos.y; i < blocks.length; i++) {
                    //System.out.println("Searched " + MyDictionary.searchBlock(blocks[i][blockPos.x]) + " at position (" + blockPos.x + ", " + i + ")");
                    if (blocks[i][blockPos.x] != R.drawable.air)
                        return new Vector2Int((int) position.x, i * Settings.CELL_SIZE);
                }
                return new Vector2Int((int)position.x, Integer.MAX_VALUE);
        }
    }
    public Vector2Int getSize() {
        return new Vector2Int(blocks[0].length - 1, blocks.length - 1);
    }
    public void fillAll(int blockResource) {
        for(int i = 0; i < blocks.length; i++) {
            for(int j = 0; j < blocks[i].length; j++) {
                setBlock(new Vector2Int(j, i), blockResource, true);
            }
        }
        System.out.println("Filled all as " + MyDictionary.searchBlock(blockResource));
    }
    public void fill(Vector2Int pos1, Vector2Int pos2, int blockResource) {
        for(int i = Math.max(0, Math.min(pos1.x, pos2.x)); i <= Math.min(Math.max(pos1.x, pos2.x), blocks[0].length - 1); i++) {
            for(int j = Math.max(0, Math.min(pos1.y, pos2.y)); j <= Math.min(Math.max(pos1.y, pos2.y), blocks.length - 1); j++) {
                setBlock(new Vector2Int(i, j), blockResource, true);
            }
        }
        System.out.println("Filled from " + new Vector2Int(Math.max(0, Math.min(pos1.x, pos2.x)), Math.max(0, Math.min(pos1.y, pos2.y))) + " to " + new Vector2Int(Math.min(Math.max(pos1.x, pos2.x), blocks[0].length - 1), Math.min(Math.max(pos1.y, pos2.y), blocks.length - 1)) + " as " + MyDictionary.searchBlock(blockResource));
    }
    public void fillAsResources(Vector2Int pos, int[][] blockResources) {
        for(int i = 0; i < blockResources[0].length; i++) {
            for(int j = 0; j < blockResources.length; j++) {
                setBlock(new Vector2Int(pos.x + i, pos.y + j), blockResources[j][i]);
            }
        }
    }
}
