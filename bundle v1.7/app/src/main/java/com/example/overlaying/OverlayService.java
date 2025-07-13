package com.example.overlaying;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.core.app.NotificationCompat;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if(action != null)
            switch(action) {
                case "CLEAR":
                    clearEntities();
                    break;
                case "STOP":
                    if(manager != null) manager.close();
                    StopAdventure();
                    stopForeground(STOP_FOREGROUND_REMOVE);
                    stopSelf();
                    break;
            }
        return START_NOT_STICKY;
    }

    // Called to begin adventuring. This starts simulation and display of entities, hitboxes, out-of-app features, etc.
    public void StartAdventure() {
        NotificationChannel channel = new NotificationChannel(
                Settings.NOTIFICATION_CHANNEL_ID,
                "Bundle Notifications",
                NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);

        startForeground(1, createNotification());

        // Initialize windowManager, used for managing hitbox windows
        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);

        if(Settings.RUN_DEBUG_SCRIPT) {
            DebugScript d = new DebugScript(context, manager);
            return;
        }

        // Inflate and initialize entity parent container
        frame = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.entity_frame, null);
        manager.transparentFrame.addView(frame);

        // Create testing entity TODO remove later
        newEntity(new Entity(context, this, 0, new Vector2(Settings.CELL_SIZE * 2, Settings.CELL_SIZE * 2), 1));

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

        // Removes entity-containing frame from view
        if(frame != null) {
            manager.transparentFrame.removeView(frame);
            frame = null;
        }

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
        return 0 == random.nextInt((int)(Settings.ENTITY_SPAWN_CHANCE + entityList.size() * Settings.CROWD_REDUCTION_FACTOR));
    }

    public Notification createNotification() {
        PendingIntent clearScreen = PendingIntent.getService(context, 0, new Intent(context, OverlayService.class).setAction("CLEAR"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        PendingIntent stopAdventure = PendingIntent.getService(context, 0, new Intent(context, OverlayService.class).setAction("STOP"), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        PendingIntent openApp = PendingIntent.getActivity(context, 0, new Intent(context, Activity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        return new NotificationCompat.Builder(context, Settings.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.bundle_item)
                .setContentTitle("Bundle")
                .setContentText("Adventure mode active")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .addAction(new NotificationCompat.Action(null, "Clear", clearScreen))
                .addAction(new NotificationCompat.Action(null, "Stop Adventuring", stopAdventure))
                .setContentIntent(openApp)
                .build();
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
