package com.github.steeldev.monstrorvm.managers;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.listeners.bases.CustomMobBase;
import com.github.steeldev.monstrorvm.util.config.Config;
import com.github.steeldev.monstrorvm.util.misc.MVParticle;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import com.github.steeldev.monstrorvm.util.misc.MVSound;
import com.github.steeldev.monstrorvm.util.mobs.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

import static com.github.steeldev.monstrorvm.util.Util.colorize;

public class MobManager {
    static Monstrorvm main = Monstrorvm.getInstance();
    public static NamespacedKey customMobKey = new NamespacedKey(main, "monstrorvm_mob");
    static Map<String, MVMob> mobMap;


    static Map<UUID, LivingEntity> spawnedCustomMobs;

    static List<String> exampleMobs = new ArrayList<>(Arrays.asList("ExampleMob"));

    static List<File> loadedFiles = new ArrayList<>();

    public static void init() {
        spawnedCustomMobs = new HashMap<>();
    }

    public static void registerNewMob(MVMob mob) {
        if (mobMap == null) mobMap = new HashMap<>();

        if (mobMap.containsKey(mob.key)) return;

        mobMap.put(mob.key, mob);

        main.getServer().getPluginManager().registerEvents(new CustomMobBase(mob.key), main);

        if (Config.DEBUG)
            main.getLogger().info(String.format("&aCustom mob &emonstrorvm:%s&a has been &2registered.", mob.key));
    }

    public static MVMob getMob(String key) {
        if (!mobMap.containsKey(key)) return null;

        return mobMap.get(key);
    }

