package com.example.overlaying;

import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

public class Settings {

    // Notification Settings
    public static final String NOTIFICATION_CHANNEL_ID = "bundle_channel";
    // General Settings
    public static final int CELL_SIZE = 60;
    public static final float ENTITY_GRAVITY = 800f;
    public static final float ENTITY_KNOCKBACK_FORCE = 200;
    public static final float ENTITY_JUMP_FORCE = 365;
    public static final float HIT_INVULNERABILITY_DURATION = .4f;

    // Timings
    public static final int HITBOX_UPDATE_FRAMES = 15;

    // Chance to spawn an entity every frame, measured in spawns per frame (60fps)
    public static final int ENTITY_SPAWN_CHANCE =  300; // About 1 per 5 seconds
    // Reduction factor multiplied by the number of currently-alive entities
    public static final int CROWD_REDUCTION_FACTOR = 60; // Increases time between spawns by about 1 second per existing entity

    // Layout parameters
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
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.RGBA_8888
    );
    public static final WindowManager.LayoutParams HITBOX_PARAMS = new WindowManager.LayoutParams(
            Settings.CELL_SIZE * 3,
            Settings.CELL_SIZE * 3,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    public static final WindowManager.LayoutParams TRANSPARENT_FRAME_PARAMS = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.RGBA_8888
    );
    public static final WindowManager.LayoutParams TOAST_PARAMS = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.RGBA_8888);
    //endregion

    // Toggleables
    public static boolean SHOW_DEBUG_VISUALS = false;
    public static boolean SEND_DEBUG_MESSAGES = true;
    public static boolean SEND_ENTITY_STATUS_MESSAGES = true;
    public static boolean RUN_DEBUG_SCRIPT = false;
    public static boolean DRAW_CLOUDS = false;
    public static boolean SHOW_DEBUG_BLOCKS = false;
    public static final int[] debugRenderBlocks = new int[] {R.drawable.red_concrete, R.drawable.orange_concrete, R.drawable.yellow_concrete, R.drawable.green_concrete, R.drawable.blue_concrete, R.drawable.magenta_concrete, R.drawable.purple_concrete};

    static {
        TRANSPARENT_FRAME_PARAMS.alpha = 0.8f;
        TOAST_PARAMS.gravity = Gravity.RIGHT|Gravity.BOTTOM;
    }
}
