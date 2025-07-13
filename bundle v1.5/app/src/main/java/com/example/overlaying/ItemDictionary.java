package com.example.overlaying;

public class ItemDictionary {
    public static Item[] dictionary = {
        new Item(1, "Stone", R.drawable.minecraft_stone),
        new Item(1.1f, "Granite", R.drawable.minecraft_granite),
        new Item(1.2f, "Polished Granite", R.drawable.minecraft_polished_granite),
        new Item(1.3f, "Diorite", R.drawable.minecraft_diorite),
        new Item(1.4f, "Polished Diorite", R.drawable.minecraft_polished_diorite),
        new Item(1.5f, "Andesite", R.drawable.minecraft_andesite),
        new Item(1.6f, "Polished Andesite", R.drawable.minecraft_polished_andesite),
        new Item(2f, "Grass", R.drawable.minecraft_grass_block),
        new Item(3f, "Dirt", R.drawable.minecraft_dirt),
        new Item(3.1f, "Coarse Dirt", R.drawable.minecraft_coarse_dirt),
        new Item(3.2f, "Podzol", R.drawable.minecraft_podzol),
        new Item(4f, "Cobblestone", R.drawable.minecraft_cobblestone),
        new Item(5f, "Oak Wood Planks", R.drawable.minecraft_oak_planks),
        new Item(5.1f, "Spruce Wood Planks", R.drawable.minecraft_spruce_planks),
        new Item(5.2f, "Birch Wood Planks", R.drawable.minecraft_birch_planks),
        new Item(5.3f, "Jungle Wood Planks", R.drawable.minecraft_jungle_planks),
        new Item(5.4f, "Acacia Wood Planks", R.drawable.minecraft_acacia_planks),
        new Item(5.5f, "Dark Oak Wood Planks", R.drawable.minecraft_dark_oak_planks),
        new Item(6f, "Oak Sapling", R.drawable.minecraft_oak_sapling),
        new Item(6.1f, "Spruce Sapling", R.drawable.minecraft_spruce_sapling),
        new Item(6.2f, "Birch Sapling", R.drawable.minecraft_birch_sapling),
        new Item(6.3f, "Jungle Sapling", R.drawable.minecraft_jungle_sapling),
        new Item(6.4f, "Acacia Sapling", R.drawable.minecraft_acacia_sapling),
        new Item(6.5f, "Dark Oak Sapling", R.drawable.minecraft_dark_oak_sapling),
        new Item(7f, "Bedrock", R.drawable.minecraft_bedrock),
        // new Item(8f, "Flowing Water", R.drawable.minecraft_lava_bucket),
        // new Item(9f, "Still Water", R.drawable.minecraft_air),
        // new Item(10f, "Flowing Lava", R.drawable.minecraft_air),
        // new Item(11f, "Still Lava", R.drawable.minecraft_air),
        new Item(12f, "Sand", R.drawable.minecraft_sand),
        new Item(12.1f, "Red Sand", R.drawable.minecraft_red_sand),
        new Item(13f, "Gravel", R.drawable.minecraft_gravel),
        new Item(14f, "Gold Ore", R.drawable.minecraft_gold_ore),
        new Item(15f, "Iron Ore", R.drawable.minecraft_iron_ore),
        new Item(16f, "Coal Ore", R.drawable.minecraft_coal_ore),
        new Item(17f, "Oak Wood", R.drawable.minecraft_oak_wood),
        new Item(17.1f, "Spruce Wood", R.drawable.minecraft_spruce_wood),
        new Item(17.2f, "Birch Wood", R.drawable.minecraft_birch_wood),
        new Item(17.3f, "Jungle Wood", R.drawable.minecraft_jungle_wood),
        new Item(18f, "Oak Leaves", R.drawable.minecraft_oak_leaves),
        new Item(18.1f, "Spruce Leaves", R.drawable.minecraft_spruce_leaves),
        new Item(18.2f, "Birch Leaves", R.drawable.minecraft_birch_leaves),
        new Item(18.3f, "Jungle Leaves", R.drawable.minecraft_jungle_leaves),
        new Item(19f, "Sponge", R.drawable.minecraft_sponge),
        new Item(19.1f, "Wet Sponge", R.drawable.minecraft_wet_sponge),
        new Item(20f, "Glass", R.drawable.minecraft_glass),
        new Item(21f, "Lapis Lazuli Ore", R.drawable.minecraft_lapis_ore),
        new Item(22f, "Lapis Lazuli Block", R.drawable.minecraft_lapis_block),
        new Item(23f, "Dispenser", R.drawable.minecraft_dispenser),
        new Item(24f, "Sandstone", R.drawable.minecraft_sandstone),
        new Item(24.1f, "Chiseled Sandstone", R.drawable.minecraft_chiseled_sandstone),
        new Item(24.2f, "Smooth Sandstone", R.drawable.minecraft_smooth_sandstone),
        new Item(25f, "Note Block", R.drawable.minecraft_note_block),
        new Item(26f, "Bed", R.drawable.minecraft_red_bed),
        new Item(27f, "Powered Rail", R.drawable.minecraft_powered_rail),
        new Item(28f, "Detector Rail", R.drawable.minecraft_detector_rail),
        new Item(29f, "Sticky Piston", R.drawable.minecraft_sticky_piston),
        new Item(30f, "Cobweb", R.drawable.minecraft_cobweb),
        new Item(31f, "Dead Shrub", R.drawable.minecraft_dead_bush),
        new Item(31.1f, "Grass", R.drawable.minecraft_short_grass),
        new Item(31.2f, "Fern", R.drawable.minecraft_fern),
        new Item(32f, "Dead Bush", R.drawable.minecraft_dead_bush),
        new Item(33f, "Piston", R.drawable.minecraft_piston)
    };
    public static Item searchItem(double id) {
        for(Item item : dictionary)
            if(item.getId() == id)
                return item;
        return dictionary[0];
    }
    public static Item searchItem(String itemName) {
        for(Item item : dictionary)
            if(item.getItemName().equals(itemName))
                return item;
        return dictionary[0];
    }
}
