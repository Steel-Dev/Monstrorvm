package com.github.steeldev.monstrorvm.util.config;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.util.WhyNoWorkException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.steeldev.monstrorvm.util.Util.formalizedString;

public class Config {
    // Config stuff
    public static boolean EXAMPLES_ENABLED;
    public static boolean DEBUG;
    public static String SELECTED_LANGUAGE;
    public static boolean NEW_UPDATE_MESSAGE_ON_JOIN;
    public static boolean NEW_UPDATE_MESSAGE_ON_RELOAD;

    public static int CUSTOM_MOB_CAP;
    static List<String> supportedLanguages = new ArrayList<>(Arrays.asList("English"));
    private static FileConfiguration config;
    private static File configFile;
    private final Monstrorvm plugin;

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
        EXAMPLES_ENABLED = config.getBoolean("ExamplesEnabled");
        DEBUG = config.getBoolean("Debug");
        SELECTED_LANGUAGE = formalizedString(config.getString("Language"));
        CUSTOM_MOB_CAP = config.getInt("CustomMobs.MobCap");
        if (!supportedLanguages.contains(SELECTED_LANGUAGE)) {
            throw new WhyNoWorkException("The specified language " + SELECTED_LANGUAGE + " is invalid, or not yet supported!");
        }
        NEW_UPDATE_MESSAGE_ON_JOIN = config.getBoolean("UpdateCheck.MessageOnJoin");
        NEW_UPDATE_MESSAGE_ON_RELOAD = config.getBoolean("UpdateCheck.MessageOnReload");
    }
}
