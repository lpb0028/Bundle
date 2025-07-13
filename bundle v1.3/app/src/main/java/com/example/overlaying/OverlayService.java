package com.example.overlaying;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
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
    private static final ArrayList<Entity> entities = new ArrayList<>();
    private static final ArrayList<TouchBox> boxes = new ArrayList<>();
    //region private static final WindowManager.LayoutParams[] params;
    private final WindowManager.LayoutParams gridParams = new WindowManager.LayoutParams(
            MainActivity.CELL_SIZE * 3,
            MainActivity.CELL_SIZE * 3,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    private final WindowManager.LayoutParams entityParams = new WindowManager.LayoutParams(
            (int)(MainActivity.CELL_SIZE * .7f),
            (int)(MainActivity.CELL_SIZE * 1.8f),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    private final WindowManager.LayoutParams boxParams = new WindowManager.LayoutParams(
            MainActivity.CELL_SIZE * 3,
            MainActivity.CELL_SIZE * 3,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    //endregion

    // Adventure Mode
    public static boolean adventuring;
    public static int nextID = 1;
    private final Random random = new Random();
    public final DisplayMetrics display = new DisplayMetrics();
    private FrameLayout frame;
    private WindowManager windowManager;
    private InventoryManager iManager;
    public float frameSpeedMult = 1;

    @SuppressLint("RtlHardcoded")
    public OverlayService() {
        boxParams.gravity = Gravity.LEFT|Gravity.TOP;
    }
    public void setContext(Context context) {
        this.context = context;
        if(iManager == null) iManager = new InventoryManager(context);
    }

    public void StartAdventure(Context context) {
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(display);
        getPermissions(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        frame = (FrameLayout) inflater.inflate(R.layout.adventure_mode, ((AppCompatActivity)context).findViewById(R.id.frame));
        initFrame();

        newEntity(new Entity(context, this, nextID, new Vector2(MainActivity.CELL_SIZE * 2, MainActivity.CELL_SIZE * 2), 1));

        OverlayService service = this;
        new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if (runSpawnManager()) {
                    newEntity(new Entity(context, service, nextID));
                }
                for (int i = entities.size() - 1; i >= 0; i--) {
                    Entity entity = entities.get(i);
                    TouchBox box = boxes.get(i);
                    entity.doFrame();
                    if (box.updateCounter == 15) {
                        Vector2 temp = entity.getCenter();
                        box.setCenter((int) temp.x, (int) temp.y);
                    }
                    box.updateCounter = box.updateCounter % 15 + 1;
                }
                if(adventuring) {
                    Choreographer.getInstance().postFrameCallbackDelayed(this, (long)(frameTimeNanos * frameSpeedMult));
                }
            }
        };
        adventuring = true;
    }
    public void StopAdventure() {
        if(frame != null) {
            windowManager.removeView(frame);
            frame = null;
        }
        for(int i = boxes.size() - 1; i >= 0; i--) {
            windowManager.removeView(boxes.get(i));
            boxes.remove(i);
        }
        entities.clear();
        boxes.clear();
        nextID = 1;
        adventuring = false;
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
        boxes.add(new TouchBox(context, null, MainActivity.CELL_SIZE * 3, entity));
        frame.addView(entity, entityParams);
        frame.addView(entity.gridView, gridParams);
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
        if(adventuring)
            StopAdventure();
    }

    public void addItemToInventory(Item item, int amount) {
        iManager.addItemToInventory(item, amount);
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
        public void SaveInventoryToFile() {
            iManager.saveInventoryToFile();
        }
        public void AddItemToInventory(Item item, int amount) {
            iManager.addItemToInventory(item, amount);
        }
        public ArrayList<Vector2> GetInventory() {
            return iManager.getInventory();
        }
        public boolean isAdventuring() {
            return adventuring;
        }
        public void stopService() {
            OverlayService.this.stopSelf();
        }
        public void setContext(Context context) {
            OverlayService.this.setContext(context);
        }
    }

}
