package com.github.steeldev.monstrorvm.util.mobs;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.config.Config;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.github.steeldev.monstrorvm.util.Util.*;

public class MVMob {
    public Plugin registeredBy;
    public String key;
    public String entityName;
    public List<EntityType> entityToReplace;
    public EntityType baseEntity;
    public MountInfo mountInfo;
    public boolean angry;
    public BurningInfo burningInfo;
    public int deathEXP;
    public float maxHP;
    public float moveSpeed;
    public int spawnChance;
    public List<World.Environment> validSpawnWorlds;
    public List<MVPotionEffect> hitEffects;
    public DeathExplosionInfo explosionOnDeathInfo;
    public List<Material> dropsToRemove;
    public List<ItemChance> drops;
    public List<ItemChance> equipment;
    public List<MVPotionEffect> spawnPotionEffects;
    public MobTargetEffect targetEffect;
    public BabyInfo babyInfo;
    public List<EntityType> targetableEntityTypes;
    Monstrorvm main = Monstrorvm.getInstance();

    public MVMob(String key,
                 EntityType baseEntity,
                 String entityName,
                 int spawnChance) {
        this.key = key;
        this.baseEntity = baseEntity;
        this.entityName = entityName;
        this.spawnChance = spawnChance;
    }

    public MVMob withEntityToReplace(EntityType entity) {
        if (this.entityToReplace == null) this.entityToReplace = new ArrayList<>();
        this.entityToReplace.add(entity);
        return this;
    }

    public MVMob withMount(MountInfo mountInfo) {
        this.mountInfo = mountInfo;
        return this;
    }

    public MVMob withAnger(boolean angry) {
        this.angry = angry;
        return this;
    }

    public MVMob withCustomDeathEXP(int exp) {
        this.deathEXP = exp;
        return this;
    }

    public MVMob withCustomMaxHP(float hp) {
        this.maxHP = hp;
        return this;
    }

    public MVMob withCustomMoveSpeed(float speed) {
        this.moveSpeed = speed;
        return this;
    }

    public MVMob withValidSpawnWorld(World.Environment world) {
        if (this.validSpawnWorlds == null) this.validSpawnWorlds = new ArrayList<>();
        this.validSpawnWorlds.add(world);
        return this;
    }

    public MVMob withHitEffect(MVPotionEffect effect) {
        if (this.hitEffects == null) this.hitEffects = new ArrayList<>();
        this.hitEffects.add(effect);
        return this;
    }

    public MVMob withSpawnEffect(MVPotionEffect effect) {
        if (this.spawnPotionEffects == null) this.spawnPotionEffects = new ArrayList<>();
        this.spawnPotionEffects.add(effect);
        return this;
    }

    public MVMob withBurningEffect(BurningInfo burningInfo) {
        this.burningInfo = burningInfo;
        return this;
    }

    public MVMob withDeathExplosion(boolean enabled, int chance, int size, boolean createsFire) {
        this.explosionOnDeathInfo = new DeathExplosionInfo(enabled, chance, size, createsFire);
        return this;
    }

    public MVMob withDropToRemove(Material drop) {
        if (this.dropsToRemove == null) this.dropsToRemove = new ArrayList<>();
        this.dropsToRemove.add(drop);
        return this;
    }

    public MVMob withDrop(ItemChance drop) {
        if (this.drops == null) this.drops = new ArrayList<>();
        this.drops.add(drop);
        return this;
    }

