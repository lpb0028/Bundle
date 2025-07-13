package com.example.overlaying;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.SystemClock;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Entity extends androidx.appcompat.widget.AppCompatImageView {

    public int id;
    private String displayText = "";
    private OverlayService service;
    private boolean isInvulnerable = false;

    private static final double JUMP_ANIM_DURATION = 1;
    private static final double HURT_ANIM_DURATION = .4;

    // Positioning
    private final Vector2 position = new Vector2();
    private final Vector2 targetPosition = new Vector2();
    public GridView gridView;
    public Vector2 gridSize;
    private double stateDuration = 0;
    private double stateStartTime;
    private final Random random = new Random();
    public MoveState moveState = MoveState.IDLE;
    public ArrayList<Vector2> manualPoints = new ArrayList<>(Arrays.asList(new Vector2(300, 600), new Vector2(300, 180), new Vector2(300, 600), new Vector2(300, 300), new Vector2(300, 600), new Vector2(300, 180), new Vector2(300, 600)));

    // Animation
    private double prevTime;
    private double jumpAnimTimer = 0;
    private double hurtAnimTimer = 0;
    private double jumpAnimPosition;
    private double hurtAnimPosition;

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

        this.setMinimumWidth((int)(MainActivity.CELL_SIZE * .7f));
        this.setMinimumHeight((int)(MainActivity.CELL_SIZE * 1.8f));

        if(MainActivity.ENTITY_DEBUG_MESSAGES) System.out.println("Created entity " + id);
        gridView = new GridView(context);
        gridSize = new Vector2((double) service.display.widthPixels / MainActivity.CELL_SIZE,(double)service.display.heightPixels / MainActivity.CELL_SIZE).round();
        if(_position == null)
            nextState(false, true);
        else
        {
            setPosition(_position);
            nextState(false);
        }

        float[] hsv = new float[3];
        Color.colorToHSV(Color.BLUE, hsv);
        hsv[0] = (random.nextInt(360)) % 360;
        paint.setColor(Color.HSVToColor(hsv));
        paint.setAlpha(140);
        paint.setTextSize(30);
    }
    public Vector2 getCenter() { return new Vector2(position.x, position.y - getHeight() / 2f); }
    public Vector2 getPosition() { return position; }
    public void setPosition(Vector2 _position) {
        position.set(_position);
        this.setX((int)(position.x - MainActivity.CELL_SIZE * .35));
        this.setY((int)(position.y - MainActivity.CELL_SIZE * 1.8));
        gridView.setPosition(_position);
    }
    public enum MoveState {
        IDLE,
        MOVE,
        EXIT,
        ATTACK // TODO attack moveState
    }
    public void nextState(boolean canExit)
    {
        nextState(canExit, false);
    }
    public void nextState(boolean canExit, boolean newPosition) {
        if(moveState == MoveState.MOVE && (position.y <= gridSize.y * MainActivity.CELL_SIZE && position.x <= gridSize.x * MainActivity.CELL_SIZE))
        {
            moveState = MoveState.IDLE;
            if(MainActivity.ENTITY_DEBUG_MESSAGES) System.out.println("ID: " + id + " Changed state to " + moveState);
        }
        else if(moveSpeed > 0 && (moveState == MoveState.IDLE || (position.y >= gridSize.y * MainActivity.CELL_SIZE || position.x >= gridSize.x * MainActivity.CELL_SIZE)))
        {
            stateDuration = ((int)(((random.nextDouble() * 7) + 3) * 10)) * .1;

            if(manualPoints.size() > 0)
            {
                moveState = MoveState.MOVE;
                targetPosition.set(manualPoints.get(0).plus(new Vector2(MainActivity.CELL_SIZE * .5f, 0)));
                manualPoints.remove(0);
                if(MainActivity.ENTITY_DEBUG_MESSAGES) System.out.println("ID: " + id + " FORCED state to " + moveState);
            }
            else if((int)(random.nextDouble() * 11) + 1 <= 10 || !canExit) {
                moveState = MoveState.MOVE;
                int newX = (int)(random.nextDouble() * (gridSize.x - 1)) * MainActivity.CELL_SIZE;
                int newY = (int)(position.y + (int)(((random.nextDouble() - .5) * ((gridSize.y - 2) * .5)) + 2) * MainActivity.CELL_SIZE);
                if(newY < MainActivity.CELL_SIZE * 2) newY = MainActivity.CELL_SIZE * 2;
                if(newY > gridSize.y * MainActivity.CELL_SIZE) newY = (int)(gridSize.y * MainActivity.CELL_SIZE);
                targetPosition.set(newX + MainActivity.CELL_SIZE * .5f, newY);
                if(MainActivity.ENTITY_DEBUG_MESSAGES) System.out.println("ID: " + id + " Changed state to " + moveState);
            }
            else
            {
                moveState = MoveState.EXIT;
                int newX = (random.nextBoolean()? - (int) gridView.radius - MainActivity.CELL_SIZE : (int)gridSize.x * MainActivity.CELL_SIZE + (int) gridView.radius + MainActivity.CELL_SIZE);
                int newY = (int)(position.y + (int)(((random.nextDouble() - .5) * ((gridSize.y - 2) * .5))) * MainActivity.CELL_SIZE);
                targetPosition.set(newX + MainActivity.CELL_SIZE * .5f, newY);
                if(MainActivity.ENTITY_DEBUG_MESSAGES) System.out.println("ID: " + id + " Changed state to " + moveState);
            }
        }
        if(newPosition)
        {
            int newX = (int)(random.nextDouble() * (gridSize.x - 1)) * MainActivity.CELL_SIZE;
            int newY = (int)(random.nextDouble() * (gridSize.y - 2) + 2) * MainActivity.CELL_SIZE;
            setPosition(new Vector2(newX + MainActivity.CELL_SIZE * .5f, newY));
            if(MainActivity.ENTITY_DEBUG_MESSAGES) System.out.println("ID: " + id + " Also set new random position to: " + position);
        }
        stateStartTime = SystemClock.elapsedRealtime() * .001;
    }
    public void destroy() {
        if(MainActivity.ENTITY_DEBUG_MESSAGES) System.out.println("Destroyed entity " + id);
        gridView = null;
        service.destroyEntity(this);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && !isInvulnerable) {
            float[] hsv = new float[3];
            int alpha = paint.getAlpha();
            Color.colorToHSV(paint.getColor(), hsv);
            hsv[0] = (hsv[0] + 20) % 360;
            paint.setColor(Color.HSVToColor(hsv));
            paint.setAlpha(alpha);
            service.addItemToInventory(ItemDictionary.searchItem(6), 1);
            isInvulnerable = true;
            groundState = -2;
            if(jumpAnimPosition > 0)
                hurtAnimPosition = - MainActivity.CELL_SIZE + jumpAnimPosition;
            else
                hurtAnimPosition = jumpAnimPosition;
            System.out.println("Hurt, jump: " + hurtAnimPosition + ", " + jumpAnimPosition);
        }
        invalidate();
        return true;
    }
    public void doFrame() {
        Vector2 movement = new Vector2(Vector2.direction(position, targetPosition).toOne().x * moveSpeed, 0);
        // Animation
        double elapsedTime = SystemClock.elapsedRealtime() * .001 - prevTime;
        prevTime = SystemClock.elapsedRealtime() * .001;
        if (position.y % MainActivity.CELL_SIZE == 0 && groundState != -2) {
            groundState = 0;
            jumpAnimTimer = elapsedTime;
            jumpAnimPosition = 0;
            hurtAnimTimer = elapsedTime;
            hurtAnimPosition = 0;
        }
        if (moveState == MoveState.IDLE) {
            if (stateDuration <= (prevTime - stateStartTime) && moveSpeed > 0) {
                if (MainActivity.ENTITY_DEBUG_MESSAGES)
                    System.out.println("ID: " + id + " Calling nextState on time up; Time = " + (prevTime - stateStartTime + " seconds"));
                nextState(true);
            }
        }
        if (moveState == MoveState.MOVE || moveState == MoveState.EXIT) {
            if (groundState == 0) {
                double dist = Vector2.distance(position, targetPosition);
                if (dist > 0 && dist < moveSpeed) {
                    movement.x = dist * movement.toOne().x;
                }
                double distanceDown = Vector2.distance(position.plus(new Vector2(0, MainActivity.CELL_SIZE)), targetPosition);
                double distanceUp = Vector2.distance(position.minus(new Vector2(0, MainActivity.CELL_SIZE)), targetPosition);
                double distanceOver = Vector2.distance(position.plus(new Vector2(movement.x * 12, 0)), targetPosition);

                if (distanceDown < distanceOver && moveSpeed > 0) { // Fall
                    groundState = 1;
                } else if (distanceUp < distanceOver && moveSpeed > 0) { // Jump
                    groundState = -1;
                }
                if (Vector2.distance(position, targetPosition) == 0) {
                    // Target reached, grounded; progress to next state
                    if (moveState == MoveState.EXIT) {
                        this.destroy();
                        return;
                    }
                    nextState(false);
                }
            }
            if (groundState == 1) { // Falling down
                // increment animTimer, evaluate animTimer / animDuration, rescale to service.cellSize and determine movement
                if (jumpAnimTimer >= JUMP_ANIM_DURATION)
                    movement.y = Math.max(MainActivity.CELL_SIZE, position.y % MainActivity.CELL_SIZE) - Math.min(MainActivity.CELL_SIZE, position.y % MainActivity.CELL_SIZE);
                else
                    movement.y = (float) AnimationCurve.Evaluate(AnimationCurve.AnimForm.CUBE, jumpAnimTimer / JUMP_ANIM_DURATION) * MainActivity.CELL_SIZE - position.y % MainActivity.CELL_SIZE;
                jumpAnimTimer += elapsedTime;

            }
            if (groundState == -1) { // Jumping up
                // increment animTimer, evaluate animTimer / animDuration, rescale to service.cellSize and determine movement
                if (jumpAnimTimer >= JUMP_ANIM_DURATION) {
                    movement.y = -(MainActivity.CELL_SIZE + jumpAnimPosition);
                } else {
                    float eval = -(float) AnimationCurve.Evaluate(AnimationCurve.AnimForm.CUSTOM1, jumpAnimTimer / JUMP_ANIM_DURATION) * MainActivity.CELL_SIZE;
                    movement.y = eval - jumpAnimPosition;
                }
                jumpAnimTimer += elapsedTime;
            }
        }
        if (groundState == -2) { // Hit recoil
            if (hurtAnimTimer >= HURT_ANIM_DURATION && hurtAnimPosition > 0) {
                // finish hurt anim
                movement.y = -hurtAnimPosition;
                groundState = 0;
                isInvulnerable = false;
            }
            else {
                // continue hurt anim
                float eval = -(float) AnimationCurve.Evaluate(AnimationCurve.AnimForm.CUSTOM2, hurtAnimTimer / HURT_ANIM_DURATION) * MainActivity.CELL_SIZE;
                movement.y = eval - hurtAnimPosition;
                hurtAnimPosition += movement.y;
            }
            hurtAnimTimer += elapsedTime;
        }
        if (!movement.equals(new Vector2())) {
            setPosition(position.plus(movement));
            System.out.println(movement.y);
            invalidate();
        }
        if (groundState == 1 || groundState == -1) jumpAnimPosition += movement.y;
        displayText = String.valueOf(groundState);
    }
    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawRect(0f, 0f, MainActivity.CELL_SIZE * .7f, MainActivity.CELL_SIZE * 1.8f, paint);
        canvas.drawText(displayText, 0, 0, paint);
    }
}

