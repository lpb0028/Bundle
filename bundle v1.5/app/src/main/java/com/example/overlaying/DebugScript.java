package com.example.overlaying;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class DebugScript {
    public DebugScript(Context context, Manager manager) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LinearLayout frame = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.toast_frame, null);
        Handler h = new Handler(Looper.getMainLooper());

        System.out.println("Added toast_frame");
        wm.addView(frame, Settings.TRANSPARENT_FRAME_PARAMS);

        h.postDelayed(() -> {
            wm.removeView(frame);
            FrameLayout frame2 = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.entity_frame, null);

            System.out.println("Added entity_frame");
            wm.addView(frame2, Settings.TRANSPARENT_FRAME_PARAMS);

            h.postDelayed(() -> {
                wm.removeView(frame2);
                System.out.println("Cleared");
            }, 5000);

        }, 5000);



        // Test Toast interaction
        /*
        if(Settings.SEND_DEBUG_MESSAGES) System.out.println(Manager.boundToService);
        manager.toastManager.sendToast("serviceBound1: " + Manager.boundToService, 5000);
        manager.toastManager.sendToast("serviceBound2: " + Manager.boundToService, 5000);
        manager.toastManager.sendToast("serviceBound3: " + Manager.boundToService, 5000);
        manager.toastManager.sendToast("serviceBound4: " + Manager.boundToService, 5000);
        manager.toastManager.sendToast("serviceBound5: " + Manager.boundToService, 5000);
        manager.toastManager.sendToast("serviceBound6: " + Manager.boundToService, 5000);
        manager.toastManager.sendToast("serviceBound7: " + Manager.boundToService, 5000);
        manager.toastManager.sendToast("serviceBound8: " + Manager.boundToService, 5000);
        manager.toastManager.sendToast("serviceBound9: " + Manager.boundToService, 5000);
        manager.toastManager.sendToast("serviceBound10: " + Manager.boundToService, 5000);
        */

        // Test Window View movement
        /*
        TouchHitBox box = new TouchHitBox(context, null, 40, null);
        wm.addView(box, Settings.HITBOX_PARAMS);
        // Center to right side of screen
        box.setCenter(0, 0);
        h.postDelayed(() -> box.setCenter(100, 0), 2000);
        h.postDelayed(() -> box.setCenter(200, 0), 4000);
        h.postDelayed(() -> box.setCenter(300, 0), 6000);
        h.postDelayed(() -> box.setCenter(400, 0), 8000);
        h.postDelayed(() -> box.setCenter(500, 0), 10000);
        h.postDelayed(() -> box.setCenter(600, 0), 12000);
        h.postDelayed(() -> box.setCenter(700, 0), 14000);

        // Center to bottom of screen
        h.postDelayed(() -> box.setCenter(0, 0), 16000);
        h.postDelayed(() -> box.setCenter(0, 100), 18000);
        h.postDelayed(() -> box.setCenter(0, 200), 20000);
        h.postDelayed(() -> box.setCenter(0, 300), 22000);
        h.postDelayed(() -> box.setCenter(0, 400), 24000);
        h.postDelayed(() -> box.setCenter(0, 500), 26000);
        h.postDelayed(() -> box.setCenter(0, 600), 28000);
        h.postDelayed(() -> box.setCenter(0, 700), 30000);
        h.postDelayed(() -> box.setCenter(0, 800), 32000);
        h.postDelayed(() -> box.setCenter(0, 900), 34000);
        h.postDelayed(() -> box.setCenter(0, 1000), 36000);
        h.postDelayed(() -> box.setCenter(0, 1100), 38000);
        h.postDelayed(() -> box.setCenter(0, 1200), 40000);

        // Corners
        h.postDelayed(() -> box.setCenter(1000, 1000), 42000);
        h.postDelayed(() -> box.setCenter(-1000, 1000), 44000);
        h.postDelayed(() -> box.setCenter(-1000, -1000), 46000);
        h.postDelayed(() -> box.setCenter(1000, -1000), 48000);
        */
    }
}
