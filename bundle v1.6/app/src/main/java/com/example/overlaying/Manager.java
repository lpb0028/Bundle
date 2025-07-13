package com.example.overlaying;

import static android.content.Context.WINDOW_SERVICE;

import android.app.WallpaperInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class Manager {
    // This class serves as a global variable retrieval point. All scripts can acquire data held here.
    // The goal is for either the service or the activity to always have a reference to this script, effectively
    // creating a singleton (but able to hold a context).

    // Service handling
    public final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (OverlayService.OverlayBinder) iBinder;
            Manager.boundToService = true;
            System.out.println("Connected");

            binder.Initialize(context,Manager.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
            Manager.boundToService = false;
            System.out.println("Disconnected");
        }
    };
    public static OverlayService.OverlayBinder binder;
    public static DisplayMetrics display = new DisplayMetrics();
    public static boolean serviceRunning = false;
    public static boolean boundToService = false;
    public static boolean adventuring = false;

    // Managers
    public final InventoryManager inventoryManager;
    public final ToastManager toastManager;

    private final WindowManager windowManager;

    // Display Frame used to draw/render all non-activity-context content.
    public final FrameLayout transparentFrame;

    private final Context context;

    // Functions
    public Manager(Context context) {
        // Add transparentFrame to WindowManager - frame must declare before sub-managers
        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        transparentFrame = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.transparent_frame, null);
        windowManager.addView(transparentFrame, Settings.TRANSPARENT_FRAME_PARAMS);
        windowManager.getDefaultDisplay().getMetrics(display);
        // Instantiate Managers - toastManager must declare before inventoryManager
        toastManager = new ToastManager(context.getApplicationContext(), this);
        inventoryManager = new InventoryManager(context, this);

        this.context = context;
    }
    public void close() {
        if(Settings.SEND_DEBUG_MESSAGES) System.out.println("Closed Manager");
        inventoryManager.writeInventoryToFile(3);
        toastManager.Close();
        windowManager.removeView(transparentFrame);
    }
    public void bindActivityToService(Context context) {
        Intent intent = new Intent(context, com.example.overlaying.OverlayService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
