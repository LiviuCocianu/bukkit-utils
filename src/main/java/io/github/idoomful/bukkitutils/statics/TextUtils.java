package io.github.idoomful.bukkitutils.statics;

import com.cryptomorin.xseries.ReflectionUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {
    /**
     * Applies color codes to the given string.
     * Supports hex colors as well, for versions greater or equal to 1.16
     *
     * @return The string with the colors applied
     */
    public static String color(String input) {
        String output = ChatColor.translateAlternateColorCodes('&', input);

        if(!VersionUtils.usesVersionBetween("1.4.x", "1.15.x")) {
            Pattern pat = Pattern.compile("\\[?#[a-fA-F0-9]{6}]?");
            Matcher mat = pat.matcher(output);

            try {
                Class<?> chatcolor = Class.forName("net.md_5.bungee.api.ChatColor");

                while(mat.find()) {
                    String color = output.substring(mat.start(), mat.end()).replaceAll("[\\[\\]]", "");
                    String toReplace = output.substring(mat.start(), mat.end()).matches("\\[#[a-fA-F0-9]{6}]") ? "[" + color + "]" : color;

                    Object hexColor = chatcolor.getMethod("of", String.class).invoke(chatcolor, color);
                    output = output.replace(toReplace, hexColor + "");
                    mat = pat.matcher(output);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return output;
    }

    /**
     * Strips any hex color codes from the given string.
     *
     * @param input The string to strip
     * @param stripSimpleColors Strip regular color codes as well
     * @return The string without any color codes
     */
    public static String stripHex(String input, boolean stripSimpleColors) {
        String output = stripSimpleColors ? ChatColor.stripColor(input) : input;

        if(!VersionUtils.usesVersionBetween("1.4.x", "1.15.x")) {
            Pattern pat = Pattern.compile("\\[?#[a-fA-F0-9]{6}]?");
            Matcher mat = pat.matcher(output);

            while (mat.find()) {
                String color = output.substring(mat.start(), mat.end()).replaceAll("[\\[\\]]", "");
                String toReplace = output.substring(mat.start(), mat.end()).matches("\\[#[a-fA-F0-9]{6}]") ? "[" + color + "]" : color;

                output = output.replace(toReplace, "");
                mat = pat.matcher(output);
            }
        }

        return output;
    }

    /**
     * Applies color codes to the given list of strings
     * Supports hex colors as well, for versions greater or equal to 1.16
     * @return The list of strings with the colors applied
     */
    public static List<String> color(List<String> input) {
        return input.stream().map(TextUtils::color).collect(Collectors.toList());
    }

    /**
     * If the given string has any PlaceholderAPI placeholders, they will be applied
     * and the string will be colored if any color codes exist.
     * @param player The player which the placeholders will be applied relative to
     * @param input The string
     */
    public static String placeholder(Player player, String input) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null)
            return TextUtils.color(PlaceholderAPI.setPlaceholders(player, input));
        else return color(input);
    }

    /**
     * If the given list of strings has any PlaceholderAPI placeholders, they will be applied
     * and each string will be colored if any color codes exist.
     * @param player The player which the placeholders will be applied relative to
     * @param input The string
     */
    public static List<String> placeholder(Player player, List<String> input) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null)
            return TextUtils.color(PlaceholderAPI.setPlaceholders(player, input));
        else return color(input);
    }

    /**
     * Formats a given integer with proper punctuation
     * Example: 250000 -> 250.000
     * @param num The number to be transformed
     */
    public static String formatNumber(int num) {
        NumberFormat output = NumberFormat.getInstance();
        output.setGroupingUsed(true);
        return output.format(num).replace(",", ".");
    }

    /**
     * Sends action bar text to the specified player using a reflection packet
     * @param player The target
     * @param message The message to be sent
     */
    public static void sendActionText(Player player, String message) {
        try {
            Class<?> playOutChat = ReflectionUtils.getNMSClass("PacketPlayOutChat");
            Class<?> ichat = ReflectionUtils.getNMSClass("IChatBaseComponent");
            Class<?> chatComp = ReflectionUtils.getNMSClass("ChatMessage");

            Object baseComp = chatComp.getConstructor(String.class, Object[].class).newInstance(message, new Object[] {});
            Object packet = playOutChat.getConstructor(ichat, byte.class).newInstance(baseComp, (byte) 2);

            Class<?> craftPlayer = ReflectionUtils.getCraftClass("entity.CraftPlayer");
            Class<?> packetClass = ReflectionUtils.getNMSClass("Packet");
            Object handle = craftPlayer.cast(player).getClass().getMethod("getHandle").invoke(craftPlayer.cast(player));
            Object conObj = handle.getClass().getField("playerConnection").get(handle);
            conObj.getClass().getMethod("sendPacket", packetClass).invoke(conObj, packet);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Formats the given integer into its roman equivalent
     * @param input The number to be transformed
     */
    public static String integerToRoman(int input) {
        if (input < 1 || input > 3999) return "Invalid";
        StringBuilder s = new StringBuilder();

        while (input >= 1000) {
            s.append("M");
            input -= 1000;
        }
        while (input >= 900) {
            s.append("CM");
            input -= 900;
        }
        while (input >= 500) {
            s.append("D");
            input -= 500;
        }
        while (input >= 400) {
            s.append("CD");
            input -= 400;
        }
        while (input >= 100) {
            s.append("C");
            input -= 100;
        }
        while (input >= 90) {
            s.append("XC");
            input -= 90;
        }
        while (input >= 50) {
            s.append("L");
            input -= 50;
        }
        while (input >= 40) {
            s.append("XL");
            input -= 40;
        }
        while (input >= 10) {
            s.append("X");
            input -= 10;
        }
        while (input >= 9) {
            s.append("IX");
            input -= 9;
        }
        while (input >= 5) {
            s.append("V");
            input -= 5;
        }
        while (input >= 4) {
            s.append("IV");
            input -= 4;
        }
        while (input >= 1) {
            s.append("I");
            input -= 1;
        }
        return s.toString();
    }

    /**
     * Processes an amount of seconds into a timestamp
     * @param seconds The amount of seconds
     * @param essFormat Whether it should be formatted so it would work if put in an Essentials lore/name
     * @return The formatted timestamp in a "#d #h #m #s" format
     */
    public static String getTimestamp(double seconds, boolean essFormat) {
        String output = "";
        double secs, minutes = 0, hours = 0, days = 0;

        if(seconds > 60) {
            minutes = Math.floor(seconds / 60);
            secs = seconds % 60;
        } else secs = seconds;

        if(minutes % 60 == 0.0 && Math.floor(minutes / 60) == 24.0) {
            minutes = 59;
        } else if(minutes > 60) {
            hours = Math.floor(minutes / 60);
            minutes = minutes % 60;
        }

        if(hours > 24) {
            days = Math.floor(hours / 24);
            hours = Math.floor(hours / 24) == 0 ? 23 : hours % 24;
        } else if(hours == 24.0 && seconds <= 86400) {
            hours = 23;
        }

        if(days > 0) {
            output = output + Math.round(days) + "d";
            if(essFormat) output = output + "_";
            else output = output + " ";
        }

        if(hours > 0) {
            output = output + Math.round(hours) + "h";
            if(essFormat) output = output + "_";
            else output = output + " ";
        }

        if(minutes > 0) {
            output = output + Math.round(minutes) + "m";
            if(essFormat) output = output + "_";
            else output = output + " ";
        }

        if(secs > 0) {
            output = output + Math.round(secs) + "s";
        }

        return output;
    }
}
