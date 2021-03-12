package com.github.steeldev.monstrorvm.util.items;


import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;

import java.util.List;

public class ItemUseEffect {
    public ItemUseEffectType type;
    public String mobID;
    public List<MVPotionEffect> potionEffects;
    public boolean consumeItemOnUse;

    public ItemUseEffect(ItemUseEffectType type,
                         List<MVPotionEffect> potionEffects,
                         boolean consumeItemOnUse) {
        this.type = type;
        this.potionEffects = potionEffects;
        this.consumeItemOnUse = consumeItemOnUse;
    }

    public ItemUseEffect(ItemUseEffectType type,
                         String mobID) {
        this.type = type;
        this.mobID = mobID;
    }

    public ItemUseEffect(ItemUseEffectType type) {
        this.type = type;
    }

    public ItemUseEffect(ItemUseEffectType type,
                         boolean consumeItemOnUse) {
        this.type = type;
        this.consumeItemOnUse = consumeItemOnUse;
    }
}
