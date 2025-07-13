package com.example.overlaying;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class CloudView extends View {
    private final Paint bottomPaint;
    private final Paint sidePaint;
    private final Path path;
    private final int cloudHeightPixels = 20;
    private final int cloudWidthPixels = 140;
    private final int cloudLengthPixels = 15;
    private final float distanceWidthFactor = 1;
    private final float distanceLengthFactor = 5;
    private final float distanceHeightFactor = 1;
    private final int viewWidth = 9;
    private int leftmostColumn = 0;
    private int offset = 0;
    private final int[][] cloudList = { // region cloudList
            {0, 0, 1, 1, 0, 1, 0, 1},
            {0, 1, 1, 1, 1, 0, 1, 1},
            {0, 1, 1, 1, 0, 0, 1, 1},
            {0, 0, 1, 1, 1, 0, 1, 0},
            {1, 0, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0},
            {0, 1, 0, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0},
            {1, 0, 0, 1, 1, 0, 0, 1},
            {0, 0, 0, 1, 0, 0, 1, 1},
            {0, 1, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 0, 1},
            {0, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 0, 1, 0, 1, 1},
            {1, 1, 1, 0, 0, 1, 1, 1},
            {0, 1, 1, 1, 1, 1, 1, 0},
            {0, 1, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
    }; // endregion cloudList

    // Points
    private final Vector2 topLeft = new Vector2();
    private final Vector2 topRight = new Vector2();
    private final Vector2 botRight = new Vector2();
    private final Vector2 botLeft = new Vector2();

    public CloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bottomPaint = new Paint();
        sidePaint = new Paint();
        path = new Path();
        bottomPaint.setColor(Color.LTGRAY);
        sidePaint.setColor(Color.WHITE);
        bottomPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        sidePaint.setStyle(Paint.Style.FILL);
        bottomPaint.setAntiAlias(true);
        sidePaint.setAntiAlias(true);
        bottomPaint.setAlpha(200);
        sidePaint.setAlpha(200);
        Handler handler = new Handler(context.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
                handler.postDelayed(this, 32);
            }
        };
        handler.post(runnable);
    }
    @Override
    public void onDraw(Canvas canvas) {
        float midColumn = viewWidth / 2f;
        int centerX = getWidth() / 2;
        offset++;
        if(offset >= cloudWidthPixels / 2) {
            offset = -cloudWidthPixels / 2;
            leftmostColumn = (leftmostColumn + 1) % cloudList.length;
        }
        for (int j = cloudList[0].length - 1; j >= 0; j--) {
            for(int i = 0; i < viewWidth; i++) {
                int col = (leftmostColumn + i) % cloudList.length;
                if (cloudList[col][j] == 1) {
                    topLeft.set(centerX + (i - midColumn - (float) offset / cloudWidthPixels) * (cloudWidthPixels - j * distanceWidthFactor), j * (cloudLengthPixels + distanceLengthFactor * ((float) (cloudList[col].length - j) / cloudList[col].length)));
                    topRight.set(centerX + (i + 1 - midColumn - (float) offset / cloudWidthPixels) * (cloudWidthPixels - j * distanceWidthFactor), j * (cloudLengthPixels + distanceLengthFactor * ((float) (cloudList[col].length - j) / cloudList[col].length)));
                    botRight.set(centerX + (i + 1 - midColumn - (float) offset / cloudWidthPixels) * (cloudWidthPixels - (j + 1) * distanceWidthFactor), (j + 1) * (cloudLengthPixels + distanceLengthFactor * ((float) (cloudList[col].length - j - 1) / cloudList[col].length)));
                    botLeft.set(centerX + (i - midColumn - (float) offset / cloudWidthPixels) * (cloudWidthPixels - (j + 1) * distanceWidthFactor), (j + 1) * (cloudLengthPixels + distanceLengthFactor * ((float) (cloudList[col].length - j - 1) / cloudList[col].length)));

                    // Draw sides of cloud
                    path.reset();
                    path.moveTo((float) botLeft.x, (float) botLeft.y);
                    if(i > midColumn)
                        path.lineTo((float) botLeft.x, (float) botLeft.y - cloudHeightPixels - distanceHeightFactor * (j + 1));
                    else
                        path.lineTo((float) topLeft.x, (float) topLeft.y);
                    path.lineTo((float) topLeft.x, (float) topLeft.y - cloudHeightPixels - distanceHeightFactor * j);
                    path.lineTo((float) topRight.x, (float) topRight.y - cloudHeightPixels - distanceHeightFactor * j);
                    if (i >= midColumn - 1)
                        path.lineTo((float) topRight.x, (float) topRight.y);
                    else
                        path.lineTo((float) botRight.x, (float) botRight.y - cloudHeightPixels - distanceHeightFactor * (j + 1));
                    path.lineTo((float) botRight.x, (float) botRight.y);
                    path.close();
                    canvas.drawPath(path, sidePaint);
                }
            }
            for(int i = 0; i < viewWidth; i++) {
                int col = (leftmostColumn + i) % cloudList.length;
                if (cloudList[col][j] == 1) {
                    topLeft.set(centerX + (i - midColumn - (float) offset / cloudWidthPixels) * (cloudWidthPixels - j * distanceWidthFactor), j * (cloudLengthPixels + distanceLengthFactor * ((float) (cloudList[col].length - j) / cloudList[col].length)));
                    topRight.set(centerX + (i + 1 - midColumn - (float) offset / cloudWidthPixels) * (cloudWidthPixels - j * distanceWidthFactor), j * (cloudLengthPixels + distanceLengthFactor * ((float) (cloudList[col].length - j) / cloudList[col].length)));
                    botRight.set(centerX + (i + 1 - midColumn - (float) offset / cloudWidthPixels) * (cloudWidthPixels - (j + 1) * distanceWidthFactor), (j + 1) * (cloudLengthPixels + distanceLengthFactor * ((float) (cloudList[col].length - j - 1) / cloudList[col].length)));
                    botLeft.set(centerX + (i - midColumn - (float) offset / cloudWidthPixels) * (cloudWidthPixels - (j + 1) * distanceWidthFactor), (j + 1) * (cloudLengthPixels + distanceLengthFactor * ((float) (cloudList[col].length - j - 1) / cloudList[col].length)));

                    // Draw bottom of cloud
                    path.reset();
                    path.moveTo((float) topLeft.x, (float) topLeft.y);  // Start top left
                    path.lineTo((float) topRight.x, (float) topRight.y);// Move top right
                    path.lineTo((float) botRight.x, (float) botRight.y);// Move bottom right
                    path.lineTo((float) botLeft.x, (float) botLeft.y);  // Move bottom left
                    path.close();                                       // Close shape, draw
                    canvas.drawPath(path, bottomPaint);
                }
            }
        }
    }
}
