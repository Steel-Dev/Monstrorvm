package com.github.steeldev.monstrorvm.util.config;

import com.github.steeldev.monstrorvm.Monstrorvm;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Lang {
    public static String PREFIX;
    public static String PLAYERS_ONLY_MSG;
    public static String CUSTOM_MOB_INVALID_MSG;
    public static String CUSTOM_MOB_SPAWNED_MSG;
    public static String CUSTOM_MOB_SPAWN_FAILED_MSG;
    public static String CUSTOM_MOBS_KILLED_MSG;
    public static String CUSTOM_MOBS_KILL_FAILED_MSG;
    public static String CUSTOM_MOB_REGISTERED_MSG;
    public static String CUSTOM_ITEM_GIVEN_MSG;
    public static String CUSTOM_ITEM_PLAYER_INVENTORY_FULL_MSG;
    public static String CUSTOM_ITEM_INVALID_MSG;
    public static String INVALID_PLAYER_MSG;
    static Monstrorvm main = Monstrorvm.getInstance();
    private final Monstrorvm plugin;
    private FileConfiguration lang;
    private File langFile;

    public Lang(Monstrorvm plugin) {
        this.plugin = plugin;
        loadLangFile();
    }

    private void loadLangFile() {
        main.getLogger().info("&7Loading lang file for " + Config.SELECTED_LANGUAGE);
        if (langFile == null) {
            langFile = new File(plugin.getDataFolder(), "Lang.yml");
        }
        if (!langFile.exists()) {
            plugin.saveResource("Lang.yml", false);
        }
        lang = YamlConfiguration.loadConfiguration(langFile);
        matchLangFile();
        loadLang();
    }

    // Used to update lang
    @SuppressWarnings("ConstantConditions")
    private void matchLangFile() {
        try {
            boolean hasUpdated = false;
            InputStream stream = plugin.getResource(langFile.getName());
            assert stream != null;
            InputStreamReader is = new InputStreamReader(stream);
            YamlConfiguration defLand = YamlConfiguration.loadConfiguration(is);
            for (String key : defLand.getConfigurationSection("").getKeys(true)) {
                if (!lang.contains(key)) {
                    lang.set(key, defLand.get(key));
                    hasUpdated = true;
                }
            }
            for (String key : lang.getConfigurationSection("").getKeys(true)) {
                if (!defLand.contains(key)) {
                    lang.set(key, null);
                    hasUpdated = true;
                }
            }
            if (hasUpdated)
                lang.save(langFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLang() {
        PREFIX = lang.getString(Config.SELECTED_LANGUAGE + ".Prefix");

        PLAYERS_ONLY_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".PlayersOnlyMsg");

        CUSTOM_MOB_INVALID_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".InvalidMVMobMsg");
        CUSTOM_MOB_SPAWNED_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".MVMobSpawnedMsg");
        CUSTOM_MOB_SPAWN_FAILED_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".MVMobSpawnFailed");
        CUSTOM_MOBS_KILLED_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".MVMobsKilled");
        CUSTOM_MOBS_KILL_FAILED_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".NoMVMobsSpawned");
        CUSTOM_MOB_REGISTERED_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".MVMobRegistered");

        CUSTOM_ITEM_GIVEN_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".MVItemGivenMsg");
        CUSTOM_ITEM_PLAYER_INVENTORY_FULL_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".MVInventoryFullMsg");
        CUSTOM_ITEM_INVALID_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".MVItemInvalidMsg");

        INVALID_PLAYER_MSG = lang.getString(Config.SELECTED_LANGUAGE + ".PlayerNotOnline");
    }
}
