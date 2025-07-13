package com.example.overlaying;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class Item implements Serializable {
    private final float id;
    private final int fileId;
    private final String itemName;

    public Item(float id, String itemName, int fileId) {
        this.id = id;
        this.itemName = itemName;
        this.fileId = fileId;
    }

    public double getId() {
        return id;
    }
    public int getFileId() {
        return fileId;
    }
    public String getItemName() {
        return itemName;
    }

    @NonNull @Override
    public String toString() {
        return itemName;
    }
}
