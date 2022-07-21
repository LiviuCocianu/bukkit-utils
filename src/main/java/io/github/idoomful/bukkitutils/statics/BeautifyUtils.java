package io.github.idoomful.bukkitutils.statics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.texture.BlockTexture;
import xyz.xenondevs.particle.data.texture.ItemTexture;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BeautifyUtils {
    /**
     * Plays a sound for all players within the range of a specified location.
     *
     * @param loc The center location where the sound should come from
     * @param sound The sound type
     * @param volume The volume. Doesn't really matter since the sound plays directly at the players'
     *              location, as long as they are within the radius
     * @param pitch The pitch
     * @param radius The radius the players need to be in relative to the location to hear the sound
     */
    public static void playSoundRadius(Location loc, Sound sound, float volume, float pitch, float radius) {
        loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream()
                .filter(en -> en instanceof Player)
                .forEach(pl -> {
                    ((Player) pl).playSound(pl.getLocation(), sound, volume, pitch);
                });
    }

    /**
     * Plays a sound for all players within the specified radius.
     * A pattern with this format will be specified: <p/>"SOUND VOLUME PITCH [REPEAT] [DELAY]"
     * <p/>
     * [REPEAT] - the amount of times the sound will repeat for<p/>
     * [DELAY] - the delay between each sound repeat
     *
     * @param loc The center location where the sound should come from
     * @param pattern The sound pattern
     * @param radius The radius the players need to be in relative to the location to hear the sound
     */
    public static void playSoundRadius(JavaPlugin main, Location loc, String pattern, float radius) {
        loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream()
                .filter(en -> en instanceof Player)
                .forEach(pl -> {
                    Player player = (Player) pl;
                    playSound(main, player, pattern);
                });
    }

    /**
     * Plays a sound for all players within the specified radius.
     * Doesn't include cross version support for Sound enums.
     * A pattern with this format will be specified: <p/>"SOUND VOLUME PITCH [REPEAT] [DELAY]"
     * <p/>
     * [REPEAT] - the amount of times the sound will repeat for<p/>
     * [DELAY] - the delay between each sound repeat
     *
     * @param loc The center location where the sound should come from
     * @param pattern The sound pattern
     * @param radius The radius the players need to be in relative to the location to hear the sound
     */
    public static void playSoundRadiusSimple(JavaPlugin main, Location loc, String pattern, float radius) {
        loc.getWorld().getNearbyEntities(loc, radius, radius, radius).stream()
                .filter(en -> en instanceof Player)
                .forEach(pl -> {
                    Player player = (Player) pl;
                    playSoundSimple(main, player, pattern);
                });
    }

    /**
     * Plays a sound for one player.
     * Doesn't include cross version support for Sound enums.
     * A pattern with this format will be specified: <p/>"SOUND VOLUME PITCH [REPEAT] [DELAY]"
     * <p/>
     * [REPEAT] - the amount of times the sound will repeat for<p/>
     * [DELAY] - the delay between each sound repeat
     *
     * @param player The player to play the sound to
     * @param pattern The sound pattern
     */
    public static void playSoundSimple(JavaPlugin main, Player player, String pattern) {
        final String[] args = pattern.split(" ");

        final Sound sound = validateSoundEnum(args[0]);
        float volume = Float.parseFloat(args[1]);
        float pitch = Float.parseFloat(args[2]);
        int repeat = 1, delay = 0;

        if (args.length >= 4) repeat = Integer.parseInt(args[3]);
        if (args.length >= 5) delay = Integer.parseInt(args[4]);

        final AtomicInteger counter = new AtomicInteger(repeat);
        final AtomicInteger ID = new AtomicInteger();

        ID.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (counter.get() > 0) {
                player.playSound(player.getLocation(), sound, volume, pitch);
                counter.decrementAndGet();
            } else Bukkit.getScheduler().cancelTask(ID.get());
        }, 0, delay));
    }

    /**
     * Plays a sound for one player.
     * A pattern with this format will be specified: <p/>"SOUND VOLUME PITCH [REPEAT] [DELAY]"
     * <p/>
     * [REPEAT] - the amount of times the sound will repeat for<p/>
     * [DELAY] - the delay between each sound repeat
     *
     * @param player The player to play the sound to
     * @param pattern The sound pattern
     */
    public static void playSound(JavaPlugin main, Player player, String pattern) {
        final String[] args = pattern.split(" ");

        final Sound sound = validateSoundEnum(args[0]);
        float volume = Float.parseFloat(args[1]);
        float pitch = Float.parseFloat(args[2]);
        int repeat = 1, delay = 0;

        if (args.length >= 4) repeat = Integer.parseInt(args[3]);
        if (args.length >= 5) delay = Integer.parseInt(args[4]);

        final AtomicInteger counter = new AtomicInteger(repeat);
        final AtomicInteger ID = new AtomicInteger();

        ID.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (counter.get() > 0) {
                player.playSound(player.getLocation(), sound, volume, pitch);
                counter.decrementAndGet();
            } else Bukkit.getScheduler().cancelTask(ID.get());
        }, 0, delay));
    }

    private static Sound validateSoundEnum(String sound) {
        try {
            return Sounds.valueOf(sound).getSound();
        } catch(IllegalArgumentException ignored) {
            return Sound.valueOf(sound);
        }
    }

    /**
     * Displays a particle for all specified players and according to the pattern.
     * A pattern with this format will be specified: <p/>"PARTICLE DX DY DZ SPEED COUNT"
     *
     * @param loc The location where the particle will appear
     * @param players The players that will be able to see the particle
     * @param pattern The pattern of the particle
     */
    public static void displayParticle(Location loc, Collection<? extends Player> players, String pattern) {
        // <particle> <x> <y> <z> <speed> <count>
        final String[] args = pattern.split(" ");

        String particle = args[0].toUpperCase().replace(" ", "_");
        float x = args.length < 2 ? 0 : Float.parseFloat(args[1]);
        float y = args.length < 3 ? 0 : Float.parseFloat(args[2]);
        float z = args.length < 4 ? 0 : Float.parseFloat(args[3]);
        float speed = args.length < 5 ? 0.05f : Float.parseFloat(args[4]);
        int count = args.length < 6 ? 10 : Integer.parseInt(args[5]);

        new ParticleBuilder(ParticleEffect.valueOf(particle), loc)
                .setOffset(new Vector(x, y, z))
                .setSpeed(speed)
                .setAmount(count)
                .display(players);
    }

    /**
     * Displays a particle for all specified players and according to the pattern
     * A pattern with this format will be specified: <p/>"PARTICLE DX DY DZ SPEED COUNT block/item=ID"
     * <p/>Example: FALLING_DUST 1 1 1 0.05 10 block=lapis_block
     *
     * @param loc The location where the particle will appear
     * @param players The players that will be able to see the particle
     * @param pattern The pattern of the particle
     */
    public static void displayTexturedParticle(Location loc, Collection<? extends Player> players, String pattern) {
        // <particle> <x> <y> <z> <speed> <count> <item/block=material>
        final String[] args = pattern.split(" ");

        String particle = args[0].toUpperCase().replace(" ", "_");
        float x = args.length < 2 ? 0 : Float.parseFloat(args[1]);
        float y = args.length < 3 ? 0 : Float.parseFloat(args[2]);
        float z = args.length < 4 ? 0 : Float.parseFloat(args[3]);
        float speed = args.length < 5 ? 0.05f : Float.parseFloat(args[4]);
        int count = args.length < 6 ? 10 : Integer.parseInt(args[5]);

        String textureType = args.length >= 7 ? args[6].split("=")[0] : "";
        Material mat = args.length >= 7 ? Material.valueOf(args[6].split("=")[1]) : null;

        ParticleBuilder pb = new ParticleBuilder(ParticleEffect.valueOf(particle), loc)
                .setOffset(new Vector(x, y, z))
                .setSpeed(speed)
                .setAmount(count);

        if(mat != null) {
            if(textureType.equalsIgnoreCase("block")) pb.setParticleData(new BlockTexture(mat));
            else if(textureType.equalsIgnoreCase("item")) pb.setParticleData(new ItemTexture(new ItemStack(mat)));
        }

        pb.display(players);
    }

    @SuppressWarnings("unused")
    public enum Sounds {
        AMBIENCE_CAVE("AMBIENCE_CAVE", "AMBIENT_CAVE"),
        AMBIENCE_RAIN("AMBIENCE_RAIN", "WEATHER_RAIN"),
        AMBIENCE_THUNDER("AMBIENCE_THUNDER", "ENTITY_LIGHTNING_THUNDER"),
        ANVIL_BREAK("ANVIL_BREAK", "BLOCK_ANVIL_BREAK"),
        ANVIL_LAND("ANVIL_LAND", "BLOCK_ANVIL_LAND"),
        ANVIL_USE("ANVIL_USE", "BLOCK_ANVIL_USE"),
        ARROW_HIT("ARROW_HIT", "ENTITY_ARROW_HIT"),
        BURP("BURP", "ENTITY_PLAYER_BURP"),
        CHEST_CLOSE("CHEST_CLOSE", "ENTITY_CHEST_CLOSE"),
        CHEST_OPEN("CHEST_OPEN", "ENTITY_CHEST_OPEN"),
        CLICK("CLICK", "UI_BUTTON_CLICK"),
        DOOR_CLOSE("DOOR_CLOSE", "BLOCK_WOODEN_DOOR_CLOSE"),
        DOOR_OPEN("DOOR_OPEN", "BLOCK_WOODEN_DOOR_OPEN"),
        DRINK("DRINK", "ENTITY_GENERIC_DRINK"),
        EAT("EAT", "ENTITY_GENERIC_EAT"),
        EXPLODE("EXPLODE", "ENTITY_GENERIC_EXPLODE"),
        FALL_BIG("FALL_BIG", "ENTITY_GENERIC_BIG_FALL"),
        FALL_SMALL("FALL_SMALL", "ENTITY_GENERIC_SMALL_FALL"),
        FIRE("FIRE", "BLOCK_FIRE_AMBIENT"),
        FIRE_IGNITE("FIRE_IGNITE", "ITEM_FLINTANDSTEEL_USE"),
        FIZZ("FIZZ", "BLOCK_FIRE_EXTINGUISH"),
        FUSE("FUSE", "ENTITY_TNT_PRIMED"),
        GLASS("GLASS", "BLOCK_GLASS_BREAK"),
        HURT_FLESH("HURT_FLESH", "ENTITY_PLAYER_HURT"),
        ITEM_BREAK("ITEM_BREAK", "ENTITY_ITEM_BREAK"),
        ITEM_PICKUP("ITEM_PICKUP", "ENTITY_ITEM_PICKUP"),
        LAVA("LAVA", "BLOCK_LAVA_AMBIENT"),
        LAVA_POP("LAVA_POP", "BLOCK_LAVA_POP"),
        LEVEL_UP("LEVEL_UP", "ENTITY_PLAYER_LEVELUP"),
        MINECART_BASE("MINECART_BASE", "ENTITY_MINECART_RIDING"),
        MINECART_INSIDE("MINECART_INSIDE", "ENTITY_MINECART_RIDING"),
        NOTE_BASS("NOTE_BASS", "BLOCK_NOTE_BASS"),
        NOTE_PIANO("NOTE_PIANO", "BLOCK_NOTE_HARP"),
        NOTE_BASS_DRUM("NOTE_BASS_DRUM", "BLOCK_NOTE_BASEDRUM"),
        NOTE_STICKS("NOTE_STICKS", "BLOCK_NOTE_HAT"),
        NOTE_BASS_GUITAR("NOTE_BASS_GUITAR", "BLOCK_NOTE_BASS"),
        NOTE_SNARE_DRUM("NOTE_SNARE_DRUM", "BLOCK_NOTE_SNARE"),
        NOTE_PLING("NOTE_PLING", "BLOCK_NOTE_PLING"),
        ORB_PICKUP("ORB_PICKUP", "ENTITY_EXPERIENCE_ORB_PICKUP"),
        PISTON_EXTEND("PISTON_EXTEND", "BLOCK_PISTON_EXTEND"),
        PISTON_RETRACT("PISTON_RETRACT", "BLOCK_PISTON_CONTRACT"),
        PORTAL("PORTAL", "BLOCK_PORTAL_AMBIENT"),
        PORTAL_TRAVEL("PORTAL_TRAVEL", "BLOCK_PORTAL_TRAVEL"),
        PORTAL_TRIGGER("PORTAL_TRIGGER", "BLOCK_PORTAL_TRIGGER"),
        SHOOT_ARROW("SHOOT_ARROW", "ENTITY_ARROW_SHOOT"),
        SPLASH("SPLASH", "ENTITY_GENERIC_SPLASH"),
        SPLASH2("SPLASH2", "ENTITY_BOBBER_SPLASH"),
        STEP_GRASS("STEP_GRASS", "BLOCK_GRASS_STEP"),
        STEP_GRAVEL("STEP_GRAVEL", "BLOCK_GRAVEL_STEP"),
        STEP_LADDER("STEP_LADDER", "BLOCK_LADDER_STEP"),
        STEP_SAND("STEP_SAND", "BLOCK_SAND_STEP"),
        STEP_SNOW("STEP_SNOW", "BLOCK_SNOW_STEP"),
        STEP_STONE("STEP_STONE", "BLOCK_STONE_STEP"),
        STEP_WOOD("STEP_WOOD", "BLOCK_WOOD_STEP"),
        STEP_WOOL("STEP_WOOL", "BLOCK_CLOTH_STEP"),
        SWIM("SWIM", "ENTITY_GENERIC_SWIM"),
        WATER("WATER", "BLOCK_WATER_AMBIENT"),
        WOOD_CLICK("WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_ON"),
        BAT_DEATH("BAT_DEATH", "ENTITY_BAT_DEATH"),
        BAT_HURT("BAT_HURT", "ENTITY_BAT_HURT"),
        BAT_IDLE("BAT_IDLE", "ENTITY_BAT_AMBIENT"),
        BAT_LOOP("BAT_LOOP", "ENTITY_BAT_LOOP"),
        BAT_TAKEOFF("BAT_TAKEOFF", "ENTITY_BAT_TAKEOFF"),
        BLAZE_BREATH("BLAZE_BREATH", "ENTITY_BLAZE_AMBIENT"),
        BLAZE_DEATH("BLAZE_DEATH", "ENTITY_BLAZE_DEATH"),
        BLAZE_HIT("BLAZE_HIT", "ENTITY_BLAZE_HURT"),
        CAT_HISS("CAT_HISS", "ENTITY_CAT_HISS"),
        CAT_HIT("CAT_HIT", "ENTITY_CAT_HURT"),
        CAT_MEOW("CAT_MEOW", "ENTITY_CAT_AMBIENT"),
        CAT_PURR("CAT_PURR", "ENTITY_CAT_PURR"),
        CAT_PURREOW("CAT_PURREOW", "ENTITY_CAT_PURREOW"),
        CHICKEN_IDLE("CHICKEN_IDLE", "ENTITY_CHICKEN_AMBIENT"),
        CHICKEN_HURT("CHICKEN_HURT", "ENTITY_CHICKEN_HURT"),
        CHICKEN_EGG_POP("CHICKEN_EGG_POP", "ENTITY_CHICKEN_EGG"),
        CHICKEN_WALK("CHICKEN_WALK", "ENTITY_CHICKEN_STEP"),
        COW_IDLE("COW_IDLE", "ENTITY_COW_AMBIENT"),
        COW_HURT("COW_HURT", "ENTITY_COW_HURT"),
        COW_WALK("COW_WALK", "ENTITY_COW_STEP"),
        CREEPER_HISS("CREEPER_HISS", "ENTITY_CREEPER_PRIMED"),
        CREEPER_DEATH("CREEPER_DEATH", "ENTITY_CREEPER_DEATH"),
        ENDERDRAGON_DEATH("ENDERDRAGON_DEATH", "ENTITY_ENDERDRAGON_DEATH"),
        ENDERDRAGON_GROWL("ENDERDRAGON_GROWL", "ENTITY_ENDERDRAGON_GROWL"),
        ENDERDRAGON_HIT("ENDERDRAGON_HIT", "ENTITY_ENDERDRAGON_HURT"),
        ENDERDRAGON_WINGS("ENDERDRAGON_WINGS", "ENTITY_ENDERDRAGON_FLAP"),
        ENDERMAN_DEATH("ENDERMAN_DEATH", "ENTITY_ENDERMEN_DEATH"),
        ENDERMAN_HIT("ENDERMAN_HIT", "ENTITY_ENDERMEN_HURT"),
        ENDERMAN_IDLE("ENDERMAN_IDLE", "ENTITY_ENDERMEN_AMBIENT"),
        ENDERMAN_TELEPORT("ENDERMAN_TELEPORT", "ENTITY_ENDERMEN_TELEPORT"),
        ENDERMAN_SCREAM("ENDERMAN_SCREAM", "ENTITY_ENDERMEN_SCREAM"),
        ENDERMAN_STARE("ENDERMAN_STARE", "ENTITY_ENDERMEN_STARE"),
        GHAST_SCREAM("GHAST_SCREAM", "ENTITY_GHAST_SCREAM"),
        GHAST_SCREAM2("GHAST_SCREAM2", "ENTITY_GHAST_HURT"),
        GHAST_CHARGE("GHAST_CHARGE", "ENTITY_GHAST_WARN"),
        GHAST_DEATH("GHAST_DEATH", "ENTITY_GHAST_DEATH"),
        GHAST_FIREBALL("GHAST_FIREBALL", "ENTITY_GHAST_SHOOT"),
        GHAST_MOAN("GHAST_MOAN", "ENTITY_GHAST_AMBIENT"),
        IRONGOLEM_DEATH("IRONGOLEM_DEATH", "ENTITY_IRONGOLEM_DEATH"),
        IRONGOLEM_HIT("IRONGOLEM_HIT", "ENTITY_IRONGOLEM_HURT"),
        IRONGOLEM_THROW("IRONGOLEM_THROW", "ENTITY_IRONGOLEM_ATTACK"),
        IRONGOLEM_WALK("IRONGOLEM_WALK", "ENTITY_IRONGOLEM_STEP"),
        MAGMACUBE_WALK("MAGMACUBE_WALK", "ENTITY_MAGMACUBE_SQUISH"),
        MAGMACUBE_WALK2("MAGMACUBE_WALK2", "ENTITY_MAGMACUBE_SQUISH"),
        MAGMACUBE_JUMP("MAGMACUBE_JUMP", "ENTITY_MAGMACUBE_JUMP"),
        PIG_IDLE("PIG_IDLE", "ENTITY_PIG_AMBIENT"),
        PIG_DEATH("PIG_DEATH", "ENTITY_PIG_DEATH"),
        PIG_WALK("PIG_WALK", "ENTITY_PIG_STEP"),
        SHEEP_IDLE("SHEEP_IDLE", "ENTITY_SHEEP_AMBIENT"),
        SHEEP_SHEAR("SHEEP_SHEAR", "ENTITY_SHEEP_SHEAR"),
        SHEEP_WALK("SHEEP_WALK", "ENTITY_SHEEP_STEP"),
        SILVERFISH_HIT("SILVERFISH_HIT", "ENTITY_SILVERFISH_HURT"),
        SILVERFISH_KILL("SILVERFISH_KILL", "ENTITY_SILVERFISH_DEATH"),
        SILVERFISH_IDLE("SILVERFISH_IDLE", "ENTITY_SILVERFISH_AMBIENT"),
        SILVERFISH_WALK("SILVERFISH_WALK", "ENTITY_SILVERFISH_STEP"),
        SKELETON_IDLE("SKELETON_IDLE", "ENTITY_SKELETON_AMBIENT"),
        SKELETON_DEATH("SKELETON_DEATH", "ENTITY_SKELETON_DEATH"),
        SKELETON_HURT("SKELETON_HURT", "ENTITY_SKELETON_HURT"),
        SKELETON_WALK("SKELETON_WALK", "ENTITY_SKELETON_STEP"),
        SLIME_ATTACK("SLIME_ATTACK", "ENTITY_SLIME_ATTACK"),
        SLIME_WALK("SLIME_WALK", "ENTITY_SLIME_JUMP"),
        SLIME_WALK2("SLIME_WALK2", "ENTITY_SLIME_SQUISH"),
        SPIDER_IDLE("SPIDER_IDLE", "ENTITY_SPIDER_AMBIENT"),
        SPIDER_DEATH("SPIDER_DEATH", "ENTITY_SPIDER_DEATH"),
        SPIDER_WALK("SPIDER_WALK", "ENTITY_SPIDER_STEP"),
        WITHER_DEATH("WITHER_DEATH", "ENTITY_WITHER_DEATH"),
        WITHER_HURT("WITHER_HURT", "ENTITY_WITHER_HURT"),
        WITHER_IDLE("WITHER_IDLE", "ENTITY_WITHER_AMBIENT"),
        WITHER_SHOOT("WITHER_SHOOT", "ENTITY_WITHER_SHOOT"),
        WITHER_SPAWN("WITHER_SPAWN", "ENTITY_WITHER_SPAWN"),
        WOLF_BARK("WOLF_BARK", "ENTITY_WOLF_AMBIENT"),
        WOLF_DEATH("WOLF_DEATH", "ENTITY_WOLF_DEATH"),
        WOLF_GROWL("WOLF_GROWL", "ENTITY_WOLF_GROWL"),
        WOLF_HOWL("WOLF_HOWL", "ENTITY_WOLF_HOWL"),
        WOLF_HURT("WOLF_HURT", "ENTITY_WOLF_HURT"),
        WOLF_PANT("WOLF_PANT", "ENTITY_WOLF_PANT"),
        WOLF_SHAKE("WOLF_SHAKE", "ENTITY_WOLF_SHAKE"),
        WOLF_WALK("WOLF_WALK", "ENTITY_WOLF_STEP"),
        WOLF_WHINE("WOLF_WHINE", "ENTITY_WOLF_WHINE"),
        ZOMBIE_METAL("ZOMBIE_METAL", "ENTITY_ZOMBIE_ATTACK_IRON_DOOR"),
        ZOMBIE_WOOD("ZOMBIE_WOOD", "ENTITY_ZOMBIE_ATTACK_DOOR_WOOD"),
        ZOMBIE_WOODBREAK("ZOMBIE_WOODBREAK", "ENTITY_ZOMBIE_BREAK_DOOR_WOOD"),
        ZOMBIE_IDLE("ZOMBIE_IDLE", "ENTITY_ZOMBIE_AMBIENT"),
        ZOMBIE_DEATH("ZOMBIE_DEATH", "ENTITY_ZOMBIE_DEATH"),
        ZOMBIE_HURT("ZOMBIE_HURT", "ENTITY_ZOMBIE_HURT"),
        ZOMBIE_INFECT("ZOMBIE_INFECT", "ENTITY_ZOMBIE_INFECT"),
        ZOMBIE_UNFECT("ZOMBIE_UNFECT", "ENTITY_ZOMBIE_VILLAGER_CONVERTED"),
        ZOMBIE_REMEDY("ZOMBIE_REMEDY", "ENTITY_ZOMBIE_VILLAGER_CURE"),
        ZOMBIE_WALK("ZOMBIE_WALK", "ENTITY_ZOMBIE_STEP"),
        ZOMBIE_PIG_IDLE("ZOMBIE_PIG_IDLE", "ENTITY_ZOMBIE_PIG_AMBIENT"),
        ZOMBIE_PIG_ANGRY("ZOMBIE_PIG_ANGRY", "ENTITY_ZOMBIE_PIG_ANGRY"),
        ZOMBIE_PIG_DEATH("ZOMBIE_PIG_DEATH", "ENTITY_ZOMBIE_PIG_DEATH"),
        ZOMBIE_PIG_HURT("ZOMBIE_PIG_HURT", "ENTITY_ZOMBIE_PIG_HURT"),
        DIG_WOOL("DIG_WOOL", "BLOCK_CLOTH_BREAK"),
        DIG_GRASS("DIG_GRASS", "BLOCK_GRASS_BREAK"),
        DIG_GRAVEL("DIG_GRAVEL", "BLOCK_GRAVEL_BREAK"),
        DIG_SAND("DIG_SAND", "BLOCK_SAND_BREAK"),
        DIG_SNOW("DIG_SNOW", "BLOCK_SNOW_BREAK"),
        DIG_STONE("DIG_STONE", "BLOCK_STONE_BREAK"),
        DIG_WOOD("DIG_WOOD", "BLOCK_WOOD_BREAK"),
        FIREWORK_BLAST("FIREWORK_BLAST", "ENTITY_FIREWORK_BLAST"),
        FIREWORK_BLAST2("FIREWORK_BLAST2", "ENTITY_FIREWORK_BLAST_FAR"),
        FIREWORK_LARGE_BLAST("FIREWORK_LARGE_BLAST", "ENTITY_FIREWORK_LARGE_BLAST"),
        FIREWORK_LARGE_BLAST2("FIREWORK_LARGE_BLAST2", "ENTITY_FIREWORK_LARGE_BLAST_FAR"),
        FIREWORK_TWINKLE("FIREWORK_TWINKLE", "ENTITY_FIREWORK_TWINKLE"),
        FIREWORK_TWINKLE2("FIREWORK_TWINKLE2", "ENTITY_FIREWORK_TWINKLE_FAR"),
        FIREWORK_LAUNCH("FIREWORK_LAUNCH", "ENTITY_FIREWORK_LAUNCH"),
        SUCCESSFUL_HIT("SUCCESSFUL_HIT", "ENTITY_PLAYER_ATTACK_STRONG"),
        HORSE_ANGRY("HORSE_ANGRY", "ENTITY_HORSE_ANGRY"),
        HORSE_ARMOR("HORSE_ARMOR", "ENTITY_HORSE_ARMOR"),
        HORSE_BREATHE("HORSE_BREATHE", "ENTITY_HORSE_BREATHE"),
        HORSE_DEATH("HORSE_DEATH", "ENTITY_HORSE_DEATH"),
        HORSE_GALLOP("HORSE_GALLOP", "ENTITY_HORSE_GALLOP"),
        HORSE_HIT("HORSE_HIT", "ENTITY_HORSE_HURT"),
        HORSE_IDLE("HORSE_IDLE", "ENTITY_HORSE_AMBIENT"),
        HORSE_JUMP("HORSE_JUMP", "ENTITY_HORSE_JUMP"),
        HORSE_LAND("HORSE_LAND", "ENTITY_HORSE_LAND"),
        HORSE_SADDLE("HORSE_SADDLE", "ENTITY_HORSE_SADDLE"),
        HORSE_SOFT("HORSE_SOFT", "ENTITY_HORSE_STEP"),
        HORSE_WOOD("HORSE_WOOD", "ENTITY_HORSE_STEP_WOOD"),
        DONKEY_ANGRY("DONKEY_ANGRY", "ENTITY_DONKEY_ANGRY"),
        DONKEY_DEATH("DONKEY_DEATH", "ENTITY_DONKEY_DEATH"),
        DONKEY_HIT("DONKEY_HIT", "ENTITY_DONKEY_HURT"),
        DONKEY_IDLE("DONKEY_IDLE", "ENTITY_DONKEY_AMBIENT"),
        HORSE_SKELETON_DEATH("HORSE_SKELETON_DEATH", "ENTITY_SKELETON_HORSE_DEATH"),
        HORSE_SKELETON_HIT("HORSE_SKELETON_HIT", "ENTITY_SKELETON_HORSE_HURT"),
        HORSE_SKELETON_IDLE("HORSE_SKELETON_IDLE", "ENTITY_SKELETON_HORSE_AMBIENT"),
        HORSE_ZOMBIE_DEATH("HORSE_ZOMBIE_DEATH", "ENTITY_ZOMBIE_HORSE_DEATH"),
        HORSE_ZOMBIE_HIT("HORSE_ZOMBIE_HIT", "ENTITY_ZOMBIE_HORSE_HURT"),
        HORSE_ZOMBIE_IDLE("HORSE_ZOMBIE_IDLE", "ENTITY_ZOMBIE_HORSE_AMBIENT"),
        VILLAGER_DEATH("VILLAGER_DEATH", "ENTITY_VILLAGER_DEATH"),
        VILLAGER_HAGGLE("VILLAGER_HAGGLE", "ENTITY_VILLAGER_TRADING"),
        VILLAGER_HIT("VILLAGER_HIT", "ENTITY_VILLAGER_HURT"),
        VILLAGER_IDLE("VILLAGER_IDLE", "ENTITY_VILLAGER_AMBIENT"),
        VILLAGER_NO("VILLAGER_NO", "ENTITY_VILLAGER_NO"),
        VILLAGER_YES("VILLAGER_YES", "ENTITY_VILLAGER_YES");

        private final String before1_9;
        private final String after1_9;
        private Sound resolvedSound = null;

        Sounds(String before1_9, String after1_9) {
            this.before1_9 = before1_9;
            this.after1_9 = after1_9;
        }

        /**
         * Get the Sound. Supports 1.8 sounds and 1.9 and beyond sounds
         */
        public Sound getSound() {
            if (resolvedSound != null) return resolvedSound;
            try {
                return resolvedSound = Sound.valueOf(after1_9);
            } catch (IllegalArgumentException e) {
                return resolvedSound = Sound.valueOf(before1_9);
            }
        }
    }
}
