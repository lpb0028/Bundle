package com.example.overlaying;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class OverlayService extends Service {

    public Manager manager;
    private Context context;

    // Containers for entity references and hitbox references
    private static final ArrayList<Entity> entityList = new ArrayList<>();
    private static final ArrayList<TouchHitBox> hitboxList = new ArrayList<>();

    public static int nextEntityID = 1; // ID holder for newly created entities
    private final Random random = new Random();
    private FrameLayout frame; // Frame used for displaying entities
    private WindowManager windowManager; // Manager used for hitboxes (android)


    // Called to begin adventuring. This starts simulation and display of entities, hitboxes, out-of-app features, etc.
    public void StartAdventure() {
        // Initialize a fresh WindowManager, retrieving screen size and necessary permissions
        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        getPermissions(context);

        // Inflate and initialize entity parent container
        frame = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.adventure_mode, ((AppCompatActivity)context).findViewById(R.id.frame));
        windowManager.addView(frame, Settings.FRAME_LAYOUT_PARAMS);

        // Create testing entity TODO remove later
        newEntity(new Entity(context, this, nextEntityID, new Vector2(Settings.CELL_SIZE * 2, Settings.CELL_SIZE * 2), 1));

        // Update manager status
        Manager.adventuring = true;

        // Create callback used for animating and drawing
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                // Evaluate whether or not to create new entity this frame
                if (decideSpawn())
                    newEntity(new Entity(context, OverlayService.this, nextEntityID));

                for (int i = entityList.size() - 1; i >= 0; i--) {
                    // Update entity positions with doFrame()
                    Entity entity = entityList.get(i);
                    entity.doFrame();

                    // Update hitbox positions occasionally to reduce lag: updating touch-interaction positions is a heavy protocol
                    TouchHitBox box = hitboxList.get(i);
                    if (box.updateCounter >= Settings.HITBOX_UPDATE_FRAMES) {
                        Vector2 temp = entity.getCenter();
                        box.setCenter((int) temp.x, (int) temp.y);
                        System.out.println(temp + " " + new Vector2(box.getX(), box.getY()));
                    }
                    // Continue hitbox update timer
                    box.updateCounter = box.updateCounter % 15 + 1;
                }

                // Posts callback every frame, creating a continuous frame cycle
                if(Manager.adventuring)
                    Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }

    // Called to stop adventuring, removing and deleting all entities/hitboxes
    public void StopAdventure() {
        // Removes entity-containing frame from view
        if(frame != null) {
            windowManager.removeView(frame);
            frame = null;
        }

        // Removes hitboxes from touch-interaction plane and deletes them
        for(int i = hitboxList.size() - 1; i >= 0; i--) {
            windowManager.removeView(hitboxList.get(i));
            hitboxList.remove(i);
        }

        // Clear display lists, nothing should be displayed or touchable by this point
        entityList.clear();
        hitboxList.clear();

        // Reset and prepare for next adventure start
        nextEntityID = 1;
        Manager.adventuring = false;
    }

    // Called to create a new entity when adventuring
    public void newEntity(Entity entity) {
        // Save references to entity and hitbox
        entityList.add(entity);
        hitboxList.add(new TouchHitBox(context, null, Settings.CELL_SIZE * 3, entity));

        // Add entity and associated gridview to display frame
        frame.addView(entity, Settings.ENTITY_PARAMS);
        frame.addView(entity.gridView, Settings.GRIDVIEW_PARAMS);

        // Add hitbox to touch plane
        windowManager.addView(hitboxList.get(hitboxList.size() - 1), Settings.HITBOX_PARAMS);

        // Increment for next entity spawn
        nextEntityID++;
    }

    // Called to destroy a current entity when either killed or despawned
    public void destroyEntity(Entity entity) {
        // Remove entity and gridview from frame
        frame.removeView(entity.gridView);
        frame.removeView(entity);

        // Remove entity/hitbox references from various lists
        int i = entityList.indexOf(entity);
        entityList.remove(i);
        windowManager.removeView(hitboxList.get(i));
        hitboxList.remove(i);
    }

    // Called to decide whether or not to spawn an entity this frame
    public boolean decideSpawn() {
        return 0 == random.nextInt((int)(Settings.ENTITY_SPAWN_CHANCE + entityList.size() * Settings.CROWD_REDUCTION_FACTOR));
    }

    // Obtains permissions if necessary
    public void getPermissions(Context context) {
        if (!android.provider.Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    @Override
    public void onCreate() {
        Manager.serviceRunning = true;
    }
    @Override
    public void onDestroy() {
        if(Manager.adventuring)
            StopAdventure();
        Manager.serviceRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new OverlayBinder();
    }

    public class OverlayBinder extends Binder {
        public void StartAdventure() {
            System.out.println("Binder Relay - start adventure");
            OverlayService.this.StartAdventure();
        }
        public void StopAdventure() {
            System.out.println("Binder Relay - stop adventure");
            OverlayService.this.StopAdventure();
        }
        public void StopService() {
            System.out.println("Binder Relay - stop service");
            OverlayService.this.stopSelf();
        }
        public void Initialize(Context context, Manager manager) {
            OverlayService.this.manager = manager;
            OverlayService.this.context = context;
        }
        public Manager RetreiveManager() {
            return manager;
        }
    }

}