    public static void registerCustomMobs() {
        for (String mobString : exampleMobs) {
            if (!new File("customthings/mobs/" + mobString + ".yml").exists())
                main.saveResource("customthings/mobs/" + mobString + ".yml", true);
        }
        File customMobFile = new File(main.getDataFolder(), "customthings/mobs");

        main.getLogger().info("&7Loading custom mobs from " + customMobFile.getPath());
        File[] mobFiles = customMobFile.listFiles();

        if (mobFiles == null ||
                mobFiles.length < 1) {
            main.getLogger().info(colorize("&e[WARNING] There are no Custom Mobs in the custom mob directory, skipping loading."));
            return;
        } else {
            main.getLogger().info("&7Successfully loaded " + mobFiles.length + " custom mobs! Registering them now.");
        }

        for (File mobFile : mobFiles) {
            boolean invalid = false;
            FileConfiguration mobYaml = YamlConfiguration.loadConfiguration(mobFile);

            if (Config.DEBUG &&
                    !loadedFiles.contains(mobFile))
                main.getLogger().info("Registering " + mobFile.getName());

            if (!mobYaml.contains("Key")) {
                main.getLogger().info(colorize("&c[ERROR] A custom mob MUST specify a Key! e.g: 'example_mob' - Error occured in: " + mobFile.getName()));
                invalid = true;
            }
            String key = mobYaml.getString("Key");

            if (!mobYaml.contains("BaseEntity")) {
                main.getLogger().info(colorize("&c[ERROR] A custom mob MUST specify a Base Entity! e.g: 'ZOMBIE' - Error occured in: " + mobFile.getName()));
                invalid = true;
            }
            EntityType baseEntity = EntityType.valueOf(mobYaml.getString("BaseEntity"));
            if (baseEntity == null) {
                main.getLogger().info(colorize("&c[ERROR] The specified base entity in " + mobFile.getName() + " is invalid!"));
                invalid = true;
            }

            if (!mobYaml.contains("Name")) {
                main.getLogger().info(colorize("&c[ERROR] A custom mob MUST specify a display name! e.g: 'Example Mob' - Error occured in: " + mobFile.getName()));
                invalid = true;
            }
            String displayName = mobYaml.getString("Name");

            if (!mobYaml.contains("SpawnChance")) {
                main.getLogger().info(colorize("&c[ERROR] A custom mob MUST specify a spawn chance! e.g: 10 - Error occured in: " + mobFile.getName()));
                invalid = true;
            }
            int spawnChance = mobYaml.getInt("SpawnChance");

            MVMob mob = new MVMob(key, baseEntity, displayName, spawnChance);

            if (mobYaml.contains("EntitiesToReplace")) {
                for (String entity : mobYaml.getStringList("EntitiesToReplace")) {
                    EntityType entityType = EntityType.valueOf(entity);
                    if (entityType == null) {
                        main.getLogger().info(colorize("&c[ERROR] The entity type " + entity + " specified in the EntitesToReplace list is invalid! - Error occured in: " + mobFile.getName()));
                        invalid = true;
                    }
                    mob.withEntityToReplace(entityType);
                }
            }

            if (mobYaml.contains("MountInfo")) {
                EntityType riding = EntityType.valueOf(mobYaml.getString("MountInfo.Riding"));
                if (riding == null) {
                    main.getLogger().info(colorize("&c[ERROR] The entity type " + mobYaml.getString("MountInfo.Riding") + " specified in the Riding section of Mount Info is invalid! - Error occured in: " + mobFile.getName()));
                    invalid = true;
                }
                int chance = mobYaml.getInt("MountInfo.Chance");

                MountInfo mountInfo = new MountInfo(riding, chance);

                if (mobYaml.contains("MountInfo.ArmorInfo")) {
                    List<Material> armorMats = new ArrayList<>();
                    int armorChance = mobYaml.getInt("MountInfo.ArmorInfo.Chance");
                    for (String matEntry : mobYaml.getStringList("MountInfo.ArmorInfo.PossibleTypes")) {
                        Material armorMat = Material.valueOf(matEntry);
                        if (armorMat.equals(Material.AIR)) {
                            main.getLogger().info(colorize("&c[ERROR] The material " + matEntry + " specified in the PossibleTypes for Mount Armor Info list is invalid! - Error occured in: " + mobFile.getName()));
                            invalid = true;
                        }
                        armorMats.add(armorMat);
                    }
                    mountInfo.armorChance = armorChance;
                    mountInfo.armorTypes = armorMats;
                }

                mob.withMount(mountInfo);
            }

            if (mobYaml.contains("AlwaysAngry"))
                mob.withAnger(mobYaml.getBoolean("AlwaysAngry"));

            if (mobYaml.contains("DeathEXP"))
                mob.withCustomDeathEXP(mobYaml.getInt("DeathEXP"));

            if (mobYaml.contains("BurnInfo")) {
                boolean enabled = mobYaml.getBoolean("BurnInfo.Enabled");
                int time = mobYaml.getInt("BurnInfo.Time");
                mob.withBurningEffect(new BurningInfo(enabled, time));
            }

            if (mobYaml.contains("MaxHP"))
                mob.withCustomMaxHP((float) mobYaml.getDouble("MaxHP"));

            if (mobYaml.contains("MoveSpeed"))
                mob.withCustomMoveSpeed((float) mobYaml.getDouble("MoveSpeed"));

            if (mobYaml.contains("ValidSpawnEnvironments")) {
                List<World.Environment> validEnvironments = new ArrayList<>();
                for (String env : mobYaml.getStringList("ValidSpawnEnvironments")) {
                    World.Environment environment = World.Environment.valueOf(env);
                    if (environment == null) {
                        main.getLogger().info(colorize("&c[ERROR] The environment " + env + " specified in the ValidSpawnEnvironments list is invalid! - Error occured in: " + mobFile.getName()));
                        invalid = true;
                    }
                    validEnvironments.add(environment);
                }
            }

            if (mobYaml.contains("HitEffects")) {
                List<MVPotionEffect> potionEffectList = new ArrayList<>();
                for (String entry : mobYaml.getConfigurationSection("HitEffects").getKeys(false)) {
                    ConfigurationSection potSection = mobYaml.getConfigurationSection("HitEffects." + entry);
                    PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                    if (potionEffectType == null) {
                        main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + mobFile.getName() + " is invalid!"));
                        invalid = true;
                    }
                    int amp = potSection.getInt("Amplifier");
                    int dur = potSection.getInt("Duration");
                    int chance = potSection.getInt("Chance");

                    potionEffectList.add(new MVPotionEffect(potionEffectType, chance, amp, dur));
                }

                for (MVPotionEffect effect : potionEffectList) {
                    mob.withHitEffect(effect);
                }
            }

            if (mobYaml.contains("DeathExplosion"))
                mob.withDeathExplosion(mobYaml.getBoolean("DeathExplosion.Enabled"),
                        mobYaml.getInt("DeathExplosion.Chance"),
                        mobYaml.getInt("DeathExplosion.Size"),
                        mobYaml.getBoolean("DeathExplosion.CreateFire"));

            if (mobYaml.contains("DropsToRemove")) {
                for (String matEnt : mobYaml.getStringList("DropsToRemove")) {
                    Material mat = Material.valueOf(matEnt);
                    if (mat.equals(Material.AIR)) {
                        main.getLogger().info(colorize("&c[ERROR] The material " + matEnt + " specified in the DropsToRemove list is invalid! - Error occured in: " + mobFile.getName()));
                        invalid = true;
                    }
                    mob.withDropToRemove(mat);
                }
            }

