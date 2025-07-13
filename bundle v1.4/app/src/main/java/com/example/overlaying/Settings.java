package com.example.overlaying;

import android.graphics.PixelFormat;
import android.view.WindowManager;

public class Settings {
    public static final int CELL_SIZE = 60;
    // Timings
    public static final int HITBOX_UPDATE_FRAMES = 15;
    public static final double JUMP_ANIM_DURATION = .5;
    public static final double HURT_ANIM_DURATION = .5;
    // Chance to spawn an entity every frame, measured in spawns per frame (60fps)
    public static final int ENTITY_SPAWN_CHANCE =  300; // About 1 per 5 seconds
    // Reduction factor multiplied by the number of currently-alive entities
    public static final int CROWD_REDUCTION_FACTOR = 60; // Increases time between spawns by about 1 second per existing entity
    //region private static final WindowManager.LayoutParams[] params;
    public static final WindowManager.LayoutParams GRIDVIEW_PARAMS = new WindowManager.LayoutParams(
            Settings.CELL_SIZE * 3,
            Settings.CELL_SIZE * 3,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.RGBA_8888
    );
    public static final WindowManager.LayoutParams ENTITY_PARAMS = new WindowManager.LayoutParams(
            (int)(Settings.CELL_SIZE * .7f),
            (int)(Settings.CELL_SIZE * 1.8f),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    public static final WindowManager.LayoutParams HITBOX_PARAMS = new WindowManager.LayoutParams(
            Settings.CELL_SIZE * 3,
            Settings.CELL_SIZE * 3,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    public static final WindowManager.LayoutParams FRAME_LAYOUT_PARAMS = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.RGBA_8888
    );
    //endregion

    public static boolean SHOW_DEBUG_VISUALS = true;
    public static boolean SEND_DEBUG_MESSAGES = true;
    public static boolean SEND_ENTITY_STATUS_MESSAGES = false;

    static {
        FRAME_LAYOUT_PARAMS.alpha = 0.8f;
    }
}
