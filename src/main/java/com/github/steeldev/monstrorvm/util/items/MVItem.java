package com.github.steeldev.monstrorvm.util.items;

import com.github.steeldev.monstrorvm.Monstrorvm;
import com.github.steeldev.monstrorvm.util.items.recipe.ItemRecipe;
import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTStringList;
import dev.dbassett.skullcreator.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Colorable;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.steeldev.monstrorvm.util.Util.colorize;
import static com.github.steeldev.monstrorvm.util.Util.rand;

public class MVItem {
    public Plugin registeredBy;
    public String key;
    public Material baseItem;
    public String category;
    public String displayName;
    public List<String> lore;
    public int customModelData;
    public List<ItemAttributeInfo> attributeInfo;
    public List<ItemEnchantInfo> enchantInfo;
    public ItemUseEffect useEffect;
    public List<MVPotionEffect> attackEffect;
    public ItemConsumeEffect consumeEffect;
    public List<ItemNBTCompound> nbtCompoundList;
    public Color color;
    public SkullInfo skullInfo;
    public List<ItemRecipe> recipes;
    public List<ItemFlag> flags;

    Monstrorvm main = Monstrorvm.getInstance();

    public MVItem(String key,
                  Material baseItem) {
        this.key = key;
        this.baseItem = baseItem;
    }

    public MVItem withCategory(String category){
        this.category = category;
        return this;
    }

    public MVItem withDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public MVItem withConsumeEffect(ItemConsumeEffect effect) {
        if (this.baseItem.isEdible())
            this.consumeEffect = effect;
        return this;
    }

    public MVItem withUseEffect(ItemUseEffect effect) {
        this.useEffect = effect;
        return this;
    }

    public MVItem withEnchant(ItemEnchantInfo enchantInfo) {
        if (this.enchantInfo == null) this.enchantInfo = new ArrayList<>();
        this.enchantInfo.add(enchantInfo);
        return this;
    }

    public MVItem withAttribute(ItemAttributeInfo attributeInfo) {
        if (this.attributeInfo == null) this.attributeInfo = new ArrayList<>();
        this.attributeInfo.add(attributeInfo);
        return this;
    }

    public MVItem withLore(String line) {
        if (this.lore == null) this.lore = new ArrayList<>();
        this.lore.add(line);
        return this;
    }

    public MVItem withCustomModelData(int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public MVItem withNBT(ItemNBTCompound compound) {
        if (this.nbtCompoundList == null) this.nbtCompoundList = new ArrayList<>();
        this.nbtCompoundList.add(compound);
        return this;
    }

    public MVItem withColor(Color color) {
        this.color = color;
        return this;
    }

    public MVItem withSkullOwnerByName(String playerName) {
        if (this.skullInfo == null) this.skullInfo = new SkullInfo();
        this.skullInfo.owningPlayer = main.getServer().getOfflinePlayer(playerName);
        return this;
    }

    public MVItem withSkullOwnerByBase64(String base64) {
        if (this.skullInfo == null) this.skullInfo = new SkullInfo();
        this.skullInfo.base64 = base64;
        return this;
    }

    public MVItem withAttackEffect(MVPotionEffect effect) {
        if (this.attackEffect == null) this.attackEffect = new ArrayList<>();
        this.attackEffect.add(effect);
        return this;
    }

    public MVItem withRecipe(ItemRecipe recipe) {
        if (this.recipes == null) this.recipes = new ArrayList<>();
        this.recipes.add(recipe);
        return this;
    }

    public MVItem withFlag(ItemFlag flag){
        if(this.flags == null) this.flags = new ArrayList<>();

        this.flags.add(flag);

        return this;
    }

    public ItemStack getItem(){
        return getItem(false);
    }

    public ItemStack getItem(boolean damaged) {
        ItemStack customItem = new ItemStack(baseItem);
        if (skullInfo != null) {
            if (skullInfo.base64 != null &&
                    !skullInfo.base64.equals("")) {
                customItem = SkullCreator.itemFromBase64(skullInfo.base64);
            }
        }

        ItemMeta customItemMeta = (customItem.getItemMeta() == null) ? Bukkit.getItemFactory().getItemMeta(baseItem) : customItem.getItemMeta();

        if (customItemMeta != null) {
            if (displayName != null && !displayName.equals(""))
                customItemMeta.setDisplayName(colorize(displayName));
            if (lore != null && lore.size() > 0) {
                List<String> lore = new ArrayList<>();
                for (String line : this.lore) {
                    lore.add(colorize(line));
                }
                customItemMeta.setLore(lore);
            }
            if (customModelData != 0)
                customItemMeta.setCustomModelData(customModelData);

            if (attributeInfo != null && attributeInfo.size() > 0) {
                for (ItemAttributeInfo attributeInfo : attributeInfo) {
                    customItemMeta.addAttributeModifier(attributeInfo.attributeMod, new AttributeModifier(UUID.randomUUID(), attributeInfo.attributeModName, attributeInfo.attributeValue, AttributeModifier.Operation.ADD_NUMBER, attributeInfo.slot));
                }
            }

            if (damaged) {
                if (customItemMeta instanceof Damageable)
                    ((Damageable) customItemMeta).setDamage(rand.nextInt(baseItem.getMaxDurability() - 20));
            }

            if (color != null) {
                if (customItemMeta instanceof LeatherArmorMeta)
                    ((LeatherArmorMeta) customItemMeta).setColor(color);
                if (customItemMeta instanceof Colorable)
                    ((Colorable) customItemMeta).setColor(DyeColor.getByColor(color));
            }

            if (skullInfo != null) {
                if (skullInfo.owningPlayer != null) {
                    if (customItemMeta instanceof SkullMeta)
                        ((SkullMeta) customItemMeta).setOwningPlayer(skullInfo.owningPlayer);
                }
            }

            customItem.setItemMeta(customItemMeta);
        }

        if (enchantInfo != null && enchantInfo.size() > 0) {
            for (ItemEnchantInfo enchantInfo : enchantInfo) {
                customItem.addUnsafeEnchantment(enchantInfo.enchant, enchantInfo.level);
            }
        }

        if(flags != null && flags.size() > 0){
            for(ItemFlag flag : flags){
                customItemMeta.addItemFlags(flag);
            }
        }

        NBTItem customItemNBT = new NBTItem(customItem);
        customItemNBT.addCompound("MVItem");
        customItemNBT.setString("MVItem", key);
        if (nbtCompoundList != null && nbtCompoundList.size() > 0) {
            for (ItemNBTCompound compound : nbtCompoundList) {
                customItemNBT.addCompound(compound.compoundKey);
                switch (compound.compoundType) {
                    case BOOLEAN:
                        customItemNBT.setBoolean(compound.compoundKey, (Boolean) compound.compoundValue);
                        break;
                    case DOUBLE:
                        customItemNBT.setDouble(compound.compoundKey, (Double) compound.compoundValue);
                        break;
                    case FLOAT:
                        customItemNBT.setFloat(compound.compoundKey, (Float) compound.compoundValue);
                        break;
                    case STRING:
                        customItemNBT.setString(compound.compoundKey, (String) compound.compoundValue);
                        break;
                    case INTEGER:
                        customItemNBT.setInteger(compound.compoundKey, (Integer) compound.compoundValue);
                        break;
                }
            }
        }
        customItem = customItemNBT.getItem();

        return customItem;
    }
}
