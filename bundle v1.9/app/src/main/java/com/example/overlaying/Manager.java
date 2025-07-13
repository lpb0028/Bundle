package com.example.overlaying;

import static android.content.Context.WINDOW_SERVICE;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
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
            binder = (BundleService.ServiceBinder) iBinder;
            Manager.boundToService = true;
            System.out.println("Manager - Connected To Service");

            binder.Initialize(context, Manager.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
            Manager.boundToService = false;
            System.out.println("Manager - Disconnected from Service");
        }
    };
    public static BundleService.ServiceBinder binder;
    public static boolean serviceRunning = false;
    public static boolean boundToService = false;
    public static boolean adventuring = false;

    // Managers
    public final InventoryManager inventoryManager;
    public final ToastManager toastManager;

    // Frame on which all overlaid content is displayed
    public final FrameLayout transparentFrame;

    // Application Context
    private final Context context;

    // Functions
    public Manager(Context context) {
        // Add transparentFrame to WindowManager - frame must declare before sub-managers
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        transparentFrame = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.transparent_frame, null);
        windowManager.addView(transparentFrame, Settings.TRANSPARENT_FRAME_PARAMS);

        // Instantiate Managers - toastManager must declare before inventoryManager
        toastManager = new ToastManager(context.getApplicationContext(), this);
        inventoryManager = new InventoryManager(context, this);

        this.context = context;
    }
    public void Close() {
        if(Settings.SEND_DEBUG_MESSAGES) System.out.println("Manager - Closed Manager");
        inventoryManager.Close();
        toastManager.Close();
        context.stopService(new Intent(context, com.example.overlaying.BundleService.class));
        if(transparentFrame.getParent() != null)
            ((WindowManager) context.getSystemService(WINDOW_SERVICE)).removeView(transparentFrame);
    }
    public void bindToService(Context context) {
        System.out.println("Manager - Attempted Binding");
        Intent intent = new Intent(context, com.example.overlaying.BundleService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
