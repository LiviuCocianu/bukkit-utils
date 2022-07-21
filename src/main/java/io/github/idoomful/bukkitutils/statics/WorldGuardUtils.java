package io.github.idoomful.bukkitutils.statics;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
                    if (entity instanceof Player && reg.getFlag(Flags.PVP) == StateFlag.State.DENY) return false;
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
}
