package io.github.idoomful.bukkitutils.statics;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

public class Utils {
    public static class Array {
        public static <A> A[] of(A ... el) {
            return el;
        }
    }

    /**
     * Get the configuration file of another plugin
     *
     * @param sourceMain The instance of the plugin this method is ran from
     * @param plugin The name of the target plugin
     * @param configName The name of the configuration file. You don't need to specify the .yml at the end
     * @return The FileConfiguration object that represents the configuration file
     */
    public static FileConfiguration getConfigOf(JavaPlugin sourceMain, String plugin, String configName) {
        File file = new File(sourceMain.getDataFolder().getPath()
                .replace(sourceMain.getDescription().getName(), plugin + "/" + configName + ".yml"));
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Returns a random number between the specified range
     *
     * @param range A string of form "a-b", where a is smaller than b
     */
    public static long randomRange(String range) {
        try {
            return Long.parseLong(range);
        } catch(NumberFormatException e) {
            int first = Integer.parseInt(range.split("-")[0]);
            int second = Integer.parseInt(range.split("-")[1]);

            return new Random().nextInt((second - first) + 1) + first;
        }
    }

    /**
     * Evaluates a simple, binary operator expression with operands of type Double or String
     * Supported operators: <, >, <=, >=, ==, !=, ~=
     *
     * The '~=' operator can only be used for strings and it does an equalsIgnoreCase operation
     *
     * @param condition Condition to evaluate
     * @return The boolean evaluation
     */
    public static boolean eval(String condition) {
        final String[] binaryComps = Array.of(" < ", " > ", " <= ", " >= ", " == ", " != ", " ~= ");
        final List<String> segments = TextUtils.segmentByStrings(condition, binaryComps);

        if(segments.size() != 3) return false;

        final String operand1 = segments.get(0).trim();
        final String operator = segments.get(1).trim();
        final String operand2 = segments.get(2).trim();

        double oper1 = 0;
        double oper2 = 0;

        final String strRegex = "(^'.*'$)|(^\".*\"$)";

        try {
            if(!operator.equals("~=")) {
                if(operand1.matches(strRegex)) oper1 = operand1.length();
                else oper1 = Double.parseDouble(operand1);

                if(operand2.matches(strRegex)) oper2 = operand1.length();
                else oper2 = Double.parseDouble(operand2);
            }
        } catch(NumberFormatException nfe) {
            return false;
        }

        boolean equals = operand1.replaceAll("['\"]", "")
                .equals(operand2.replaceAll("['\"]", ""));
        boolean operandsAreString = operand1.matches(strRegex)
                && operand2.matches(strRegex);

        switch(operator) {
            case "<": return oper1 < oper2;
            case ">": return oper1 > oper2;
            case "<=": return oper1 <= oper2;
            case ">=": return oper1 >= oper2;

            case "==":
                if(operandsAreString) return equals;
                else return oper1 == oper2;
            case "!=":
                if(operandsAreString) return !equals;
                else return oper1 != oper2;
            case "~=":
                if(operandsAreString)
                    return operand1.replaceAll("['\"]", "")
                            .equalsIgnoreCase(operand2.replaceAll("['\"]", ""));
                break;
        }

        return false;
    }

    public static String getCurrentTimestamp(String format, String timezone) {
        final Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

        return dateFormat.format(date);
    }

    public static String formatEpoch(long epoch, String format, String timezone) {
        final Date date = new Date(epoch);

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

        return dateFormat.format(date);
    }

    public static String getCurrentTimestamp(String format, String timezone, String lang) {
        final Date date = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat(format,
                new Locale(lang.split("-")[0], lang.split("-")[1]));
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

        return dateFormat.format(date);
    }

    public static String formatEpoch(long epoch, String format, String timezone, String lang) {
        final Date date = new Date(epoch);

        SimpleDateFormat dateFormat = new SimpleDateFormat(format,
                new Locale(lang.split("-")[0], lang.split("-")[1]));
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));

        return dateFormat.format(date);
    }
}
