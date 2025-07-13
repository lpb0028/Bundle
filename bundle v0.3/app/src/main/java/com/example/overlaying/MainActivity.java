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
import android.view.WindowManager;

import java.util.Random;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public OverlayService service;

    private Context context;
    private static final int cellSize = 60;
    public static int nextID = 1;
    private final Random random = new Random();

    private static final ArrayList<Entity> entities = new ArrayList<>();
    private static final WindowManager.LayoutParams gridParams = new WindowManager.LayoutParams(
            160,
            160,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.RGBA_8888
    );
    private static final WindowManager.LayoutParams entityParams = new WindowManager.LayoutParams(
            (int)(cellSize * .7),
            (int)(cellSize * 1.8),
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
    );
    public final DisplayMetrics display = new DisplayMetrics();
    private Choreographer choreo = Choreographer.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(nextID != 1)
        {
            nextID = -1;
            return;
        }
        context = this;

        getWindowManager().getDefaultDisplay().getMetrics(display);
        choreo = Choreographer.getInstance();
        service = new OverlayService(this);
        gridParams.alpha = .8f;

        getPermissions();

        newEntity(new Entity(context, nextID, new Vector2(cellSize * 2, cellSize * 2), 60, 1));

        Choreographer.FrameCallback callback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos)
            {
                if(nextID < 1)
                    System.exit(1);
                if(runSpawnManager())
                {
                    newEntity(new Entity(context, nextID));
                }
                for(int i = entities.size() - 1; i >= 0; i--)
                {
                    Entity entity = entities.get(i);
                    entity.doFrame();
                }
                choreo.postFrameCallback(this);
            }
        };
        choreo.postFrameCallback(callback);
        finish();
    }
    public void destroyEntity(Entity entity)
    {
        service.removeView(entity.gridView);
        service.removeView(entity);
        entities.remove(entity);
    }
    public boolean runSpawnManager()
    {
        int timeChance = 360; // Avg one every 6 seconds
        int countChance = 100; // Multiplies by number of entities
        int chance = timeChance + countChance * entities.size();
        return 0 == (int)(Math.round(random.nextDouble() * chance));
    }
    public void newEntity(Entity entity) {
        entities.add(entity);
        service.addView(entity, entityParams);
        service.addView(entity.gridView, gridParams);
        nextID++;
    }
    public void getPermissions()
    {
        if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }
}
