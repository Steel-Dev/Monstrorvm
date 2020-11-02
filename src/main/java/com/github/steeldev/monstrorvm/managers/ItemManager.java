package com.github.steeldev.monstrorvm.managers;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.listeners.bases.CustomItemBase;
import com.github.steeldev.monstrorvm.util.config.Config;
import com.github.steeldev.monstrorvm.util.items.*;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

import static com.github.steeldev.monstrorvm.util.Util.colorize;
import static com.github.steeldev.monstrorvm.util.Util.getRGB;

public class ItemManager {
    static Monstrorvm main = Monstrorvm.getInstance();
    static Map<String, MVItem> mvItemMap;

    static List<String> exampleItems = new ArrayList<>(Arrays.asList("ExampleItem",
            "ExampleFood",
            "ExampleCustomModelDataItem",
            "ExampleCustomMobSpawnEgg",
            "ExampleNBTItem",
            "ExampleSkullWithBase64",
            "ExampleSkullWithOwner",
            "ExampleUseEffectItem",
            "ExampleWeapon",
            "ExampleColoredItem"));

    static List<File> loadedFiles = new ArrayList<>();

    public static void registerNewItem(MVItem item) {
        if (mvItemMap == null) mvItemMap = new HashMap<>();

        if (mvItemMap.containsKey(item.key)) return;

        mvItemMap.put(item.key, item);

        main.getServer().getPluginManager().registerEvents(new CustomItemBase(item.key), main);

        if (Config.DEBUG)
            main.getLogger().info(String.format("&aCustom item &emonstrorvm:%s&a has been &2registered.", item.key));
    }

    public static MVItem getItem(String key) {
        if (!mvItemMap.containsKey(key)) return null;

        return mvItemMap.get(key);
    }

    public static void registerCustomItems() {
        for (String itemString : exampleItems) {
            if (!new File("customthings/items/" + itemString + ".yml").exists())
                main.saveResource("customthings/items/" + itemString + ".yml", true);
        }
        File customItemFile = new File(main.getDataFolder(), "customthings/items");

        main.getLogger().info("&7Loading custom items from " + customItemFile.getPath());
        File[] itemFiles = customItemFile.listFiles();

        if (itemFiles == null ||
                itemFiles.length < 1) {
            main.getLogger().info(colorize("&e[WARNING] There are no Custom Items in the custom item directory, skipping loading."));
            return;
        } else {
            main.getLogger().info("&7Successfully loaded " + itemFiles.length + " custom items! Registering them now.");
        }

        for (File itemFile : itemFiles) {
            boolean invalid = false;
            FileConfiguration itemYaml = YamlConfiguration.loadConfiguration(itemFile);

            if (Config.DEBUG &&
                    !loadedFiles.contains(itemFile))
                main.getLogger().info("Registering " + itemFile.getName());

            if (!itemYaml.contains("Key")) {
                main.getLogger().info(colorize("&c[ERROR] A custom item MUST specify a Key! e.g: 'example_item' - Error occured in: " + itemFile.getName()));
                invalid = true;
            }
            if (!itemYaml.contains("BaseItem")) {
                main.getLogger().info(colorize("&c[ERROR] A custom item MUST specify a BaseItem! e.g: 'STICK' - Error occured in: " + itemFile.getName()));
                invalid = true;
            }

            String itemKey = itemYaml.getString("Key");
            Material baseItem = Material.valueOf(itemYaml.getString("BaseItem"));
            if (baseItem == null ||
                    baseItem.equals(Material.AIR)) {
                main.getLogger().info(colorize("&c[ERROR] The specified BaseItem in " + itemFile.getName() + " is invalid, or air!"));
                invalid = true;
            }

            MVItem item = new MVItem(itemKey, baseItem);

            if (itemYaml.contains("DisplayName")) {
                if (itemYaml.getString("DisplayName").equals("")) {
                    main.getLogger().info(colorize("&e[WARNING] The specified DisplayName for " + itemFile.getName() + " is empty!"));
                }
                item.withDisplayName(itemYaml.getString("DisplayName"));
            }

            if (itemYaml.contains("Lore")) {
                if (itemYaml.getStringList("Lore").size() < 1) {
                    main.getLogger().info(colorize("&c[ERROR] The specified Lore for " + itemFile.getName() + " is empty!"));
                    invalid = true;
                }
                for (String line : itemYaml.getStringList("Lore")) {
                    item.withLore(line);
                }
            }

            if (itemYaml.contains("CustomModelData"))
                item.withCustomModelData(itemYaml.getInt("CustomModelData"));

            if (itemYaml.contains("AttributeInfo")) {
                for (String entry : itemYaml.getConfigurationSection("AttributeInfo").getKeys(false)) {
                    ConfigurationSection attributeSection = itemYaml.getConfigurationSection("AttributeInfo." + entry);
                    String modName = entry.toLowerCase().replace("generic_", "generic.");
                    EquipmentSlot slot = EquipmentSlot.valueOf(attributeSection.getString("Slot"));
                    Attribute attribute = Attribute.valueOf(entry);
                    if (slot == null) {
                        main.getLogger().info(colorize("&c[ERROR] The specified Slot for " + entry + " in " + itemFile.getName() + " is invalid!"));
                        invalid = true;
                    }
                    if (attribute == null) {
                        main.getLogger().info(colorize("&c[ERROR] The specified Attribute for " + entry + " in " + itemFile.getName() + " is invalid!"));
                        invalid = true;
                    }
                    double value = attributeSection.getDouble("Value");
                    item.withAttribute(new ItemAttributeInfo(modName, slot, attribute, value));
                }
            }

            if (itemYaml.contains("EnchantInfo")) {
                for (String entry : itemYaml.getConfigurationSection("EnchantInfo").getKeys(false)) {
                    ConfigurationSection enchantSection = itemYaml.getConfigurationSection("EnchantInfo." + entry);
                    Enchantment enchant = Enchantment.getByName(entry);
                    if (enchant == null) {
                        main.getLogger().info(colorize("&c[ERROR] The specified Enchant for " + entry + " in " + itemFile.getName() + " is invalid!"));
                        invalid = true;
                    }
                    int level = enchantSection.getInt("Value");

                    item.withEnchant(new ItemEnchantInfo(enchant, level));
                }
            }

            if (itemYaml.contains("UseEffect")) {
                ItemUseEffectType type = ItemUseEffectType.valueOf(itemYaml.getString("UseEffect.EffectType"));
                boolean consumeOnUse = itemYaml.getBoolean("UseEffect.ConsumeOnUse");
                List<MVPotionEffect> potionEffectList = new ArrayList<>();
                ConfigurationSection potEffectSection = itemYaml.getConfigurationSection("UseEffect.PotionEffects");
                if (potEffectSection != null) {
                    for (String entry : potEffectSection.getKeys(false)) {
                        ConfigurationSection potSection = itemYaml.getConfigurationSection("UseEffect.PotionEffects." + entry);
                        PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                        if (potionEffectType == null) {
                            main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + itemFile.getName() + " is invalid!"));
                            invalid = true;
                        }
                        int amp = potSection.getInt("Amplifier");
                        int dur = potSection.getInt("Duration");
                        int chance = potSection.getInt("Chance");

                        potionEffectList.add(new MVPotionEffect(potionEffectType, chance, amp, dur));
                    }
                    item.withUseEffect(new ItemUseEffect(type, potionEffectList, consumeOnUse));
                } else {
                    String mobID = itemYaml.getString("UseEffect.MobID");
                    item.withUseEffect(new ItemUseEffect(type, mobID));
                }
            }

