package com.example.overlaying;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class TouchBox extends View {

    Paint paint = new Paint();
    public int updateCounter = 0;

    private Entity entity;
    private WindowManager windowManager;
    WindowManager.LayoutParams params = (WindowManager.LayoutParams)getLayoutParams();
    public TouchBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public TouchBox(Context context, AttributeSet attrs, int size, Entity entity)
    {
        this(context, attrs);
        this.entity = entity;
        this.setMinimumHeight(size);
        this.setMinimumWidth(size);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        paint.setAlpha(40);
    }

    public void setCenter(int newX, int newY)
    {
        if(this.getWindowToken() == null) return;
        params = (WindowManager.LayoutParams) getLayoutParams();
        params.x = newX - (getWidth() / 2);
        params.y = newY - (getWidth() / 2);

        windowManager.updateViewLayout(this, params);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return entity.onTouchEvent(event);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        if(MainActivity.ENTITY_DEBUG_VISUALS) canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    }
}
