package io.github.idoomful.bukkitutils.statics;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PlayerUtils {
    enum Damage {
        WOOD_SWORD(4, 5),
        GOLD_SWORD(4, 5),
        STONE_SWORD(5, 6),
        IRON_SWORD(6, 7),
        DIAMOND_SWORD(7, 8),
        WOOD_AXE(3, 4),
        GOLD_AXE(3, 4),
        STONE_AXE(4, 5),
        IRON_AXE(5, 6),
        DIAMOND_AXE(6, 7);

        private final int amount18, amountpost;

        Damage(int amount18, int amountpost) {
            this.amount18 = amount18;
            this.amountpost = amountpost;
        }

        public int get() {
            if(VersionUtils.usesVersionBetween("1.4.x", "1.8.x")) return amount18;
            else return amountpost;
        }
    }

    /**
     * Hides the armor of the player for everyone.
     * This method is unstable, as it doesn't always hide the full set of armor
     * @param player The target
     */
    public static void hideArmor(Player player) {
        try {
            Object packet;

            for(int i = 1; i <= 4; i++) {
                if(!VersionUtils.usesVersionBetween("1.4.x", "1.8.x")) {
                    String[] armor = {"HEAD", "CHEST", "LEGS", "FEET"};

                    Object value = Enum.valueOf((Class<Enum>) ReflectionUtils.getNMSClass("EnumItemSlot"), armor[i - 1]);

                    packet = ReflectionUtils.getNMSClass("PacketPlayOutEntityEquipment")
                            .getConstructor(int.class, ReflectionUtils.getNMSClass("EnumItemSlot"), ReflectionUtils.getNMSClass("ItemStack"))
                            .newInstance(player.getEntityId(), value, null);
                } else {
                    packet = ReflectionUtils.getNMSClass("PacketPlayOutEntityEquipment")
                            .getConstructor(int.class, int.class, ReflectionUtils.getNMSClass("ItemStack"))
                            .newInstance(player.getEntityId(), i,
                                    ReflectionUtils.getOBCClass("inventory.CraftItemStack")
                                            .getMethod("asNMSCopy", ItemStack.class)
                                            .invoke(ReflectionUtils.getOBCClass("inventory.CraftItemStack"), new ItemStack(Material.AIR))
                            );
                }

                for (Player pl : Bukkit.getOnlinePlayers()) ReflectionUtils.sendPacket(pl, packet);
            }
        } catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the armor of the player again, if it was hidden before.
     * This method is unstable, as it doesn't always show the full set of armor
     * @param player The target
     */
    public static void showArmor(Player player) {
        try {
            Object packet;

            for(int i = 1; i <= 4; i++) {
                if(!VersionUtils.usesVersionBetween("1.4.x", "1.8.x")) {
                    String[] armor = {"HEAD", "CHEST", "LEGS", "FEET"};
                    Object value = Enum.valueOf((Class<Enum>) ReflectionUtils.getNMSClass("EnumItemSlot"), armor[i - 1]);

                    packet = ReflectionUtils.getNMSClass("PacketPlayOutEntityEquipment")
                            .getConstructor(int.class, ReflectionUtils.getNMSClass("EnumItemSlot"), ReflectionUtils.getNMSClass("ItemStack"))
                            .newInstance(player.getEntityId(), value, player.getInventory().getChestplate());
                } else {
                    packet = ReflectionUtils.getNMSClass("PacketPlayOutEntityEquipment")
                            .getConstructor(int.class, int.class, ReflectionUtils.getNMSClass("ItemStack"))
                            .newInstance(player.getEntityId(), i,
                                    ReflectionUtils.getOBCClass("inventory.CraftItemStack")
                                            .getMethod("asNMSCopy", ItemStack.class)
                                            .invoke(ReflectionUtils.getOBCClass("inventory.CraftItemStack"), player.getInventory().getArmorContents()[i - 1])
                            );
                }

                for (Player pl : Bukkit.getOnlinePlayers()) ReflectionUtils.sendPacket(pl, packet);
            }
        } catch(NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the amount of damage a player would be able to deal, based
     * on certain criteria: melee damage, enchantments and potion effects
     * that would boost damage.
     * @param player The analysed player
     */
    public static float relativeDamage(Player player) {
        ItemStack item = ItemUtils.getItemInHand(player);
        float output;

        try {
            output = Damage.valueOf(item.getType().name()).get();
        } catch(IllegalArgumentException ie) {
            output = 1;
        }

        if(item.hasItemMeta()) {
            if(item.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                int level = item.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                if(VersionUtils.usesVersionBetween("1.4.x", "1.8.x")) output += 1.25 * level;
                else output += 0.5 * level + 0.5;
            }
        }

        if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            for(PotionEffect eff : player.getActivePotionEffects()) {
                if(eff.getType().getName().equals(PotionEffectType.INCREASE_DAMAGE.getName())) {
                    int level = eff.getAmplifier() + 1;
                    output += 3 * level;
                    break;
                }
            }
        }

        return output;
    }

    /**
     * Gets any living entity the player is looking at in a certain radius
     *
     * @param player Player who is looking
     * @param radius Checking radius relative to player in all directions
     * @return The looked-at entity, or null if none
     */
    public static LivingEntity getLookedAtEntity(Player player, float radius, boolean excludeNPCs) {
        for(final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
            if(excludeNPCs && entity.hasMetadata("NPC")) continue;

            if(entity instanceof LivingEntity) {
                LivingEntity en = (LivingEntity) entity;

                final Location eye = player.getEyeLocation();
                final Vector toEntity = en.getEyeLocation().toVector().subtract(eye.toVector());
                final double dot = toEntity.normalize().dot(eye.getDirection());

                final Vector toEntity2 = en.getLocation().add(0, 1, 0).toVector().subtract(eye.toVector());
                final double dot2 = toEntity2.normalize().dot(eye.getDirection());

                final Vector toEntity3 = en.getLocation().add(0, 0.5, 0).toVector().subtract(eye.toVector());
                final double dot3 = toEntity3.normalize().dot(eye.getDirection());

                if(dot > 0.99D || dot2 > 0.99D || dot3 > 0.99D) return en;
            }
        }

        return null;
    }

    /**
     * Gets a list of chunks in a 2x2 radius from the player's location
     *
     * @param player Target player
     * @param radius Chunk radius around player. A radius of 1 is the chunk
     *               the player is in
     * @param includeCenter If radius is 1 and includeCenter is false, an empty list will be returned
     * @return List of surrounding chunks, including the center
     */
    public static List<Chunk> chunksAroundPlayer(Player player, int radius, boolean includeCenter) {
        final List<Integer> offset = new ArrayList<>();
        final int processedRadius = Math.max(radius - 1, 0);

        for(int i = -processedRadius; i <= processedRadius; i++)
            offset.add(i);

        final int baseX = player.getLocation().getChunk().getX();
        final int baseZ = player.getLocation().getChunk().getZ();

        List<Chunk> chunksAroundPlayer = new ArrayList<>();

        for(int x : offset) {
            for(int z : offset) {
                Chunk chunk = player.getWorld().getChunkAt(baseX + x, baseZ + z);
                if(!includeCenter && (x == baseX && z == baseZ)) continue;
                chunksAroundPlayer.add(chunk);
            }
        }

        return chunksAroundPlayer;
    }

    public static boolean isCritting(Player p) {
        return p.getFallDistance() > 0.0F && !p.isOnGround();
    }
}
