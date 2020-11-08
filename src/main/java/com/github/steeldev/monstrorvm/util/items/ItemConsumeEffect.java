package com.github.steeldev.monstrorvm.util.items;

import com.github.steeldev.monstrorvm.util.misc.MVPotionEffect;

import java.util.List;

public class ItemConsumeEffect {
    public String effectDisplay;
    public List<MVPotionEffect> potionEffects;
    public int hungerValue;

    public ItemConsumeEffect(String effectDisplay,
                             List<MVPotionEffect> potionEffects,
                             int hungerValue) {
        this.effectDisplay = effectDisplay;
        this.potionEffects = potionEffects;
        this.hungerValue = hungerValue;
    }

    public ItemConsumeEffect(String effectDisplay) {
        this.effectDisplay = effectDisplay;
    }
}
