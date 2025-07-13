package com.example.overlaying;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class Activity extends AppCompatActivity {
    public Manager manager;
    private int activeMenu = -1;

    // Called on program start
    @Override
    public void onStart() {
        super.onStart();
        getPermissions(this);
        // Either start or retrieve manager script
        if(Manager.serviceRunning) {
            manager = Manager.binder.RetreiveManager();
            Manager.binder.StopAdventure();
        }
        else {
            manager = new Manager(this);
            manager.bindActivityToService(this);
        }

        setContentView(R.layout.main_activity); // Initialize menu container
        switchMenu(1); // Load first menu
        loadMenus(); // Add click listeners to menu buttons
    }

    // Called to stop activity
    public void close(boolean stopProgram) {
        if(stopProgram)
            manager.close();
        finishAndRemoveTask();
    }
    // Called to switch activity menu
    public void switchMenu(int newMenu) {
        if(newMenu != activeMenu) {
            System.out.println("Switching menu from " + activeMenu + " to " + newMenu);
            updateNavigation(activeMenu, newMenu);
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
            manager.inventoryManager.addItemToInventory(ItemDictionary.searchItem(newMenu), 1);
            fragmentTransaction.commit();
        }
    }
    // Called to update navigation layout
    public void updateNavigation(int oldMenu, int newMenu) {
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
    // Called to set initial button click listeners
    private void loadMenus() {
        findViewById(R.id.menu_button1).setOnClickListener(v -> switchMenu(1));
        findViewById(R.id.menu_button2).setOnClickListener(v -> switchMenu(2));
        findViewById(R.id.menu_button3).setOnClickListener(v -> switchMenu(3));
        findViewById(R.id.menu_button4).setOnClickListener(v -> switchMenu(4));
        findViewById(R.id.menu_button5).setOnClickListener(v -> switchMenu(5));
    }

    // Obtains permissions if necessary
    public void getPermissions(Context context) {
        if (!android.provider.Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }
}
