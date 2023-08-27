package io.github.idoomful.bukkitutils.statics;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityUtils {
    /**
     * Knocks any LivingEntity away from the specified location for the given speed.
     * Armor stands are also considered LivingEntities, so keep that in mind
     * @param affected The entity to knock away
     * @param source The location relative to which the entity is knocked away from
     * @param speed The speed which the entity will be knocked at
     * @param y The Y velocity. Controls how high the entity will fly upwards
     */
    public static void knockEntityAway(LivingEntity affected, Location source, double speed, double y) {
        Vector v = affected.getLocation().toVector().subtract(source.toVector()).normalize().setY(y);
        affected.setVelocity(v.multiply(speed));
    }

    /**
     * Launched any LivingEntity in any direction
     * @param entity The entity to launch
     * @param vector The vector
     */
    public static void launch(LivingEntity entity, Vector vector) {
        entity.setVelocity(vector);
    }

    /**
     * Modifies the speed of any LivingEntity by settings their attribute modifier with a reflection packet
     * @param en The entity to affect
     * @param speed The speed to set
     */
    public static void setEntitySpeed(LivingEntity en, double speed) {
        try {
            Class<?> craftEntity = ReflectionUtils.getOBCClass("entity.CraftEntity");
            Class<?> entityLiving = ReflectionUtils.getNMSClass("EntityLiving");
            Class<?> genericAttr = ReflectionUtils.getNMSClass("GenericAttributes");
            Class<?> iAttr = ReflectionUtils.getNMSClass("IAttribute");
            Class<?> attrMod = ReflectionUtils.getNMSClass("AttributeModifier");

            Object castEntity = craftEntity.cast(en);
            Object handle = castEntity.getClass().getMethod("getHandle").invoke(castEntity);
            Object entityLivingObj = entityLiving.cast(handle);

            Object movSpeed = genericAttr.getField("MOVEMENT_SPEED").get(null);
            Object attrInstObj = entityLivingObj.getClass().getMethod("getAttributeInstance", iAttr)
                    .invoke(entityLivingObj, movSpeed);
            Object attrModObj = attrMod.getConstructor(UUID.class, String.class, double.class, int.class)
                    .newInstance(en.getUniqueId(), "SpeedIncreaser", speed, 1);

            attrInstObj.getClass().getMethod("b", attrMod).invoke(attrInstObj, attrModObj);
            attrInstObj.getClass().getMethod("a", attrMod).invoke(attrInstObj, attrModObj);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all entities for the chunk of the specified location
     *
     * @param at Location inside of the targeted chunk
     * @return List of all entities in chunk
     */
    public static List<Entity> getChunkEntities(Location at) {
        return new ArrayList<>(Arrays.asList(at.getChunk().getEntities()));
    }

    /**
     * Gets all living entities for the chunk of the specified location
     *
     * @param at Location inside of the targeted chunk
     * @return List of all living entities in chunk
     */
    public static List<LivingEntity> getLivingChunkEntities(Location at) {
        return Stream.of(at.getChunk().getEntities())
                .filter(en -> en instanceof LivingEntity)
                .map(LivingEntity.class::cast)
                .collect(Collectors.toList());
    }

    public static boolean arrowIsFullyCharged(Arrow arrow) {
        final double velX = arrow.getVelocity().getX();
        final double velY = arrow.getVelocity().getY();
        final double velZ = arrow.getVelocity().getZ();

        final boolean chargedInX = (velX >= -3 && velX <= -2) || (velX >= 2 && velX <= 3);
        final boolean chargedInY = (velY >= -3 && velY <= -2) || (velY >= 2 && velY <= 3);
        final boolean chargedInZ = (velZ >= -3 && velZ <= -2) || (velZ >= 2 && velZ <= 3);

        return chargedInX || chargedInY || chargedInZ;
    }
}
