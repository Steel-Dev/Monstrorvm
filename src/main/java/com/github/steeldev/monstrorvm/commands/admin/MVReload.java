package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.UpdateCheck;
import com.github.steeldev.monstrorvm.util.config.Config;
import com.github.steeldev.monstrorvm.util.config.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.github.steeldev.monstrorvm.util.Util.colorize;

public class MVReload implements CommandExecutor {
    final Monstrorvm main = Monstrorvm.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        main.loadCustomConfigs();
        ItemManager.registerCustomItems();
        MobManager.registerCustomMobs();
        sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, "&aSuccessfully reloaded all configurations!")));
        if (sender instanceof Player) {
            if (Config.NEW_UPDATE_MESSAGE_ON_RELOAD)
                UpdateCheck.sendNewUpdateMessageToPlayer((Player) sender);
        }
        return true;
    }
}