            if (mobYaml.contains("Drops")) {
                for (String entry : mobYaml.getConfigurationSection("Drops").getKeys(false)) {
                    ConfigurationSection entrySec = mobYaml.getConfigurationSection("Drops." + entry);
                    ItemChance item = new ItemChance();
                    float chance = (float) entrySec.getDouble("Chance");

                    if (entrySec.getString("Item").startsWith("monstrorvm:")) {
                        item.item = ItemManager.getItem(entrySec.getString("Item").replace("monstrorvm:", ""));

                        if (item.item == null) {
                            main.getLogger().info(colorize("&c[ERROR] The custom item " + entrySec.getString("Item") + " specified in the Item in the Drops is invalid! - Error occured in: " + mobFile.getName()));
                            invalid = true;
                        }
                    } else {
                        try {
                            item.nItem = Material.valueOf(entrySec.getString("Item"));
                        } catch (IllegalArgumentException ex) {
                            main.getLogger().info(colorize("&c[ERROR] The material " + entrySec.getString("Item") + " specified in the Item in the Drops is invalid! - Error occured in: " + mobFile.getName()));
                            invalid = true;
                        }
                    }

                    if (entrySec.contains("MaxAmount"))
                        item.maxAmount = entrySec.getInt("MaxAmount");

                    if (chance == (int) chance)
                        item.chance = (int) chance;
                    else
                        item.chanceF = chance;

                    mob.withDrop(item);
                }
            }

            if (mobYaml.contains("Equipment")) {
                for (String entry : mobYaml.getConfigurationSection("Equipment").getKeys(false)) {
                    ConfigurationSection entrySec = mobYaml.getConfigurationSection("Equipment." + entry);
                    ItemChance item = new ItemChance();
                    float chance = (float) entrySec.getDouble("Chance");

                    if (entrySec.getString("Item").startsWith("monstrorvm:")) {
                        item.item = ItemManager.getItem(entrySec.getString("Item").replace("monstrorvm:", ""));

                        if (item.item == null) {
                            main.getLogger().info(colorize("&c[ERROR] The custom item " + entrySec.getString("Item") + " specified in the Drops is invalid! - Error occured in: " + mobFile.getName()));
                            invalid = true;
                        }
                    } else {
                        try {
                            item.nItem = Material.valueOf(entrySec.getString("Item"));
                        } catch (IllegalArgumentException ex) {
                            main.getLogger().info(colorize("&c[ERROR] The material " + entrySec.getString("Item") + " specified the Drops is invalid! - Error occured in: " + mobFile.getName()));
                            invalid = true;
                        }
                    }

                    if (entrySec.contains("MaxAmount"))
                        item.maxAmount = entrySec.getInt("MaxAmount");

                    if (chance == (int) chance)
                        item.chance = (int) chance;
                    else
                        item.chanceF = chance;

                    if (entry.toLowerCase().equals("helmet"))
                        mob.withHelmet(item);
                    else if (entry.toLowerCase().equals("chestplate"))
                        mob.withChestplate(item);
                    else if (entry.toLowerCase().equals("leggings"))
                        mob.withLeggings(item);
                    else if (entry.toLowerCase().equals("boots"))
                        mob.withBoots(item);
                    else if (entry.toLowerCase().equals("hand"))
                        mob.withMainHandItem(item);
                    else if (entry.toLowerCase().equals("offhand"))
                        mob.withOffhandItem(item);
                    else {
                        main.getLogger().info(colorize("&c[ERROR] The Slot " + entry + " specified in the Equipment is invalid! - Error occured in: " + mobFile.getName()));
                        invalid = true;
                    }
                }
            }

            if (mobYaml.contains("SpawnEffects")) {
                List<MVPotionEffect> potionEffectList = new ArrayList<>();
                for (String entry : mobYaml.getConfigurationSection("SpawnEffects").getKeys(false)) {
                    ConfigurationSection potSection = mobYaml.getConfigurationSection("SpawnEffects." + entry);
                    PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                    if (potionEffectType == null) {
                        main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + mobFile.getName() + " is invalid!"));
                        invalid = true;
                    }
                    int amp = potSection.getInt("Amplifier");
                    int dur = potSection.getInt("Duration");
                    int chance = potSection.getInt("Chance");

