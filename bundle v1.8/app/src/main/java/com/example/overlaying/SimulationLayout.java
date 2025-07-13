package com.example.overlaying;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Random;

public class SimulationLayout extends FrameLayout {

    public Manager manager;
    private WindowManager windowManager;

    // Containers for entity references and hitbox references
    private static final ArrayList<Entity> entityList = new ArrayList<>();
    private static final ArrayList<TouchHitBox> hitboxList = new ArrayList<>();

    public static int nextEntityID = 1; // ID holder for newly created entities
    private final Random random = new Random();

    public SimulationLayout(Context context) {
        super(context);
    }
    public SimulationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public SimulationLayout(Context context, Manager manager) {
        super(context);
        this.manager = manager;
    }

    // Called to begin adventuring. This starts simulation and display of entities, hitboxes, out-of-app features, etc.
    public void StartAdventure() {
        if(Settings.RUN_DEBUG_SCRIPT) {
            new DebugScript(getContext(), manager);
            return;
        }

        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        // Update manager status
        Manager.adventuring = true;

        // Create callback used for animating and drawing
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                // Evaluate whether or not to create new entity this frame
                if (decideSpawn())
                    newEntity(new Entity(getContext(), SimulationLayout.this, nextEntityID));

                for (int i = entityList.size() - 1; i >= 0; i--) {
                    // Update entity positions with doFrame()
                    Entity entity = entityList.get(i);
                    entity.doFrame();

                    // Update hitbox positions occasionally to reduce lag: updating touch-interaction positions is a heavy protocol
                    if(hitboxList.size() > i) {
                        TouchHitBox box = hitboxList.get(i);
                        if (box != null) {
                            if (box.updateCounter >= Settings.HITBOX_UPDATE_FRAMES) {
                                box.setCenter(entity.getCenter());
                            }
                            // Continue hitbox update timer
                            box.updateCounter = box.updateCounter % 15 + 1;
                        }
                    }
                }
                // Posts callback every frame, creating a continuous frame cycle
                if(Manager.adventuring)
                    Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }

    // Called to stop adventuring, removing and deleting all entities/hitboxes
    public void StopAdventure() {
        clearEntities();

        // Reset and prepare for next adventure start
        nextEntityID = 1;
        Manager.adventuring = false;
    }

    // Called to create a new entity when adventuring
    public void newEntity(Entity entity) {
        // Save references to entity and hitbox
        entityList.add(entity);
        hitboxList.add(new TouchHitBox(getContext(), null, Settings.CELL_SIZE * 3, entity));

        // Add entity and associated gridview to display frame
        addView(entity, Settings.ENTITY_PARAMS);
        addView(entity.gridView, Settings.GRIDVIEW_PARAMS);

        // Add hitbox to touch plane
        windowManager.addView(hitboxList.get(hitboxList.size() - 1), Settings.HITBOX_PARAMS);

        // Increment for next entity spawn
        nextEntityID++;
    }

    // Called to destroy a current entity when either killed or despawned
    public void destroyEntity(Entity entity) {
        // Remove entity and gridview from frame
        removeView(entity.gridView);
        removeView(entity);

        // Remove entity/hitbox references from various lists
        int i = entityList.indexOf(entity);
        entityList.remove(i);
        windowManager.removeView(hitboxList.get(i));
        hitboxList.remove(i);
    }

    // Called to clear all entities, on stop adventure and Clear Entity button
    public void clearEntities() {
        // Removes entities from frame
        if(Settings.SEND_DEBUG_MESSAGES) System.out.println("Clearing entities");
        for(int i = entityList.size() - 1; i >= 0; i--) {
            entityList.get(i).destroy();
        }
    }

    // Called to decide whether or not to spawn an entity this frame
    public boolean decideSpawn() {
        if(Settings.DISABLE_NATURAL_SPAWNS)
            return false;
        return 0 == random.nextInt((Settings.ENTITY_SPAWN_CHANCE + entityList.size() * Settings.CROWD_REDUCTION_FACTOR));
    }
}
