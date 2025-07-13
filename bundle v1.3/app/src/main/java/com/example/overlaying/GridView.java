package com.example.overlaying;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;


public class GridView extends View {
    private Vector2 offset = new Vector2();
    private AreaType areaType;
    public float radius;
    private static int cellSize = 60;
    Path clipPath = new Path();
    Paint gridPaint = new Paint();

    public GridView(Context context) {
        this(context, 80, 60);
    }
    public GridView(Context context, float _radius, int _cellSize) {
        this(context, _radius, _cellSize, Color.BLACK, AreaType.CIRCLE);
    }
    public GridView(Context context, float _radius, int _cellSize, int _gridColor, AreaType _areaType) {
        super(context, null);
        this.setMinimumWidth((int)_radius * 2);
        this.setMinimumHeight((int)_radius * 2);

        areaType = _areaType;
        radius = _radius;
        cellSize = _cellSize;
        
        gridPaint.setColor(_gridColor);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAlpha(180);

        if(areaType == AreaType.CIRCLE)
            clipPath.addCircle(radius, radius, radius, Path.Direction.CW);
        if(areaType == AreaType.SQUARE)
            clipPath.addRect(0, 0, radius * 2, radius * 2, Path.Direction.CW);

    }
    public enum AreaType{
        CIRCLE,
        SQUARE
    }
    public void setPosition(Vector2 position)
    {
        this.setX((float)position.x - radius);
        this.setY((float)position.y - radius);
        offset.set(position.mod(cellSize).plus(cellSize - radius, cellSize - radius));
        invalidate();
    }
    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.clipPath(clipPath);

        // Canvas grid outline
        if(MainActivity.ENTITY_DEBUG_VISUALS) canvas.drawCircle(radius, radius, radius, gridPaint);

        int left = (int)(-offset.x - cellSize);
        int right = (int)(left + radius * 2 + cellSize * 1.5);
        int top = (int)(-offset.y - cellSize);
        int bottom = (int)(top + radius * 2 + cellSize * 1.5);

        for (int row = top; row <= bottom; row += cellSize) {
            canvas.drawLine(left, row, right, row, gridPaint);
        }
        for (int col = left; col <= right; col += cellSize) {
            canvas.drawLine(col, top, col, bottom, gridPaint);
        }
    }
}
