package com.github.steeldev.monstrorvm.listeners.server;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.github.steeldev.monstrorvm.util.Util.colorize;

public class PlayerJoin implements Listener {
    Monstrorvm main = Monstrorvm.getInstance();
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();

        if(!main.recipesRegistered){
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.kickPlayer(colorize(Util.getPrefix() + "&cStill registering item recipes! You have been kicked to avoid any errors, join back in a few seconds!")),10l);
        }
    }
}
