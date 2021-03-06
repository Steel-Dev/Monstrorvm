package com.github.steeldev.monstrorvm.managers;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.listeners.bases.CustomItemBase;
import com.github.steeldev.monstrorvm.util.Message;
import com.github.steeldev.monstrorvm.util.Util;
import com.github.steeldev.monstrorvm.util.items.*;
import com.github.steeldev.monstrorvm.util.items.recipe.ItemCraftingRecipe;
import com.github.steeldev.monstrorvm.util.items.recipe.ItemRecipe;
import com.github.steeldev.monstrorvm.util.items.recipe.ItemSmeltingRecipe;
import com.github.steeldev.monstrorvm.util.items.recipe.ItemSmithingRecipe;
import com.github.steeldev.monstrorvm.util.items.recipe.types.CraftType;
import com.github.steeldev.monstrorvm.util.items.recipe.types.ItemRecipeType;
import com.github.steeldev.monstrorvm.util.items.recipe.types.SmeltType;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

import static com.github.steeldev.monstrorvm.util.Util.getRGB;

public class ItemManager {
    public static List<String> errorList = new ArrayList<>();
    public static List<String> warningList = new ArrayList<>();
    static Monstrorvm main = Monstrorvm.getInstance();
    static Map<String, MVItem> itemMap;
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

