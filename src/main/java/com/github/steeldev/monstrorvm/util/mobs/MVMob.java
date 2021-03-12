package com.github.steeldev.monstrorvm.util.mobs;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.managers.MobManager;
import com.github.steeldev.monstrorvm.util.Message;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
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
    public List<Integer> deathEXP;
    public List<Double> maxHPs;
    public List<Double> moveSpeeds;
    public int spawnChance;
    public List<String> validSpawnWorlds;
    public List<MVPotionEffect> hitEffects;
    public DeathExplosionInfo explosionOnDeathInfo;
    public List<Material> dropsToRemove;
    public List<ItemChance> drops;
    public List<ItemChance> equipment;
    public List<MVPotionEffect> spawnPotionEffects;
    public MobTargetEffect targetEffect;
    public BabyInfo babyInfo;
    public List<EntityType> targetableEntityTypes;
    public List<Double> attackDamages;
    public List<Double> attackKnockbacks;
    public List<Double> knockbackresistances;
    public List<Double> armors;
    public List<Double> armorToughnesses;
    public List<Double> flySpeeds;
    public List<Double> followRanges;
    public float jumpStrength;
    public boolean spawnNaturally;
    Monstrorvm main = Monstrorvm.getInstance();

    public MVMob(String key,
                 EntityType baseEntity,
                 String entityName,
                 int spawnChance) {
        this.key = key;
        this.baseEntity = baseEntity;
        this.entityName = entityName;
        this.spawnChance = spawnChance;

        spawnNaturally(true);
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
        this.deathEXP = Collections.singletonList(exp);
        return this;
    }

    public MVMob withCustomMaxHP(double hp) {
        this.maxHPs = Collections.singletonList(hp);
        return this;
    }

    public MVMob withCustomAttackKnockback(double knockback) {
        this.attackKnockbacks = Collections.singletonList(knockback);
        return this;
    }

    public MVMob withCustomKnockbackResistance(double knockbackResistance) {
        this.knockbackresistances = Collections.singletonList(knockbackResistance);
        return this;
    }

    public MVMob withCustomMoveSpeed(double speed) {
        this.moveSpeeds = Collections.singletonList(speed);
        return this;
    }

    public MVMob withCustomAttackDamage(double attackDamage) {
        this.attackDamages = Collections.singletonList(attackDamage);
        return this;
    }

    public MVMob withCustomArmor(double arm) {
        this.armors = Collections.singletonList(arm);
        return this;
    }

    public MVMob withCustomArmorToughness(double armToughness) {
        this.armorToughnesses = Collections.singletonList(armToughness);
        return this;
    }

    public MVMob withCustomFlySpeed(double flySpeed) {
        this.flySpeeds = Collections.singletonList(flySpeed);
        return this;
    }

    public MVMob withCustomFollowRange(double followRange) {
        this.followRanges = Collections.singletonList(followRange);
        return this;
    }

    public MVMob withCustomDeathEXP(List<Integer> exps) {
        this.deathEXP = exps;
        return this;
    }

    public MVMob withCustomMaxHP(List<Double> hps) {
        this.maxHPs = hps;
        return this;
    }

    public MVMob withCustomAttackKnockback(List<Double> knockbacks) {
        this.attackKnockbacks = knockbacks;
        return this;
    }

    public MVMob withCustomKnockbackResistance(List<Double> knockbackResistances) {
        this.knockbackresistances = knockbackResistances;
        return this;
    }

    public MVMob withCustomMoveSpeed(List<Double> speeds) {
        this.moveSpeeds = speeds;
        return this;
    }

    public MVMob withCustomAttackDamage(List<Double> attackDamages) {
        this.attackDamages = attackDamages;
        return this;
    }

    public MVMob withCustomArmor(List<Double> arms) {
        this.armors = arms;
        return this;
    }

    public MVMob withCustomArmorToughness(List<Double> armToughnesses) {
        this.armorToughnesses = armToughnesses;
        return this;
    }

    public MVMob withCustomFlySpeed(List<Double> flySpeeds) {
        this.flySpeeds = flySpeeds;
        return this;
    }

    public MVMob withCustomFollowRange(List<Double> followRanges) {
        this.followRanges = followRanges;
        return this;
    }

    public MVMob withCustomJumpStrength(float str) {
        this.jumpStrength = str;
        return this;
    }

    public MVMob withValidSpawnWorld(String world) {
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

    public MVMob spawnNaturally(boolean val) {
        this.spawnNaturally = val;
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
                    //  Don't like it? Yeah, me neither. Deal with it :D
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
                }
            }
        }
        // Make hostile towards anything the user defines as its targets
        if (targetableEntityTypes != null && targetableEntityTypes.size() > 0) {
            if (spawnedEnt instanceof Mob) {
                Mob finalSpawnedEnt1 = (Mob) spawnedEnt;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!finalSpawnedEnt1.isDead()) {
                            if (finalSpawnedEnt1.getTarget() == null) {
                                List<Entity> nearbyEntities = finalSpawnedEnt1.getNearbyEntities(5, 5, 5);
                                List<LivingEntity> nearbyLivingEntities = new ArrayList<>();
                                for (Entity entity : nearbyEntities) {
                                    if (entity instanceof LivingEntity) {
                                        if (targetableEntityTypes == null || targetableEntityTypes.contains(entity.getType()))
                                            nearbyLivingEntities.add((LivingEntity) entity);
                                    }
                                }
                                if (nearbyLivingEntities.size() > 0) {
                                    boolean canTargetEntity = true;
                                    LivingEntity newTarget = nearbyLivingEntities.get(rand.nextInt(nearbyLivingEntities.size()));

                                    if (newTarget instanceof Player) {
                                        Player targetPlayer = (Player) newTarget;
                                        if (targetPlayer.getGameMode().equals(GameMode.CREATIVE) || targetPlayer.getGameMode().equals(GameMode.SPECTATOR))
                                            canTargetEntity = false;
                                    }
                                    if (canTargetEntity)
                                        finalSpawnedEnt1.setTarget(newTarget);
                                }
                            }
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(main, 5, 100);
            }
        }

        spawnedEnt.setCustomName(colorize(entityName));

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

        if (maxHPs != null && maxHPs.size() > 0) {
            double maxhp = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || maxHPs.size() < 2) maxhp = maxHPs.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (maxHPs.size() > 1 && world.getDifficulty().equals(Difficulty.EASY)) maxhp = maxHPs.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (maxHPs.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL)) maxhp = maxHPs.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (maxHPs.size() > 3 && world.getDifficulty().equals(Difficulty.HARD)) maxhp = maxHPs.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB SPAWNED - Difficulty: " + world.getDifficulty().toString() + " | HP: " + maxhp);

            spawnedEnt.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxhp);
            spawnedEnt.setHealth(maxhp);
        }

        if (moveSpeeds != null && moveSpeeds.size() > 0) {
            double moveSpeed = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || moveSpeeds.size() < 2)
                moveSpeed = moveSpeeds.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (moveSpeeds.size() > 1 && world.getDifficulty().equals(Difficulty.EASY))
                moveSpeed = moveSpeeds.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (moveSpeeds.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL))
                moveSpeed = moveSpeeds.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (moveSpeeds.size() > 3 && world.getDifficulty().equals(Difficulty.HARD))
                moveSpeed = moveSpeeds.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB SPAWNED - Difficulty: " + world.getDifficulty().toString() + " | MoveSpeed: " + moveSpeed);

            spawnedEnt.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(moveSpeed);
        }

        if (attackDamages != null && attackDamages.size() > 0) {
            double attackDamage = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || attackDamages.size() < 2)
                attackDamage = attackDamages.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (attackDamages.size() > 1 && world.getDifficulty().equals(Difficulty.EASY))
                attackDamage = attackDamages.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (attackDamages.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL))
                attackDamage = attackDamages.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (attackDamages.size() > 3 && world.getDifficulty().equals(Difficulty.HARD))
                attackDamage = attackDamages.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB SPAWNED - Difficulty: " + world.getDifficulty().toString() + " | AttackDamage: " + attackDamage);

            spawnedEnt.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(attackDamage);
        }

        if (attackKnockbacks != null && attackKnockbacks.size() > 0) {
            double attackKnockback = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || attackKnockbacks.size() < 2)
                attackKnockback = attackKnockbacks.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (attackKnockbacks.size() > 1 && world.getDifficulty().equals(Difficulty.EASY))
                attackKnockback = attackKnockbacks.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (attackKnockbacks.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL))
                attackKnockback = attackKnockbacks.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (attackKnockbacks.size() > 3 && world.getDifficulty().equals(Difficulty.HARD))
                attackKnockback = attackKnockbacks.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB SPAWNED - Difficulty: " + world.getDifficulty().toString() + " | AttackKnockback: " + attackKnockback);

            spawnedEnt.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).setBaseValue(attackKnockback);
        }

        if (knockbackresistances != null && knockbackresistances.size() > 0) {
            double knockbackResistance = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || knockbackresistances.size() < 2)
                knockbackResistance = knockbackresistances.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (knockbackresistances.size() > 1 && world.getDifficulty().equals(Difficulty.EASY))
                knockbackResistance = knockbackresistances.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (knockbackresistances.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL))
                knockbackResistance = knockbackresistances.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (knockbackresistances.size() > 3 && world.getDifficulty().equals(Difficulty.HARD))
                knockbackResistance = knockbackresistances.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB SPAWNED - Difficulty: " + world.getDifficulty().toString() + " | KnockbackResistance: " + knockbackResistance);

            spawnedEnt.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(knockbackResistance);
        }

        if (armors != null && armors.size() > 0) {
            double armor = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || armors.size() < 2) armor = armors.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (armors.size() > 1 && world.getDifficulty().equals(Difficulty.EASY)) armor = armors.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (armors.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL)) armor = armors.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (armors.size() > 3 && world.getDifficulty().equals(Difficulty.HARD)) armor = armors.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB SPAWNED - Difficulty: " + world.getDifficulty().toString() + " | Armor: " + armor);

            spawnedEnt.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
        }

        if (armorToughnesses != null && armorToughnesses.size() > 0) {
            double armorToughness = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || armorToughnesses.size() < 2)
                armorToughness = armorToughnesses.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (armorToughnesses.size() > 1 && world.getDifficulty().equals(Difficulty.EASY))
                armorToughness = armorToughnesses.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (armorToughnesses.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL))
                armorToughness = armorToughnesses.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (armorToughnesses.size() > 3 && world.getDifficulty().equals(Difficulty.HARD))
                armorToughness = armorToughnesses.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB SPAWNED - Difficulty: " + world.getDifficulty().toString() + " | ArmorToughness: " + armorToughness);

            spawnedEnt.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(armorToughness);
        }

        if (flySpeeds != null && flySpeeds.size() > 0 && (spawnedEnt instanceof Flying)) {
            double flySpeed = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || flySpeeds.size() < 2) flySpeed = flySpeeds.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (flySpeeds.size() > 1 && world.getDifficulty().equals(Difficulty.EASY)) flySpeed = flySpeeds.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (flySpeeds.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL))
                flySpeed = flySpeeds.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (flySpeeds.size() > 3 && world.getDifficulty().equals(Difficulty.HARD)) flySpeed = flySpeeds.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB SPAWNED - Difficulty: " + world.getDifficulty().toString() + " | FlySpeed: " + flySpeed);

            spawnedEnt.getAttribute(Attribute.GENERIC_FLYING_SPEED).setBaseValue(flySpeed);
        }

        if (followRanges != null && followRanges.size() > 0) {
            double followRange = 0;

            // If difficulty is Peaceful OR if the list is only 1
            if (world.getDifficulty().equals(Difficulty.PEACEFUL) || followRanges.size() < 2)
                followRange = followRanges.get(0);
                // Else, if the list is greater than 1, and the difficulty is Easy
            else if (followRanges.size() > 1 && world.getDifficulty().equals(Difficulty.EASY))
                followRange = followRanges.get(1);
                // Else, if the list is greater than 2, and the difficulty is Normal
            else if (followRanges.size() > 2 && world.getDifficulty().equals(Difficulty.NORMAL))
                followRange = followRanges.get(2);
                // Else, if the list is greater than 3, and the difficulty is Hard
            else if (followRanges.size() > 3 && world.getDifficulty().equals(Difficulty.HARD))
                followRange = followRanges.get(3);

            if (main.config.DEBUG)
                main.getLogger().info("MOB SPAWNED - Difficulty: " + world.getDifficulty().toString() + " | FollowRange: " + followRange);

            spawnedEnt.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(followRange);
        }

        if (jumpStrength > 0 && (spawnedEnt instanceof Horse))
            spawnedEnt.getAttribute(Attribute.HORSE_JUMP_STRENGTH).setBaseValue(jumpStrength);

        MobManager.addMobToSpawned(spawnedEnt);

        if (main.config.DEBUG) {
            String mobName = getUncoloredName();
            if (ridingMob)
                mobName += " Rider";
            if (isBaby)
                mobName += " Baby";
            Message.MOB_SPAWNED_DEBUG.log(mobName, location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
        }

        return spawnedEnt;
    }
}
