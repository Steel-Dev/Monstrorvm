package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Message;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
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

public class SpawnMVMob implements CommandExecutor, TabCompleter {
    Monstrorvm main = Monstrorvm.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            if (args.length < 1) return false;
            MVMob specifiedMob = MobManager.getMob(args[0]);
            if (specifiedMob != null) {
                Player player = (Player) sender;
                try {
                    specifiedMob.spawnMob(player.getLocation(), null);
                    Message.MOB_SPAWNED.send(sender, true, specifiedMob.entityName);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message.MOB_FAILED_SPAWNED.send(sender, true, specifiedMob.entityName);
                }
            } else {
                Message.MOB_NOT_VALID.send(sender, true, args[0]);
            }
        } else {
            Message.ONLY_PLAYERS_CAN_EXECUTE.send(sender, true);
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
