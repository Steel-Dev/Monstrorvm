package com.github.steeldev.monstrorvm.util.config;

import com.github.steeldev.monstrorvm.Monstrorvm;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {
    private static FileConfiguration config;
    private static File configFile;
    private final Monstrorvm plugin;
    // Config stuff
    public String PREFIX;
    public boolean EXAMPLES_ENABLED;
    public boolean DEBUG;
    public boolean NEW_UPDATE_MESSAGE_ON_JOIN;
    public boolean NEW_UPDATE_MESSAGE_ON_RELOAD;
    public int CUSTOM_MOB_CAP;

    public Config(Monstrorvm plugin) {
        this.plugin = plugin;
        loadConfigFile();
    }

    public static void setString(String path, String value) throws IOException {
        config.set(path, value);

        config.save(configFile);
    }

    public static void setBool(String path, boolean value) throws IOException {
        config.set(path, value);

        config.save(configFile);
    }

    private void loadConfigFile() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "Config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("Config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        matchConfig();
        loadConfigs();
    }

    // Used to update config
    @SuppressWarnings("ConstantConditions")
    private void matchConfig() {
        try {
            boolean hasUpdated = false;
            InputStream stream = plugin.getResource(configFile.getName());
            assert stream != null;
            InputStreamReader is = new InputStreamReader(stream);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(is);
            for (String key : defConfig.getConfigurationSection("").getKeys(true)) {
                if (!config.contains(key)) {
                    config.set(key, defConfig.get(key));
                    hasUpdated = true;
                }
            }
            for (String key : config.getConfigurationSection("").getKeys(true)) {
                if (!defConfig.contains(key)) {
                    config.set(key, null);
                    hasUpdated = true;
                }
            }
            if (hasUpdated)
                config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfigs() {
        PREFIX = config.getString("Prefix");
        EXAMPLES_ENABLED = config.getBoolean("ExamplesEnabled");
        DEBUG = config.getBoolean("Debug");
        CUSTOM_MOB_CAP = config.getInt("CustomMobs.MobCap");
        NEW_UPDATE_MESSAGE_ON_JOIN = config.getBoolean("UpdateCheck.MessageOnJoin");
        NEW_UPDATE_MESSAGE_ON_RELOAD = config.getBoolean("UpdateCheck.MessageOnReload");
    }
}
