package io.github.idoomful.bukkitutils.statics;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialUtils {
    private static final Map<String, Material> preItems = new HashMap<>();
    private static final Map<String, Material> postItems = new HashMap<>();

    /**
     * Get the Material associated with the given ID.
     * If the ID is invalid, Material.STONE will be returned
     * @param id The Minecraft ID of the item
     */
    public static Material getMaterialByID(String id) {
        if(VersionUtils.usesVersionBetween("1.4.x", "1.12.x")) {
            if(preItems.containsKey(id)) return preItems.get(id);
            else {
                for(String idd : preItems.keySet()) {
                    if(id.equals(idd.replace("_", ""))) return preItems.get(idd);
                }
            }
        } else {
            if (postItems.containsKey(id)) return postItems.get(id);
            else {
                for(String idd : postItems.keySet()) {
                    if(id.equals(idd.replace("_", ""))) return postItems.get(idd);
                }
            }
        }

        return null;
    }

    /**
     * Get the ID associated with the given Material.
     * If the Material is invalid, "stone" will be returned
     * @param material The Material
     */
    public static String getIDbyMaterial(Material material) {
        if(VersionUtils.usesVersionBetween("1.4.x", "1.12.x")) {
            for (Map.Entry<String, Material> entry : preItems.entrySet()) {
                if (entry.getValue().equals(material)) return entry.getKey();
            }
        } else {
            for (Map.Entry<String, Material> entry : postItems.entrySet()) {
                if (entry.getValue().equals(material)) return entry.getKey();
            }
        }

        return "stone";
    }

    /**
     * Gets a list of all blocks of this material from this chunk
     *
     * @param chunk Chunk to be scanned
     * @param mat Material to be searched
     * @return A list of blocks
     */
    public static List<Block> getBlocksInChunk(Chunk chunk, Material mat, int minY, int maxY) throws IllegalArgumentException {
        final List<Block> blocks = new ArrayList<>();

        if(minY > maxY) throw new IllegalArgumentException("minY cannot be bigger than maxY");

        final int constMaxY = !VersionUtils.usesVersionBetween("1.4.x", "1.17.x")
                    && chunk.getWorld().getEnvironment() == World.Environment.NORMAL
                    ?  -64 : 0;
        final int processedMin = Math.max(minY, constMaxY);
        final int processedMax = Math.min(maxY, 255);

        for(int y = processedMax; y >= processedMin; y--) {
            for(int x = 0; x < 15; x++) {
                for(int z = 0; z < 15; z++) {
                    final Block block = chunk.getBlock(x, y, z);

                    if(block.getType() == mat)
                        blocks.add(block);
                }
            }
        }

        return blocks;
    }

    /**
     * Checks if the material is a block that can be stood on. Material#isSolid returns some blocks
     * that cannot be stood on, so it's not a reliable method of checking
     *
     * @param material The material of the block
     * @return Whether it can be stood on or not
     */
    public static boolean canStandOn(Material material) {
        if(VersionUtils.usesVersionBetween("1.1.x", "1.12.x")) {
            switch(material.name()) {
                case "TRAP_DOOR":
                case "WOODEN_DOOR":
                case "WALL_SIGN":
                case "SIGN_POST":
                case "STONE_PLATE":
                case "WOOD_PLATE":
                case "GOLD_PLATE":
                case "WALL_BANNER":
                case "STANDING_BANNER":
                    return false;
            }
        } else {
            if(material.name().contains("TRAPDOOR")
                    || material.name().contains("SIGN")
                    || material.name().contains("_PLATE")
                    || material.name().contains("BANNER"))
                return false;
        }

        switch (material) {
            case SPRUCE_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case ACACIA_DOOR:
            case DARK_OAK_DOOR:
            case IRON_TRAPDOOR:
                return false;
            default:
                return material.isSolid();
        }
    }

    static {
        for(Material material : Material.values()) {
            postItems.put(material.name().toLowerCase(), material);
        }
    }

    static {
        for(Material material : Material.values()) {
            switch (material.toString()) {
                case "ACACIA_DOOR_ITEM": preItems.put("acacia_door", material); continue;
                case "BIRCH_DOOR_ITEM": preItems.put("birch_door", material); continue;
                case "BIRCH_WOOD_STAIRS": preItems.put("birch_stairs", material); continue;
                case "BOAT_ACACIA": preItems.put("acacia_boat", material); continue;
                case "BOAT_BIRCH": preItems.put("birch_boat", material); continue;
                case "BOAT_DARK_OAK": preItems.put("dark_oak_boat", material); continue;
                case "BOAT_JUNGLE": preItems.put("jungle_boat", material); continue;
                case "BOAT_SPRUCE": preItems.put("spruce_boat", material); continue;
                case "BOOK_AND_QUILL": preItems.put("writable_book", material); continue;
                case "BREWING_STAND_ITEM": preItems.put("brewing_stand", material); continue;
                case "BRICK": preItems.put("brick_block", material); continue;
                case "CARROT_ITEM": preItems.put("carrot", material); continue;
                case "CARROT_STICK": preItems.put("carrot_on_a_stick", material); continue;
                case "CAULDRON_ITEM": preItems.put("cauldron", material); continue;
                case "CLAY_BRICK": preItems.put("brick", material); continue;
                case "COBBLE_WALL": preItems.put("cobblestone_wall", material); continue;
                case "COMMAND": preItems.put("command_block", material); continue;
                case "COMMAND_CHAIN": preItems.put("chain_command_block", material); continue;
                case "COMMAND_MINECART": preItems.put("command_block_minecart", material); continue;
                case "COMMAND_REPEATING": preItems.put("repeating_command_block", material); continue;
                case "DARK_OAK_DOOR_ITEM": preItems.put("dark_oak_door", material); continue;
                case "DEAD_BUSH": preItems.put("deadbush", material); continue;
                case "DIAMOND_BARDING": preItems.put("diamond_horse_armor", material); continue;
                case "DIAMOND_SPADE": preItems.put("diamond_shovel", material); continue;
                case "DIODE": preItems.put("repeater", material); continue;
                case "DOUBLE_STEP": preItems.put("double_stone_slab", material); continue;
                case "DRAGONS_BREATH": preItems.put("dragon_breath", material); continue;
                case "EMPTY_MAP": preItems.put("map", material); continue;
                case "ENDER_STONE": preItems.put("end_stone", material); continue;
                case "EXPLOSIVE_MINECART": preItems.put("tnt_minecart", material); continue;
                case "EXP_BOTTLE": preItems.put("experience_bottle", material); continue;
                case "EYE_OF_ENDER": preItems.put("ender_eye", material); continue;
                case "FIREBALL": preItems.put("fire_charge", material); continue;
                case "FIREWORK": preItems.put("fireworks", material); continue;
                case "FLOWER_POT_ITEM": preItems.put("flower_pot", material); continue;
                case "GOLD_AXE": preItems.put("golden_axe", material); continue;
                case "GOLD_BARDING": preItems.put("golden_horse_armor", material); continue;
                case "GOLD_BOOTS": preItems.put("golden_boots", material); continue;
                case "GOLD_CHESTPLATE": preItems.put("golden_chestplate", material); continue;
                case "GOLD_HELMET": preItems.put("golden_helmet", material); continue;
                case "GOLD_HOE": preItems.put("golden_hoe", material); continue;
                case "GOLD_LEGGINGS": preItems.put("golden_leggings", material); continue;
                case "GOLD_PICKAXE": preItems.put("golden_pickaxe", material); continue;
                case "GOLD_PLATE": preItems.put("light_weighted_pressure_plate", material); continue;
                case "GOLD_RECORD": preItems.put("record_13", material); continue;
                case "GOLD_SPADE": preItems.put("golden_shovel", material); continue;
                case "GOLD_SWORD": preItems.put("golden_sword", material); continue;
                case "GREEN_RECORD": preItems.put("record_cat", material); continue;
                case "GRILLED_PORK": preItems.put("cooked_porkchop", material); continue;
                case "HARD_CLAY": preItems.put("hardened_clay", material); continue;
                case "HUGE_MUSHROOM_1": preItems.put("brown_mushroom_block", material); continue;
                case "HUGE_MUSHROOM_2": preItems.put("red_mushroom_block", material); continue;
                case "INK_SACK": preItems.put("dye", material); continue;
                case "IRON_BARDING": preItems.put("iron_horse_armor", material); continue;
                case "IRON_FENCE": preItems.put("iron_bars", material); continue;
                case "IRON_PLATE": preItems.put("heavy_weighted_pressure_plate", material); continue;
                case "JACK_O_LANTERN": preItems.put("lit_pumpkin", material); continue;
                case "JUNGLE_DOOR_ITEM": preItems.put("jungle_door", material); continue;
                case "JUNGLE_WOOD_STAIRS": preItems.put("jungle_stairs", material); continue;
                case "LEASH": preItems.put("lead", material); continue;
                case "LEAVES_2": preItems.put("leaves2", material); continue;
                case "LOG_2": preItems.put("log2", material); continue;
                case "LONG_GRASS": preItems.put("tallgrass", material); continue;
                case "MAP": preItems.put("filled_map", material); continue;
                case "MONSTER_EGG": preItems.put("spawn_egg", material); continue;
                case "MONSTER_EGGS": preItems.put("monster_egg", material); continue;
                case "MUSHROOM_SOUP": preItems.put("mushroom_stew", material); continue;
                case "MYCEL": preItems.put("mycelium", material); continue;
                case "NETHER_BRICK_ITEM": preItems.put("netherbrick", material); continue;
                case "NETHER_FENCE": preItems.put("nether_brick_fence", material); continue;
                case "NETHER_WARTS": preItems.put("nether_wart", material); continue;
                case "NOTE_BLOCK": preItems.put("noteblock", material); continue;
                case "PISTON_BASE": preItems.put("piston", material); continue;
                case "POTATO_ITEM": preItems.put("potato", material); continue;
                case "PORK": preItems.put("porkchop", material); continue;
                case "POWERED_MINECART": preItems.put("furnace_minecart", material); continue;
                case "RAILS": preItems.put("rail", material); continue;
                case "RAW_CHICKEN": preItems.put("chicken", material); continue;
                case "RAW_FISH": preItems.put("fish", material); continue;
                case "RAW_BEEF": preItems.put("beef", material); continue;
                case "RECORD_3": preItems.put("record_blocks", material); continue;
                case "RECORD_4": preItems.put("record_chirp", material); continue;
                case "RECORD_5": preItems.put("record_far", material); continue;
                case "RECORD_6": preItems.put("record_mall", material); continue;
                case "RECORD_7": preItems.put("record_mellohi", material); continue;
                case "RECORD_8": preItems.put("record_stal", material); continue;
                case "RECORD_9": preItems.put("record_strad", material); continue;
                case "RECORD_10": preItems.put("record_ward", material); continue;
                case "RECORD_12": preItems.put("record_wait", material); continue;
                case "REDSTONE_COMPARATOR": preItems.put("comparator", material); continue;
                case "RED_ROSE": preItems.put("red_flower", material); continue;
                case "SEEDS": preItems.put("wheat_seeds", material); continue;
                case "SKULL_ITEM": preItems.put("skull", material); continue;
                case "SLIME_BLOCK": preItems.put("slime", material); continue;
                case "SMOOTH_BRICK": preItems.put("stonebrick", material); continue;
                case "SMOOTH_STAIRS": preItems.put("stone_brick_stairs", material); continue;
                case "SNOW_BALL": preItems.put("snowball", material); continue;
                case "SPRUCE_DOOR_ITEM": preItems.put("spruce_door", material); continue;
                case "SPRUCE_WOOD_STAIRS": preItems.put("spruce_stairs", material); continue;
                case "STAINED_CLAY": preItems.put("stained_hardened_clay", material); continue;
                case "STEP": preItems.put("stone_slab", material); continue;
                case "STONE_PLATE": preItems.put("stone_pressure_plate", material); continue;
                case "STONE_SPADE": preItems.put("stone_shovel", material); continue;
                case "STORAGE_MINECART": preItems.put("chest_minecart", material); continue;
                case "SUGAR_CANE": preItems.put("reeds", material); continue;
                case "SULPHUR": preItems.put("gunpowder", material); continue;
                case "THIN_GLASS": preItems.put("glass_pane", material); continue;
                case "TOTEM": preItems.put("totem_of_undying", material); continue;
                case "TRAP_DOOR": preItems.put("trapdoor", material); continue;
                case "WATCH": preItems.put("clock", material); continue;
                case "WOOD_AXE": preItems.put("wooden_axe", material); continue;
                case "WOOD_BUTTON": preItems.put("wooden_button", material); continue;
                case "WOOD_DOOR": preItems.put("wooden_door", material); continue;
                case "WOOD_HOE": preItems.put("wooden_hoe", material); continue;
                case "WOOD_SWORD": preItems.put("wooden_sword", material); continue;
                case "WOOD_PICKAXE": preItems.put("wooden_pickaxe", material); continue;
                case "WOOD_PLATE": preItems.put("wooden_pressure_plate", material); continue;
                case "WOOD_SPADE": preItems.put("wooden_shovel", material); continue;
                case "WOOD_STEP": preItems.put("wooden_slab", material); continue;
                case "WOOD_STAIRS": preItems.put("oak_stairs", material); continue;

                default: preItems.put(material.name().toLowerCase(), material);
            }
        }
    }
}
