package com.example.overlaying;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class OverlayService extends Service {
    private FrameLayout frame;
    private WindowManager windowManager;

    public void Initialize(Context context, FrameLayout frame) {
        this.frame = frame;
        System.out.println(frame.isClickable());

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.RGBA_8888
        );

        params.alpha = .8f;

        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.addView(frame, params);
    }

    public void addView(View view, WindowManager.LayoutParams params)
    {
        System.out.println("Added view " + view);
        windowManager.addView(view, params);
    }

    public void removeView(View view)
    {
        System.out.println("Removed view " + view);
        windowManager.removeView(view);
    }

    @Override
    public void onDestroy() {
        windowManager.removeView(frame);
        super.onDestroy();
        this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
