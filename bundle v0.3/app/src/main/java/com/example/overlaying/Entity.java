package com.example.overlaying;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

public class Entity extends androidx.appcompat.widget.AppCompatImageView {

    public int id;
    private final Context context;

    // Positioning
    private final Vector2 position = new Vector2();
    private final Vector2 targetPosition = new Vector2();
    public GridView gridView;
    public Vector2 gridSize;
    private static int cellSize;
    private double stateDuration = 0;
    private double stateStartTime;
    private final Random random = new Random();
    public MoveState moveState = MoveState.IDLE;
    public ArrayList<Vector2> manualPoints = new ArrayList<>();//(Arrays.asList(new Vector2(300, 600), new Vector2(300, 180), new Vector2(300, 600), new Vector2(300, 300), new Vector2(300, 600), new Vector2(300, 180), new Vector2(300, 600)));

    // Animation
    private double prevTime;
    private double animTimer = 0;
    private final double animDuration = .4;
    private double animPosition;

    // Misc.
    public double moveSpeed;
    public double hp;
    public int groundState;
    Paint paint = new Paint();

    public Entity(Context context) {
        this(context, -1);
    }
    public Entity(Context context, int id) {
        this(context, id, null, 60, 2);
    }
    public Entity(Context context, int id, Vector2 _position, int _cellSize, double moveSpeed) {
        super(context, null);
        this.context = context;
        this.moveSpeed = moveSpeed;
        this.id = id;
        prevTime = SystemClock.elapsedRealtime() * .001;
        cellSize = _cellSize;

        this.setMinimumWidth((int)(_cellSize * .7f));
        this.setMinimumHeight((int)(_cellSize * 1.8f));

        System.out.println("Created entity " + id);
        gridView = new GridView(context);
        gridSize = new Vector2((double) ((MainActivity) context).display.widthPixels / cellSize,(double)((MainActivity) context).display.heightPixels / cellSize).round();
        if(_position == null)
            nextState(false, true);
        else
        {
            setPosition(_position);
            nextState(false);
        }

        paint.setColor(Color.BLUE);
        paint.setAlpha(100);
        paint.setTextSize(30);

        setClickable(true);
        setOnTouchListener((v, event) -> {
            System.out.println(event.getAction());
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                if (paint.getColor() == Color.GREEN)
                    paint.setColor(Color.RED);
                else
                    paint.setColor(Color.GREEN);
            }
            invalidate();
            return true;
        });
    }
    public Vector2 getPosition() { return position; }
    public Vector2 getTargetPos() { return targetPosition; }
    public void setPosition(Vector2 _position) {
        position.set(_position);
        ((MainActivity)context).service.setWindowPosition(this, (int)(position.x - cellSize * .35), (int)(position.y - cellSize * 1.8));
        gridView.setPosition(_position);
    }
    public enum MoveState
    {
        IDLE,
        MOVE,
        EXIT,
        ATTACK // TODO attack moveState
    }
    public void nextState(boolean canExit)
    {
        nextState(canExit, false);
    }
    public void nextState(boolean canExit, boolean newPosition)
    {
        if(moveState == MoveState.MOVE)
        {
            moveState = MoveState.IDLE;
            System.out.println("ID: " + id + " Changed state to " + moveState);
        }
        else if(moveState == MoveState.IDLE && moveSpeed > 0)
        {
            stateDuration = ((int)(((random.nextDouble() * 7) + 3) * 10)) * .1;

            if(manualPoints.size() > 0)
            {
                moveState = MoveState.MOVE;
                targetPosition.set(manualPoints.get(0).plus(new Vector2(cellSize * .5f, 0)));
                manualPoints.remove(0);
                System.out.println("ID: " + id + " FORCED state to " + moveState);
            }
            else if((int)(random.nextDouble() * 11) + 1 <= 10 || !canExit) {
                moveState = MoveState.MOVE;
                int newX = (int)(random.nextDouble() * (gridSize.x - 1)) * cellSize;
                int newY = (int)(position.y + (int)(((random.nextDouble() - .5) * ((gridSize.y - 2) * .5)) + 2) * cellSize);
                if(newY < cellSize * 2) newY = cellSize * 2;
                if(newY > gridSize.y * cellSize) newY = (int)(gridSize.y * cellSize);
                targetPosition.set(newX + cellSize * .5f, newY);
                System.out.println("ID: " + id + " Changed state to " + moveState);
            }
            else
            {
                moveState = MoveState.EXIT;
                int newX = (random.nextBoolean()? - (int) gridView.radius - cellSize : (int)gridSize.x * cellSize + (int) gridView.radius + cellSize);
                int newY = (int)(position.y + (int)(((random.nextDouble() - .5) * ((gridSize.y - 2) * .5))) * cellSize);
                targetPosition.set(newX + cellSize * .5f, newY);
                System.out.println("ID: " + id + " Changed state to " + moveState);
            }
        }
        if(newPosition)
        {
            int newX = (int)(random.nextDouble() * (gridSize.x - 1)) * cellSize;
            int newY = (int)(random.nextDouble() * (gridSize.y - 2) + 2) * cellSize;
            setPosition(new Vector2(newX + cellSize * .5f, newY));
            System.out.println("ID: " + id + " Also set new random position to: " + position);
        }
        stateStartTime = SystemClock.elapsedRealtime() * .001;
    }
    public void destroy()
    {
        System.out.println("Destroyed entity " + id);
        gridView = null;
        ((MainActivity) context).destroyEntity(this);
    }
    public void doFrame()
    {
        // Animation
        double elapsedTime = SystemClock.elapsedRealtime() * .001 - prevTime;
        prevTime = SystemClock.elapsedRealtime() * .001;
        if(position.y % cellSize == 0)
        {
            groundState = 0;
            animTimer = elapsedTime;
            animPosition = 0;
        }
        if(moveState == MoveState.IDLE)
        {
            if(stateDuration <= (prevTime - stateStartTime) && moveSpeed > 0)
            {
                System.out.println("ID: " + id + " Calling nextState on time up; Time = " + (prevTime - stateStartTime + " seconds"));
                nextState(true);
            }
        }
        if(moveState == MoveState.MOVE || moveState == MoveState.EXIT) {
            Vector2 movement = new Vector2(Vector2.direction(position, targetPosition).toOne().x * moveSpeed, 0);
            if (groundState == 0) {
                double dist = Vector2.distance(position, targetPosition);
                if(dist > 0 && dist < moveSpeed) {
                    movement.x = dist * movement.toOne().x;
                }
                double distanceDown = Vector2.distance(position.plus(new Vector2(0, cellSize)), targetPosition);
                double distanceUp = Vector2.distance(position.minus(new Vector2(0, cellSize)), targetPosition);
                double distanceOver = Vector2.distance(position.plus(new Vector2(movement.x * 12, 0)), targetPosition);

                if (distanceDown < distanceOver && moveSpeed > 0) {
                    // Fall
                    groundState = 1;
                } else if (distanceUp < distanceOver && moveSpeed > 0) {
                    // Jump
                    groundState = -1;
                }
                if (Vector2.distance(position, targetPosition) == 0) {
                    // Target reached, grounded; progress to next state
                    if(moveState == MoveState.EXIT)
                    {
                        this.destroy();
                        return;
                    }
                    nextState(false);
                }
            }
            if (groundState == -1) // Jumping up
            {
                // increment animTimer, evaluate animTimer / animDuration, rescale to cellSize and determine movement
                if(animTimer >= animDuration) {
                    movement.y = -(cellSize + animPosition);
                }
                else
                {
                    float eval = -(float)AnimationCurve.Evaluate(AnimationCurve.AnimForm.CUSTOM, animTimer / animDuration) * cellSize;
                    movement.y = eval - animPosition;
                }
                animTimer += elapsedTime;

            } if (groundState == 1) // Falling down
            {
                // increment animTimer, evaluate animTimer / animDuration, rescale to cellSize and determine movement
                if(animTimer >= animDuration)
                    movement.y = Math.max(cellSize, position.y % cellSize) - Math.min(cellSize, position.y % cellSize);
                else
                    movement.y = (float)AnimationCurve.Evaluate(AnimationCurve.AnimForm.CUBE, animTimer / animDuration) * cellSize - position.y % cellSize;
                animTimer += elapsedTime;
            }
            if(!position.plus(movement).round().equals(Math.round(getX()), Math.round(getY())))
            {
                setPosition(position.plus(movement));
                invalidate();
            }
            animPosition += movement.y;
        }
    }
    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawRect(0f, 0f, cellSize * .7f, cellSize * 1.8f, paint);
        canvas.drawText("(" + Math.round(position.x) + ", " + Math.round(position.y) + ")", cellSize, cellSize, paint);
        //canvas.drawCircle(cellSize * .35f, cellSize * 1.8f, 10, paint);
    }
}

