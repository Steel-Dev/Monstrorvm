package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.listeners.inventory.MVtemListInventory;
import com.github.steeldev.monstrorvm.util.config.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.github.steeldev.monstrorvm.util.Util.colorize;

public class ListMVItems implements CommandExecutor {
    static Monstrorvm main = Monstrorvm.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            MVtemListInventory.openListInventory(player, 0);
        } else {
            sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.PLAYERS_ONLY_MSG)));
        }
        return true;
    }
}
