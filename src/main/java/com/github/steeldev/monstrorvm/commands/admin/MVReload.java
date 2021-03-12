package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static com.github.steeldev.monstrorvm.util.Util.colorize;

public class MVReload implements CommandExecutor {
    final Monstrorvm main = Monstrorvm.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        try {
            boolean successfulReload = true;
            Message.PLUGIN_RELOADING.send(sender, true);
            main.loadCustomConfigs();
            ItemManager.registerCustomItems();
            MobManager.registerCustomMobs();

            // Wil bit of a mess :c might improve later if i can think of a better method.
            if (ItemManager.warningList.size() > 0) {
                for (String wMsg : ItemManager.warningList) {
                    sender.sendMessage(colorize(wMsg));
                }
                successfulReload = false;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Message.RELOADED_ITEM_WARNINGS.send(sender, false, ItemManager.warningList.size());
                    }
                }.runTaskLater(main, 10l);
            }
            if (ItemManager.errorList.size() > 0) {
                for (String eMsg : ItemManager.errorList) {
                    sender.sendMessage(colorize(eMsg));
                }
                successfulReload = false;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Message.RELOADED_ITEM_ERRORS.send(sender, false, ItemManager.errorList.size());
                    }
                }.runTaskLater(main, 10l);
            }
            if (MobManager.warningList.size() > 0) {
                for (String wMsg : MobManager.warningList) {
                    sender.sendMessage(colorize(wMsg));
                }
                successfulReload = false;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Message.RELOADED_MOB_WARNINGS.send(sender, false, MobManager.warningList.size());
                    }
                }.runTaskLater(main, 10l);
            }
            if (MobManager.errorList.size() > 0) {
                for (String eMsg : MobManager.errorList) {
                    sender.sendMessage(colorize(eMsg));
                }
                successfulReload = false;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Message.RELOADED_MOB_ERRORS.send(sender, false, MobManager.errorList.size());
                    }
                }.runTaskLater(main, 10l);
            }

            if (successfulReload)
                Message.PLUGIN_RELOADED.send(sender, true);
            if (sender instanceof Player) {
                if (main.config.NEW_UPDATE_MESSAGE_ON_RELOAD)
                    main.versionManager.sendNewUpdateMessageToPlayer((Player) sender);
            }
        } catch (Exception e) {
            Message.PLUGIN_RELOAD_FAILED.send(sender, true);
            e.printStackTrace();
        }
        return true;
    }
}