            if (itemYaml.contains("AttackEffects")) {
                List<MVPotionEffect> potionEffectList = new ArrayList<>();
                for (String entry : itemYaml.getConfigurationSection("AttackEffects").getKeys(false)) {
                    ConfigurationSection potSection = itemYaml.getConfigurationSection("AttackEffects." + entry);
                    PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                    if (potionEffectType == null) {
                        main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + itemFile.getName() + " is invalid!"));
                        invalid = true;
                    }
                    int amp = potSection.getInt("Amplifier");
                    int dur = potSection.getInt("Duration");
                    int chance = potSection.getInt("Chance");

                    potionEffectList.add(new MVPotionEffect(potionEffectType, chance, amp, dur));
                }

                for (MVPotionEffect effect : potionEffectList) {
                    item.withAttackEffect(effect);
                }
            }

            if (itemYaml.contains("ConsumeEffect")) {
                String effectDisplay = itemYaml.getString("ConsumeEffect.EffectDisplay");

                List<MVPotionEffect> potionEffectList = new ArrayList<>();
                for (String entry : itemYaml.getConfigurationSection("ConsumeEffect.PotionEffects").getKeys(false)) {
                    ConfigurationSection potSection = itemYaml.getConfigurationSection("ConsumeEffect.PotionEffects." + entry);
                    PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                    if (potionEffectType == null) {
                        main.getLogger().info(colorize("&c[ERROR] The specified PotionEffectType for " + entry + " in " + itemFile.getName() + " is invalid!"));
                        invalid = true;
                    }
                    int amp = potSection.getInt("Amplifier");
                    int dur = potSection.getInt("Duration");
                    int chance = potSection.getInt("Chance");

                    potionEffectList.add(new MVPotionEffect(potionEffectType, chance, amp, dur));
                }

                int hungerValue = itemYaml.getInt("ConsumeEffect.HungerValue");

                item.withConsumeEffect(new ItemConsumeEffect(effectDisplay, potionEffectList, hungerValue));
            }

            if (itemYaml.contains("NBT")) {
                List<ItemNBTCompound> nbtList = new ArrayList<>();
                for (String entry : itemYaml.getConfigurationSection("NBT").getKeys(false)) {
                    ConfigurationSection compoundSec = itemYaml.getConfigurationSection("NBT." + entry);

                    String key = compoundSec.getString("Key");
                    Object value = compoundSec.get("Value");

                    nbtList.add(new ItemNBTCompound(key, value));
                }

                for (ItemNBTCompound nbtCompound : nbtList) {
                    item.withNBT(nbtCompound);
                }
            }

            if (itemYaml.contains("Color")) {
                int[] rgb = getRGB(itemYaml.getString("Color").replace("#", ""));
                Color color = Color.fromRGB(rgb[0], rgb[1], rgb[2]);
                if (color == null) {
                    main.getLogger().info(colorize("&c[ERROR] The specified Color in " + itemFile.getName() + " is invalid!"));
                    invalid = true;
                }
                item.withColor(color);
            }

            if (itemYaml.contains("SkullInfo")) {
                if (itemYaml.contains("SkullInfo.OwnerName"))
                    item.withSkullOwnerByName(itemYaml.getString("SkullInfo.OwnerName"));
                if (itemYaml.contains("SkullInfo.Base64"))
                    item.withSkullOwnerByBase64(itemYaml.getString("SkullInfo.Base64"));
            }

            if (!invalid) {
                registerNewItem(item);
                loadedFiles.add(itemFile);
            }
            else {
                main.getLogger().info(colorize("&e[WARNING] The custom item " + itemFile.getName() + " has not been registered due to errors!"));
            }
        }
    }

    public static List<String> getValidItemList() {
        if (mvItemMap == null) return new ArrayList<>();
        return new ArrayList<>(mvItemMap.keySet());
    }
}
