package io.github.idoomful.bukkitutils.statics;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static class Array {
        public static <A> A[] of(A ... el) {
            return el;
        }
    }

    /**
     * Executes the given list of commands as the console for the given player.
     * Also comes with a map of placeholders and their values to be applied to all given commands
     * and PlaceholderAPI support. If one of the strings in the command list is proceeded by [message],
     * it will send whatever is after it as a message, with color codes and everything.
     *
     * @param main The main instance of the plugin
     * @param player This will be used to set the PlaceholderAPI placeholders, if any, or send the
     *               messages proceeded by [message] to the player
     * @param commands The commands/messages that will be executed
     * @param placeholders A map of any placeholder you want to be replaced inside your commands and their values
     */
    public static void executeCommands(JavaPlugin main, Player player, List<String> commands, HashMap<String, String> placeholders) {
        for(String cmd : commands) {
            String processedCmd = cmd;

            for(String placeholder : placeholders.keySet())
                processedCmd = processedCmd.replace(placeholder, placeholders.get(placeholder));

            if(processedCmd.startsWith("[message]")) {
                String message = processedCmd.replace("[message]", "");
                message = message.trim();

                if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
                    player.sendMessage(PlaceholderAPI.setPlaceholders(player, message));
                else player.sendMessage(TextUtils.color(message));
            } else if(processedCmd.matches("^\\[delay=(\\d+)].+")) {
                Matcher matcher = Pattern.compile("^\\[delay=(\\d+)]").matcher(processedCmd);
                while(matcher.find()) {
                    int delay = Integer.parseInt(matcher.group(1));

                    String finalProcessedCmd = processedCmd;
                    Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                        String command = finalProcessedCmd.replaceAll("^\\[delay=(\\d+)]", "");
                        command = command.trim();

                        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, command));
                        else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    }, delay);
                }
            } else {
                if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceholderAPI.setPlaceholders(player, processedCmd));
                else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCmd);
            }
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
}
