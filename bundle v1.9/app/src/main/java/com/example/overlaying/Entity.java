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

    // Entity Data
    public int entityID = -1;
    private String displayText = "";
    private boolean isInvulnerable = false;
    private double invulnerabilityTimer = 0;

    // References
    private SimulationLayout simulation;
    private final Random random = new Random();
    public EntityGridView gridView;

    // Positioning
    private final Vector2 currentPosition = new Vector2();
    private final Vector2 targetPosition = new Vector2();
    public Vector2Int gridSize = new Vector2Int();
    private double prevTime = 0;

    // Drawing
    private final Paint paint = new Paint();

    // Physics
    private int floorY = 0;
    private int ceilingY = 0;
    private int rightWallX = 0;
    private int leftWallX = 0;
    private double yVelocity = 0;
    private boolean grounded = true;

    // Movement
    public double moveSpeed;
    public MoveState moveState = MoveState.IDLE;
    public ArrayList<Vector2> manualPoints;
    private double stateDuration = 0;
    private double stateStartTime = 0;

    // Constructors
    public Entity(Context context) {
        super(context);
    }
    public Entity(Context context, SimulationLayout simulation) {
        this(context, simulation, -1);
    }
    public Entity(Context context, SimulationLayout simulation, int entityID) {
        this(context, simulation, entityID, null, 1);
    }
    public Entity(Context context, SimulationLayout simulation, int entityID, Vector2 _position, double moveSpeed) {
        super(context, null);
        // Testing
        // manualPoints = new ArrayList<>(Arrays.asList(new Vector2(entityID * Settings.CELL_SIZE, 600), new Vector2(entityID * Settings.CELL_SIZE, 180), new Vector2(entityID * Settings.CELL_SIZE, 600), new Vector2(entityID * Settings.CELL_SIZE, 300), new Vector2(entityID * Settings.CELL_SIZE, 600), new Vector2(entityID * Settings.CELL_SIZE, 180), new Vector2(entityID * Settings.CELL_SIZE, 600)));

        // Assigning variables
        this.simulation = simulation;
        this.moveSpeed = moveSpeed;
        this.entityID = entityID;
        prevTime = SystemClock.elapsedRealtime() * .001;
        gridView = new EntityGridView(context);
        gridSize = new Vector2((double) simulation.getWidth() / Settings.CELL_SIZE,(double) simulation.getHeight() / Settings.CELL_SIZE).round();

        // Registering min size so as not to be overwritten
        setMinimumWidth((int)(Settings.CELL_SIZE * .7f));
        setMinimumHeight((int)(Settings.CELL_SIZE * 1.8f));

        // Set preliminary walls (walls cannot be measured until blockGrid is initialized).

        // If not assigned starting position, find one and get new state
        if(_position == null)
            nextState(false, true);
        // If assigned starting position, go there and get new state
        else {
            setPosition(_position.minus(0, 1));
            floorY = (int)_position.y - 1;
            nextState(false);
        }

        // Color initiation (temporary)
        float[] hsv = new float[3];
        Color.colorToHSV(Color.BLUE, hsv);
        hsv[0] = (random.nextInt(360)) % 360;
        paint.setColor(Color.HSVToColor(hsv));
        paint.setAlpha(140);
        paint.setTextSize(30);

        // Debug
        if(Settings.SEND_ENTITY_STATUS_MESSAGES) System.out.println("Created entity " + entityID);
        findWalls();
    }

    // Position fetch functions
    public Vector2 getCenterPos() { return new Vector2(currentPosition.x, currentPosition.y - getMinimumHeight() / 2f); }

    public Vector2 getBottomPos() { return new Vector2(currentPosition.x, currentPosition.y); }
    public Vector2 getTopPos() { return new Vector2(currentPosition.x, currentPosition.y - getMinimumHeight()); }
    public Vector2 getLeftPos() {
        Vector2 result = getCenterPos();
        result.x -= getMinimumWidth() / 2f;
        return result;
    }
    public Vector2 getRightPos() {
        Vector2 result = getCenterPos();
        result.x += getMinimumWidth() / 2f;

        return result;
    }

    // Used to set a new position and move
    public void setPosition(Vector2 _position) {
        // Change position variable
        currentPosition.set(_position);
        // Change position of View
        this.setX((int)(currentPosition.x - Settings.CELL_SIZE * .35));
        this.setY((int)(currentPosition.y - Settings.CELL_SIZE * 1.8));
        // Change position of associated gridview
        gridView.setPosition(_position);
    }
    // Used to set (and log) moveState
    public void setMoveState(MoveState newState) {
        setMoveState(newState, false);
    }
    public void setMoveState(MoveState newState, boolean forced) {
        moveState = newState;
        if(Settings.SEND_ENTITY_STATUS_MESSAGES){
            if(forced)
                System.out.println("ID: " + entityID + " FORCED state to " + moveState);
            else
                System.out.println("ID: " + entityID + " Changed state to " + moveState);
        }
    }
    // States of movement (or waiting)
    public enum MoveState {
        IDLE,
        MOVE,
        EXIT,
        ATTACK, // TODO attack moveState
        FLEE // TODO flee moveState
    }

    // Decides next state of movement / Assigns starting position
    public void nextState(boolean canExit) {
        nextState(canExit, false);
    }
    public void nextState(boolean canExit, boolean newPosition) {
        if(moveSpeed <= 0) {
            moveState = MoveState.IDLE;
            return;
        }
        // If previously EXIT, keep exiting
        if(moveState == MoveState.EXIT) {
            if(Settings.SEND_ENTITY_STATUS_MESSAGES) System.out.println("ID: " + entityID + " Continued exit on state timer end");
        }
        // If previously MOVE, either IDLE or MOVE again depending on whether on-screen or not
        else if(moveState == MoveState.MOVE && isInBounds()) {
            if(isInBounds())
                setMoveState(MoveState.IDLE);
            else
                setMoveState(MoveState.MOVE);
        }
        // If previously IDLE, either MOVE to a new point or EXIT to an offscreen point
        else if(moveState == MoveState.IDLE) {
            // Assign random state duration
            stateDuration = ((int)(((random.nextDouble() * 7) + 3) * 10)) * .1;

            // If manual points are set, target the next one
            if(manualPoints != null && !manualPoints.isEmpty()) {
                setMoveState(MoveState.MOVE, true);
                targetPosition.set(manualPoints.get(0).plus(new Vector2(Settings.CELL_SIZE * .5f, -1)));
                manualPoints.remove(0);
            }
            // Otherwise either EXIT or MOVE depending on roll and if allowed to exit right now
            else if((int)(random.nextDouble() * 10) <= 8 || !canExit) {
                // Set MOVE and determine new position
                setMoveState(MoveState.MOVE);
                targetPosition.set(newTargetPosition(0));
            }
            else {
                // Set EXIT and determine exit position
                setMoveState(MoveState.EXIT);
                targetPosition.set(newTargetPosition(-1));
            }
        }
        // Assign new position if necessary
        if(newPosition) {
            setPosition(newTargetPosition(1));
            floorY = (int) currentPosition.y;
            if(Settings.SEND_ENTITY_STATUS_MESSAGES) System.out.println("ID: " + entityID + " Also set new random position to: " + currentPosition);
        }
        stateStartTime = SystemClock.elapsedRealtime() * .001;
    }

    // Used to get a new position where -1 is offscreen, 0 is onscreen near player, and 1 is onscreen anywhere
    public Vector2 newTargetPosition(int where) {
        int newX;
        int newY;
        // On-screen near current
        if(where == 0) {
            newX = (int)(random.nextDouble() * (gridSize.x - 1)) * Settings.CELL_SIZE;
            newY = (int)(currentPosition.y + (int)(((random.nextDouble() - .5) * ((gridSize.y - 2) * .5)) + 2) * Settings.CELL_SIZE);
            if(newY < Settings.CELL_SIZE * 2) newY = Settings.CELL_SIZE * 2;
            if(newY > gridSize.y * Settings.CELL_SIZE) newY = (gridSize.y * Settings.CELL_SIZE);
        }
        // Off-screen
        else if(where == -1) {
            newX = (random.nextBoolean() ? -(int) gridView.radius - Settings.CELL_SIZE : gridSize.x * Settings.CELL_SIZE + (int) gridView.radius + Settings.CELL_SIZE);
            newY = (int) (currentPosition.y + (int) (((random.nextDouble() - .5) * ((gridSize.y - 2) * .5))) * Settings.CELL_SIZE);
            return new Vector2(newX + Settings.CELL_SIZE * .5f, newY);
        }
        // On-screen anywhere
        else {
            newX = (int) (random.nextDouble() * (gridSize.x - 1)) * Settings.CELL_SIZE;
            newY = (int) (random.nextDouble() * (gridSize.y - 2) + 2) * Settings.CELL_SIZE;
        }
        return new Vector2(newX + Settings.CELL_SIZE * .5f, newY - 1);
    }

    // Used to destroy this entity
    public void destroy() {
        if(Settings.SEND_ENTITY_STATUS_MESSAGES) System.out.println("Destroyed entity " + entityID);
        simulation.destroyEntity(this);
    }

    // Touch events registered from associated hitbox
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO implement entity death, drops

        // If touchable right now
        if(event.getAction() == MotionEvent.ACTION_DOWN && !isInvulnerable) {

            // Change color (testing)
            float[] hsv = new float[3];
            int alpha = paint.getAlpha();
            Color.colorToHSV(paint.getColor(), hsv);
            hsv[0] = (hsv[0] + 20) % 360;
            paint.setColor(Color.HSVToColor(hsv));
            paint.setAlpha(alpha);

            // Add item to inventory (testing)
            simulation.manager.inventoryManager.addItemToInventory(MyDictionary.searchItem(entityID + 5), 1);

            // Begin hurt knockback
            isInvulnerable = true;
            invulnerabilityTimer = Settings.HIT_INVULNERABILITY_DURATION;
            grounded = false;
            addVerticalForce(Settings.ENTITY_KNOCKBACK_FORCE);
        }
        // Request redraw to register color change (if idle at the moment and not constantly updating)
        if(moveState == MoveState.IDLE)
            invalidate();
        // Touch handled
        return true;
    }
    // Returns true if entity is currently on-screen
    public boolean isInBounds() {
        return currentPosition.y <= gridSize.y * Settings.CELL_SIZE && currentPosition.x <= gridSize.x * Settings.CELL_SIZE;
    }

    // Called every frame on frame callback by OverlayService
    public void doFrame() {
        // If unable to move, no need to calculate movement
        if(moveSpeed <= 0) return;

        // Movement this frame
        Vector2 movement = new Vector2(Vector2.direction(currentPosition, targetPosition).toOne().x * moveSpeed, 0);
        // Time since previous frame - similar to Unity Time.deltaTime
        double deltaTime = SystemClock.elapsedRealtime() *.001 - prevTime;
        prevTime = SystemClock.elapsedRealtime() *.001; // Update previous time to this frame

        findWalls();
        
        grounded = floorY == currentPosition.y;
        if(!simulation.requireGroundedEntities) // If greater than a block from the floor, snap floor up a block
            if (floorY - currentPosition.y > Settings.CELL_SIZE)
                floorY -= Settings.CELL_SIZE;

        // If IDLE and timer up proceed to next state
        if (moveState == MoveState.IDLE && (prevTime - stateStartTime) >= stateDuration)
            nextState(true);

        // Either continuing to MOVE or EXIT or just finished IDLE and reassigned to MOVE or EXIT
        if (moveState == MoveState.MOVE || moveState == MoveState.EXIT) {
            // If grounded (have the ability to jump/fall)
            if (grounded) {
                double distToTarget = Vector2.distance(currentPosition, targetPosition);
                // Target reached from MOVE or EXIT, grounded; either progress to next state or despawn if exited
                if (distToTarget == 0) {
                    if (moveState == MoveState.EXIT)
                        this.destroy();

                    nextState(false);
                    return;
                }
                // If target less than one step away, move exactly the required amount to avoid overshooting infinitely
                if (distToTarget < moveSpeed) {
                    movement.x = distToTarget * movement.toOne().x;
                }

                // Evaluate distances to decide whether jump is worth or not
                double distanceUp = Vector2.distance(currentPosition.minus(new Vector2(0, Settings.CELL_SIZE)), targetPosition);
                double distanceOver = Vector2.distance(currentPosition.plus(new Vector2(movement.x * 12, 0)), targetPosition);

                // Decide whether to jump or fall
                if(!simulation.requireGroundedEntities) {
                    double distanceDown = Vector2.distance(currentPosition.plus(new Vector2(0, Settings.CELL_SIZE)), targetPosition);
                    if (distanceDown < distanceOver) // Fall a block
                        floorY += Settings.CELL_SIZE;
                }
                if (distanceUp < distanceOver) // Jump a block
                    addVerticalForce(Settings.ENTITY_JUMP_FORCE);
            }
        }
        // Add the effect of gravity to Y velocity if in the air
        if(!grounded) {
            yVelocity += Settings.ENTITY_GRAVITY * deltaTime;
        }
        // If invulnerable continue timer and/or end invulnerability
        if(isInvulnerable) {
            invulnerabilityTimer -= deltaTime;
            if (invulnerabilityTimer < 0) {
                isInvulnerable = false;
                invulnerabilityTimer = 0;
            }
        }
        // Set Y movement to velocity scaled for time
        movement.y = yVelocity * deltaTime;

        // COLLISION CHECKS AND CORRECTIONS
        // Overrun corrections
        if(getLeftPos().x < leftWallX) { // left wall correction
            System.out.println("Beyond left wall: " + getLeftPos().x + " < " + leftWallX);
            currentPosition.x = leftWallX + getMinimumWidth() / 2f;
            movement.x = 0;
        }
        if(getRightPos().x > rightWallX) { // right wall correction
            System.out.println("Beyond right wall: " + getRightPos().x + " > " + rightWallX);
            currentPosition.x = rightWallX - getMinimumWidth() / 2f;
            movement.x = 0;
        }
        if (currentPosition.y > floorY) { // floor correction
            System.out.println("Below floor: " + currentPosition.y + " > " + floorY);
            currentPosition.y = floorY;
            yVelocity = 0;
            movement.y = 0;
        }
        if (getTopPos().y < ceilingY) { // ceiling correction
            System.out.println("Above ceiling: " + currentPosition.y + " < " + ceilingY);
            currentPosition.y = ceilingY - getMinimumHeight();
            yVelocity = 0;
            movement.y = 0;
        }
        // Next Frame Overrun Corrections
        if(getLeftPos().x + movement.x < leftWallX) // left wall correction
            movement.x = leftWallX - getLeftPos().x;

        if(getRightPos().x + movement.x > rightWallX) // right wall correction
            movement.x = rightWallX - getRightPos().x;

        if (currentPosition.y + movement.y > floorY)  // floor correction
            movement.y = floorY - currentPosition.y;

        if (getTopPos().y - movement.y < ceilingY)  // ceiling correction
            movement.y = ceilingY - getTopPos().y;

        // If movement > 0, update position, otherwise no need to
        if (!movement.equals(new Vector2())) {
            setPosition(currentPosition.plus(movement));
            invalidate();
        }
    }

    // Used to add vertical forces to entity, such as jumps or hits
    public void addVerticalForce(float strength) {
        addVerticalForce(strength, true);
    }
    public void addVerticalForce(float strength, boolean upwards) {
        if(upwards)
            yVelocity = -strength;
        else
            yVelocity = strength;
    }
    private void findWalls() {
        if(simulation.blockGrid == null) return;
        floorY = simulation.blockGrid.rayCastToBlock(new Vector2(getRightPos().x, getBottomPos().y), 1).y - 1;
        ceilingY = simulation.blockGrid.rayCastToBlock(new Vector2(getLeftPos().x, getTopPos().y), 3).y + 1;
        leftWallX = simulation.blockGrid.rayCastToBlock(new Vector2(getLeftPos().x, getBottomPos().y), 4).x + 1;
        rightWallX = simulation.blockGrid.rayCastToBlock(new Vector2(getRightPos().x, getBottomPos().y), 2).x - 1;
    }

    // Called when needed to update view
    @Override
    public void onDraw(Canvas canvas)
    {
        canvas.drawRect(0f, 0f, Settings.CELL_SIZE * .7f, Settings.CELL_SIZE * 1.8f, paint);
        canvas.drawText(displayText, 0, 0, paint);
    }
}

