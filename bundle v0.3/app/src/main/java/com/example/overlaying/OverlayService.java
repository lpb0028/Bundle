package com.example.overlaying;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

public class OverlayService extends Service {
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;

    public OverlayService(Context context) {
        System.out.println("Created");
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
    }

    public void addView(View view, WindowManager.LayoutParams params)
    {
        windowManager.addView(view, params);
    }
    public void removeView(View view)
    {
        windowManager.removeView(view);
    }
    public void setWindowPosition(View view, int newX, int newY)
    {
        if(view.getWindowToken() == null) return;
        params = (WindowManager.LayoutParams) view.getLayoutParams();
        params.x = newX;
        params.y = newY;

        windowManager.updateViewLayout(view, params);
        //view.setLayoutParams(params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("bind");
        return null;
    }
}
