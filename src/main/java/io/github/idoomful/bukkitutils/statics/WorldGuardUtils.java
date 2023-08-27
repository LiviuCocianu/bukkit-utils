package io.github.idoomful.bukkitutils.statics;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public class WorldGuardUtils {
    /**
     * Checks if the specified entity can be damaged (if they are protected by WorldGuard).
     * @param entity The entity to be checked
     */
    public static boolean canBeDamaged(LivingEntity entity) {
        final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionManager rm = rc.get(BukkitAdapter.adapt(entity.getWorld()));

        if(rm != null) {
            final int x = entity.getLocation().getBlockX();
            final int y = entity.getLocation().getBlockY();
            final int z = entity.getLocation().getBlockZ();

            if (rm.getApplicableRegions(BlockVector3.at(x, y, z)).getRegions().isEmpty()) {
                return !entity.hasMetadata("NPC");
            } else {
                for (ProtectedRegion reg : rm.getApplicableRegions(BlockVector3.at(x, y, z)).getRegions()) {
                    if (entity instanceof Animals && reg.getFlag(Flags.DAMAGE_ANIMALS) == StateFlag.State.DENY)
                        return false;
                    if (entity instanceof Player && reg.getFlag(Flags.PVP) == StateFlag.State.DENY)
                        return false;
                    if (entity instanceof Player && reg.getFlag(Flags.INVINCIBILITY) == StateFlag.State.ALLOW)
                        return false;
                    return !entity.hasMetadata("NPC");
                }
            }
        }

        return false;
    }

    /**
     * Damage an entity, checking if they can be damaged first (if they are protected by WorldGuard)
     * @param entity The entity to be affected
     * @param amount Damage amount
     */
    public static void damageEntity(LivingEntity entity, double amount) {
        if(canBeDamaged(entity)) entity.damage(amount);
    }

    /**
     * Set entity on fire, checking if they can be damaged first (if they are protected by WorldGuard)
     * @param entity
     * @param ticks
     */
    public static void burnEntity(LivingEntity entity, int ticks) {
        if(canBeDamaged(entity)) entity.setFireTicks(ticks);
    }

    /**
     * Checks if the region the entity is in, if any, meets the given condition
     * @param entity The entity that is to be checked
     * @param condition Condition to be met
     * @return Whether the region the entity is in, if any, meets the given condition
     */
    public static boolean isInsideRegion(Entity entity, Predicate<ProtectedRegion> condition) {
        final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionManager rm = rc.get(BukkitAdapter.adapt(entity.getWorld()));

        if(rm != null) {
            final int x = entity.getLocation().getBlockX();
            final int y = entity.getLocation().getBlockY();
            final int z = entity.getLocation().getBlockZ();

            return rm.getApplicableRegions(BlockVector3.at(x, y, z)).getRegions()
                    .stream().anyMatch(condition);
        }

        return false;
    }

    public static boolean isFlagOn(Player player, Location at, StateFlag flag) {
        final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionManager rm = rc.get(BukkitAdapter.adapt(player.getWorld()));

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        BlockVector3 position = BlockVector3.at(at.getBlockX(), at.getBlockY(), at.getBlockZ());
        ApplicableRegionSet set = rm.getApplicableRegions(position);

        return set.testState(localPlayer, flag);
    }
}
