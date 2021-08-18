package io.github.idoomful.bukkitutils.statics;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ItemUtils {
    /**
     * Checks if two items have the same type and display name
     * @param item1 First item
     * @param item2 Second item
     */
    public static boolean isWeakSimilar(ItemStack item1, ItemStack item2) {
        if(item1.getType().equals(item2.getType())) {
            return item2.getItemMeta().getDisplayName().equalsIgnoreCase(item2.getItemMeta().getDisplayName());
        }
        return false;
    }

    /**
     * Gets the item in the main hand of a player. This method is made to support version above or equal to 1.8
     * @param player The target player
     * @return The item they are holding in their main hand
     */
    public static ItemStack getItemInHand(Player player) {
        return VersionUtils.usesVersionBetween("1.4.x", "1.8.x") ? player.getItemInHand() : player.getInventory().getItemInMainHand();
    }

    /**
     * Turns a list of ItemStack into a base64 string.
     * Very useful for storing inventories in a compact way
     * @return The encoded array in base64
     */
    public static String serialize(ItemStack[] obj) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(obj.length);

            for (int i = 0; i < obj.length; i++) {
                dataOutput.writeObject(obj[i]);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * Turns a base64 encoded string back into an array of ItemStack
     */
    public static ItemStack[] deserialize(String str) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(str));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            return new ItemStack[0];
        }
    }

    /**
     * Deserializes a base64 string inventory and sets its
     * contents into the given inventory.
     * Only provide inventories of the same type as the one
     * who was serialized, otherwise unexpected behavior might occur.
     *
     * @param inv The inventory which the deserialized items will be set in
     * @param str The encoded string to deserialize
     */
    public static void deserialize(Inventory inv, String str) {
        final ItemStack[] des = deserialize(str);
        for(int i = 0; i < des.length; i++) {
            if(des[i] == null) continue;
            inv.setItem(i, des[i]);
        }
    }
}
