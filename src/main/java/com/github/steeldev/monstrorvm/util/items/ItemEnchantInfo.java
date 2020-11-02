package com.github.steeldev.monstrorvm.util.items;

import org.bukkit.enchantments.Enchantment;

public class ItemEnchantInfo {
    public Enchantment enchant;
    public int level;

    public ItemEnchantInfo(Enchantment enchant,
                           int level) {
        this.enchant = enchant;
        this.level = level;
    }
}
