package com.github.steeldev.monstrorvm;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.github.steeldev.monstrorvm.commands.admin.*;
import com.github.steeldev.monstrorvm.listeners.inventory.MVtemListInventory;
import com.github.steeldev.monstrorvm.listeners.world.MVWorldListener;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.MVLogger;
import com.github.steeldev.monstrorvm.util.UpdateCheck;
import com.github.steeldev.monstrorvm.util.config.Config;
import com.github.steeldev.monstrorvm.util.config.Lang;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class Monstrorvm extends JavaPlugin {
    private static Monstrorvm instance;
    public Config config = null;
    public Lang lang = null;
    public boolean outdated;
    public String newVersion;

    public Logger logger;

    SkriptAddon monstrorvmAddon;
    Plugin skript;

    public static Monstrorvm getInstance() {
        return instance;
    }

    @Override
    public @NotNull Logger getLogger() {
        if (logger == null) logger = MVLogger.getLogger();
        return this.logger;
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;

        MinecraftVersion.replaceLogger(getLogger());

        loadNBTAPI();

        loadCustomConfigs();

        MobManager.init();
        registerListeners();
        ItemManager.registerCustomItems();
        MobManager.registerCustomMobs();
        registerCommands();
        registerInventoryListeners();

        enableMetrics();

        getLogger().info(String.format("&aSuccessfully enabled &2%s &ain &e%s Seconds&a.", getDescription().getVersion(), (float) (System.currentTimeMillis() - start) / 1000));

        checkForNewVersion();
    }

    @Override
    public void onDisable() {
        getLogger().info("&cSuccessfully disabled!");
        instance = null;
    }

    public void checkForNewVersion() {
        getLogger().info("&e&oChecking for a new version...");
        new UpdateCheck(this, 85464).getVersion(version -> {
            int latestVersion = Integer.parseInt(version.replaceAll("\\.", ""));
            int currentVersion = Integer.parseInt(this.getDescription().getVersion().replaceAll("\\.", ""));

            if (currentVersion == latestVersion) {
                outdated = false;
                getLogger().info(String.format("&2&oYou are on the latest version! &7&o(%s)", version));
            } else if (currentVersion > latestVersion) {
                outdated = false;
                getLogger().info(String.format("&e&oYou are on an in-dev preview version! &7&o(%s)", this.getDescription().getVersion()));
            } else {
                outdated = true;
                newVersion = version;
                getLogger().info(String.format("&a&oA new version is available! &7&o(Current: %s, Latest: %s)", this.getDescription().getVersion(), version));
                getLogger().info("&e&ohttps://www.spigotmc.org/resources/monstrorvm.85464/");
            }
        });
    }

    public void loadNBTAPI() {
        getLogger().info("&aLoading NBT-API...");
        NBTItem loadingItem = new NBTItem(new ItemStack(Material.STONE));
        loadingItem.addCompound("Glob");
        loadingItem.setString("Glob", "yes");
        getLogger().info("&aSuccessfully loaded NBT-API!");
    }

    public void loadCustomConfigs() {
        this.config = new Config(this);
        this.lang = new Lang(this);
    }

    public void enableMetrics() {
        Metrics metrics = new Metrics(this, 9288);

        if (metrics.isEnabled())
            getLogger().info("&7Starting Metrics. Opt-out using the global bStats config.");
    }

    public void registerCommands() {
        this.getCommand("listmonstrorvmitems").setExecutor(new ListMVItems());
        this.getCommand("givemonstrorvmitem").setExecutor(new GiveMVItem());
        this.getCommand("spawnmonstrorvmmob").setExecutor(new SpawnMVMob());
        this.getCommand("killallmonstrorvmmobs").setExecutor(new KillAllMVMobs());
        this.getCommand("monstrorvmreload").setExecutor(new MVReload());
    }

    public void registerInventoryListeners() {
        getServer().getPluginManager().registerEvents(new MVtemListInventory(), this);
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new MVWorldListener(), this);
    }
}