    public MVMob withMainHandItem(ItemChance item) {
        if (this.equipment == null) {
            this.equipment = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                this.equipment.add(null);
            }
        }
        this.equipment.set(0, item);
        return this;
    }

    public MVMob withOffhandItem(ItemChance item) {
        if (this.equipment == null) {
            this.equipment = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                this.equipment.add(null);
            }
        }
        this.equipment.set(1, item);
        return this;
    }

    public MVMob withHelmet(ItemChance item) {
        if (this.equipment == null) {
            this.equipment = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                this.equipment.add(null);
            }
        }
        this.equipment.set(2, item);
        return this;
    }

    public MVMob withChestplate(ItemChance item) {
        if (this.equipment == null) {
            this.equipment = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                this.equipment.add(null);
            }
        }
        this.equipment.set(3, item);
        return this;
    }

    public MVMob withLeggings(ItemChance item) {
        if (this.equipment == null) {
            this.equipment = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                this.equipment.add(null);
            }
        }
        this.equipment.set(4, item);
        return this;
    }

    public MVMob withBoots(ItemChance item) {
        if (this.equipment == null) {
            this.equipment = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                this.equipment.add(null);
            }
        }
        this.equipment.set(5, item);
        return this;
    }

    public MVMob withTargetEffect(MobTargetEffect effect) {
        this.targetEffect = effect;
        return this;
    }

    public MVMob setBaby(BabyInfo babyInfo) {
        this.babyInfo = babyInfo;
        return this;
    }

    public MVMob withPossibleTarget(EntityType type) {
        if (this.targetableEntityTypes == null) this.targetableEntityTypes = new ArrayList<>();
        this.targetableEntityTypes.add(type);
        return this;
    }

    public String getColoredName() {
        return colorize(entityName);
    }

    public String getUncoloredName() {
        return ChatColor.stripColor(getColoredName());
    }

    public Entity spawnMob(Location location, LivingEntity spawnedEnt) {
        World world = location.getWorld();
        if (spawnedEnt != null) {
            if (!spawnedEnt.getType().equals(baseEntity))
                spawnedEnt.remove();
        }
        spawnedEnt = (LivingEntity) world.spawnEntity(location, baseEntity);
        spawnedEnt.getPersistentDataContainer().set(MobManager.customMobKey, PersistentDataType.STRING, key);

        spawnedEnt.setPortalCooldown(Integer.MAX_VALUE);
        if (baseEntity.equals(EntityType.WOLF)) {
            if (spawnedEnt instanceof Wolf) {
                if (angry) {
                    Wolf finalSpawnedEnt = (Wolf) spawnedEnt;
                    finalSpawnedEnt.setAngry(true);

                    // Because Bukkit is a dildo and doesn't let me modify the ticks of anger.
                    // or make anger forever.. guess thats a minecraft issue.
                    //  So I have to do this bullshit.
                    //  Don't like it? Yeah, me either. Deal with it :D
                    //   Tried using NBTApi but it didn't work, rip.
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!finalSpawnedEnt.isDead()) {
                                if (!finalSpawnedEnt.isAngry())
                                    finalSpawnedEnt.setAngry(angry);
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(main, 70, 70);

                    // Make hostile towards anything
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!finalSpawnedEnt.isDead()) {
                                if (finalSpawnedEnt.isAngry()) {
                                    if (finalSpawnedEnt.getTarget() == null) {
                                        List<Entity> nearbyEntities = finalSpawnedEnt.getNearbyEntities(5, 5, 5);
                                        List<LivingEntity> nearbyLivingEntities = new ArrayList<>();
                                        for (Entity entity : nearbyEntities) {
                                            if (entity instanceof LivingEntity) {
                                                if (targetableEntityTypes == null || targetableEntityTypes.contains(entity.getType()))
                                                    nearbyLivingEntities.add((LivingEntity) entity);
                                            }
                                        }
                                        if (nearbyLivingEntities.size() > 0) {
                                            LivingEntity newTarget = nearbyLivingEntities.get(rand.nextInt(nearbyLivingEntities.size()));

                                            finalSpawnedEnt.setTarget(newTarget);
                                        }
                                    }
                                }
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(main, 5, 100);
                }
            }
        }

        spawnedEnt.setCustomName(colorize(entityName));
        if (maxHP > 0) {
            spawnedEnt.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHP);
            spawnedEnt.setHealth(maxHP);
        }

        if (moveSpeed > 0)
            spawnedEnt.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(moveSpeed);

        if (equipment != null) {
            ItemChance mainHand = equipment.get(0);
            if (mainHand != null) {
                spawnedEnt.getEquipment().setItemInMainHand(mainHand.getItem(mainHand.damaged));
                spawnedEnt.getEquipment().setItemInMainHandDropChance(mainHand.chanceF);
            }
            ItemChance offHand = equipment.get(1);
            if (offHand != null) {
                spawnedEnt.getEquipment().setItemInOffHand(offHand.getItem(offHand.damaged));
                spawnedEnt.getEquipment().setItemInOffHandDropChance(offHand.chanceF);
            }
            ItemChance helmet = equipment.get(2);
            if (helmet != null) {
                spawnedEnt.getEquipment().setHelmet(helmet.getItem(helmet.damaged));
                spawnedEnt.getEquipment().setHelmetDropChance(helmet.chanceF);
            }
            ItemChance chestplate = equipment.get(3);
            if (chestplate != null) {
                spawnedEnt.getEquipment().setChestplate(chestplate.getItem(chestplate.damaged));
                spawnedEnt.getEquipment().setChestplateDropChance(chestplate.chanceF);
            }
            ItemChance leggings = equipment.get(4);
            if (leggings != null) {
                spawnedEnt.getEquipment().setLeggings(leggings.getItem(leggings.damaged));
                spawnedEnt.getEquipment().setLeggingsDropChance(leggings.chanceF);
            }
            ItemChance boots = equipment.get(5);
            if (boots != null) {
                spawnedEnt.getEquipment().setBoots(boots.getItem(boots.damaged));
                spawnedEnt.getEquipment().setBootsDropChance(boots.chanceF);
            }
        }

        boolean ridingMob = false;
        if (mountInfo != null) {
            if (mountInfo.riding != null) {
                if (chanceOf(mountInfo.chance)) {
                    LivingEntity entityToRide = mountInfo.spawnMount(location);
                    entityToRide.setPassenger(spawnedEnt);
                    ridingMob = true;
                }
            }
        }

        if (burningInfo != null)
            if (burningInfo.burning)
                spawnedEnt.setFireTicks(burningInfo.burnTime);

        if (spawnPotionEffects != null) {
            for (MVPotionEffect effect : spawnPotionEffects) {
                if (chanceOf(effect.chance))
                    spawnedEnt.addPotionEffect(effect.getPotionEffect(), false);
            }
        }

        boolean isBaby = false;
        if (spawnedEnt instanceof Ageable) {
            if (babyInfo != null && babyInfo.canBeBaby) {
                if (chanceOf(babyInfo.chance)) {
                    ((Ageable) spawnedEnt).setBaby();
                    isBaby = true;
                }
            } else {
                ((Ageable) spawnedEnt).setAdult();
                isBaby = false;
            }
        }

        MobManager.addMobToSpawned(spawnedEnt);

        if (Config.DEBUG) {
            String mobName = getUncoloredName();
            if (ridingMob)
                mobName += " Rider";
            if (isBaby)
                mobName += " Baby";
            main.getLogger().info(colorize(String.format("&aCustom Mob &6%s &aspawned at &e%s&a!", mobName, location)));
        }

        return spawnedEnt;
    }
}
