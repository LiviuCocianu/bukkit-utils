package io.github.idoomful.bukkitutils.statics;

import com.cryptomorin.xseries.ReflectionUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextUtils {
    public enum ColorType {
        NONE, STANDARD, HEX
    }

    /**
     * Applies color codes to the given string.
     * Supports hex colors as well, for versions greater or equal to 1.16
     *
     * @return The string with the colors applied
     */
    public static String color(String input) {
        String output = input;

        if(!VersionUtils.usesVersionBetween("1.4.x", "1.15.x")) {
            Pattern pat = Pattern.compile("\\[?#[a-fA-F0-9]{6}]?");
            Matcher mat = pat.matcher(input);

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

            output = ChatColor.translateAlternateColorCodes('&', output);
        } else {
            output = ChatColor.translateAlternateColorCodes('&', input);
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

    public static String colorlessPlaceholder(Player player, String input) {
        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && player != null)
            return PlaceholderAPI.setPlaceholders(player, input);
        else return input;
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

    /**
     * Splits a string by the provided collection of strings
     * Example of input:
     * > "fizz buzz fizz fizz buzz"
     * Example of output when split by the string "buzz":
     * > ["fizz", "buzz", "fizz fizz", "buzz"]
     *
     * @param text String to be segmented
     * @param strings Strings used in the splitting condition
     * @return List of string segments
     */
    public static List<String> segmentByStrings(String text, Collection<String> strings) {
        final List<String> segments = new ArrayList<>();
        String temp = text;
        StringBuilder segment = new StringBuilder();

        while(!temp.isEmpty()) {
            char c = temp.charAt(0);

            boolean found = false;

            for(String ph : strings) {
                if(temp.toLowerCase().startsWith(ph.toLowerCase())) {
                    found = true;
                    if(!segment.toString().isEmpty())
                        segments.add(segment.toString());
                    segments.add(temp.substring(0, ph.length()));
                    segment = new StringBuilder();
                    temp = temp.replaceFirst("(?i)" + TextUtils.escapeMetaCharacters(ph), "");
                    break;
                }
            }

            if(!found) {
                segment.append(c);
                temp = temp.substring(1);
            }
        }

        if(!segment.toString().isEmpty())
            segments.add(segment.toString());

        return segments;
    }

    public static List<String> segmentByString(String text, String separator) {
        return segmentByStrings(text, Collections.singletonList(separator));
    }

    /**
     * Alters standard and hex color codes in a string so they can't be converted into colors
     * through methods like ChatColor#translateAlternateColorCodes
     *
     * @param text String with color codes in it
     * @return Jammed text
     */
    public static String jamColorCodes(final String text) {
        return jamHexColorCodes(jamStandardColorCodes(text));
    }

    /**
     * Undo string alterations to both standard and hex color codes
     *
     * @param text String with altered color codes in it
     * @return Unjammed text
     */
    public static String unjamColorCodes(final String text) {
        return unjamHexColorCodes(unjamStandardColorCodes(text));
    }

    /**
     * Alters standard color codes in a string so they can't be converted into colors
     * through methods like ChatColor#translateAlternateColorCodes
     *
     * @param text String with color codes in it
     * @return Jammed text
     */
    public static String jamStandardColorCodes(final String text) {
        String output = text;

        final Pattern stdPat = Pattern.compile("(&)([0-9a-fk-or])");
        Matcher stdMat = stdPat.matcher(output);

        while (stdMat.find()) {
            final String half1 = stdMat.group(1);
            final String half2 = stdMat.group(2);

            output = output.replaceFirst(Pattern.quote(stdMat.group()), half1 + ";" + half2);
            stdMat = stdPat.matcher(output);
        }

        return output;
    }

    /**
     * Alters hex color codes in a string so they can't be converted into colors
     * through methods like ChatColor#translateAlternateColorCodes
     *
     * @param text String with color codes in it
     * @return Jammed text
     */
    public static String jamHexColorCodes(final String text) {
        String output = text;

        final Pattern hexPat = Pattern.compile("(\\[#)([a-fA-F0-9]{6}])");
        Matcher hexMat = hexPat.matcher(output);

        while (hexMat.find()) {
            final String half1 = hexMat.group(1);
            final String half2 = hexMat.group(2);

            output = output.replaceFirst(Pattern.quote(hexMat.group()), half1 + ";" + half2);
            hexMat = hexPat.matcher(output);
        }

        return output;
    }

    /**
     * Undo string alterations to standard color codes
     *
     * @param text String with altered color codes in it
     * @return Unjammed text
     */
    public static String unjamStandardColorCodes(final String text) {
        String output = text;

        final Pattern stdPat = Pattern.compile("(&);([0-9a-fk-or])");
        Matcher stdMat = stdPat.matcher(output);

        while (stdMat.find()) {
            final String match = stdMat.group();

            output = output.replaceFirst(Pattern.quote(match), match.replace(";", ""));
            stdMat = stdPat.matcher(output);
        }

        return output;
    }

    /**
     * Undo string alterations to hex color codes
     *
     * @param text String with altered color codes in it
     * @return Unjammed text
     */
    public static String unjamHexColorCodes(final String text) {
        String output = text;

        final Pattern hexPat = Pattern.compile("(\\[#);([a-fA-F0-9]{6}])");
        Matcher hexMat = hexPat.matcher(output);

        while (hexMat.find()) {
            final String match = hexMat.group();

            output = output.replaceFirst(Pattern.quote(match), match.replace(";", ""));
            hexMat = hexPat.matcher(output);
        }

        return output;
    }

    public static List<String> findAllColorCodes(String input) {
        final List<String> codes = new ArrayList<>();

        final Pattern pat = Pattern.compile("(\\[?#[a-fA-F0-9]{6}]?)|([&§][0-9a-fk-or])");
        final Matcher mat = pat.matcher(input);

        while(mat.find())
            codes.add(mat.group());

        return codes;
    }

    public static String getLastColorCodeCluster(String input) {
        final List<String> colorCodes = TextUtils.findAllColorCodes(input);
        Collections.reverse(colorCodes);
        final Deque<String> res = new LinkedList<>();

        for(String code : colorCodes) {
            if(code.matches("[&§][k-or]")) {
                res.addFirst(code);
            } else {
                res.addFirst(code);
                break;
            }
        }

        final StringBuilder out = new StringBuilder();
        for(String code : res) out.append(code);

        return out.toString();
    }

    public static String escapeMetaCharacters(String inputString){
        final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};

        for (int i = 0 ; i < metaCharacters.length ; i++){
            if(inputString.contains(metaCharacters[i])){
                inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
            }
        }
        return inputString;
    }
}