                    potionEffectList.add(new MVPotionEffect(potionEffectType, chance, amp, dur));
                }

                for (MVPotionEffect effect : potionEffectList) {
                    mob.withSpawnEffect(effect);
                }
            }

            if (mobYaml.contains("TargetEffect")) {
                int effectChance = mobYaml.getInt("TargetEffect.Chance");

                ConfigurationSection partInfoSec = mobYaml.getConfigurationSection("TargetEffect.ParticleInfo");
                Particle targetPart = Particle.valueOf(partInfoSec.getString("Particle"));
                if (targetPart == null) {
                    main.getLogger().info(colorize("&c[ERROR] The specified ParticleEffectType for TargetEffect  in " + mobFile.getName() + " is invalid!"));
                    invalid = true;
                }
                int targetPartAmount = partInfoSec.getInt("Amount");

                MVParticle targetPartInfo = new MVParticle(targetPart, targetPartAmount);

                ConfigurationSection soundInfoSec = mobYaml.getConfigurationSection("TargetEffect.SoundInfo");
                Sound targetSound = Sound.valueOf(soundInfoSec.getString("Sound"));
                if (targetSound == null) {
                    main.getLogger().info(colorize("&c[ERROR] The specified Sound for TargetEffect  in " + mobFile.getName() + " is invalid!"));
                    invalid = true;
                }
                SoundCategory targetSoundCategory = SoundCategory.valueOf(soundInfoSec.getString("Category"));
                if (targetSoundCategory == null) {
                    main.getLogger().info(colorize("&c[ERROR] The specified SoundCategory for TargetEffect  in " + mobFile.getName() + " is invalid!"));
                    invalid = true;
                }
                float targetSoundVolume = (float) soundInfoSec.getDouble("Volume");
                float targetSoundPitch = (float) soundInfoSec.getDouble("Pitch");

                MVSound targetSoundInfo = new MVSound(targetSound, targetSoundCategory, targetSoundVolume, targetSoundPitch);

                List<MVPotionEffect> selfEffects = new ArrayList<>();
                for (String entry : mobYaml.getConfigurationSection("TargetEffect.SelfEffects").getKeys(false)) {
                    ConfigurationSection potSection = mobYaml.getConfigurationSection("TargetEffect.SelfEffects." + entry);
                    PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                    if (potionEffectType == null) {
                        main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + mobFile.getName() + " is invalid!"));
                        invalid = true;
                    }
                    int amp = potSection.getInt("Amplifier");
                    int dur = potSection.getInt("Duration");
                    int chance = potSection.getInt("Chance");

                    selfEffects.add(new MVPotionEffect(potionEffectType, chance, amp, dur));
                }

                List<MVPotionEffect> targetEffects = new ArrayList<>();
                for (String entry : mobYaml.getConfigurationSection("TargetEffect.TargetEffects").getKeys(false)) {
                    ConfigurationSection potSection = mobYaml.getConfigurationSection("TargetEffect.TargetEffects." + entry);
                    PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                    if (potionEffectType == null) {
                        main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + mobFile.getName() + " is invalid!"));
                        invalid = true;
                    }
                    int amp = potSection.getInt("Amplifier");
                    int dur = potSection.getInt("Duration");
                    int chance = potSection.getInt("Chance");

                    targetEffects.add(new MVPotionEffect(potionEffectType, chance, amp, dur));
                }

                mob.withTargetEffect(new MobTargetEffect(effectChance, targetPartInfo, targetSoundInfo, selfEffects, targetEffects));
            }

            if (mobYaml.contains("BabyInfo"))
                mob.setBaby(new BabyInfo(mobYaml.getBoolean("BabyInfo.CanBeBaby"), mobYaml.getInt("BabyInfo.Chance")));

            if (mobYaml.contains("ValidTargets")) {
                for (String targ : mobYaml.getStringList("ValidTargets")) {
                    EntityType ent = EntityType.valueOf(targ);
                    if (ent == null) {
                        main.getLogger().info(colorize("&c[ERROR] The EntityType " + targ + " specified in the ValidTargets list is invalid! - Error occured in: " + mobFile.getName()));
                        invalid = true;
                    }
                    mob.withPossibleTarget(ent);
                }
            }

            if (!invalid) {
                registerNewMob(mob);
                loadedFiles.add(mobFile);
            }
            else {
                main.getLogger().info(colorize("&e[WARNING] The custom mob " + mobFile.getName() + " has not been registered due to errors!"));
            }
        }
    }

    public static List<String> getValidMobList() {
        return new ArrayList<>(mobMap.keySet());
    }

    public static void addMobToSpawned(LivingEntity entity) {
        if (spawnedCustomMobs.containsKey(entity.getUniqueId())) return;
        spawnedCustomMobs.put(entity.getUniqueId(), entity);
    }

    public static void removeMobFromSpawned(LivingEntity entity) {
        if (!spawnedCustomMobs.containsKey(entity.getUniqueId())) return;
        spawnedCustomMobs.remove(entity.getUniqueId());
    }

    public static Map<UUID, LivingEntity> getSpawnedMobs() {
        return spawnedCustomMobs;
    }
}
