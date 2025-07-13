package com.example.overlaying;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToastManager {
    private final Context context;
    private int activeToasts = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final LinearLayout toastFrame;

    private Manager manager;

    public ToastManager(Context context, Manager manager) {
        this.context = context;
        this.manager = manager;
        toastFrame = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.toast_frame, null);
        manager.transparentFrame.addView(toastFrame);
    }

    public void Close() {
        if(toastFrame.getParent() != null)
            manager.transparentFrame.removeView(toastFrame);
    }

    public int getActiveToasts() {
        return activeToasts;
    }

    // Send new toast with given parameters
    public void sendToast(CharSequence message, int duration) {
        // Ensure valid parameters
        if (message == null || duration <= 0) return;
        activeToasts++;

        // Schedule next message with handler to ensure run on UI thread
        handler.post(() -> {
            // Inflate custom toast layout
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.custom_toast, null);
            TextView text = layout.findViewById(R.id.custom_toast_message);

            text.setText(message);
            toastFrame.addView(layout, 0, Settings.TOAST_PARAMS);

            // Apply fade-in animation
            Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            layout.startAnimation(fadeIn);
            text.startAnimation(fadeIn);

            // Load fade-out animation
            Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);

            // Remove on delay and show next (if applicable)
            handler.postDelayed(() -> {
                // Apply fade-out animation
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        toastFrame.removeView(layout);
                        activeToasts--;
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                text.startAnimation(fadeOut);
                layout.startAnimation(fadeOut);
            }, Math.max(duration - fadeOut.getDuration(), fadeIn.getDuration())); // Ensure positive delay duration, ? statement for quick display (if queue is not empty)
        });
    }
}
