package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.util.Message;
import com.github.steeldev.monstrorvm.util.items.MVItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiveMVItem implements CommandExecutor, TabCompleter {
    Monstrorvm main = Monstrorvm.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player specifiedPlayer = (Player) sender;
            if (args.length < 1) return false;
            MVItem specifiedItem = ItemManager.getItem(args[0]);
            int amount = 1;
            if (args.length > 1) {
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    Message.EXPECTED_NUMBER.send(sender, true);
                    return true;
                }
            }
            if (args.length > 2) {
                if (main.getServer().getPlayer(args[2]) != null) {
                    specifiedPlayer = main.getServer().getPlayer(args[2]);
                } else {
                    Message.PLAYER_NOT_ONLINE.send(sender, true);
                    return true;
                }
            }
            if (specifiedPlayer == null) {
                Message.PLAYER_NOT_ONLINE.send(sender, true);
                return true;
            }
            if (specifiedItem != null) {
                if (specifiedPlayer.getInventory().firstEmpty() != -1) {
                    ItemStack item = specifiedItem.getItem(false);
                    for (int i = 0; i < amount; i++) {
                        specifiedPlayer.getInventory().addItem(item);
                    }
                    Message.GIVEN_ITEM.send(sender, true, amount, specifiedItem.displayName, specifiedPlayer.getDisplayName());
                } else
                    Message.GIVE_ITEM_FAIL_FULL_INV.send(sender, true, amount, specifiedItem.displayName, specifiedPlayer.getDisplayName());
            } else {
                Message.ITEM_DOESNT_EXIST.send(sender, true, args[0]);
            }
        } else {
            Message.ONLY_PLAYERS_CAN_EXECUTE.send(sender, true);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 2) {
            List<String> onlinePlayers = new ArrayList<>();
            for (Player player : main.getServer().getOnlinePlayers()) {
                onlinePlayers.add(player.getName());
            }

            final List<String> completions = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], onlinePlayers, completions);
            Collections.sort(completions);
            return completions;
        }
        if (args.length > 1)
            return new ArrayList<>();

        List<String> items = ItemManager.getValidItemList();

        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], items, completions);
        Collections.sort(completions);
        return completions;
    }
}