    public static void registerNewItem(MVItem item, Plugin source) {
        if (itemMap == null) itemMap = new HashMap<>();

        if (itemMap.containsKey(item.key)) itemMap.replace(item.key, item);

        item.registeredBy = source;

        itemMap.put(item.key, item);

        if (item.consumeEffect != null || item.useEffect != null || item.attackEffect != null)
            main.getServer().getPluginManager().registerEvents(new CustomItemBase(item.key), main);

        if (main.config.DEBUG) {
            if (source != null)
                Message.ITEM_REGISTERED_BY.log(item.key, source.getName());
            else
                Message.ITEM_REGISTERED.log(item.key);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
            if (item.recipes != null) {
                for (ItemRecipe recipe : item.recipes) {
                    if (recipe instanceof ItemCraftingRecipe) {
                        ItemCraftingRecipe craftRec = (ItemCraftingRecipe) recipe;
                        if (craftRec.craftingIngredientsChoice != null) {
                            RecipeManager.addCraftingRecipe(craftRec.key,
                                    craftRec.craftType,
                                    new RecipeChoice.ExactChoice(item.getItem()),
                                    craftRec.resultAmount,
                                    craftRec.craftingPattern,
                                    craftRec.craftingIngredientsChoice);
                        }
                    }
                    if (recipe instanceof ItemSmeltingRecipe) {
                        ItemSmeltingRecipe smeltRec = (ItemSmeltingRecipe) recipe;

                        Material result = null;

                        ItemStack resultItem = null;


                        RecipeChoice inputChoice;
                        RecipeChoice resultChoice;


                        if (smeltRec.smeltingResult.startsWith("monstrorvm:")) {
                            resultItem = ItemManager.getItem(smeltRec.smeltingResult.replace("monstrorvm:", "")).getItem(false);
                        } else {
                            result = Material.valueOf(smeltRec.smeltingResult.replace("monstrorvm:", ""));
                            if (result == null) {
                                main.getLogger().info("&c[ERROR] Result material for SMELTING recipe is invalid! - Error occured in: " + item.toString());
                            }
                        }
                        resultChoice = (resultItem == null) ? new RecipeChoice.MaterialChoice(result) : new RecipeChoice.ExactChoice(resultItem);

                        inputChoice = new RecipeChoice.ExactChoice(item.getItem(false));

                        RecipeManager.addSmeltingRecipe(smeltRec.key,
                                smeltRec.smeltType,
                                resultChoice,
                                smeltRec.resultAmount,
                                inputChoice,
                                smeltRec.smeltEXP,
                                smeltRec.smeltTime);
                    }
                    if (recipe instanceof ItemSmithingRecipe) {
                        ItemSmithingRecipe smithingRec = (ItemSmithingRecipe) recipe;

                        ItemStack result = item.getItem(false);

                        ItemStack baseStack = null;
                        Material baseMat = null;
                        RecipeChoice baseChoice;

                        ItemStack additionStack = null;
                        Material additionMat = null;
                        RecipeChoice additionChoice;

                        if (smithingRec.smithingBaseMat.startsWith("monstrorvm:")) {
                            baseStack = ItemManager.getItem(smithingRec.smithingBaseMat.replace("monstrorvm:", "")).getItem(false);
                        } else {
                            Material resMat = Material.valueOf(smithingRec.smithingBaseMat);
                            if (resMat == null) {
                                main.getLogger().info("&c[ERROR] Base material for SMITHING recipe is invalid! - Error occured in: " + item.toString());
                            }
                            baseMat = resMat;
                        }
                        baseChoice = (baseStack == null) ? new RecipeChoice.MaterialChoice(baseMat) : new RecipeChoice.ExactChoice(baseStack);

                        if (smithingRec.smithingItemNeeded.startsWith("monstrorvm:")) {
                            additionStack = ItemManager.getItem(smithingRec.smithingItemNeeded.replace("monstrorvm:", "")).getItem(false);
                        } else {
                            Material resMat = Material.valueOf(smithingRec.smithingItemNeeded);
                            if (resMat == null) {
                                main.getLogger().info("&c[ERROR] Addition material for SMITHING recipe is invalid! - Error occured in: " + item.toString());
                            }
                            additionMat = resMat;
                        }
                        additionChoice = (additionStack == null) ? new RecipeChoice.MaterialChoice(additionMat) : new RecipeChoice.ExactChoice(additionStack);

                        RecipeManager.addSmithingRecipe(smithingRec.key,
                                result,
                                baseChoice,
                                additionChoice);
                    }
                }
            }
            main.recipesRegistered = true;
        }, 60l);

    }

    public static MVItem getItem(String key) {
        if (!itemMap.containsKey(key)) return null;

        return itemMap.get(key);
    }

    public static void registerCustomItems() {
        if (itemMap == null) itemMap = new HashMap<>();
        errorList.clear();
        warningList.clear();
        for (String itemString : exampleItems) {
            if (main.config.EXAMPLES_ENABLED) {
                File exampItemFile = new File(main.getDataFolder(), "customthings/items/" + itemString + ".yml");
                if (!exampItemFile.exists())
                    main.saveResource("customthings/items/" + itemString + ".yml", false);
            } else {
                File exampItemFile = new File(main.getDataFolder(), "customthings/items/" + itemString + ".yml");
                if (exampItemFile.exists())
                    exampItemFile.delete();
            }
        }
        File customItemFile = new File(main.getDataFolder(), "customthings/items");

        main.getLogger().info("&7Loading custom items from " + customItemFile.getPath());
        File[] itemFiles = customItemFile.listFiles();

        if (itemFiles == null ||
                itemFiles.length < 1) {
            Util.log("&e[WARNING] There are no Custom Items in the custom item directory, skipping loading.");
            return;
        } else {
            main.getLogger().info("&7Successfully loaded " + itemFiles.length + " custom items! Registering them now.");
        }

        for (File itemFile : itemFiles) {
            boolean invalid = false;
            boolean canRegister = true;
            if (!main.config.EXAMPLES_ENABLED) {
                if (exampleItems.contains(itemFile.getName().replace(".yml", "")))
                    canRegister = false;
            }
            if (canRegister) {
                FileConfiguration itemYaml = YamlConfiguration.loadConfiguration(itemFile);

                if (main.config.DEBUG)
                    main.getLogger().info("Registering " + itemFile.getName());

                if (!itemYaml.contains("Key")) {
                    Util.log("&c[ERROR] A custom item MUST specify a Key! e.g: 'example_item' - Error occured in: " + itemFile.getName());
                    errorList.add(Util.latestLog);
                    invalid = true;
                }
                if (!itemYaml.contains("BaseItem")) {
                    Util.log("&c[ERROR] A custom item MUST specify a BaseItem! e.g: 'STICK' - Error occured in: " + itemFile.getName());
                    errorList.add(Util.latestLog);
                    invalid = true;
                }

                String itemKey = itemYaml.getString("Key");
                Material baseItem = Material.valueOf(itemYaml.getString("BaseItem"));
                if (baseItem == null ||
                        baseItem.equals(Material.AIR)) {
                    Util.log("&c[ERROR] The specified BaseItem in " + itemFile.getName() + " is invalid, or air!");
                    errorList.add(Util.latestLog);
                    invalid = true;
                }

                MVItem item = new MVItem(itemKey, baseItem);

                if (itemYaml.contains("Category")) {
                    item.withCategory(itemYaml.getString("Category"));
                }

                if (itemYaml.contains("DisplayName")) {
                    if (itemYaml.getString("DisplayName").equals("")) {
                        Util.log("&e[WARNING] The specified DisplayName for " + itemFile.getName() + " is empty!");
                        warningList.add(Util.latestLog);
                    }
                    item.withDisplayName(itemYaml.getString("DisplayName"));
                }

                if (itemYaml.contains("Lore")) {
                    if (itemYaml.getStringList("Lore").size() < 1) {
                        Util.log("&c[ERROR] The specified Lore for " + itemFile.getName() + " is empty!");
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    for (String line : itemYaml.getStringList("Lore")) {
                        item.withLore(line);
                    }
                }

                if (itemYaml.contains("CustomModelData"))
                    item.withCustomModelData(itemYaml.getInt("CustomModelData"));

                if (itemYaml.contains("AttributeInfo")) {
                    if (itemYaml.getConfigurationSection("AttributeInfo").getKeys(false).size() < 1) {
                        Util.log("&e[WARNING] You added the EntitiesToReplace module, but didn't populate the list! - Warning occured in: " + itemFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String entry : itemYaml.getConfigurationSection("AttributeInfo").getKeys(false)) {
                        ConfigurationSection attributeSection = itemYaml.getConfigurationSection("AttributeInfo." + entry);
                        String modName = entry.toLowerCase().replace("generic_", "generic.");
                        EquipmentSlot slot = EquipmentSlot.valueOf(attributeSection.getString("Slot"));
                        Attribute attribute = Attribute.valueOf(entry);
                        if (slot == null) {
                            Util.log("&c[ERROR] The specified Slot for " + entry + " in " + itemFile.getName() + " is invalid!");
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (attribute == null) {
                            Util.log("&c[ERROR] The specified Attribute for " + entry + " in " + itemFile.getName() + " is invalid!");
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!attributeSection.contains("Value")) {
                            Util.log("&c[ERROR] You are missing the Value for the AttributeModifier " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        double value = attributeSection.getDouble("Value");
                        item.withAttribute(new ItemAttributeInfo(modName, slot, attribute, value));
                    }
                }

                if (itemYaml.contains("EnchantInfo")) {
                    if (itemYaml.getConfigurationSection("EnchantInfo").getKeys(false).size() < 1) {
                        Util.log("&e[WARNING] You added the EnchantInfo module, but didn't populate list! - Warning occured in: " + itemFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String entry : itemYaml.getConfigurationSection("EnchantInfo").getKeys(false)) {
                        ConfigurationSection enchantSection = itemYaml.getConfigurationSection("EnchantInfo." + entry);
                        Enchantment enchant = Enchantment.getByName(entry);
                        if (enchant == null) {
                            Util.log("&c[ERROR] The specified Enchant for " + entry + " in " + itemFile.getName() + " is invalid!");
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!enchantSection.contains("Level")) {
                            Util.log("&c[ERROR] You are missing the Level for the Enchant " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        int level = enchantSection.getInt("Level");

                        item.withEnchant(new ItemEnchantInfo(enchant, level));
                    }
                }

                if (itemYaml.contains("UseEffect")) {
                    ItemUseEffectType type = ItemUseEffectType.valueOf(itemYaml.getString("UseEffect.EffectType"));
                    boolean consumeOnUse = itemYaml.getBoolean("UseEffect.ConsumeOnUse");
                    List<MVPotionEffect> potionEffectList = new ArrayList<>();
                    ConfigurationSection potEffectSection = itemYaml.getConfigurationSection("UseEffect.PotionEffects");
                    if (potEffectSection != null) {
                        if (potEffectSection.getKeys(false).size() < 1) {
                            Util.log("&e[WARNING] You added the PotionEffects module to the UseEffect module, but didn't populate list! - Warning occured in: " + itemFile.getName());
                            warningList.add(Util.latestLog);
                        }
                        for (String entry : potEffectSection.getKeys(false)) {
                            ConfigurationSection potSection = itemYaml.getConfigurationSection("UseEffect.PotionEffects." + entry);
                            PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                            if (potionEffectType == null) {
                                Util.log("&c[ERROR] The specified PotionEffectType for " + entry + " in " + itemFile.getName() + " is invalid!");
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                            if (!potSection.contains("Amplifier")) {
                                Util.log("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occured in -  " + itemFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                            if (!potSection.contains("Duration")) {
                                Util.log("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occured in -  " + itemFile.getName());
                                errorList.add(Util.latestLog);
                                invalid = true;
                            }
                            if (!potSection.contains("Chance")) {
                                Util.log("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occured in -  " + itemFile.getName());
                                errorList.add(Util.latestLog);
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
                    if (itemYaml.getConfigurationSection("AttackEffects").getKeys(false).size() < 1) {
                        Util.log("&e[WARNING] You added the AttackEffects module, but didn't populate list! - Warning occured in: " + itemFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String entry : itemYaml.getConfigurationSection("AttackEffects").getKeys(false)) {
                        ConfigurationSection potSection = itemYaml.getConfigurationSection("AttackEffects." + entry);
                        PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                        if (potionEffectType == null) {
                            Util.log("&c[ERROR] The specified PotionEffectType for " + entry + " in " + itemFile.getName() + " is invalid!");
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Amplifier")) {
                            Util.log("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Duration")) {
                            Util.log("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Chance")) {
                            Util.log("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
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
                    if (itemYaml.getConfigurationSection("ConsumeEffect.PotionEffects").getKeys(false).size() < 1) {
                        Util.log("&e[WARNING] You added the PotionEffects module to the ConsumeEffect module, but didn't populate list! - Warning occured in: " + itemFile.getName());
                        errorList.add(Util.latestLog);
                    }
                    for (String entry : itemYaml.getConfigurationSection("ConsumeEffect.PotionEffects").getKeys(false)) {
                        ConfigurationSection potSection = itemYaml.getConfigurationSection("ConsumeEffect.PotionEffects." + entry);
                        PotionEffectType potionEffectType = PotionEffectType.getByName(entry);
                        if (potionEffectType == null) {
                            Util.log("&c[ERROR] The specified PotionEffectType for " + entry + " in " + itemFile.getName() + " is invalid!");
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Amplifier")) {
                            Util.log("&c[ERROR] You are missing the Amplifier for the PotionEffect " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Duration")) {
                            Util.log("&c[ERROR] You are missing the Duration for the PotionEffect " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!potSection.contains("Chance")) {
                            Util.log("&c[ERROR] You are missing the Chance for the PotionEffect " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
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
                    if (itemYaml.getConfigurationSection("NBT").getKeys(false).size() < 1) {
                        Util.log("&e[WARNING] You added the NBT module, but didn't populate the list! - Warning occured in: " + itemFile.getName());
                        errorList.add(Util.latestLog);
                    }
                    for (String entry : itemYaml.getConfigurationSection("NBT").getKeys(false)) {
                        ConfigurationSection compoundSec = itemYaml.getConfigurationSection("NBT." + entry);

                        if (!compoundSec.contains("Key")) {
                            Util.log("&c[ERROR] You are missing the Key for the NBTCompound " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        if (!compoundSec.contains("Value")) {
                            Util.log("&c[ERROR] You are missing the Value for the NBTCompound " + entry + "! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }

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
                        Util.log("&c[ERROR] The specified Color in " + itemFile.getName() + " is invalid!");
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }
                    item.withColor(color);
                }

                if (itemYaml.contains("SkullInfo")) {
                    if (itemYaml.contains("SkullInfo.OwnerName") && itemYaml.contains("SkullInfo.Base64")) {
                        Util.log("&c[ERROR] You can only have Base64 or OwnerName in SkullInfo, not both! Error occured in -  " + itemFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    } else if (!itemYaml.contains("SkullInfo.OwnerName") && !itemYaml.contains("SkullInfo.Base64")) {
                        Util.log("&c[ERROR] You must either have Base64 or OwnerName in SkullInfo! Found neither! Error occured in -  " + itemFile.getName());
                        errorList.add(Util.latestLog);
                        invalid = true;
                    }

                    if (itemYaml.contains("SkullInfo.OwnerName"))
                        item.withSkullOwnerByName(itemYaml.getString("SkullInfo.OwnerName"));
                    if (itemYaml.contains("SkullInfo.Base64"))
                        item.withSkullOwnerByBase64(itemYaml.getString("SkullInfo.Base64"));
                }

                if (itemYaml.contains("Flags")) {
                    for (String flag : itemYaml.getStringList("Flags")) {
                        item.withFlag(ItemFlag.valueOf(flag));
                    }
                }

                if (itemYaml.contains("BookInfo")) {
                    String author = itemYaml.getString("BookInfo.Author");
                    String title = itemYaml.getString("BookInfo.Title");
                    BookMeta.Generation generation = BookMeta.Generation.valueOf(itemYaml.getString("BookInfo.Generation").toUpperCase());
                    List<String> pages = itemYaml.getStringList("BookInfo.Pages");

                    item.withAuthor(author);
                    item.withTitle(title);
                    item.withGeneration(generation);
                    for (String page : pages) {
                        item.withPage(page);
                    }
                }

                if (itemYaml.contains("Recipes")) {
                    if (itemYaml.getConfigurationSection("Recipes").getKeys(false).size() < 1) {
                        Util.log("&e[WARNING] You added the Recipes module, but didn't populate the list! - Warning occured in: " + itemFile.getName());
                        warningList.add(Util.latestLog);
                    }
                    for (String entry : itemYaml.getConfigurationSection("Recipes").getKeys(false)) {
                        ItemRecipeType type = ItemRecipeType.valueOf(entry);
                        if (type == null || !Arrays.asList(ItemRecipeType.values()).contains(type)) {
                            Util.log("&c[ERROR] The specified RecipeType " + entry + " is invalid! Error occured in -  " + itemFile.getName());
                            errorList.add(Util.latestLog);
                            invalid = true;
                        }
                        ConfigurationSection recipeSec = itemYaml.getConfigurationSection("Recipes." + entry);
                        switch (type) {
                            case CRAFTING:
                                if (recipeSec.getKeys(false).size() < 1) {
                                    Util.log("&e[WARNING] You specified the CRAFTING section in the Recipes Module, but didn't populate the list! - Warning occured in: " + itemFile.getName());
                                    warningList.add(Util.latestLog);
                                    invalid = true;
                                }
                                for (String key : recipeSec.getKeys(false)) {
                                    if (key.contains("SHAPED")) {
                                        ConfigurationSection shapedSec = recipeSec.getConfigurationSection("SHAPED");
                                        if (!shapedSec.contains("Key")) {
                                            Util.log("&c[ERROR] You specify a Key for a recipe! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        if (!shapedSec.contains("Pattern")) {
                                            Util.log("&c[ERROR] You specified the RecipeType as SHAPED, but you didn't provide a pattern! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        List<String> pattern = shapedSec.getStringList("Pattern");
                                        if (pattern.size() < 3) {
                                            Util.log("&c[ERROR] You specified a Pattern in the SHAPED recipe, but you didn't define all 3 rows! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        if (pattern.get(0).length() < 3) {
                                            Util.log("&c[ERROR] The first row of the SHAPED recipe is less than 3 characters! If you meant for a slot to be nothing, just put a space! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        if (pattern.get(0).length() > 3) {
                                            Util.log("&c[ERROR] The first row of the SHAPED recipe has too many characters! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }

                                        if (pattern.get(1).length() < 3) {
                                            Util.log("&c[ERROR] The second row of the SHAPED recipe is less than 3 characters! If you meant for a slot to be nothing, just put a space! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        if (pattern.get(1).length() > 3) {
                                            Util.log("&c[ERROR] The second row of the SHAPED recipe has too many characters! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }

                                        if (pattern.get(2).length() < 3) {
                                            Util.log("&c[ERROR] The third row of the SHAPED recipe is less than 3 characters! If you meant for a slot to be nothing, just put a space! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        if (pattern.get(2).length() > 3) {
                                            Util.log("&c[ERROR] The third row of the SHAPED recipe has too many characters! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }

                                        if (!shapedSec.contains("Ingredients")) {
                                            Util.log("&c[ERROR] You specified the RecipeType as SHAPED, but you didn't provide any ingredients! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }

                                        ConfigurationSection ingredients = shapedSec.getConfigurationSection("Ingredients");
                                        if (ingredients.getKeys(false).size() < 1) {
                                            Util.log("&c[ERROR] You specified Ingredients in the SHAPED recipe, but didn't populate it! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        Map<Character, RecipeChoice> finalIngredients = new HashMap<>();

                                        for (String mat : ingredients.getKeys(false)) {
                                            if (!mat.startsWith("monstrorvm:")) {
                                                Material ingMat = Material.valueOf(ingredients.getString(mat));
                                                Character recChar = mat.toCharArray()[0];
                                                if (ingMat == null) {
                                                    Util.log("&c[ERROR] The specified Material " + ingredients.getString(mat) + " in the SHAPED recipe ingredient list is invalid! Error occured in -  " + itemFile.getName());
                                                    errorList.add(Util.latestLog);
                                                    invalid = true;
                                                }
                                                finalIngredients.put(recChar, new RecipeChoice.MaterialChoice(ingMat));
                                            } else {
                                                ItemStack ingItem = getItem(mat.replace("monstrorvm:", "")).getItem();
                                                Character recChar = mat.toCharArray()[0];
                                                if (ingItem == null) {
                                                    Util.log("&c[ERROR] The specified Item " + ingredients.getString(mat) + " in the SHAPED recipe ingredient list is invalid! Error occured in -  " + itemFile.getName());
                                                    errorList.add(Util.latestLog);
                                                    invalid = true;
                                                }
                                                finalIngredients.put(recChar, new RecipeChoice.ExactChoice(ingItem));
                                            }
                                        }

                                        int amount = shapedSec.getInt("Amount");
                                        if (amount == 0) amount = 1;

                                        item.withRecipe(new ItemCraftingRecipe(CraftType.SHAPED, pattern, finalIngredients, amount, shapedSec.getString("Key")));

                                    } else if (key.contains("SHAPELESS")) {
                                        ConfigurationSection shapelessSec = recipeSec.getConfigurationSection("SHAPELESS");
                                        if (!shapelessSec.contains("Key")) {
                                            Util.log("&c[ERROR] You must specify a Key for a recipe! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        if (!shapelessSec.contains("Ingredients")) {
                                            Util.log("&c[ERROR] You specified the RecipeType as SHAPELESS, but you didn't provide any ingredients! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }

                                        ConfigurationSection ingredients = shapelessSec.getConfigurationSection("Ingredients");
                                        if (ingredients.getKeys(false).size() < 1) {
                                            Util.log("&c[ERROR] You specified Ingredients in the SHAPED recipe, but didn't populate it! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        Map<Character, RecipeChoice> finalIngredients = new HashMap<>();

                                        for (String mat : ingredients.getKeys(false)) {
                                            Material ingMat = Material.valueOf(ingredients.getString(mat));
                                            Character recChar = mat.toCharArray()[0];
                                            if (ingMat == null) {
                                                Util.log("&c[ERROR] The specified Material " + ingredients.getString(mat) + " in the SHAPED recipe ingredient list is invalid! Error occured in -  " + itemFile.getName());
                                                errorList.add(Util.latestLog);
                                                invalid = true;
                                            }
                                            finalIngredients.put(recChar, new RecipeChoice.MaterialChoice(ingMat));
                                        }

                                        int amount = shapelessSec.getInt("Amount");
                                        if (amount == 0) amount = 1;

                                        item.withRecipe(new ItemCraftingRecipe(CraftType.SHAPELESS, finalIngredients, amount, shapelessSec.getString("Key")));
                                    } else {
                                        Util.log("&c[ERROR] The specified Recipe Type " + entry + " in the Recipes list is invalid! Error occured in -  " + itemFile.getName());
                                        errorList.add(Util.latestLog);
                                        invalid = true;
                                    }
                                }
                                break;
                            case SMELTING:
                                if (recipeSec.getKeys(false).size() < 1) {
                                    Util.log("&e[WARNING] You specified the SMELTING section in the Recipes Module, but didn't populate the list! - Error occured in: " + itemFile.getName());
                                    errorList.add(Util.latestLog);
                                    invalid = true;
                                }
                                for (String key : recipeSec.getKeys(false)) {
                                    if (key.contains("FURNACE")) {
                                        ConfigurationSection furnaceSec = recipeSec.getConfigurationSection("FURNACE");
                                        if (!furnaceSec.contains("Key")) {
                                            Util.log("&c[ERROR] You must specify a Key for a recipe! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        String result = furnaceSec.getString("Result");
                                        int time = furnaceSec.getInt("Time");
                                        int exp = furnaceSec.getInt("EXP");
                                        int amount = furnaceSec.getInt("Amount");

                                        item.withRecipe(new ItemSmeltingRecipe(SmeltType.FURNACE, result, time, exp, amount, furnaceSec.getString("Key")));
                                    } else if (key.contains("SMOKER")) {
                                        ConfigurationSection smokerSec = recipeSec.getConfigurationSection("SMOKER");
                                        if (!smokerSec.contains("Key")) {
                                            Util.log("&c[ERROR] You specify a Key for a recipe! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        String result = smokerSec.getString("Result");
                                        int time = smokerSec.getInt("Time");
                                        int exp = smokerSec.getInt("EXP");
                                        int amount = smokerSec.getInt("Amount");

                                        item.withRecipe(new ItemSmeltingRecipe(SmeltType.SMOKER, result, time, exp, amount, smokerSec.getString("Key")));
                                    } else if (key.contains("BLASTING")) {
                                        ConfigurationSection blastingSec = recipeSec.getConfigurationSection("BLASTING");
                                        if (!blastingSec.contains("Key")) {
                                            Util.log("&c[ERROR] You must specify a Key for a recipe! Error occured in -  " + itemFile.getName());
                                            errorList.add(Util.latestLog);
                                            invalid = true;
                                        }
                                        String result = blastingSec.getString("Result");
                                        int time = blastingSec.getInt("Time");
                                        int exp = blastingSec.getInt("EXP");
                                        int amount = blastingSec.getInt("Amount");

                                        item.withRecipe(new ItemSmeltingRecipe(SmeltType.BLASTING, result, time, exp, amount, blastingSec.getString("Key")));
                                    } else {
                                        Util.log("&c[ERROR] The specified Recipe Type " + entry + " in the Recipes list is invalid! Error occured in -  " + itemFile.getName());
                                        errorList.add(Util.latestLog);
                                        invalid = true;
                                    }
                                }
                                break;
                            case SMITHING:
                                if (!recipeSec.contains("Key")) {
                                    Util.log("&c[ERROR] You must specify a Key for a recipe! Error occured in -  " + itemFile.getName());
                                    errorList.add(Util.latestLog);
                                    invalid = true;
                                }
                                String itemNeeded = recipeSec.getString("ItemNeeded");
                                String baseMat = recipeSec.getString("BaseMat");

                                item.withRecipe(new ItemSmithingRecipe(itemNeeded, baseMat, recipeSec.getString("Key")));
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + type);
                        }
                    }
                }

                if (!invalid) {
                    if (itemMap.containsKey(item.key)) {
                        itemMap.remove(item.key);
                        itemMap.put(item.key, item);
                        main.getLogger().info("&aThe custom item " + itemFile.getName() + " has successfully been updated!");
                    } else
                        registerNewItem(item, null);
                } else {
                    Util.log("&e[WARNING] The custom item " + itemFile.getName() + " has not been registered due to errors!");
                }
            }
        }
    }

    public static List<String> getValidItemList() {
        if (itemMap == null) return new ArrayList<>();
        return new ArrayList<>(itemMap.keySet());
    }
}
