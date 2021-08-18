package io.github.idoomful.bukkitutils.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;

public class JsonLocation {
    private final double x, y, z;
    private final float yaw, pitch;
    private final String world;

    public JsonLocation(double x, double y, double z, String world) {
        this.x = x + 0.500;
        this.y = y;
        this.z = z + 0.500;
        this.world = world;
        this.yaw = 0;
        this.pitch = 0;
    }

    public JsonLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = location.getWorld().getName();
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

    public Location getLocation() {
        Location loc = new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
        loc.setYaw(getYaw());
        loc.setPitch(getPitch());
        return loc;
    }

    @Override
    public String toString() {
        return "(" + getWorld() + ") "
                + round(getX(), 2) + ", "
                + round(getY(), 2) + ", "
                + round(getZ(), 2);
    }

    public String toStringRounded() {
        return "(" + getWorld() + ") "
                + ((int) Math.floor(getX())) + ", "
                + ((int) Math.floor(getY())) + ", "
                + ((int) Math.floor(getZ()));
    }

    public String toJSON() {
        return new Gson().toJson(this);
    }

    public static String toJsonList(Collection<JsonLocation> input) {
        ArrayList<String> output = new ArrayList<>();
        input.forEach(obj -> output.add(obj.toJSON()));
        return new Gson().toJson(output);
    }

    public static JsonLocation fromJSON(String json) {
        return new Gson().fromJson(json, JsonLocation.class);
    }

    public static ArrayList<JsonLocation> fromJSONList(String json) {
        Gson gson = new Gson();
        ArrayList<JsonLocation> output = new ArrayList<>();
        ArrayList<String> jsonList = gson.fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
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
