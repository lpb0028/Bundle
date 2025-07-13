package com.example.overlaying;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class TouchHitBox extends View {

    private Entity entity; // Associated entity (to inform of touches)
    public int updateCounter = 0; // Counter used to update every so often
    Paint paint = new Paint(); // Used for debug drawing
    private WindowManager windowManager; // Android window manager used for updating position

    public TouchHitBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public TouchHitBox(Context context, AttributeSet attrs, int size, Entity entity) {
        this(context, attrs);
        this.entity = entity;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // Set minimum size so as not to be overwritten
        this.setMinimumHeight(size);
        this.setMinimumWidth(size);
        paint.setAlpha(40);
    }

    // Reposition to new coordinates
    public void setCenter(Vector2 newCenter) {
        setCenter((int) newCenter.x, (int) newCenter.y);
    }
    public void setCenter(int newX, int newY) {
        if(this.getWindowToken() == null) return;
        WindowManager.LayoutParams params = (WindowManager.LayoutParams) getLayoutParams();

        params.x = newX - Manager.display.widthPixels / 2;
        params.y = newY - Manager.display.heightPixels / 2;

        windowManager.updateViewLayout(this, params);
    }

    // Send touch to associated entity
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return entity.onTouchEvent(event);
    }

    // Debug drawing hitbox
    @Override
    public void onDraw(Canvas canvas)
    {
        if(Settings.SHOW_DEBUG_VISUALS) canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }
}
