package com.github.steeldev.monstrorvm.managers;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.listeners.bases.CustomMobBase;
import com.github.steeldev.monstrorvm.util.Message;
import com.github.steeldev.monstrorvm.util.Util;
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

public class MobManager {
    public static List<String> errorList = new ArrayList<>();
    public static List<String> warningList = new ArrayList<>();
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

        mob.registeredBy = source;

        mobMap.put(mob.key, mob);

        main.getServer().getPluginManager().registerEvents(new CustomMobBase(mob.key), main);

        if (main.config.DEBUG) {
            if (source != null)
                Message.MOB_REGISTERED_BY.log(mob.key, source.getName());
            else
                Message.MOB_REGISTERED.log(mob.key);
        }
    }

    public static MVMob getMob(String key) {
        if (!mobMap.containsKey(key)) return null;

        return mobMap.get(key);
    }

    public static void registerCustomMobs() {
        if (mobMap == null) mobMap = new HashMap<>();
        errorList.clear();
        warningList.clear();
        for (String mobString : exampleMobs) {
            if (main.config.EXAMPLES_ENABLED) {
                File exampMobFile = new File(main.getDataFolder(), "customthings/mobs/" + mobString + ".yml");
                if (!exampMobFile.exists())
                    main.saveResource("customthings/mobs/" + mobString + ".yml", false);
            } else {
                File exampMobFile = new File(main.getDataFolder(), "customthings/mobs/" + mobString + ".yml");
                if (exampMobFile.exists())
                    exampMobFile.delete();
            }
        }
        File customMobFile = new File(main.getDataFolder(), "customthings/mobs");

        FileConfiguration spigotConfig = main.getServer().spigot().getConfig();

        double maxServerHealth = spigotConfig.getDouble("settings.attribute.maxHealth.max");
        double maxServerMoveSpeed = spigotConfig.getDouble("settings.attribute.movementSpeed.max");
        double maxServerAttackDamage = spigotConfig.getDouble("settings.attribute.attackDamage.max");

        Util.log("&7Loading custom mobs from " + customMobFile.getPath());
        File[] mobFiles = customMobFile.listFiles();

        if (mobFiles == null ||
                mobFiles.length < 1) {
            Util.log("&e[WARNING] There are no Custom Mobs in the custom mob directory, skipping loading.");
            return;
        } else {
            Util.log("&7Successfully loaded " + mobFiles.length + " custom mobs! Registering them now.");
        }

        for (File mobFile : mobFiles) {
            boolean invalid = false;
            boolean canRegister = true;
            if (!main.config.EXAMPLES_ENABLED) {
                if (exampleMobs.contains(mobFile.getName().replace(".yml", "")))
                    canRegister = false;
            }
            if (canRegister) {
                FileConfiguration mobYaml = YamlConfiguration.loadConfiguration(mobFile);

                if (main.config.DEBUG)
                    main.getLogger().info("Registering " + mobFile.getName());

                if (!mobYaml.contains("Key")) {
                    Util.log("&c[ERROR] A custom mob MUST specify a Key! e.g: 'example_mob' - Error occurred in: " + mobFile.getName());
                    errorList.add(Util.latestLog);
                    invalid = true;
                }
                String key = mobYaml.getString("Key");

                if (!mobYaml.contains("BaseEntity")) {
                    Util.log("&c[ERROR] A custom mob MUST specify a Base Entity! e.g: 'ZOMBIE' - Error occurred in: " + mobFile.getName());
                    errorList.add(Util.latestLog);
                    invalid = true;
                }
                EntityType baseEntity = EntityType.valueOf(mobYaml.getString("BaseEntity"));
                if (baseEntity == null) {
                    Util.log("&c[ERROR] The specified base entity in " + mobFile.getName() + " is invalid!");
                    errorList.add(Util.latestLog);
                    invalid = true;
                }

                if (!mobYaml.contains("Name")) {
                    Util.log("&c[ERROR] A custom mob MUST specify a display name! e.g: 'Example Mob' - Error occurred in: " + mobFile.getName());
                    errorList.add(Util.latestLog);
                    invalid = true;
                }
                String displayName = mobYaml.getString("Name");

                if (!mobYaml.contains("SpawnChance")) {
                    Util.log("&c[ERROR] A custom mob MUST specify a spawn chance! e.g: 10 - Error occurred in: " + mobFile.getName());
                    errorList.add(Util.latestLog);
                    invalid = true;
                }
                int spawnChance = mobYaml.getInt("SpawnChance");

                MVMob mob = new MVMob(key, baseEntity, displayName, spawnChance);

                if (mobYaml.contains("EntitiesToReplace")) {
                    if (mobYaml.getStringList("EntitiesToReplace").size() < 1) {
                        Util.log("&e[WARNING] You added the EntitiesToReplace module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String entity : mobYaml.getStringList("EntitiesToReplace")) {
                        EntityType entityType = EntityType.valueOf(entity);
                        if (entityType == null) {
                            Util.log("&c[ERROR] The entity type " + entity + " specified in the EntitesToReplace list is invalid! - Error occurred in: " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        mob.withEntityToReplace(entityType);
                    }
                }

                if (mobYaml.contains("MountInfo")) {
                    EntityType riding = EntityType.valueOf(mobYaml.getString("MountInfo.Riding"));
                    if (riding == null) {
                        Util.log("&c[ERROR] The entity type " + mobYaml.getString("MountInfo.Riding") + " specified in the Riding section of Mount Info is invalid! - Error occurred in: " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    if (!mobYaml.contains("MountInfo.Chance")) {
                        Util.log("&c[ERROR] You are missing the Chance for the MountInfo! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    int chance = mobYaml.getInt("MountInfo.Chance");

                    MountInfo mountInfo = new MountInfo(riding, chance);

                    if (mobYaml.contains("MountInfo.ArmorInfo")) {
                        List<Material> armorMats = new ArrayList<>();
                        if (!mobYaml.contains("MountInfo.ArmorInfo.Chance")) {
                            Util.log("&c[ERROR] You are missing the Chance for the ArmorInfo in MountInfo! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        int armorChance = mobYaml.getInt("MountInfo.ArmorInfo.Chance");
                        if (!mobYaml.contains("MountInfo.ArmorInfo.PossibleTypes")) {
                            Util.log("&c[ERROR] You are missing the PossibleTypes for the ArmorInfo in MountInfo! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (mobYaml.getStringList("MountInfo.ArmorInfo.PossibleTypes").size() < 1) {
                            Util.log("&e[WARNING] You added the ArmorInfo module to the MountInfo module, but didn't populate the armor list! - Warning occurred in: " + mobFile.getName());
                            warningList.add(Util.latestLog);
                        }
                        for (String matEntry : mobYaml.getStringList("MountInfo.ArmorInfo.PossibleTypes")) {
                            Material armorMat = Material.valueOf(matEntry);
                            if (armorMat.equals(Material.AIR)) {
                                Util.log("&c[ERROR] The material " + matEntry + " specified in the PossibleTypes for Mount Armor Info list is invalid! - Error occurred in: " + mobFile.getName());
                                errorList.add(Util.latestLog);
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

                if (mobYaml.contains("DeathEXP")) {
                    if (mobYaml.getList("DeathEXP") != null)
                        mob.withCustomDeathEXP(mobYaml.getIntegerList("DeathEXP"));
                    else
                        mob.withCustomDeathEXP(mobYaml.getInt("DeathEXP"));
                }

                if (mobYaml.contains("BurnInfo")) {
                    if (!mobYaml.contains("BurnInfo.Enabled")) {
                        Util.log("&c[ERROR] You are missing the Enabled value for the BurnInfo! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    if (!mobYaml.contains("BurnInfo.Time")) {
                        Util.log("&c[ERROR] You are missing the Time value for the BurnInfo! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    boolean enabled = mobYaml.getBoolean("BurnInfo.Enabled");
                    int time = mobYaml.getInt("BurnInfo.Time");
                    mob.withBurningEffect(new BurningInfo(enabled, time));
                }

                if (mobYaml.contains("MaxHP")) {
                    if (mobYaml.getList("MaxHP") != null) {
                        mob.withCustomMaxHP(mobYaml.getDoubleList("MaxHP"));
                        for (int i = 0; i < mob.maxHPs.size(); i++) {
                            Double val = mob.maxHPs.get(i);
                            if (val > maxServerHealth) {
                                Util.log("&e[WARNING] The specified max health " + val + " at index " + i + " is greater than the server max defined within the spigot.yml (" + maxServerHealth + ") the mobs max health has been set to the servers max instead. - Warning occurred in: " + mobFile.getName());
                                warningList.add(Util.latestLog);
                                mob.maxHPs.set(i, maxServerHealth);
                            }
                        }
                    } else {
                        if (mobYaml.getDouble("MaxHP") > maxServerHealth) {
                            Util.log("&e[WARNING] The specified max health " + mobYaml.getDouble("MaxHP") + " is greater than the server max defined within the spigot.yml (" + maxServerHealth + ") the mobs max health has been set to the servers max instead. - Warning occurred in: " + mobFile.getName());
                            warningList.add(Util.latestLog);
                            mob.withCustomMaxHP(Collections.singletonList(maxServerHealth));
                        } else mob.withCustomMaxHP(mobYaml.getDouble("MaxHP"));
                    }
                }

                if (mobYaml.contains("MoveSpeed")) {
                    if (mobYaml.getList("MoveSpeed") != null) {
                        mob.withCustomMoveSpeed(mobYaml.getDoubleList("MoveSpeed"));
                        for (int i = 0; i < mob.moveSpeeds.size(); i++) {
                            Double val = mob.moveSpeeds.get(i);
                            if (val > maxServerMoveSpeed) {
                                Util.log("&e[WARNING] The specified move speed " + val + " at index " + i + " is greater than the server max defined within the spigot.yml (" + maxServerMoveSpeed + ") the mobs move speed has been set to the servers max instead. - Warning occurred in: " + mobFile.getName());
                                warningList.add(Util.latestLog);
                                mob.maxHPs.set(i, maxServerHealth);
                            }
                        }
                    } else {
                        if (mobYaml.getDouble("MoveSpeed") > maxServerMoveSpeed) {
                            Util.log("&e[WARNING] The specified move speed " + mobYaml.getDouble("MoveSpeed") + " is greater than the server max defined within the spigot.yml (" + maxServerMoveSpeed + ") the mobs move speed has been set to the servers max instead. - Warning occurred in: " + mobFile.getName());
                            warningList.add(Util.latestLog);
                            mob.withCustomMoveSpeed(Collections.singletonList(maxServerMoveSpeed));
                        } else mob.withCustomMoveSpeed(mobYaml.getDouble("MoveSpeed"));
                    }
                }

                if (mobYaml.contains("AttackDamage")) {
                    if (mobYaml.getList("AttackDamage") != null) {
                        mob.withCustomMaxHP(mobYaml.getDoubleList("AttackDamage"));
                        for (int i = 0; i < mob.attackDamages.size(); i++) {
                            Double val = mob.attackDamages.get(i);
                            if (val > maxServerAttackDamage) {
                                Util.log("&e[WARNING] The specified attack damage " + val + " at index " + i + " is greater than the server max defined within the spigot.yml (" + maxServerAttackDamage + ") the mobs attack damage has been set to the servers max instead. - Warning occurred in: " + mobFile.getName());
                                warningList.add(Util.latestLog);
                                mob.maxHPs.set(i, maxServerHealth);
                            }
                        }
                    } else {
                        if (mobYaml.getDouble("AttackDamage") > maxServerMoveSpeed) {
                            Util.log("&e[WARNING] The specified attack damage " + mobYaml.getDouble("AttackDamage") + " is greater than the server max defined within the spigot.yml (" + maxServerAttackDamage + ") the mobs attack damage has been set to the servers max instead. - Warning occurred in: " + mobFile.getName());
                            warningList.add(Util.latestLog);
                            mob.withCustomAttackDamage(Collections.singletonList(maxServerAttackDamage));
                        } else mob.withCustomAttackDamage(mobYaml.getDouble("AttackDamage"));
                    }
                }

                if (mobYaml.contains("AttackKnockback")) {
                    if (mobYaml.getList("AttackKnockback") != null)
                        mob.withCustomAttackKnockback(mobYaml.getDoubleList("AttackKnockback"));
                    else
                        mob.withCustomAttackKnockback(mobYaml.getDouble("AttackKnockback"));
                }

                if (mobYaml.contains("KnockbackResistance")) {
                    if (mobYaml.getList("KnockbackResistance") != null)
                        mob.withCustomKnockbackResistance(mobYaml.getDoubleList("KnockbackResistance"));
                    else
                        mob.withCustomKnockbackResistance(mobYaml.getDouble("KnockbackResistance"));
                }

                if (mobYaml.contains("Armor")) {
                    if (mobYaml.getList("Armor") != null)
                        mob.withCustomArmor(mobYaml.getDoubleList("Armor"));
                    else
                        mob.withCustomArmor(mobYaml.getDouble("Armor"));
                }

                if (mobYaml.contains("ArmorToughness")) {
                    if (mobYaml.getList("ArmorToughness") != null)
                        mob.withCustomArmorToughness(mobYaml.getDoubleList("ArmorToughness"));
                    else
                        mob.withCustomArmorToughness(mobYaml.getDouble("ArmorToughness"));
                }

                if (mobYaml.contains("FlySpeed")) {
                    if (mobYaml.getList("FlySpeed") != null)
                        mob.withCustomFlySpeed(mobYaml.getDoubleList("FlySpeed"));
                    else
                        mob.withCustomFlySpeed(mobYaml.getDouble("FlySpeed"));
                }

                if (mobYaml.contains("FollowRange")) {
                    if (mobYaml.getList("FollowRange") != null)
                        mob.withCustomFollowRange(mobYaml.getDoubleList("FollowRange"));
                    else
                        mob.withCustomFollowRange(mobYaml.getDouble("FollowRange"));
                }

                if (mobYaml.contains("JumpStrength"))
                    mob.withCustomJumpStrength((float) mobYaml.getDouble("JumpStrength"));

                if (mobYaml.contains("ValidSpawnEnvironments")) {
                    if (mobYaml.getStringList("ValidSpawnEnvironments").size() < 1) {
                        Util.log("&e[WARNING] You added the ValidSpawnEnvironments module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String env : mobYaml.getStringList("ValidSpawnEnvironments")) {
                        mob.withValidSpawnWorld(env);
                    }
                }

                if (mobYaml.contains("SpawnNaturally"))
                    mob.spawnNaturally(mobYaml.getBoolean("SpawnNaturally"));

                if (mobYaml.contains("HitEffects")) {
                    List<MVPotionEffect> potionEffectList = new ArrayList<>();
                    if (mobYaml.getConfigurationSection("HitEffects").getKeys(false).size() < 1) {
                        Util.log("&e[WARNING] You added the DropsToRemove module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String entry : mobYaml.getConfigurationSection("HitEffects").getKeys(false)) {
                        ConfigurationSection potSection = mobYaml.getConfigurationSection("HitEffects." + entry);
                        PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                        if (potionEffectType == null) {
                            Util.log("&c[ERROR] The specified PotionEffectType for " + entry + " is invalid! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Amplifier")) {
                            Util.log("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Duration")) {
                            Util.log("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Chance")) {
                            Util.log("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
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
                        Util.log("&c[ERROR] You are missing the Enabled value for the DeathExplosion! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    if (!mobYaml.contains("DeathExplosion.Chance")) {
                        Util.log("&c[ERROR] You are missing the Chance value for the DeathExplosion! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    if (!mobYaml.contains("DeathExplosion.Size")) {
                        Util.log("&c[ERROR] You are missing the Size value for the DeathExplosion! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    if (!mobYaml.contains("DeathExplosion.CreateFire")) {
                        Util.log("&c[ERROR] You are missing the CreateFire value for the DeathExplosion! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    mob.withDeathExplosion(mobYaml.getBoolean("DeathExplosion.Enabled"),
                            mobYaml.getInt("DeathExplosion.Chance"),
                            mobYaml.getInt("DeathExplosion.Size"),
                            mobYaml.getBoolean("DeathExplosion.CreateFire"));
                }
                if (mobYaml.contains("DropsToRemove")) {
                    if (mobYaml.getStringList("DropsToRemove").size() < 1) {
                        Util.log("&e[WARNING] You added the DropsToRemove module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String matEnt : mobYaml.getStringList("DropsToRemove")) {
                        Material mat = Material.valueOf(matEnt);
                        if (mat.equals(Material.AIR)) {
                            Util.log("&c[ERROR] The material " + matEnt + " specified in the DropsToRemove list is invalid! - Error occurred in: " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        mob.withDropToRemove(mat);
                    }
                }

                if (mobYaml.contains("Drops")) {
                    if (mobYaml.getConfigurationSection("Drops").getKeys(false).size() < 1) {
                        Util.log("&e[WARNING] You added the Drops module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String entry : mobYaml.getConfigurationSection("Drops").getKeys(false)) {
                        ConfigurationSection entrySec = mobYaml.getConfigurationSection("Drops." + entry);
                        ItemChance item = new ItemChance();
                        if (!entrySec.contains("Chance")) {
                            Util.log("&c[ERROR] You are missing the Chance value for the Drop " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!entrySec.contains("Item")) {
                            Util.log("&c[ERROR] You are missing the Item value for the Drop " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        float chance = (float) entrySec.getDouble("Chance");

                        if (entrySec.getString("Item").startsWith("monstrorvm:")) {
                            item.item = ItemManager.getItem(entrySec.getString("Item").replace("monstrorvm:", ""));

                            if (item.item == null) {
                                Util.log("&c[ERROR] The custom item " + entrySec.getString("Item") + " specified in the Item in the Drops is invalid! - Error occurred in: " + mobFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                        } else {
                            try {
                                item.nItem = Material.valueOf(entrySec.getString("Item"));
                            } catch (IllegalArgumentException ex) {
                                Util.log("&c[ERROR] The material " + entrySec.getString("Item") + " specified in the Item in the Drops is invalid! - Error occurred in: " + mobFile.getName());
                                errorList.add(Util.latestLog);
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
                        Util.log("&e[WARNING] You added the Equipment module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String entry : mobYaml.getConfigurationSection("Equipment").getKeys(false)) {
                        ConfigurationSection entrySec = mobYaml.getConfigurationSection("Equipment." + entry);
                        ItemChance item = new ItemChance();
                        if (!entrySec.contains("DropChance")) {
                            Util.log("&c[ERROR] You are missing the DropChance value for the Equipment " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!entrySec.contains("Item")) {
                            Util.log("&c[ERROR] You are missing the Item value for the Equipment " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        float chance = (float) entrySec.getDouble("DropChance");

                        if (entrySec.getString("Item").startsWith("monstrorvm:")) {
                            item.item = ItemManager.getItem(entrySec.getString("Item").replace("monstrorvm:", ""));

                            if (item.item == null) {
                                Util.log("&c[ERROR] The custom item " + entrySec.getString("Item") + " specified in the Drops is invalid! - Error occurred in: " + mobFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                        } else {
                            try {
                                item.nItem = Material.valueOf(entrySec.getString("Item"));
                            } catch (IllegalArgumentException ex) {
                                Util.log("&c[ERROR] The material " + entrySec.getString("Item") + " specified the Drops is invalid! - Error occurred in: " + mobFile.getName());
                                errorList.add(Util.latestLog);
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
                            Util.log("&c[ERROR] The Slot " + entry + " specified in the Equipment is invalid! - Error occurred in: " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                    }
                }

                if (mobYaml.contains("SpawnEffects")) {
                    List<MVPotionEffect> potionEffectList = new ArrayList<>();
                    if (mobYaml.getConfigurationSection("SpawnEffects").getKeys(false).size() < 1) {
                        Util.log("&e[WARNING] You added the SpawnEffects module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                        errorList.add(Util.latestLog);
                    }
                    for (String entry : mobYaml.getConfigurationSection("SpawnEffects").getKeys(false)) {
                        ConfigurationSection potSection = mobYaml.getConfigurationSection("SpawnEffects." + entry);
                        PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                        if (potionEffectType == null) {
                            Util.log("&c[ERROR] The specified PotionEffectType for " + entry + " in " + mobFile.getName() + " is invalid!");
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Amplifier")) {
                            Util.log("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Duration")) {
                            Util.log("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Chance")) {
                            Util.log("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
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
                        Util.log("&c[ERROR] You are missing the Chance for the TargetEffect! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    int effectChance = mobYaml.getInt("TargetEffect.Chance");
                    if (mobYaml.contains("TargetEffect.ParticleInfo")) {
                        ConfigurationSection partInfoSec = mobYaml.getConfigurationSection("TargetEffect.ParticleInfo");
                        if (!partInfoSec.contains("Particle")) {
                            Util.log("&c[ERROR] You are missing the Particle for the ParticleInfo in TargetEffect! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        Particle targetPart = Particle.valueOf(partInfoSec.getString("Particle"));
                        if (targetPart == null) {
                            Util.log("&c[ERROR] The specified ParticleEffectType for TargetEffect is invalid! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!partInfoSec.contains("Amount")) {
                            Util.log("&c[ERROR] You are missing the Particle Amount for the TargetEffect! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        int targetPartAmount = partInfoSec.getInt("Amount");

                        targetPartInfo = new MVParticle(targetPart, targetPartAmount);
                    }


                    if (mobYaml.contains("TargetEffect.SoundInfo")) {
                        ConfigurationSection soundInfoSec = mobYaml.getConfigurationSection("TargetEffect.SoundInfo");

                        if (!soundInfoSec.contains("Sound")) {
                            Util.log("&c[ERROR] You are missing the Sound for the Sound Info in TargetEffect! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }

                        Sound targetSound = Sound.valueOf(soundInfoSec.getString("Sound"));
                        if (targetSound == null) {
                            Util.log("&c[ERROR] The specified Sound for TargetEffect is invalid! Error occurred in " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        SoundCategory targetSoundCategory = SoundCategory.valueOf(soundInfoSec.getString("Category"));
                        if (targetSoundCategory == null) {
                            Util.log("&c[ERROR] The specified SoundCategory for TargetEffect is invalid! Error occurred in: " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }

                        if (!soundInfoSec.contains("Volume")) {
                            Util.log("&c[ERROR] You are missing the Volume for the SoundInfo in TargetEffect! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }

                        if (!soundInfoSec.contains("Pitch")) {
                            Util.log("&c[ERROR] You are missing the Pitch for the SoundInfo in TargetEffect! Error occurred in:  " + mobFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }

                        float targetSoundVolume = (float) soundInfoSec.getDouble("Volume");
                        float targetSoundPitch = (float) soundInfoSec.getDouble("Pitch");

                        targetSoundInfo = new MVSound(targetSound, targetSoundCategory, targetSoundVolume, targetSoundPitch);
                    }

                    List<MVPotionEffect> selfEffects = new ArrayList<>();
                    if (mobYaml.contains("TargetEffect.SelfEffects")) {
                        if (mobYaml.getConfigurationSection("TargetEffect.SelfEffects").getKeys(false).size() < 1) {
                            Util.log("&e[WARNING] You added the SelEffects module to the TargetEffect module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                            warningList.add(Util.latestLog);
                        }
                        for (String entry : mobYaml.getConfigurationSection("TargetEffect.SelfEffects").getKeys(false)) {
                            ConfigurationSection potSection = mobYaml.getConfigurationSection("TargetEffect.SelfEffects." + entry);
                            PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                            if (potionEffectType == null) {
                                Util.log("&c[ERROR] The specified PotionEffectType for " + entry + " is invalid! Error occurred in:  " + mobFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                            if (!potSection.contains("Amplifier")) {
                                Util.log("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                            if (!potSection.contains("Duration")) {
                                Util.log("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                            if (!potSection.contains("Chance")) {
                                Util.log("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                                errorList.add(Util.latestLog);
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
                            Util.log("&e[WARNING] You added the TargetEffects module to the TargetEffect module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                            warningList.add(Util.latestLog);
                        }
                        for (String entry : mobYaml.getConfigurationSection("TargetEffect.TargetEffects").getKeys(false)) {
                            ConfigurationSection potSection = mobYaml.getConfigurationSection("TargetEffect.TargetEffects." + entry);
                            PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                            if (potionEffectType == null) {
                                Util.log("&c[ERROR] The specified PotionEffectType for " + entry + " is invalid! Error occurred in: " + mobFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                            if (!potSection.contains("Amplifier")) {
                                Util.log("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                            if (!potSection.contains("Duration")) {
                                Util.log("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                            if (!potSection.contains("Chance")) {
                                Util.log("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occurred in:  " + mobFile.getName());
                                errorList.add(Util.latestLog);
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
                        Util.log("&c[ERROR] You are missing the CanBeBaby for the BabyInfo! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    if (!mobYaml.contains("BabyInfo.Chance")) {
                        Util.log("&c[ERROR] You are missing the Chance for the BabyInfo! Error occurred in:  " + mobFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    mob.setBaby(new BabyInfo(mobYaml.getBoolean("BabyInfo.CanBeBaby"), mobYaml.getInt("BabyInfo.Chance")));
                }

                if (mobYaml.contains("ValidTargets")) {
                    if (mobYaml.getStringList("ValidTargets").size() < 1) {
                        Util.log("&e[WARNING] You added the ValidTargets module, but didn't populate the list! - Warning occurred in: " + mobFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String targ : mobYaml.getStringList("ValidTargets")) {
                        EntityType ent = EntityType.valueOf(targ);
                        if (ent == null) {
                            Util.log("&c[ERROR] The EntityType " + targ + " specified in the ValidTargets list is invalid! - Error occurred in: " + mobFile.getName());
                            errorList.add(Util.latestLog);
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
                    Util.log("&e[WARNING] The custom mob " + mobFile.getName() + " has not been registered due to errors!");
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
