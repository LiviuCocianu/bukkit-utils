package io.github.idoomful.bukkitutils.object;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration class made for easy managing of the files
 *
 * @param <T> The main class
 * @author iDoomful
 */
public class ConfigManager<T extends JavaPlugin> {
    private final T plugin;
    private final HashMap<String, Map.Entry<File, FileConfiguration>> files = new HashMap<>();

    public ConfigManager(T plugin) {
        this.plugin = plugin;
    }

    /**
     * Virtualizes a configuration file existing in the resources folder and loads it.
     * Can be later accessed using getFile
     * @param name The name of the YAML file. You don't need to specify the .yml at the end
     */
    public ConfigManager<T> addConfigurationFile(String name) {
        // Add required files
        Map.Entry<File, FileConfiguration> filePair = new ConfigPair<>(new File(plugin.getDataFolder(), name + ".yml"), new YamlConfiguration());
        files.put(name, filePair);

        // Create <name>.yml
        final File configFile = files.get(name).getKey();
        final FileConfiguration configYAML = files.get(name).getValue();

        if(!configFile.exists()) {
            boolean ignored = configFile.getParentFile().mkdirs();
            plugin.saveResource(name + ".yml", false);
        }
        try {
            configYAML.load(configFile);
        } catch(IOException | InvalidConfigurationException ie) {
            ie.printStackTrace();
        }

        return this;
    }

    /**
     * Reloads all configuration files
     */
    public void reloadConfigs() {
        files.forEach((name, pair) -> {
            final File configFile = pair.getKey();
            final FileConfiguration configYAML = pair.getValue();

            if (!configFile.exists()) {
                addConfigurationFile(name);
                plugin.getLogger().info(name + "'.yml' was not found, recreating default file...");
            } else {
                try {
                    configYAML.load(configFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        plugin.getLogger().info("Configuration has been reloaded.");
    }

    /**
     * Get a configuration file by name.
     * @param name The name of the file. You don't need to specify the .yml at the end
     * @return The FileConfiguration associated with it
     */
    public FileConfiguration getFile(String name) {
        return files.get(name).getValue();
    }

    public boolean fileExists(String name) {
        return files.containsKey(name);
    }
}