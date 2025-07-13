package com.example.overlaying;

public class MyDictionary {
    public static Item[] itemDictionary = {
        new Item(1, "Stone", R.drawable.stone_item),
        new Item(1.1f, "Granite", R.drawable.granite_item),
        new Item(1.2f, "Polished Granite", R.drawable.polished_granite_item),
        new Item(1.3f, "Diorite", R.drawable.diorite_item),
        new Item(1.4f, "Polished Diorite", R.drawable.polished_diorite_item),
        new Item(1.5f, "Andesite", R.drawable.andesite_item),
        new Item(1.6f, "Polished Andesite", R.drawable.polished_andesite_item),
        new Item(2f, "Grass", R.drawable.grass_block_item),
        new Item(3f, "Dirt", R.drawable.dirt_item),
        new Item(3.1f, "Coarse Dirt", R.drawable.coarse_dirt_item),
        new Item(3.2f, "Podzol", R.drawable.podzol_item),
        new Item(4f, "Cobblestone", R.drawable.cobblestone_item),
        new Item(5f, "Oak Wood Planks", R.drawable.oak_planks_item),
        new Item(5.1f, "Spruce Wood Planks", R.drawable.spruce_planks_item),
        new Item(5.2f, "Birch Wood Planks", R.drawable.birch_planks_item),
        new Item(5.3f, "Jungle Wood Planks", R.drawable.jungle_planks_item),
        new Item(5.4f, "Acacia Wood Planks", R.drawable.acacia_planks_item),
        new Item(5.5f, "Dark Oak Wood Planks", R.drawable.dark_oak_planks_item),
        new Item(6f, "Oak Sapling", R.drawable.oak_sapling_item),
        new Item(6.1f, "Spruce Sapling", R.drawable.spruce_sapling_item),
        new Item(6.2f, "Birch Sapling", R.drawable.birch_sapling_item),
        new Item(6.3f, "Jungle Sapling", R.drawable.jungle_sapling_item),
        new Item(6.4f, "Acacia Sapling", R.drawable.acacia_sapling_item),
        new Item(6.5f, "Dark Oak Sapling", R.drawable.dark_oak_sapling_item),
        new Item(7f, "Bedrock", R.drawable.bedrock_item),
        //new Item(8f, "Flowing Water", R.drawable.water_bucket_item),
        new Item(9f, "Still Water", R.drawable.water_bucket_item),
        // new Item(10f, "Flowing Lava", R.drawable.air_item),
        new Item(11f, "Still Lava", R.drawable.lava_bucket_item),
        new Item(12f, "Sand", R.drawable.sand_item),
        new Item(12.1f, "Red Sand", R.drawable.red_sand_item),
        new Item(13f, "Gravel", R.drawable.gravel_item),
        new Item(14f, "Gold Ore", R.drawable.gold_ore_item),
        new Item(15f, "Iron Ore", R.drawable.iron_ore_item),
        new Item(16f, "Coal Ore", R.drawable.coal_ore_item),
        new Item(17f, "Oak Wood", R.drawable.oak_wood_item),
        new Item(17.1f, "Spruce Wood", R.drawable.spruce_wood_item),
        new Item(17.2f, "Birch Wood", R.drawable.birch_wood_item),
        new Item(17.3f, "Jungle Wood", R.drawable.jungle_wood_item),
        new Item(18f, "Oak Leaves", R.drawable.oak_leaves_item),
        new Item(18.1f, "Spruce Leaves", R.drawable.spruce_leaves_item),
        new Item(18.2f, "Birch Leaves", R.drawable.birch_leaves_item),
        new Item(18.3f, "Jungle Leaves", R.drawable.jungle_leaves_item),
        new Item(19f, "Sponge", R.drawable.sponge_item),
        new Item(19.1f, "Wet Sponge", R.drawable.wet_sponge_item),
        new Item(20f, "Glass", R.drawable.glass_item),
        new Item(21f, "Lapis Lazuli Ore", R.drawable.lapis_ore_item),
        new Item(22f, "Lapis Lazuli Block", R.drawable.lapis_block_item),
        new Item(23f, "Dispenser", R.drawable.dispenser_item),
        new Item(24f, "Sandstone", R.drawable.sandstone_item),
        new Item(24.1f, "Chiseled Sandstone", R.drawable.chiseled_sandstone_item),
        new Item(24.2f, "Smooth Sandstone", R.drawable.smooth_sandstone_item),
        new Item(25f, "Note Block", R.drawable.note_block_item),
        new Item(26f, "Bed", R.drawable.red_bed_item),
        new Item(27f, "Powered Rail", R.drawable.powered_rail_item),
        new Item(28f, "Detector Rail", R.drawable.detector_rail_item),
        new Item(29f, "Sticky Piston", R.drawable.sticky_piston_item),
        new Item(30f, "Cobweb", R.drawable.cobweb_item),
        new Item(31f, "Dead Shrub", R.drawable.dead_bush_item),
        new Item(31.1f, "Grass", R.drawable.short_grass_item),
        new Item(31.2f, "Fern", R.drawable.fern_item),
        new Item(32f, "Dead Bush", R.drawable.dead_bush_item),
        new Item(33f, "Piston", R.drawable.piston_item)
    };
    public static String[] blockDictionary = {
            "unassigned",
            "air",
            "red_concrete",
            "orange_concrete",
            "yellow_concrete",
            "green_concrete"
    };
    public static int[] blockIdDictionary = {
            0,
            R.drawable.air,
            R.drawable.red_concrete,
            R.drawable.orange_concrete,
            R.drawable.yellow_concrete,
            R.drawable.green_concrete
    };
    public static Item searchItem(double id) {
        for(Item item : itemDictionary)
            if(item.getId() == id)
                return item;
        return itemDictionary[0];
    }
    public static Item searchItem(String itemName) {
        for(Item item : itemDictionary)
            if(item.getItemName().equals(itemName))
                return item;
        return itemDictionary[0];
    }
    public static String searchBlock(int id) {
        for(int i = 0; i < blockIdDictionary.length; i++) {
            if(blockIdDictionary[i] == id)
                return blockDictionary[i];
        }
        return "Not Found (" + id + ")";
    }
    public static void printBlocks() {
        for(int i = 0; i < blockIdDictionary.length; i++) {
            System.out.println(blockIdDictionary[i] + " : " + blockDictionary[i]);
        }
    }
}
