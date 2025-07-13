package com.example.overlaying;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Choreographer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.Random;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final OverlayService service = new OverlayService();

    private Context context;
    private static final int cellSize = 60;
    private static final int touchTargetSize = 120;
    public static int nextID = 1;
    private final Random random = new Random();

    private static final ArrayList<Entity> entities = new ArrayList<>();
    private static final ArrayList<TouchBox> boxes = new ArrayList<>();

    //region LayoutParams
    private static final WindowManager.LayoutParams gridParams = new WindowManager.LayoutParams(
        160,
        160,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.RGBA_8888
    );
    private static final WindowManager.LayoutParams entityParams = new WindowManager.LayoutParams(
        (int)(cellSize * .7f),
        (int)(cellSize * 1.8f),
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.RGBA_8888
    );
    private static final WindowManager.LayoutParams boxParams = new WindowManager.LayoutParams(
            touchTargetSize,
            touchTargetSize,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    //endregion

    public final DisplayMetrics display = new DisplayMetrics();
    private Choreographer choreo = Choreographer.getInstance();
    private FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(nextID != 1)
        {
            nextID = -1;
            return;
        }
        context = this;

        getWindowManager().getDefaultDisplay().getMetrics(display);
        choreo = Choreographer.getInstance();

        getPermissions();
        LayoutInflater inflater = getLayoutInflater();
        frame = (FrameLayout) inflater.inflate(R.layout.main_activity, findViewById(R.id.frame));
        service.Initialize(context, frame);
        boxParams.gravity = Gravity.LEFT|Gravity.TOP;

        frame.setMinimumWidth(display.widthPixels);
        frame.setMinimumHeight(display.heightPixels);

        newEntity(new Entity(context, nextID, new Vector2(cellSize * 2, cellSize * 2), 60, 1));

        Choreographer.FrameCallback callback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if(nextID < 1)
                    System.exit(1);
                if(runSpawnManager())
                {
                    newEntity(new Entity(context, nextID));
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
        finish();
    }
    public void destroyEntity(Entity entity) {
        frame.removeView(entity.gridView);
        frame.removeView(entity);
        int i = entities.indexOf(entity);
        service.removeView(boxes.get(i));
        entities.remove(i);
        boxes.remove(i);
    }
    public boolean runSpawnManager() {
        int timeChance = 360; // Avg one every 6 seconds
        int countChance = 100; // Multiplies by number of entities
        int chance = timeChance + countChance * entities.size();
        return 0 == (int)(Math.round(random.nextDouble() * chance));
    }
    public void newEntity(Entity entity) {
        entities.add(entity);
        frame.addView(entity, entityParams);
        frame.addView(entity.gridView, gridParams);
        boxes.add(new TouchBox(this, null, touchTargetSize, entity));
        service.addView(boxes.get(boxes.size() - 1), boxParams);
        nextID++;
    }
    public void getPermissions() {
        if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }
}
