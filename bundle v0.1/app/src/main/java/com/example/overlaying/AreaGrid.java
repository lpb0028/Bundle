package com.example.overlaying;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;


public class AreaGrid {
    private Vector2 position = new Vector2();
    private final AreaType areaType;
    public float size;
    private final int cellSize;
    private int numRows;
    private int numColumns;
    public Vector2 targetBox = new Vector2();
    Path clipPath = new Path();
    public final Bitmap myBitmap;
    private final Canvas myCanvas;
    private final Canvas sharedCanvas;
    Paint gridPaint = new Paint();
    Paint maskPaint = new Paint();
    Vector2 center = new Vector2();
    Vector2 topLeft = new Vector2();
    Vector2 botRight = new Vector2();

    public AreaGrid(Context context, Vector2 position, float _size, int _cellSize)
    {
        this(context, position, _size, _cellSize, Color.LTGRAY, AreaType.CIRCLE);
    }
    public AreaGrid(Context context, Vector2 _position, float _size, int _cellSize, int _gridColor, AreaType _areaType) {
        MainActivity ma = ((MainActivity) context);
        if(position != null)
            position = _position;
        areaType = _areaType;
        size = _size;
        cellSize = _cellSize;
        setSize(ma.display.heightPixels, ma.display.widthPixels);

        maskPaint.setColor(Color.GRAY);
        maskPaint.setAlpha(180);
        maskPaint.setTextSize(48);
        gridPaint.setColor(_gridColor);
        gridPaint.setStyle(Paint.Style.STROKE);

        myBitmap = Bitmap.createBitmap(ma.display.widthPixels, ma.display.heightPixels, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas(myBitmap);
        sharedCanvas = ma.canvas;
    }
    public enum AreaType{
        CIRCLE,
        SQUARE
    }
    public void setSize(int height, int width)
    {
        numRows = (int)Math.ceil((double) height / cellSize);
        numColumns = (int)Math.ceil((double) width / cellSize);
    }
    public Vector2 getGridSize()
    {
        return new Vector2(numColumns, numRows);
    }
    public void setPosition(Vector2 _position)
    {
        position.set(_position);
    }

    public void doFrame(int id)
    {
        // Clearing myBitmap
        myCanvas.drawColor(Color.TRANSPARENT);
        myBitmap.eraseColor(Color.TRANSPARENT);

        myCanvas.save();

        clipPath.reset();
        if(areaType == AreaType.CIRCLE)
            clipPath.addCircle((float)position.x, (float)position.y, size, Path.Direction.CW);
        if(areaType == AreaType.SQUARE)
            clipPath.addRect((float)position.x - size, (float)position.y - size, (float)position.x + size, (float)position.y + size, Path.Direction.CW);
        myCanvas.clipPath(clipPath);

        // Canvas grid circle
        //myCanvas.drawCircle((float)position.x, (float)position.y, size, maskPaint);

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                center.set((col * cellSize) + (cellSize * .5), (row * cellSize) + (cellSize * .5));
                if (center.x > (float) position.x - size - (size / 5) && center.x < (float) position.x + size + (size / 5) && center.y > (float) position.y - size - (size / 5) && center.y < (float) position.y + size + (size / 5)) {
                    topLeft.set(col * cellSize, row * cellSize);
                    botRight.set(topLeft.x + cellSize, topLeft.y + cellSize);
                    myCanvas.drawRect((float) topLeft.x, (float) topLeft.y, (float) botRight.x, (float) botRight.y, gridPaint);
                }
            }
        }
        myCanvas.restore();

        myCanvas.drawRect((float)targetBox.x, (float)targetBox.y, (float)targetBox.x + cellSize, (float)targetBox.y + cellSize, maskPaint);
        myCanvas.drawRect((float)position.x - cellSize * .35f, (float)position.y - cellSize * 1.8f, (float)position.x + cellSize * .35f, (float)position.y, maskPaint);
        myCanvas.drawText(String.valueOf(id), (float)position.x - cellSize * .2f, (float)position.y - cellSize * .2f, maskPaint);
        myCanvas.drawText(position.round().toString(), (float)position.x + cellSize * .5f, (float)position.y - cellSize * .2f, gridPaint);
        myCanvas.drawText(String.valueOf(id), (float)targetBox.x + cellSize * .3f, (float)targetBox.y + cellSize * .8f, maskPaint);

        sharedCanvas.drawBitmap(myBitmap, 0, 0, null);
    }
}
