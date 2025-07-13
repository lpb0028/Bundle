package com.example.overlaying;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

public class ToastQueue {
    private final Queue<CharSequence> toastQueue = new LinkedList<>();
    private final Queue<Integer> durationQueue = new LinkedList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context context;
    private boolean isShowing = false;

    public ToastQueue(Context context) {
        this.context = context;
    }

    public void addToast(CharSequence message, int duration) {
        toastQueue.add(message);
        durationQueue.add(duration);
        if (!isShowing) {
            showNextToast();
        }
    }

    private void showNextToast() {
        CharSequence message = toastQueue.poll();
        if (message == null) return;

        handler.post(() -> {
            isShowing = true;
            Toast toast = new Toast(context);
            TextView text = new TextView(context);
            text.setText(message);
            text.setBackgroundResource(R.drawable.rounded_background);
            text.setTextColor(Color.BLACK);
            toast.setView(text);
            toast.show();
            //t.show();
            handler.postDelayed(() -> {
                toast.cancel();
                isShowing = false;
                if (!toastQueue.isEmpty()) {
                    showNextToast();
                }
            }, durationQueue.poll()); // Duration of the toast
        });

    }
}
