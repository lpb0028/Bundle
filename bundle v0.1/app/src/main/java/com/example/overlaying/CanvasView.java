package com.example.overlaying;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;

public class CanvasView extends View {
    private Bitmap sharedBitmap;
    public Context context;

    public CanvasView (Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public CanvasView (Context context, AttributeSet attrs, Bitmap _sharedBitmap)
    {
        super(context, attrs);
        this.context = context;
        sharedBitmap = _sharedBitmap;
        this.setBackgroundColor(Color.TRANSPARENT);
        
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(sharedBitmap, 0, 0, null);
    }
}
