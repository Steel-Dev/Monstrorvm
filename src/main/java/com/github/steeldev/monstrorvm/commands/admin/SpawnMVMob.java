package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.config.Lang;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.Difficulty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.steeldev.monstrorvm.util.Util.colorize;

public class SpawnMVMob implements CommandExecutor, TabCompleter {
    Monstrorvm main = Monstrorvm.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (args.length < 1) return false;
            MVMob specifiedMob = MobManager.getMob(args[0]);
            if (specifiedMob != null) {
                Player player = (Player) sender;
                if (!player.getWorld().getDifficulty().equals(Difficulty.PEACEFUL)) {
                    specifiedMob.spawnMob(player.getLocation(), null);
                    sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.CUSTOM_MOB_SPAWNED_MSG.replace("MOBNAME", specifiedMob.entityName))));
                } else {
                    sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.CUSTOM_MOB_SPAWN_FAILED_MSG.replaceAll("MOBNAME", specifiedMob.entityName))));
                }
            } else {
                sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.CUSTOM_MOB_INVALID_MSG.replaceAll("MOBID", args[0]))));
            }
        } else {
            sender.sendMessage(colorize(String.format("%s%s", Lang.PREFIX, Lang.PLAYERS_ONLY_MSG)));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> mobs = MobManager.getValidMobList();

        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], mobs, completions);
        Collections.sort(completions);
        return completions;
    }
}
