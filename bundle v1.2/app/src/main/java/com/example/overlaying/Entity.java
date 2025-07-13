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
    private OverlayService service;

    // Positioning
    private final Vector2 position = new Vector2();
    private final Vector2 targetPosition = new Vector2();
    public GridView gridView;
    public Vector2 gridSize;
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
    public int groundState;
    Paint paint = new Paint();

    public Entity(Context context) { super(context); }
    public Entity(Context context, OverlayService service) {
        this(context, service, -1);
    }
    public Entity(Context context, OverlayService service, int id) {
        this(context, service, id, null, 2);
    }
    public Entity(Context context, OverlayService service, int id, Vector2 _position, double moveSpeed) {
        super(context, null);
        this.service = service;
        this.moveSpeed = moveSpeed;
        this.id = id;
        prevTime = SystemClock.elapsedRealtime() * .001;

        this.setMinimumWidth((int)(service.cellSize * .7f));
        this.setMinimumHeight((int)(service.cellSize * 1.8f));

        System.out.println("Created entity " + id);
        gridView = new GridView(context);
        gridSize = new Vector2((double) service.display.widthPixels / service.cellSize,(double)service.display.heightPixels / service.cellSize).round();
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
    }
    public Vector2 getCenter() { return new Vector2(position.x, position.y - getHeight() / 2f); }
    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 _position) {
        position.set(_position);
        this.setX((int)(position.x - service.cellSize * .35));
        this.setY((int)(position.y - service.cellSize * 1.8));
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
        if(moveState == MoveState.MOVE && (position.y <= gridSize.y * service.cellSize && position.x <= gridSize.x * service.cellSize))
        {
            moveState = MoveState.IDLE;
            System.out.println("ID: " + id + " Changed state to " + moveState);
        }
        else if(moveSpeed > 0 && (moveState == MoveState.IDLE || (position.y >= gridSize.y * service.cellSize || position.x >= gridSize.x * service.cellSize)))
        {
            stateDuration = ((int)(((random.nextDouble() * 7) + 3) * 10)) * .1;

            if(manualPoints.size() > 0)
            {
                moveState = MoveState.MOVE;
                targetPosition.set(manualPoints.get(0).plus(new Vector2(service.cellSize * .5f, 0)));
                manualPoints.remove(0);
                System.out.println("ID: " + id + " FORCED state to " + moveState);
            }
            else if((int)(random.nextDouble() * 11) + 1 <= 10 || !canExit) {
                moveState = MoveState.MOVE;
                int newX = (int)(random.nextDouble() * (gridSize.x - 1)) * service.cellSize;
                int newY = (int)(position.y + (int)(((random.nextDouble() - .5) * ((gridSize.y - 2) * .5)) + 2) * service.cellSize);
                if(newY < service.cellSize * 2) newY = service.cellSize * 2;
                if(newY > gridSize.y * service.cellSize) newY = (int)(gridSize.y * service.cellSize);
                targetPosition.set(newX + service.cellSize * .5f, newY);
                System.out.println("ID: " + id + " Changed state to " + moveState);
            }
            else
            {
                moveState = MoveState.EXIT;
                int newX = (random.nextBoolean()? - (int) gridView.radius - service.cellSize : (int)gridSize.x * service.cellSize + (int) gridView.radius + service.cellSize);
                int newY = (int)(position.y + (int)(((random.nextDouble() - .5) * ((gridSize.y - 2) * .5))) * service.cellSize);
                targetPosition.set(newX + service.cellSize * .5f, newY);
                System.out.println("ID: " + id + " Changed state to " + moveState);
            }
        }
        if(newPosition)
        {
            int newX = (int)(random.nextDouble() * (gridSize.x - 1)) * service.cellSize;
            int newY = (int)(random.nextDouble() * (gridSize.y - 2) + 2) * service.cellSize;
            setPosition(new Vector2(newX + service.cellSize * .5f, newY));
            System.out.println("ID: " + id + " Also set new random position to: " + position);
        }
        stateStartTime = SystemClock.elapsedRealtime() * .001;
    }
    public void destroy()
    {
        System.out.println("Destroyed entity " + id);
        gridView = null;
        service.destroyEntity(this);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        System.out.println(event.getAction());
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            if(paint.getColor() == Color.GREEN)
                paint.setColor(Color.RED);
            else
                paint.setColor(Color.GREEN);
        invalidate();
        return true;
    }
    public void doFrame()
    {
        // Animation
        double elapsedTime = SystemClock.elapsedRealtime() * .001 - prevTime;
        prevTime = SystemClock.elapsedRealtime() * .001;
        if(position.y % service.cellSize == 0)
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
                double distanceDown = Vector2.distance(position.plus(new Vector2(0, service.cellSize)), targetPosition);
                double distanceUp = Vector2.distance(position.minus(new Vector2(0, service.cellSize)), targetPosition);
                double distanceOver = Vector2.distance(position.plus(new Vector2(movement.x * 12, 0)), targetPosition);

                if (distanceDown < distanceOver && moveSpeed > 0) { // Fall
                    groundState = 1;
                } else if (distanceUp < distanceOver && moveSpeed > 0) { // Jump
                    groundState = -1;
                }
                if (Vector2.distance(position, targetPosition) == 0) {
                    // Target reached, grounded; progress to next state
                    if(moveState == MoveState.EXIT) {
                        this.destroy();
                        return;
                    }
                    nextState(false);
                }
            }
            if (groundState == -1) { // Jumping up
                // increment animTimer, evaluate animTimer / animDuration, rescale to service.cellSize and determine movement
                if(animTimer >= animDuration) {
                    movement.y = -(service.cellSize + animPosition);
                } else {
                    float eval = -(float)AnimationCurve.Evaluate(AnimationCurve.AnimForm.CUSTOM, animTimer / animDuration) * service.cellSize;
                    movement.y = eval - animPosition;
                }
                animTimer += elapsedTime;

            } if (groundState == 1) { // Falling down
                // increment animTimer, evaluate animTimer / animDuration, rescale to service.cellSize and determine movement
                if(animTimer >= animDuration)
                    movement.y = Math.max(service.cellSize, position.y % service.cellSize) - Math.min(service.cellSize, position.y % service.cellSize);
                else
                    movement.y = (float)AnimationCurve.Evaluate(AnimationCurve.AnimForm.CUBE, animTimer / animDuration) * service.cellSize - position.y % service.cellSize;
                animTimer += elapsedTime;
            }
            if(!movement.equals(new Vector2()))
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
        canvas.drawRect(0f, 0f, service.cellSize * .7f, service.cellSize * 1.8f, paint);
        //canvas.drawCircle(service.cellSize * .35f, service.cellSize * 1.8f, 10, paint);
    }
}

