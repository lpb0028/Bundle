package com.example.overlaying;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.content.Context;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Choreographer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class OverlayService extends Service {
    private Context context;

    // Settings
    public final int cellSize = 60;
    private static final int touchTargetSize = 120;
    private static final ArrayList<Entity> entities = new ArrayList<>();
    private static final ArrayList<TouchBox> boxes = new ArrayList<>();
    //region private static final WindowManager.LayoutParams[] params;
    private final WindowManager.LayoutParams gridParams = new WindowManager.LayoutParams(
            160,
            160,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    private final WindowManager.LayoutParams entityParams = new WindowManager.LayoutParams(
            (int)(cellSize * .7f),
            (int)(cellSize * 1.8f),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    private final WindowManager.LayoutParams boxParams = new WindowManager.LayoutParams(
            touchTargetSize,
            touchTargetSize,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    //endregion

    // Vars
    public static int nextID = 1;
    private final Random random = new Random();
    public final DisplayMetrics display = new DisplayMetrics();
    private Choreographer choreo = Choreographer.getInstance();
    private FrameLayout frame;
    private WindowManager windowManager;
    private static Choreographer.FrameCallback callback;

    public OverlayService() {
        System.out.println("I AM ALIVE WOOOOOOO");
        choreo = Choreographer.getInstance();
        boxParams.gravity = Gravity.LEFT|Gravity.TOP;
    }

    public void StartAdventure(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(display);
        getPermissions(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        frame = (FrameLayout) inflater.inflate(R.layout.adventure_mode, ((AppCompatActivity)context).findViewById(R.id.frame));
        initFrame();

        newEntity(new Entity(context, this, nextID, new Vector2(cellSize * 2, cellSize * 2), 5));

        OverlayService service = this;
        callback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if(runSpawnManager())
                {
                    newEntity(new Entity(context, service, nextID));
                }
                for(int i = entities.size() - 1; i >= 0; i--)
                {
                    Entity entity = entities.get(i);
                    TouchBox box = boxes.get(i);
                    entity.doFrame();
                    if(box.updateCounter == 15) {
                        Vector2 temp = entity.getCenter();
                        box.setCenter((int)temp.x, (int)temp.y);
                    }
                    box.updateCounter = box.updateCounter % 15 + 1;
                }
                choreo.postFrameCallback(this);
            }
        };
        choreo.postFrameCallback(callback);
    }
    public void StopAdventure() {
        choreo.removeFrameCallback(callback);
        windowManager.removeView(frame);
        frame = null;
        for(int i = boxes.size() - 1; i >= 0; i--) {
            windowManager.removeView(boxes.get(i));
            boxes.remove(i);
        }
        nextID = 1;
    }

    public void initFrame() {

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.RGBA_8888
        );

        params.alpha = .8f;
        windowManager.addView(frame, params);
    }
    public void newEntity(Entity entity) {
        entities.add(entity);
        frame.addView(entity, entityParams);
        frame.addView(entity.gridView, gridParams);
        boxes.add(new TouchBox(context, null, touchTargetSize, entity));
        windowManager.addView(boxes.get(boxes.size() - 1), boxParams);
        nextID++;
    }
    public void destroyEntity(Entity entity) {
        frame.removeView(entity.gridView);
        frame.removeView(entity);

        int i = entities.indexOf(entity);
        entities.remove(i);
        windowManager.removeView(boxes.get(i));
        boxes.remove(i);
    }
    public boolean runSpawnManager() {
        int timeChance = 360; // Avg one every 6 seconds
        int countChance = 100; // Multiplies by number of entities
        int chance = timeChance + countChance * entities.size();
        return 0 == (int)(Math.round(random.nextDouble() * chance));
    }
    public void getPermissions(Context context) {
        if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        if(nextID != 1)
            StopAdventure();
        super.onDestroy();
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new OverlayBinder();
    }
    public class OverlayBinder extends Binder {
        public void StartAdventure(Context context) {
            System.out.println("BINDER - Started Adventure");
            OverlayService.this.StartAdventure(context);
        }
        public void StopAdventure() {
            System.out.println("BINDER - Stopped Adventure");
            OverlayService.this.StopAdventure();
        }
        public boolean isAdventuring() {
            return nextID != 1;
        }
        public void stopService() {
            onDestroy();
        }
    }
}
