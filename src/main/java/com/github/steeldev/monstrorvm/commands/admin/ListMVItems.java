package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.listeners.inventory.MVItemListInventory;
import com.github.steeldev.monstrorvm.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ListMVItems implements CommandExecutor {
    static Monstrorvm main = Monstrorvm.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            MVItemListInventory.openListInventory(player, 0);
        } else {
            Message.ONLY_PLAYERS_CAN_EXECUTE.send(sender, true);
        }
        return true;
    }
}
