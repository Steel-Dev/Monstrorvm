package com.github.steeldev.monstrorvm;

import com.github.steeldev.monstrorvm.commands.admin.*;
import com.github.steeldev.monstrorvm.listeners.inventory.MVItemListInventory;
import com.github.steeldev.monstrorvm.listeners.server.PlayerJoin;
import com.github.steeldev.monstrorvm.listeners.world.MVWorldListener;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.MVLogger;
import com.github.steeldev.monstrorvm.util.Message;
import com.github.steeldev.monstrorvm.util.UpdateChecker;
import com.github.steeldev.monstrorvm.util.Util;
import com.github.steeldev.monstrorvm.util.config.Config;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class Monstrorvm extends JavaPlugin {
    private static Monstrorvm instance;
    public Config config = null;
    public UpdateChecker versionManager;

    public Logger logger;

    public boolean recipesRegistered;

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
        enableMetrics();

        Message.PLUGIN_ENABLED.log(getDescription().getVersion(), (float) (System.currentTimeMillis() - start) / 1000);

        versionManager = new UpdateChecker(this, 85464);
        versionManager.checkForNewVersion();
    }

    @Override
    public void onDisable() {
        Message.PLUGIN_DISABLED.log();
        instance = null;
    }

    public void loadNBTAPI() {
        Message.LOADING_NBT_API.log();
        NBTItem loadingItem = new NBTItem(new ItemStack(Material.STONE));
        loadingItem.addCompound("Glob");
        loadingItem.setString("Glob", "yes");
        Message.NBT_API_LOADED.log();
    }

    public void loadCustomConfigs() {
        this.config = new Config(this);
    }

    public void enableMetrics() {
        Metrics metrics = new Metrics(this, 9288);

        if (metrics.isEnabled())
            Message.STARTING_METRICS.log();
    }

    public void registerCommands() {
        Util.registerCommand("listmonstrorvmitems", new ListMVItems());
        Util.registerCommand("givemonstrorvmitem", new GiveMVItem());
        Util.registerCommand("spawnmonstrorvmmob", new SpawnMVMob());
        Util.registerCommand("killallmonstrorvmmobs", new KillAllMVMobs());
        Util.registerCommand("monstrorvmreload", new MVReload());
        Util.registerCommand("monstrorvmdocumentation", new MVDocs());
    }

    public void registerListeners() {
        Util.registerEvent(new MVWorldListener());
        Util.registerEvent(new PlayerJoin());
        Util.registerEvent(new MVItemListInventory());
    }
}
