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
    private GridLayout gridLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.inventory_fragment, container, false);
        gridLayout = fragment.findViewById(R.id.item_container);
        updateInventoryDisplay(getActivity());
        return fragment;
    }
    public void updateInventoryDisplay(Context context) {
        ArrayList<Vector2> inventory = ((MainActivity) context).binder.GetInventory();
        StringBuilder newText = new StringBuilder();
        for(Vector2 v : inventory) {
            newText.append((int)v.y);
            newText.append(" ");
            newText.append(ItemDictionary.searchItem((float)v.x));
            newText.append(", ");
        }
        newText.delete(newText.length() - 2, newText.length());

        if(gridLayout != null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            if (inventory.size() > gridLayout.getChildCount())
                for (int i = gridLayout.getChildCount(); i < inventory.size(); i++) {
                    gridLayout.addView(inflater.inflate(R.layout.inventory_fragment_item, null));
                }
            if (gridLayout.getChildCount() > inventory.size())
                for (int i = gridLayout.getChildCount() - 1; i > inventory.size(); i--) {
                    gridLayout.removeView(gridLayout.getChildAt(i));
                }

            for (int i = 0; i < gridLayout.getChildCount(); i++) {
                ImageView itemImage = (ImageView) ((ViewGroup) gridLayout.getChildAt(i)).getChildAt(0);
                TextView itemText = (TextView) ((ViewGroup) gridLayout.getChildAt(i)).getChildAt(1);
                itemImage.setImageResource(ItemDictionary.searchItem(inventory.get(i).x).getFileId());
                itemText.setText(String.valueOf((int) inventory.get(i).y));
            }
        }

        System.out.println("Updated Inventory: " + newText);
    }
}
