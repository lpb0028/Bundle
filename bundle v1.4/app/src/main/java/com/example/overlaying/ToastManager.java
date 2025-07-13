package com.example.overlaying;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import java.util.LinkedList;
import java.util.Queue;

public class ToastManager {
    private final Context context;

    private final Queue<CharSequence> toastQueue = new LinkedList<>();
    private boolean isShowing = false; // Message currently showing?

    // Queue and Handler to manage timings
    private final Queue<Integer> durationQueue = new LinkedList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ToastManager(Context context) {
        this.context = context;
    }

    // Add a toast to queue (or if queue empty display message now)
    public void addToast(CharSequence message, int duration) {
        toastQueue.add(message);
        durationQueue.add(duration);
        if (!isShowing)
            showNextToast();
    }

    // Show next message in queue (returns if queue empty)
    private void showNextToast() {
        // Get message and ensure non-null
        CharSequence message = toastQueue.poll();
        if (message == null) return;

        // Schedule next message with handler to ensure run on UI thread
        handler.post(() -> {
            isShowing = true;

            // Create text view
            TextView text = new TextView(context);
            text.setText(message);
            text.setBackgroundResource(R.drawable.rounded_background);
            text.setTextColor(Color.BLACK);

            // Create toast
            Toast toast = new Toast(context);
            toast.setView(text);
            toast.show();

            // Remove on delay and show next (if applicable)
            handler.postDelayed(() -> {
                toast.cancel();
                isShowing = false;
                if (!toastQueue.isEmpty()) {
                    showNextToast();
                }
            }, (durationQueue.poll())); // Duration of the toast
        });

    }
}
