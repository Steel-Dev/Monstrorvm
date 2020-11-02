package com.github.steeldev.monstrorvm.listeners.world;

import com.github.steeldev.monstrorvm.managers.MobManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataType;

public class MVWorldListener implements Listener {

    @EventHandler
    public void chunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity) {
                if (entity.getPersistentDataContainer().has(MobManager.customMobKey, PersistentDataType.STRING))
                    MobManager.addMobToSpawned((LivingEntity) entity);
            }
        }
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity instanceof LivingEntity) {
                if (entity.getPersistentDataContainer().has(MobManager.customMobKey, PersistentDataType.STRING))
                    MobManager.removeMobFromSpawned((LivingEntity) entity);
            }
        }
    }
}
