package com.example.overlaying;

import static android.content.Context.WINDOW_SERVICE;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.WindowManager;

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
    public final InventoryManager inventoryManager;
    public final ToastManager toastManager;

    private final Context context;

    // Functions
    public Manager(Context context) {
        // toastManager must declare before inventoryManager
        toastManager = new ToastManager(context);
        inventoryManager = new InventoryManager(context, this);
        ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(display);
        this.context = context;
    }
    public void closeManagers() {
        if(Settings.SEND_DEBUG_MESSAGES) System.out.println("Closed Managers");
        inventoryManager.writeInventoryToFile(3);
        toastManager.Close(context);
    }
    public void bindToService(Context context) {
        Intent intent = new Intent(context, com.example.overlaying.OverlayService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
