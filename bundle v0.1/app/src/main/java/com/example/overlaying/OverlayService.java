package com.example.overlaying;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.WindowManager;

public class OverlayService extends Service {
    private CanvasView canvasView;
    private Context context;

    public void Initialize(Context context, CanvasView canvasView) {
        this.context = context;
        this.canvasView = canvasView;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.RGBA_8888
        );

        params.alpha = .8f;

        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.addView(canvasView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.removeView(canvasView);
        this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
