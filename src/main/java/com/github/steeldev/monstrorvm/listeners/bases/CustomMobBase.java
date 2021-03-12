package com.github.steeldev.monstrorvm.listeners.bases;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Message;
import com.github.steeldev.monstrorvm.util.Util;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import com.github.steeldev.monstrorvm.util.mobs.ItemChance;
import com.github.steeldev.monstrorvm.util.mobs.MVMob;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;

import static com.github.steeldev.monstrorvm.util.Util.chanceOf;
import static com.github.steeldev.monstrorvm.util.Util.rand;

public class CustomMobBase implements Listener {
    Monstrorvm main = Monstrorvm.getInstance();
    MVMob mob;

    public CustomMobBase(String mobID) {
        this.mob = MobManager.getMob(mobID);
    }

    public CustomMobBase() {
    }

    @EventHandler
    public void customMobSpawn(EntitySpawnEvent event) {
        World world = event.getLocation().getWorld();
        if (mob == null) return;
        if (mob.validSpawnWorlds == null || !mob.validSpawnWorlds.contains(world.getName())) return;
        if (!mob.spawnNaturally) return;
        if (event.getEntity().getCustomName() != null) return;
        if (Util.isMVMob(event.getEntity())) return;
        if (mob.entityToReplace == null ||
                mob.entityToReplace.size() < 1 ||
                !mob.entityToReplace.contains(event.getEntityType())) return;

        if (chanceOf(mob.spawnChance)) {
            int bnMobCount = MobManager.getSpawnedMobs().size();
            if (bnMobCount >= main.config.CUSTOM_MOB_CAP) {
                event.setCancelled(true);
                return;
            }

            mob.spawnMob(event.getLocation(), (LivingEntity) event.getEntity());
        }
    }

    @EventHandler
    public void customMobDeath(EntityDeathEvent event) {
        if (mob == null) return;
        if (!event.getEntityType().equals(mob.baseEntity)) return;
        if (event.getEntity().getCustomName() == null) return;
        if (!Util.isMVMob(event.getEntity())) return;
        if (!ChatColor.stripColor(event.getEntity().getCustomName()).equals(mob.getUncoloredName())) return;

        if (mob.dropsToRemove != null && mob.dropsToRemove.size() > 0)
            event.getDrops().removeIf(item -> mob.dropsToRemove.contains(item.getType()));

        if (mob.drops != null && mob.drops.size() > 0) {
            for (ItemChance entry : mob.drops) {
                if (chanceOf(entry.chance)) {
                    ItemStack dropItem = entry.getItem(entry.damaged);
                    event.getDrops().add(dropItem);
                }
            }
        }
        World world = event.getEntity().getWorld();
        if (mob.deathEXP != null && mob.deathEXP.size() > 0) {
            int exp = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || mob.deathEXP.size() < 2) exp = mob.deathEXP.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (mob.deathEXP.size() > 1 && world.getDifficulty().equals(Difficulty.EASY))
                exp = mob.deathEXP.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (mob.deathEXP.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL))
                exp = mob.deathEXP.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (mob.deathEXP.size() > 3 && world.getDifficulty().equals(Difficulty.HARD))
                exp = mob.deathEXP.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB DIED - Difficulty: " + world.getDifficulty().toString() + " | DeathEXP: " + exp);

            event.setDroppedExp(rand.nextInt(exp));
        }

        if (mob.explosionOnDeathInfo == null) return;
        if (!mob.explosionOnDeathInfo.enabled) return;
        if (chanceOf(mob.explosionOnDeathInfo.chance))
            event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), mob.explosionOnDeathInfo.size, mob.explosionOnDeathInfo.createsFire);
        MobManager.removeMobFromSpawned(event.getEntity());
    }

    @EventHandler
    public void customMobDamageEntity(EntityDamageByEntityEvent event) {
        if (mob == null) return;
        if (!event.getDamager().getType().equals(mob.baseEntity)) return;
        if (event.getDamager().getCustomName() == null) return;
        if (!Util.isMVMob(event.getEntity())) return;
        if (!ChatColor.stripColor(event.getDamager().getCustomName()).equals(mob.getUncoloredName())) return;

        if (event.getEntity() instanceof LivingEntity) {
            if (mob.hitEffects != null && mob.hitEffects.size() > 0) {
                for (MVPotionEffect entry : mob.hitEffects) {
                    LivingEntity victim = (LivingEntity) event.getEntity();
                    if (chanceOf(entry.chance)) {
                        victim.addPotionEffect(entry.getPotionEffect(), false);
                        if (main.config.DEBUG)
                            Message.MOB_INFLICTED_DEBUG.log(mob.getUncoloredName(), victim.getName(), entry.effect.toString());
                    }
                }
            }
        }
    }

    @EventHandler
    public void customMobTarget(EntityTargetLivingEntityEvent event) {
        if (mob == null) return;
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity entity = (LivingEntity) event.getEntity();
        LivingEntity target = event.getTarget();
        if (target == null) return;
        if (target.isDead()) return;
        if (!entity.getType().equals(mob.baseEntity)) return;
        if (entity.getCustomName() == null) return;
        if (!Util.isMVMob(event.getEntity())) return;
        if (!ChatColor.stripColor(entity.getCustomName()).equals(mob.getUncoloredName())) return;
        if (mob.targetEffect == null) return;

        if (mob.targetableEntityTypes != null && mob.targetableEntityTypes.size() > 0) {
            if (!mob.targetableEntityTypes.contains(target.getType())) {
                event.setCancelled(true);
                return;
            }
        }

        if (chanceOf(mob.targetEffect.chance)) {
            if (mob.targetEffect.targetParticle != null) {
                if (mob.targetEffect.targetParticle.particle != null &&
                        mob.targetEffect.targetParticle.amount > 0)
                    mob.targetEffect.targetParticle.spawnParticle(entity.getEyeLocation());
            }

            if (mob.targetEffect.targetSound != null)
                if (mob.targetEffect.targetSound.sound != null &&
                        mob.targetEffect.targetSound.category != null &&
                        mob.targetEffect.targetSound.volume > 0 &&
                        mob.targetEffect.targetSound.pitch > 0)
                    mob.targetEffect.targetSound.playSound(entity.getLocation());

            if (mob.targetEffect.selfEffects != null && mob.targetEffect.selfEffects.size() > 0) {
                for (MVPotionEffect effect : mob.targetEffect.selfEffects) {
                    if (chanceOf(effect.chance))
                        entity.addPotionEffect(effect.getPotionEffect(), false);
                }
            }

            if (mob.targetEffect.targetEffects != null && mob.targetEffect.targetEffects.size() > 0) {
                for (MVPotionEffect effect : mob.targetEffect.targetEffects) {
                    if (chanceOf(effect.chance))
                        target.addPotionEffect(effect.getPotionEffect(), false);
                }
            }
        }
    }
}
