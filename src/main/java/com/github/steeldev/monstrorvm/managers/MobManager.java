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
import org.bukkit.plugin.Plugin;
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

    public static void init() {
        spawnedCustomMobs = new HashMap<>();
    }

    public static void registerNewMob(MVMob mob, Plugin source) {
        if (mobMap == null) mobMap = new HashMap<>();

        if (mobMap.containsKey(mob.key)) return;

        mobMap.put(mob.key, mob);

        main.getServer().getPluginManager().registerEvents(new CustomMobBase(mob.key), main);

        if (Config.DEBUG) {
            if (source != null)
                main.getLogger().info(String.format("&aCustom mob &emonstrorvm:%s&a has been &2registered by " + source.getName() + ".", mob.key));
            else
                main.getLogger().info(String.format("&aCustom mob &emonstrorvm:%s&a has been &2registered.", mob.key));
        }
    }

    public static MVMob getMob(String key) {
        if (!mobMap.containsKey(key)) return null;

        return mobMap.get(key);
    }

    public static void registerCustomMobs() {
        if (mobMap == null) mobMap = new HashMap<>();
        for (String mobString : exampleMobs) {
            if (Config.EXAMPLES_ENABLED) {
                File exampMobFile = new File(main.getDataFolder(), "customthings/mobs/" + mobString + ".yml");
                if (!exampMobFile.exists())
                    main.saveResource("customthings/mobs/" + mobString + ".yml", false);
            }
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
            boolean canRegister = true;
            if (!Config.EXAMPLES_ENABLED) {
                if (exampleMobs.contains(mobFile.getName().replace(".yml", "")))
                    canRegister = false;
            }
            if (canRegister) {
                FileConfiguration mobYaml = YamlConfiguration.loadConfiguration(mobFile);

                if (Config.DEBUG)
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
                    if (mobYaml.getStringList("EntitiesToReplace").size() < 1) {
                        main.getLogger().info(colorize("&e[WARNING] You added the EntitiesToReplace module, but didn't populate the armor list! - Error occured in: " + mobFile.getName()));
                    }
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
                    if (!mobYaml.contains("MountInfo.Chance")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the Chance for the MountInfo! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
                    int chance = mobYaml.getInt("MountInfo.Chance");

                    MountInfo mountInfo = new MountInfo(riding, chance);

                    if (mobYaml.contains("MountInfo.ArmorInfo")) {
                        List<Material> armorMats = new ArrayList<>();
                        if (!mobYaml.contains("MountInfo.ArmorInfo.Chance")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Chance for the ArmorInfo in MountInfo! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        int armorChance = mobYaml.getInt("MountInfo.ArmorInfo.Chance");
                        if (!mobYaml.contains("MountInfo.ArmorInfo.PossibleTypes")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the PossibleTypes for the ArmorInfo in MountInfo! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        if (mobYaml.getStringList("MountInfo.ArmorInfo.PossibleTypes").size() < 1) {
                            main.getLogger().info(colorize("&e[WARNING] You added the ArmorInfo module to the MountInfo module, but didn't populate the armor list! - Error occured in: " + mobFile.getName()));
                        }
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
                    if (!mobYaml.contains("BurnInfo.Enabled")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the Enabled value for the BurnInfo! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
                    if (!mobYaml.contains("BurnInfo.Time")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the Time value for the BurnInfo! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
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
                    if (mobYaml.getStringList("ValidSpawnEnvironments").size() < 1) {
                        main.getLogger().info(colorize("&e[WARNING] You added the ValidSpawnEnvironments module, but didn't populate the list! - Error occured in: " + mobFile.getName()));
                    }
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
                    if (mobYaml.getConfigurationSection("HitEffects").getKeys(false).size() < 1) {
                        main.getLogger().info(colorize("&e[WARNING] You added the DropsToRemove module, but didn't populate the list! - Error occured in: " + mobFile.getName()));
                    }
                    for (String entry : mobYaml.getConfigurationSection("HitEffects").getKeys(false)) {
                        ConfigurationSection potSection = mobYaml.getConfigurationSection("HitEffects." + entry);
                        PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                        if (potionEffectType == null) {
                            main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + mobFile.getName() + " is invalid!"));
                            invalid = true;
                        }
                        if (!potSection.contains("Amplifier")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        if (!potSection.contains("Duration")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        if (!potSection.contains("Chance")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
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

                if (mobYaml.contains("DeathExplosion")) {
                    if (!mobYaml.contains("DeathExplosion.Enabled")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the Enabled value for the DeathExplosion! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
                    if (!mobYaml.contains("DeathExplosion.Chance")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the Chance value for the DeathExplosion! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
                    if (!mobYaml.contains("DeathExplosion.Size")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the Size value for the DeathExplosion! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
                    if (!mobYaml.contains("DeathExplosion.CreateFire")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the CreateFire value for the DeathExplosion! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
                    mob.withDeathExplosion(mobYaml.getBoolean("DeathExplosion.Enabled"),
                            mobYaml.getInt("DeathExplosion.Chance"),
                            mobYaml.getInt("DeathExplosion.Size"),
                            mobYaml.getBoolean("DeathExplosion.CreateFire"));
                }
                if (mobYaml.contains("DropsToRemove")) {
                    if (mobYaml.getStringList("DropsToRemove").size() < 1) {
                        main.getLogger().info(colorize("&e[WARNING] You added the DropsToRemove module, but didn't populate the list! - Error occured in: " + mobFile.getName()));
                    }
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
                    if (mobYaml.getConfigurationSection("Drops").getKeys(false).size() < 1) {
                        main.getLogger().info(colorize("&e[WARNING] You added the Drops module, but didn't populate the list! - Error occured in: " + mobFile.getName()));
                    }
                    for (String entry : mobYaml.getConfigurationSection("Drops").getKeys(false)) {
                        ConfigurationSection entrySec = mobYaml.getConfigurationSection("Drops." + entry);
                        ItemChance item = new ItemChance();
                        if (!entrySec.contains("Chance")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Chance value for the Drop " + entry + "! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        if (!entrySec.contains("Item")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Item value for the Drop " + entry + "! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
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

                        if (entrySec.contains("Damaged"))
                            item.damaged = entrySec.getBoolean("Damaged");

                        if (chance == (int) chance)
                            item.chance = (int) chance;
                        else
                            item.chanceF = chance;

                        mob.withDrop(item);
                    }
                }

                if (mobYaml.contains("Equipment")) {
                    if (mobYaml.getConfigurationSection("Equipment").getKeys(false).size() < 1) {
                        main.getLogger().info(colorize("&e[WARNING] You added the Equipment module, but didn't populate the list! - Error occured in: " + mobFile.getName()));
                    }
                    for (String entry : mobYaml.getConfigurationSection("Equipment").getKeys(false)) {
                        ConfigurationSection entrySec = mobYaml.getConfigurationSection("Equipment." + entry);
                        ItemChance item = new ItemChance();
                        if (!entrySec.contains("DropChance")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the DropChance value for the Equipment " + entry + "! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        if (!entrySec.contains("Item")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Item value for the Equipment " + entry + "! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        float chance = (float) entrySec.getDouble("DropChance");

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

                        if (entrySec.contains("Damaged"))
                            item.damaged = entrySec.getBoolean("Damaged");

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
                    if (mobYaml.getConfigurationSection("SpawnEffects").getKeys(false).size() < 1) {
                        main.getLogger().info(colorize("&e[WARNING] You added the SpawnEffects module, but didn't populate the list! - Error occured in: " + mobFile.getName()));
                    }
                    for (String entry : mobYaml.getConfigurationSection("SpawnEffects").getKeys(false)) {
                        ConfigurationSection potSection = mobYaml.getConfigurationSection("SpawnEffects." + entry);
                        PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                        if (potionEffectType == null) {
                            main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + mobFile.getName() + " is invalid!"));
                            invalid = true;
                        }
                        if (!potSection.contains("Amplifier")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        if (!potSection.contains("Duration")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        if (!potSection.contains("Chance")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
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
                    MVParticle targetPartInfo = null;
                    MVSound targetSoundInfo = null;
                    if (!mobYaml.contains("TargetEffect.Chance")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the Chance for the TargetEffect! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
                    int effectChance = mobYaml.getInt("TargetEffect.Chance");
                    if (mobYaml.contains("TargetEffect.ParticleInfo")) {
                        ConfigurationSection partInfoSec = mobYaml.getConfigurationSection("TargetEffect.ParticleInfo");
                        if (!partInfoSec.contains("Particle")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Particle for the ParticleInfo in TargetEffect! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        Particle targetPart = Particle.valueOf(partInfoSec.getString("Particle"));
                        if (targetPart == null) {
                            main.getLogger().info(colorize("&c[ERROR] The specified ParticleEffectType for TargetEffect  in " + mobFile.getName() + " is invalid!"));
                            invalid = true;
                        }
                        if (!partInfoSec.contains("Amount")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Particle Amount for the TargetEffect! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }
                        int targetPartAmount = partInfoSec.getInt("Amount");

                        targetPartInfo = new MVParticle(targetPart, targetPartAmount);
                    }


                    if (mobYaml.contains("TargetEffect.SoundInfo")) {
                        ConfigurationSection soundInfoSec = mobYaml.getConfigurationSection("TargetEffect.SoundInfo");

                        if (!soundInfoSec.contains("Sound")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Sound for the Sound Info in TargetEffect! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }

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

                        if (!soundInfoSec.contains("Volume")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Volume for the SoundInfo in TargetEffect! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }

                        if (!soundInfoSec.contains("Pitch")) {
                            main.getLogger().info(colorize("&c[ERROR] You are missing the Pitch for the SoundInfo in TargetEffect! Error occured in -  " + mobFile.getName()));
                            invalid = true;
                        }

                        float targetSoundVolume = (float) soundInfoSec.getDouble("Volume");
                        float targetSoundPitch = (float) soundInfoSec.getDouble("Pitch");

                        targetSoundInfo = new MVSound(targetSound, targetSoundCategory, targetSoundVolume, targetSoundPitch);
                    }

                    List<MVPotionEffect> selfEffects = new ArrayList<>();
                    if (mobYaml.contains("TargetEffect.SelfEffects")) {
                        if (mobYaml.getConfigurationSection("TargetEffect.SelfEffects").getKeys(false).size() < 1) {
                            main.getLogger().info(colorize("&e[WARNING] You added the SelEffects module to the TargetEffect module, but didn't populate the list! - Error occured in: " + mobFile.getName()));
                        }
                        for (String entry : mobYaml.getConfigurationSection("TargetEffect.SelfEffects").getKeys(false)) {
                            ConfigurationSection potSection = mobYaml.getConfigurationSection("TargetEffect.SelfEffects." + entry);
                            PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                            if (potionEffectType == null) {
                                main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + mobFile.getName() + " is invalid!"));
                                invalid = true;
                            }
                            if (!potSection.contains("Amplifier")) {
                                main.getLogger().info(colorize("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                                invalid = true;
                            }
                            if (!potSection.contains("Duration")) {
                                main.getLogger().info(colorize("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                                invalid = true;
                            }
                            if (!potSection.contains("Chance")) {
                                main.getLogger().info(colorize("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                                invalid = true;
                            }
                            int amp = potSection.getInt("Amplifier");
                            int dur = potSection.getInt("Duration");
                            int chance = potSection.getInt("Chance");

                            selfEffects.add(new MVPotionEffect(potionEffectType, chance, amp, dur));
                        }
                    }

                    List<MVPotionEffect> targetEffects = new ArrayList<>();
                    if (mobYaml.contains("TargetEffect.TargetEffects")) {
                        if (mobYaml.getConfigurationSection("TargetEffect.TargetEffects").getKeys(false).size() < 1) {
                            main.getLogger().info(colorize("&e[WARNING] You added the TargetEffects module to the TargetEffect module, but didn't populate the list! - Error occured in: " + mobFile.getName()));
                        }
                        for (String entry : mobYaml.getConfigurationSection("TargetEffect.TargetEffects").getKeys(false)) {
                            ConfigurationSection potSection = mobYaml.getConfigurationSection("TargetEffect.TargetEffects." + entry);
                            PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                            if (potionEffectType == null) {
                                main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + mobFile.getName() + " is invalid!"));
                                invalid = true;
                            }
                            if (!potSection.contains("Amplifier")) {
                                main.getLogger().info(colorize("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                                invalid = true;
                            }
                            if (!potSection.contains("Duration")) {
                                main.getLogger().info(colorize("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                                invalid = true;
                            }
                            if (!potSection.contains("Chance")) {
                                main.getLogger().info(colorize("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occured in -  " + mobFile.getName()));
                                invalid = true;
                            }
                            int amp = potSection.getInt("Amplifier");
                            int dur = potSection.getInt("Duration");
                            int chance = potSection.getInt("Chance");

                            targetEffects.add(new MVPotionEffect(potionEffectType, chance, amp, dur));
                        }
                    }

                    mob.withTargetEffect(new MobTargetEffect(effectChance, targetPartInfo, targetSoundInfo, selfEffects, targetEffects));
                }

                if (mobYaml.contains("BabyInfo")) {
                    if (!mobYaml.contains("BabyInfo.CanBeBaby")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the CanBeBaby for the BabyInfo! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
                    if (!mobYaml.contains("BabyInfo.Chance")) {
                        main.getLogger().info(colorize("&c[ERROR] You are missing the Chance for the BabyInfo! Error occured in -  " + mobFile.getName()));
                        invalid = true;
                    }
                    mob.setBaby(new BabyInfo(mobYaml.getBoolean("BabyInfo.CanBeBaby"), mobYaml.getInt("BabyInfo.Chance")));
                }

                if (mobYaml.contains("ValidTargets")) {
                    if (mobYaml.getStringList("ValidTargets").size() < 1) {
                        main.getLogger().info(colorize("&e[WARNING] You added the ValidTargets module, but didn't populate the list! - Error occured in: " + mobFile.getName()));
                    }
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
                    if (mobMap.containsKey(mob.key)) {
                        mobMap.remove(mob.key);
                        mobMap.put(mob.key, mob);
                        main.getLogger().info("&aThe custom mob " + mobFile.getName() + " has successfully been updated!");
                    } else
                        registerNewMob(mob, null);
                } else {
                    main.getLogger().info(colorize("&e[WARNING] The custom mob " + mobFile.getName() + " has not been registered due to errors!"));
                }
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
