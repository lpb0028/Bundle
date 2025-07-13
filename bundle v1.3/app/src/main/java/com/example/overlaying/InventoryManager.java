package com.example.overlaying;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class InventoryManager {
    private final Context context;
    private final ToastQueue toastQueue;
    private ArrayList<Vector2> inventory;

    public InventoryManager(Context context) {
        this.context = context;
        toastQueue = new ToastQueue(context);
        readInventoryFromFile();
    }

    @SuppressWarnings("unchecked")
    public void readInventoryFromFile() throws RuntimeException {
        File file;
        try (FileInputStream ignored = context.openFileInput("inventory.dat")) {
        } catch (IOException e) {
            file = new File(context.getFilesDir(), "inventory.dat");
            try {
                file.createNewFile();
                System.out.println("Created new file");
            } catch (IOException ex) {
                System.out.println("IOError upon creating new file");
            }
        }
        try (FileInputStream fis = context.openFileInput("inventory.dat"); ObjectInputStream ois = new ObjectInputStream(fis)) {
            Serializable ser = (Serializable) ois.readObject();
            if (ser instanceof ArrayList) {
                ArrayList<?> list = (ArrayList<?>) ser;
                if (list.isEmpty() || list.get(0) instanceof Vector2) {
                    inventory = (ArrayList<Vector2>) list;
                    System.out.println("Inventory Read Successful!");
                    toastQueue.addToast("Inventory Read Successful!", 1500);

                    fis.close();
                    ois.close();
                    return;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("<!> INVENTORY READ FAILED <!>");

            toastQueue.addToast("<!> Inventory Read Failed <!>", 1500);
        }
        inventory = new ArrayList<>();
    }

    public void writeInventoryToFile(ArrayList<Vector2> inventory) {
        try (FileOutputStream fos = context.openFileOutput("inventory.dat", Context.MODE_PRIVATE); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(inventory);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("<!> INVENTORY READ FAILED <!>");
        }
    }

    public void saveInventoryToFile() {
        writeInventoryToFile(inventory);
    }

    public ArrayList<Vector2> getInventory() {
        return inventory;
    }

    public void addItemToInventory(Item item, int amount) {
        Drawable drawable = ContextCompat.getDrawable(context, item.getFileId());
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 3, drawable.getIntrinsicHeight() / 3);
        ImageSpan img = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        SpannableString message = new SpannableString("+" + amount + " " + item.getItemName() + "  ");
        message.setSpan(img, message.length() - 1, message.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        System.out.println(message);
        toastQueue.addToast(message, 750);

        for(Vector2 v : inventory) {
            if(v.x == item.getId()) {
                v.y += amount;
                return;
            }
        }
        inventory.add(new Vector2(item.getId(), amount));
    }

    public void removeItemFromInventory(Item item, int amount) {
        for(Vector2 v : inventory) {
            if(v.x == item.getId()) {
                if(v.y >= amount)
                    v.y -= amount;
                else {
                    amount -= v.y;
                    v.y = 0;
                }
                if(amount == 0) break;
            }
        }
        for(int i = inventory.size() - 1; i >= 0; i--) {
            if(inventory.get(i).y <= 0)
                inventory.remove(i);
        }
    }
}
