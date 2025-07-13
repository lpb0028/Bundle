package com.example.overlaying;

import android.content.Context;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Random;

public class Entity {
    public int id;
    private final MainActivity mainActivity;

    // Positioning
    private final Vector2 position = new Vector2();
    private final Vector2 targetPosition = new Vector2();
    public AreaGrid areaGrid;
    private final int cellSize;
    public double moveSpeed;
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
    public double hp;
    public int groundState;

    public Entity(Context context, int id, Vector2 position, int cellSize) {
        this(context, id, position, cellSize, 3);
    }
    public Entity(Context context, int id, Vector2 _position, int cellSize, double moveSpeed) {
        mainActivity = (MainActivity) context;
        this.moveSpeed = moveSpeed;
        this.cellSize = cellSize;
        this.id = id;
        prevTime = SystemClock.elapsedRealtime() * .001;

        System.out.println("Created entity " + id);
        areaGrid = new AreaGrid(mainActivity, new Vector2(), 80, cellSize);
        if(_position == null)
            nextState(false, true);
        else
        {
            position.set(_position);
            nextState(false);
        }
    }
    public Vector2 getPosition() { return position; }
    public Vector2 getTargetPos() { return targetPosition; }
    public void setPosition(Vector2 _position) {
        position.set(_position);
        areaGrid.setPosition(_position);
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
        else if(moveState == MoveState.IDLE)
        {
            stateDuration = ((int)(((random.nextDouble() * 7) + 3) * 10)) * .1;
            int val = (int)(random.nextDouble() * 11) + 1;
            if(manualPoints.size() > 0)
            {
                moveState = MoveState.MOVE;
                targetPosition.set(manualPoints.get(0).plus(new Vector2(cellSize * .5f, 0)));
                areaGrid.targetBox.set(manualPoints.get(0).minus(new Vector2(0, cellSize)));
                manualPoints.remove(0);
                System.out.println("ID: " + id + " FORCED state to " + moveState);
            }
            else if(val <= 10 || !canExit) {
                moveState = MoveState.MOVE;
                Vector2 gridSize = areaGrid.getGridSize();
                int newX = (int)(random.nextDouble() * (gridSize.x - 1)) * cellSize;
                int newY = (int)(position.y + (int)(((random.nextDouble() - .5) * ((gridSize.y - 2) * .5))) * cellSize);
                if(newY < 120) newY = 120;
                if(newY > gridSize.y * cellSize) newY = (int)(gridSize.y * cellSize);
                areaGrid.targetBox.set(newX, newY - cellSize);
                targetPosition.set(newX + cellSize * .5f, newY);
                System.out.println("ID: " + id + " Changed state to " + moveState);
            }
            else
            {
                moveState = MoveState.EXIT;
                Vector2 gridSize = areaGrid.getGridSize();
                int newX = (random.nextBoolean()? - (cellSize + (int)areaGrid.size) : (int)gridSize.x * cellSize + cellSize + (int)areaGrid.size);
                int newY = (int)(position.y + (int)(((random.nextDouble() - .5) * ((gridSize.y - 2) * .5))) * cellSize);
                areaGrid.targetBox = new Vector2(-1, -1).times(cellSize);
                targetPosition.set(newX + cellSize * .5f, newY);
                System.out.println("ID: " + id + " Changed state to " + moveState);
            }
        }
        if(newPosition)
        {
            Vector2 gridSize = areaGrid.getGridSize();
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
        areaGrid = null;
        mainActivity.destroyEntity(this);
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
            if(stateDuration <= (prevTime - stateStartTime))
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

                if (distanceDown < distanceOver) {
                    // Fall
                    groundState = 1;
                } else if (distanceUp < distanceOver) {
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
            if(movement.x != 0 || movement.y != 0)
            {
                setPosition(position.plus(movement));
            }
            animPosition += movement.y;
        }
        areaGrid.doFrame(id);
    }
}

