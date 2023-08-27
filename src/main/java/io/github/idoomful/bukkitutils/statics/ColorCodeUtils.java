package io.github.idoomful.bukkitutils.statics;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorCodeUtils {
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
        final List<String> colorCodes = findAllColorCodes(input);
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
}
