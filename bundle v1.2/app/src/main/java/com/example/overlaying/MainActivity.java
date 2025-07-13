package com.example.overlaying;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    // Adventure Activity
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // Called on Connection
            System.out.println("Connected");
            binder = (OverlayService.OverlayBinder)iBinder;
            isBound = true;
            if(binder.isAdventuring())
                binder.StopAdventure();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // Called on UNEXPECTED disconnection
            System.out.println("Disconnected");
            binder = null;
            isBound = false;
        }
    };
    public OverlayService.OverlayBinder binder;
    public boolean isBound = false;
    private int activeMenu = -1;

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.main_activity);
        Intent myIntent = new Intent(this, com.example.overlaying.OverlayService.class);
        startService(myIntent);
        bindService(new Intent(this, OverlayService.class), connection, Context.BIND_AUTO_CREATE);
        switchMenu(1);

        findViewById(R.id.menu_button1).setOnClickListener(v -> switchMenu(1));
        findViewById(R.id.menu_button2).setOnClickListener(v -> switchMenu(2));
        findViewById(R.id.menu_button3).setOnClickListener(v -> switchMenu(3));
        findViewById(R.id.menu_button4).setOnClickListener(v -> switchMenu(4));
        findViewById(R.id.menu_button5).setOnClickListener(v -> switchMenu(5));

    }
    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    public void switchMenu(int newMenu) {
        if(newMenu != activeMenu) {
            System.out.println("Switching menu from " + activeMenu + " to " + newMenu);
            updateMenu(activeMenu, newMenu);
            activeMenu = newMenu;

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (newMenu) {
                case 1: fragmentTransaction.replace(R.id.fragment_container, new HomeFragment());
                    break;
                case 2: fragmentTransaction.replace(R.id.fragment_container, new BuildFragment());
                    break;
                case 3: fragmentTransaction.replace(R.id.fragment_container, new InventoryFragment());
                    break;
                case 4: fragmentTransaction.replace(R.id.fragment_container, new ShopFragment());
                    break;
                case 5: fragmentTransaction.replace(R.id.fragment_container, new SettingsFragment());
                    break;
            }
            fragmentTransaction.commit();
        }
    }
    public void updateMenu(int oldMenu, int newMenu) {
        View button;
        ConstraintLayout.LayoutParams params;

        switch (oldMenu) {
            case 2: button = findViewById(R.id.menu_button2);
                break;
            case 3: button = findViewById(R.id.menu_button3);
                break;
            case 4: button = findViewById(R.id.menu_button4);
                break;
            case 5: button = findViewById(R.id.menu_button5);
                break;
            default: button = findViewById(R.id.menu_button1);
                break;
        }
        params = (ConstraintLayout.LayoutParams) button.getLayoutParams();
        params.setMargins(3, 6, 3, 6);
        button.setLayoutParams(params);

        switch (newMenu) {
            case 1: button = findViewById(R.id.menu_button1);
                break;
            case 2: button = findViewById(R.id.menu_button2);
                break;
            case 3: button = findViewById(R.id.menu_button3);
                break;
            case 4: button = findViewById(R.id.menu_button4);
                break;
            case 5: button = findViewById(R.id.menu_button5);
                break;
            default:
                return;
        }
        params = (ConstraintLayout.LayoutParams) button.getLayoutParams();
        params.setMargins(3, 0, 3, 12);
        button.setLayoutParams(params);
    }
}
