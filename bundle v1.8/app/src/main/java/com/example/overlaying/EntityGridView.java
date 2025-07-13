package com.example.overlaying;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;


public class EntityGridView extends View {
    // Drawing offset relative to position
    private final Vector2 offset = new Vector2();
    // Shape and size of mask
    public AreaShape areaShape;
    public float radius;
    Path clipPath = new Path();
    // Grid-drawing paint
    Paint gridPaint = new Paint();

    public EntityGridView(Context context) {
        this(context, 80);
    }
    public EntityGridView(Context context, float _radius) {
        this(context, _radius, Color.BLACK, AreaShape.CIRCLE);
    }
    public EntityGridView(Context context, float _radius, int _gridColor, AreaShape _areaShape) {
        super(context, null);

        // Specify minimum size so as not to be overwritten
        this.setMinimumWidth((int)_radius * 2);
        this.setMinimumHeight((int)_radius * 2);

        // Set values
        areaShape = _areaShape;
        radius = _radius;

        // Modify paint
        gridPaint.setColor(_gridColor);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAlpha(180); // 180/255

        // Set clip mask
        if(areaShape == AreaShape.CIRCLE)
            clipPath.addCircle(radius, radius, radius, Path.Direction.CW);
        if(areaShape == AreaShape.SQUARE)
            clipPath.addRect(0, 0, radius * 2, radius * 2, Path.Direction.CW);
    }

    // Shape of the clip mask
    public enum AreaShape {
        CIRCLE,
        SQUARE
    }

    // Changes position of gridview (to match associated entity)
    public void setPosition(Vector2 position) {
        // Change position
        this.setX((float)position.x - radius);
        this.setY((float)position.y - radius);
        // Change offset
        offset.set(position.mod(Settings.CELL_SIZE).plus(Settings.CELL_SIZE - radius, Settings.CELL_SIZE - radius));
        // Request a redraw
        invalidate();
    }
    public Vector2 getPosition() {
        return new Vector2(this.getX(), this.getY());
    }

    // Called every frame to redraw/move grid
    @Override
    public void onDraw(Canvas canvas) {
        canvas.clipPath(clipPath);

        // Debug outline
        if(Settings.SHOW_DEBUG_VISUALS) {
            if(areaShape == AreaShape.CIRCLE) canvas.drawCircle(radius, radius, radius, gridPaint);
            if(areaShape == AreaShape.SQUARE) canvas.drawRect(-radius / 2, -radius / 2, radius / 2, radius / 2, gridPaint);
        }

        // Find drawing positions relative to offsets
        int left = (int)(-offset.x - Settings.CELL_SIZE);
        int right = (int)(left + radius * 2 + Settings.CELL_SIZE * 1.5);
        int top = (int)(-offset.y - Settings.CELL_SIZE);
        int bottom = (int)(top + radius * 2 + Settings.CELL_SIZE * 1.5);

        // Draw grid
        for (int row = top; row <= bottom; row += Settings.CELL_SIZE) {
            canvas.drawLine(left, row, right, row, gridPaint);
        }
        for (int col = left; col <= right; col += Settings.CELL_SIZE) {
            canvas.drawLine(col, top, col, bottom, gridPaint);
        }
    }
}
