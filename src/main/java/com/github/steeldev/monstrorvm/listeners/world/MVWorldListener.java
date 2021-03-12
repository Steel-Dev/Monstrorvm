package com.github.steeldev.monstrorvm.listeners.world;

import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Util;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class MVWorldListener implements Listener {

    @EventHandler
    public void chunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity) {
                if (Util.isMVMob(entity))
                    MobManager.addMobToSpawned((LivingEntity) entity);
            }
        }
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity) {
                if (Util.isMVMob(entity))
                    MobManager.removeMobFromSpawned((LivingEntity) entity);
            }
        }
    }
}
