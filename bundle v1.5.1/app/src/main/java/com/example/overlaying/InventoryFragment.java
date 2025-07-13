package com.example.overlaying;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class InventoryFragment extends Fragment {
    // Inventory display layout
    private GridLayout inventoryGrid;

    // Handle creation logic
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.inventory_fragment, container, false);
        inventoryGrid = fragment.findViewById(R.id.item_container);
        updateInventoryDisplay(getActivity());

        return fragment;
    }

    // Called to update displayed inventory (on item pickups, menu initiation)
    public void updateInventoryDisplay(Context context) {
        ArrayList<Vector2> inventory = ((Activity) context).manager.inventoryManager.getInventory();

        // String for temporary display
        StringBuilder newText = new StringBuilder();
        for(Vector2 v : inventory) {
            newText.append((int)v.y);
            newText.append(" ");
            newText.append(ItemDictionary.searchItem((float)v.x));
            newText.append(", ");
        }
        newText.delete(newText.length() - 2, newText.length());

        if(inventoryGrid != null) {
            LayoutInflater inflater = LayoutInflater.from(context);

            // Inflate displays to match inventory size
            if (inventory.size() > inventoryGrid.getChildCount()) {
                for (int i = inventoryGrid.getChildCount(); i < inventory.size(); i++) {
                    inventoryGrid.addView(inflater.inflate(R.layout.inventory_fragment_item, null));
                }
            }
            else if (inventoryGrid.getChildCount() > inventory.size()) {
                for (int i = inventoryGrid.getChildCount() - 1; i > inventory.size(); i--) {
                    inventoryGrid.removeView(inventoryGrid.getChildAt(i));
                }
            }

            // Iterate through displays to correct images and amounts
            for (int i = 0; i < inventoryGrid.getChildCount(); i++) {
                ImageView itemImage = (ImageView) ((ViewGroup) inventoryGrid.getChildAt(i)).getChildAt(0);
                TextView itemText = (TextView) ((ViewGroup) inventoryGrid.getChildAt(i)).getChildAt(1);
                itemImage.setImageResource(ItemDictionary.searchItem(inventory.get(i).x).getFileId());
                itemText.setText(String.valueOf((int) inventory.get(i).y));
            }
        }

        System.out.println("Updated Inventory: " + newText);
    }
}
