package com.example.overlaying;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class InventoryManager {
    private ArrayList<Vector2> inventory;
    private final Context context;
    private final Manager manager;

    public InventoryManager(Context context, Manager manager) {
        this.context = context;
        this.manager = manager;
        readInventoryFromFile();
    }
    public void readInventoryFromFile() throws RuntimeException {
        // Open inventory file, instantiate inventory var
        File file = new File("inventory.dat");
        inventory = new ArrayList<>();
        try {
            // Attempts to open inventory file and reads an object from it
            Object obj = new ObjectInputStream(context.openFileInput(file.getName())).readObject();

            // Transform read object into ArrayList<Vector2> for inventory
            if(obj instanceof ArrayList) {
                ArrayList<?> qList = (ArrayList<?>) obj;
                for(Object item : qList)
                    if(item instanceof Vector2)
                        inventory.add((Vector2) item);
            }
            System.out.println("Inventory Read Successful!");
            manager.toastManager.sendToast("Inventory Read Successful!", 1500);
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("<!> Inventory Read Failed <!>");
            manager.toastManager.sendToast("<!> Inventory Read Failed <!>", 1500);
        }
    }
    public boolean writeInventoryToFile() {
        try {
            // Attempt open file and write object to it, otherwise throw error.
            new ObjectOutputStream(context.openFileOutput("inventory.dat", Context.MODE_PRIVATE)).writeObject(inventory);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("<!> Inventory Write Failed <!>");
            manager.toastManager.sendToast("<!> Inventory Write Failed <!>", 1500);
        }
        return false;
    }
    public boolean writeInventoryToFile(int attempts) {
        // Attempt to save inventory [attempts] number of times before accepting failure
        for(int i = 1; i <= attempts; i++) {
            if(writeInventoryToFile())
                return true;
            else if(i < attempts)
                System.out.println("Attempted save #" + i + ", retrying...");
        }
        return false;
    }
    public ArrayList<Vector2> getInventory() {
        return inventory;
    }
    public void addItemToInventory(Item item, int amount) {

        // Send pickup message
        Drawable drawable = ContextCompat.getDrawable(context, item.getFileId());
        if(drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 3, drawable.getIntrinsicHeight() / 3);
            // Insert item icon in pickup message
            SpannableString message = new SpannableString("+" + amount + " " + item.getItemName() + "  ");
            message.setSpan(new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM), message.length() - 1, message.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            System.out.println(message);
            manager.toastManager.sendToast(message, 2000);
        }

        // Search if the item is already in the inventory: if found, add to that slot.
        for(Vector2 v : inventory) {
            if(v.x == item.getId()) {
                v.y += amount;
                return;
            }
        }
        // If not already in inventory, add new slot with item.
        inventory.add(new Vector2(item.getId(), amount));

    }
    public void removeItemFromInventory(Item item, int amount) {
        // Search for item in inventory
        for(Vector2 v : inventory) {
            if(v.x == item.getId()) {
                // If found, removes the correct number (across multiple slots if necessary)
                if(v.y >= amount)
                    v.y -= amount;
                else {
                    amount -= v.y;
                    v.y = 0;
                }
                if(amount <= 0) break;
            }
        }
        // Clean up all empty slots
        for(int i = inventory.size() - 1; i >= 0; i--) {
            if(inventory.get(i).y <= 0)
                inventory.remove(i);
        }
    }
}
