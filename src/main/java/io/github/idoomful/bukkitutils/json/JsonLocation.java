package io.github.idoomful.bukkitutils.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.Collection;

public class JsonLocation implements JSONable {
    private final double x, y, z;
    private final float yaw, pitch;
    private final String world;

    public JsonLocation(int x, int y, int z, @NotNull String world) {
        this.x = x + 0.500;
        this.y = y;
        this.z = z + 0.500;
        this.world = world;
        this.yaw = 0;
        this.pitch = 0;
    }

    public JsonLocation(int x, int y, int z, @NotNull String world, float yaw, float pitch) {
        this.x = x + 0.500;
        this.y = y;
        this.z = z + 0.500;
        this.world = world;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public JsonLocation(double x, double y, double z, @NotNull String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.yaw = 0;
        this.pitch = 0;
    }

    public JsonLocation(double x, double y, double z, @NotNull String world, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public JsonLocation(@NotNull Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        if(location.getWorld() == null) this.world = "";
        else this.world = location.getWorld().getName();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public float getYaw() {
        return yaw;
    }
    public float getPitch() {
        return pitch;
    }
    public String getWorld() {
        return world;
    }

    @Nullable
    public Location getLocation() {
        final Location loc = new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
        loc.setYaw(getYaw());
        loc.setPitch(getPitch());
        return loc;
    }

    @Override
    @NotNull
    public String toString() {
        return "(" + getWorld() + ") "
                + round(getX(), 2) + ", "
                + round(getY(), 2) + ", "
                + round(getZ(), 2);
    }

    @NotNull
    public String toStringRounded() {
        return "(" + getWorld() + ") "
                + ((int) Math.floor(getX())) + ", "
                + ((int) Math.floor(getY())) + ", "
                + ((int) Math.floor(getZ()));
    }

    // <WORLD> <X> <Y> <Z> [YAW] [PITCH]
    @CheckForNull
    @Nullable
    public static JsonLocation fromStringInterpretation(@NotNull String string) {
        final String[] args = string.split(" ");

        if(args.length < 4) return null;

        final World world = Bukkit.getWorld(args[0]);

        if(world == null) return null;

        final double x = Double.parseDouble(args[1]);
        final double y = Double.parseDouble(args[2]);
        final double z = Double.parseDouble(args[3]);

        if(args.length >= 6) {
            final float yaw = Float.parseFloat(args[4]);
            final float pitch = Float.parseFloat(args[5]);

            if(!args[1].contains(".") || !args[2].contains(".") || !args[3].contains("."))
                return new JsonLocation((int) x, (int) y, (int) z, args[0], yaw, pitch);

            return new JsonLocation(x, y, z, args[0], yaw, pitch);
        }

        if(!args[1].contains(".") || !args[2].contains(".") || !args[3].contains("."))
            return new JsonLocation((int) x, (int) y, (int) z, args[0]);

        return new JsonLocation(x, y, z, args[0]);
    }

    @NotNull
    public String toStringInterpretation(@NotNull JsonLocation loc) {
        if(loc.getYaw() == 0 && loc.getPitch() == 0)
            return loc.getWorld() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ();
        return loc.getWorld() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ()
                + " " + loc.getYaw() + " " + loc.getPitch();
    }

    @NotNull
    public static String toJsonList(Collection<JsonLocation> input) {
        ArrayList<String> output = new ArrayList<>();
        input.forEach(obj -> output.add(obj.toJSON()));
        return new Gson().toJson(output);
    }

    @NotNull
    public static ArrayList<JsonLocation> fromJSONList(String json) {
        Gson gson = new Gson();

        final ArrayList<JsonLocation> output = new ArrayList<>();
        final ArrayList<String> jsonList = gson.fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
        jsonList.forEach(pos -> output.add(gson.fromJson(pos, JsonLocation.class)));

        return output;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
