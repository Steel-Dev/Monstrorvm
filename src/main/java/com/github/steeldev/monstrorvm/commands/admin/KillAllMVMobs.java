package com.github.steeldev.monstrorvm.commands.admin;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class KillAllMVMobs implements CommandExecutor {
    Monstrorvm main = Monstrorvm.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player) {
            int entityAmount = MobManager.getSpawnedMobs().size();
            if (entityAmount == 0) {
                Message.MOB_KILL_FAILED.send(sender, true);
                return true;
            }

            Iterator<Map.Entry<UUID, LivingEntity>> it = MobManager.getSpawnedMobs().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, LivingEntity> entityEntry = it.next();
                if (entityEntry != null) {
                    entityEntry.getValue().remove();
                    it.remove();
                }
            }

            Message.MOBS_KILLED.send(sender, true, entityAmount);
        } else {
            Message.ONLY_PLAYERS_CAN_EXECUTE.send(sender, true);
        }
        return true;
    }
}
