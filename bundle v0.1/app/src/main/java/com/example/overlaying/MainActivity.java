package com.example.overlaying;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Choreographer;
import java.util.Random;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final OverlayService service = new OverlayService();

    private Context context;
    private static final int cellSize = 60;
    public static int nextID = 1;
    private Bitmap sharedBitmap;
    public Canvas canvas;
    private final Random random = new Random();

    private static final ArrayList<Entity> entities = new ArrayList<>();
    public final DisplayMetrics display = new DisplayMetrics();
    private Choreographer choreo = Choreographer.getInstance();
    public CanvasView cv;

    public void destroyEntity(Entity entity)
    {
        entities.remove(entity);
    }
    public boolean runSpawnManager()
    {
        int timeChance = 360; // Avg one every 6 seconds
        int countChance = 100; // Multiplies by number of entities
        int chance = timeChance + countChance * entities.size();
        return 0 == (int)(Math.round(random.nextDouble() * chance));
    }
    public void getPermissions()
    {
        if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }
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

        sharedBitmap = Bitmap.createBitmap(display.widthPixels, display.heightPixels, Bitmap.Config.ARGB_8888);
        cv = new CanvasView(this, null, sharedBitmap);
        getPermissions();
        service.Initialize(context, cv);
        canvas = new Canvas(sharedBitmap);

        entities.add(new Entity(context, nextID, null, cellSize));
        nextID++;

        Choreographer.FrameCallback callback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos)
            {
                if(nextID < 1)
                    System.exit(1);
                if(runSpawnManager())
                {
                    entities.add(new Entity(context, nextID, null, cellSize));
                    nextID++;
                }
                sharedBitmap.eraseColor(Color.TRANSPARENT);
                for(int i = entities.size() - 1; i >= 0; i--)
                {
                    Entity entity = entities.get(i);

                    entity.doFrame();
                }
                cv.invalidate();
                choreo.postFrameCallback(this);
            }
        };
        choreo.postFrameCallback(callback);
        finish();
    }
}
