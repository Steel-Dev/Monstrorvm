package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.ItemManager;
import com.github.steeldev.monstrorvm.util.config.Lang;
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

import static com.github.steeldev.monstrorvm.util.Util.colorize;

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
                    sender.sendMessage(colorize(String.format("%s&cExpected a number.", Lang.PREFIX)));
                    return true;
                }
            }
            if (args.length > 2) {
                if (main.getServer().getPlayer(args[2]) != null) {
                    specifiedPlayer = main.getServer().getPlayer(args[2]);
                } else {
                    sender.sendMessage(colorize(Lang.PREFIX + Lang.INVALID_PLAYER_MSG));
                    return true;
                }
            }
            if (specifiedPlayer == null) {
                sender.sendMessage(colorize(Lang.PREFIX + Lang.INVALID_PLAYER_MSG));
                return true;
            }
            if (specifiedItem != null) {
                if (specifiedPlayer.getInventory().firstEmpty() != -1) {
                    ItemStack item = specifiedItem.getItem(false);
                    for (int i = 0; i < amount; i++) {
                        specifiedPlayer.getInventory().addItem(item);
                    }
                    sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.CUSTOM_ITEM_GIVEN_MSG.replace("ITEMNAME", specifiedItem.displayName).replace("PLAYERNAME", specifiedPlayer.getDisplayName()).replace("ITEMAMOUNT", String.valueOf(amount)))));
                } else
                    sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.CUSTOM_ITEM_PLAYER_INVENTORY_FULL_MSG.replace("ITEMNAME", specifiedItem.displayName).replace("PLAYERNAME", specifiedPlayer.getDisplayName()).replace("ITEMAMOUNT", String.valueOf(amount)))));
            } else {
                sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.CUSTOM_ITEM_INVALID_MSG.replaceAll("ITEMID", args[0]))));
            }
        } else {
            sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.PLAYERS_ONLY_MSG)));
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
